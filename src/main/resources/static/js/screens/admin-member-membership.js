document.addEventListener("DOMContentLoaded", () => {
    const panel = document.querySelector(".membership-panel");
    if (!panel) return;

    const memberId = panel.dataset.memberId;
    const list = document.querySelector("#membership-list");
    const feedback = document.querySelector("#membership-feedback");
    const openButton = document.querySelector("#open-membership-modal");
    const modal = document.querySelector("#membership-modal");
    const closeButton = document.querySelector("#close-membership-modal");
    const cancelButton = document.querySelector("#cancel-membership-registration");
    const form = document.querySelector("#membership-registration-form");
    const productSelect = document.querySelector("#membership-product");
    const startDate = document.querySelector("#membership-start-date");
    const productSummary = document.querySelector("#membership-product-summary");
    const modalMessage = document.querySelector("#membership-modal-message");
    const submitButton = document.querySelector("#submit-membership-registration");
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    const statusLabels = {
        PENDING_PAYMENT: "결제 대기",
        ACTIVE: "이용 중",
        PAUSED: "일시정지",
        EXPIRED: "만료",
        CANCELLED: "취소"
    };
    let products = [];
    let previousFocus = null;
    let processing = false;

    openButton.addEventListener("click", openModal);
    closeButton.addEventListener("click", closeModal);
    cancelButton.addEventListener("click", closeModal);
    form.addEventListener("submit", registerMembership);
    productSelect.addEventListener("change", showSelectedProduct);
    startDate.addEventListener("input", () => clearFieldError("startDate"));
    modal.addEventListener("click", event => {
        if (event.target === modal) closeModal();
    });
    document.addEventListener("keydown", event => {
        if (event.key === "Escape" && !modal.hidden) closeModal();
    });
    list.addEventListener("click", handleStatusAction);

    loadMemberships();

    async function loadMemberships() {
        list.setAttribute("aria-busy", "true");
        list.replaceChildren(createState("membership-loading", "회원권 정보를 불러오는 중입니다."));
        try {
            const result = await request(`/api/members/${memberId}/memberships`);
            renderMemberships(result.data || []);
        } catch (error) {
            list.replaceChildren(createState("membership-empty", error.message));
        } finally {
            list.setAttribute("aria-busy", "false");
        }
    }

    function renderMemberships(memberships) {
        list.replaceChildren();
        if (!memberships.length) {
            list.append(createState("membership-empty", "등록된 회원권이 없습니다."));
            return;
        }
        memberships.forEach(membership => list.append(createMembershipCard(membership)));
    }

    function createMembershipCard(membership) {
        const article = element("article", "membership-history-card");
        const heading = element("div", "membership-history-heading");
        const titleBox = element("div");
        titleBox.append(
            textElement("h3", membership.productName || "회원권"),
            textElement("p", `${formatDate(membership.startDate)} – ${formatDate(membership.endDate)}`)
        );
        const badge = textElement("span", statusLabels[membership.status] || membership.status);
        badge.className = `membership-status status-${String(membership.status).toLowerCase()}`;
        heading.append(titleBox, badge);

        const details = element("dl", "membership-history-details");
        appendDetail(details, "구분", productTypeLabel(membership.productType));
        appendDetail(details, "잔여 기간", `${Math.max(membership.remainingDays ?? 0, 0)}일`);
        if (["PT", "COMBINED"].includes(membership.productType)) {
            appendDetail(details, "남은 PT", `${membership.remainingPtSessions ?? 0}회`);
        }
        appendDetail(details, "등록일", formatDateTime(membership.createdAt));

        article.append(heading, details);
        const actions = createActions(membership);
        if (actions.childElementCount) article.append(actions);
        return article;
    }

    function createActions(membership) {
        const actions = element("div", "membership-history-actions");
        if (membership.status === "ACTIVE") actions.append(actionButton("일시정지", "pause", membership.membershipId));
        if (membership.status === "PAUSED") actions.append(actionButton("이용 재개", "resume", membership.membershipId));
        const history = actionButton("결제 내역", "history", membership.membershipId);
        actions.append(history);
        if (membership.status === "PENDING_PAYMENT") {
            actions.append(actionButton("결제 완료", "pay", membership.membershipId));
            const cancel = actionButton("회원권 취소", "cancel", membership.membershipId);
            cancel.classList.add("danger-button");
            actions.append(cancel);
        }
        return actions;
    }

    async function handleStatusAction(event) {
        const button = event.target.closest("button[data-action]");
        if (!button || processing) return;
        if (button.dataset.action === "history") {
            const params = new URLSearchParams({
                memberId,
                view: "history"
            });
            window.location.href = `/admin/memberships?${params}`;
            return;
        }
        if (button.dataset.action === "pay") {
            await handlePayment(button);
            return;
        }
        const labels = {pause: "일시정지", resume: "이용 재개", cancel: "취소"};
        if (!window.confirm(`이 회원권을 ${labels[button.dataset.action]} 처리하시겠습니까?`)) return;
        setProcessing(true, button);
        clearFeedback();
        try {
            const result = await request(
                `/api/members/${memberId}/memberships/${button.dataset.membershipId}/${button.dataset.action}`,
                {method: "PATCH"}
            );
            showFeedback(result.message || "회원권 상태가 변경되었습니다.", false);
            await loadMemberships();
        } catch (error) {
            showFeedback(error.message, true);
        } finally {
            setProcessing(false, button);
        }
    }

    async function handlePayment(button) {
        const paymentMethod = window.prompt("결제수단을 입력해 주세요: CARD, CASH, TRANSFER", "CARD");
        if (paymentMethod === null) return;
        const normalized = paymentMethod.trim().toUpperCase();
        if (!["CARD", "CASH", "TRANSFER"].includes(normalized)) {
            showFeedback("결제수단은 CARD, CASH, TRANSFER 중 하나여야 합니다.", true);
            return;
        }
        if (!window.confirm("선택한 회원권의 결제를 완료하시겠습니까?")) return;
        setProcessing(true, button);
        clearFeedback();
        try {
            const result = await request("/api/payments", {
                method: "POST",
                body: JSON.stringify({
                    membershipId: Number(button.dataset.membershipId),
                    paymentMethod: normalized
                })
            });
            showFeedback(result.message || "결제가 완료되었습니다.", false);
            await loadMemberships();
        } catch (error) {
            showFeedback(error.message, true);
        } finally {
            setProcessing(false, button);
        }
    }

    async function openModal() {
        if (processing) return;
        previousFocus = document.activeElement;
        products = [];
        clearModalMessage();
        form.reset();
        productSummary.hidden = true;
        startDate.value = localDate(new Date());
        modal.hidden = false;
        document.body.classList.add("modal-open");
        productSelect.disabled = true;
        productSelect.replaceChildren(new Option("상품을 불러오는 중입니다.", ""));
        try {
            const result = await request("/api/membership-products");
            products = result.data || [];
            productSelect.replaceChildren(new Option("상품을 선택해 주세요", ""));
            products.forEach(product => {
                productSelect.add(new Option(
                    `${product.name} · ${formatPrice(product.price)}`,
                    String(product.productId)
                ));
            });
            if (!products.length) showModalMessage("등록 가능한 회원권 상품이 없습니다.");
        } catch (error) {
            productSelect.replaceChildren(new Option("상품을 불러오지 못했습니다.", ""));
            showModalMessage(error.message);
        } finally {
            productSelect.disabled = products.length === 0;
            (products.length ? productSelect : closeButton).focus();
        }
    }

    function closeModal() {
        if (processing) return;
        modal.hidden = true;
        document.body.classList.remove("modal-open");
        previousFocus?.focus();
    }

    async function registerMembership(event) {
        event.preventDefault();
        if (processing) return;
        clearModalMessage();
        clearFieldErrors();
        if (!productSelect.value || !startDate.value) {
            if (!productSelect.value) showFieldError("productId", "회원권 상품을 선택해 주세요.");
            if (!startDate.value) showFieldError("startDate", "이용 시작일을 입력해 주세요.");
            form.querySelector('[aria-invalid="true"]')?.focus();
            return;
        }
        setProcessing(true, submitButton);
        try {
            const result = await request(`/api/members/${memberId}/memberships`, {
                method: "POST",
                body: JSON.stringify({productId: Number(productSelect.value), startDate: startDate.value})
            });
            closeModalAfterProcessing();
            showFeedback(result.message || "회원권이 등록되었습니다. 결제 완료 후 이용 상태로 변경됩니다.", false);
            await loadMemberships();
        } catch (error) {
            showModalMessage(error.message);
        } finally {
            setProcessing(false, submitButton);
        }
    }

    function showSelectedProduct() {
        clearFieldError("productId");
        const product = products.find(item => String(item.productId) === productSelect.value);
        if (!product) {
            productSummary.hidden = true;
            return;
        }
        const parts = [`이용 기간 ${product.durationDays}일`, formatPrice(product.price)];
        if (["PT", "COMBINED"].includes(product.productType)) {
            parts.push(`PT ${product.ptSessionCount ?? 0}회`);
        }
        productSummary.textContent = parts.join(" · ");
        productSummary.hidden = false;
    }

    async function request(url, options = {}) {
        const headers = {Accept: "application/json", ...(options.headers || {})};
        if (options.body) headers["Content-Type"] = "application/json";
        if (options.method && options.method !== "GET") {
            if (!csrfToken || !csrfHeader) throw new Error("보안 토큰을 확인할 수 없습니다. 페이지를 새로고침해 주세요.");
            headers[csrfHeader] = csrfToken;
        }
        const response = await fetch(url, {...options, headers});
        if (response.status === 401) {
            window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`;
            throw new Error("로그인이 필요합니다.");
        }
        const result = await readJson(response);
        if (!response.ok) throw new Error(result?.error?.detail || result?.message || "요청을 처리하지 못했습니다.");
        return result;
    }

    function setProcessing(value, button) {
        processing = value;
        panel.setAttribute("aria-busy", String(value));
        button.disabled = value;
    }

    function closeModalAfterProcessing() {
        modal.hidden = true;
        document.body.classList.remove("modal-open");
        previousFocus?.focus();
    }

    function showFeedback(message, isError) {
        feedback.textContent = message;
        feedback.className = `membership-feedback ${isError ? "is-error" : "is-success"}`;
        feedback.hidden = false;
        feedback.focus();
    }

    function clearFeedback() {
        feedback.hidden = true;
        feedback.textContent = "";
    }

    function showModalMessage(message) {
        modalMessage.textContent = message;
        modalMessage.hidden = false;
    }

    function clearModalMessage() {
        modalMessage.hidden = true;
        modalMessage.textContent = "";
        clearFieldErrors();
    }

    function showFieldError(name, message) {
        const field = form.elements.namedItem(name);
        field?.setAttribute("aria-invalid", "true");
        const error = form.querySelector(`[data-error-for="${name}"]`);
        if (error) error.textContent = message;
    }

    function clearFieldError(name) {
        form.elements.namedItem(name)?.removeAttribute("aria-invalid");
        const error = form.querySelector(`[data-error-for="${name}"]`);
        if (error) error.textContent = "";
    }

    function clearFieldErrors() {
        clearFieldError("productId");
        clearFieldError("startDate");
    }

    function appendDetail(listElement, label, value) {
        const wrapper = element("div");
        wrapper.append(textElement("dt", label), textElement("dd", value));
        listElement.append(wrapper);
    }

    function actionButton(label, action, membershipId) {
        const button = textElement("button", label);
        button.type = "button";
        button.className = "secondary-button";
        button.dataset.action = action;
        button.dataset.membershipId = membershipId;
        return button;
    }

    function createState(className, message) {
        const state = element("div", className);
        state.append(textElement("strong", message));
        return state;
    }

    function textElement(tag, value) {
        const node = element(tag);
        node.textContent = value ?? "-";
        return node;
    }

    function element(tag, className) {
        const node = document.createElement(tag);
        if (className) node.className = className;
        return node;
    }

    function productTypeLabel(type) {
        const labels = {GYM: "자유 이용권", PT: "PT 이용권", COMBINED: "복합 이용권"};
        return labels[type] || type || "-";
    }

    function formatPrice(value) {
        return `${Number(value || 0).toLocaleString("ko-KR")}원`;
    }

    function formatDate(value) {
        return value ? value.replaceAll("-", ".") : "-";
    }

    function formatDateTime(value) {
        if (!value) return "-";
        return value.slice(0, 16).replace("T", " ").replaceAll("-", ".");
    }

    function localDate(date) {
        const offset = date.getTimezoneOffset();
        return new Date(date.getTime() - offset * 60000).toISOString().slice(0, 10);
    }

    async function readJson(response) {
        try { return await response.json(); } catch (error) { return null; }
    }
});
