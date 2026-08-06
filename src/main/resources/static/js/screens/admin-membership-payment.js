document.addEventListener("DOMContentLoaded", () => {
    const page = document.querySelector(".payment-content");
    if (!page) return;

    const memberFilter = document.querySelector("#payment-member-filter");
    const keywordFilter = document.querySelector("#payment-keyword");
    const methodFilter = document.querySelector("#payment-method-filter");
    const statusFilter = document.querySelector("#payment-status-filter");
    const dateFromFilter = document.querySelector("#payment-date-from");
    const dateToFilter = document.querySelector("#payment-date-to");
    const resetButton = document.querySelector("#reset-payment-filters");
    const historyBody = document.querySelector("#payment-history-body");
    const historyCount = document.querySelector("#payment-history-count");
    const feedback = document.querySelector("#payment-page-feedback");
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    let historyItems = [];
    let processing = false;

    memberFilter.addEventListener("change", () => {
        updateUrlContext();
        loadHistory();
    });
    [keywordFilter, methodFilter, statusFilter, dateFromFilter, dateToFilter]
        .forEach(control => control.addEventListener("input", applyFilters));
    resetButton.addEventListener("click", resetFilters);
    historyBody.addEventListener("click", handleRefund);
    loadHistory();

    async function loadHistory() {
        historyBody.innerHTML = '<tr><td class="history-loading" colspan="8">결제 내역을 불러오는 중입니다.</td></tr>';
        const query = memberFilter.value ? `?memberId=${encodeURIComponent(memberFilter.value)}` : "";
        try {
            const result = await request(`/api/payments${query}`);
            historyItems = result.data || [];
            applyFilters();
        } catch (error) {
            historyItems = [];
            historyBody.innerHTML = `<tr><td class="history-loading" colspan="8">${escapeHtml(error.message)}</td></tr>`;
            updateSummary([]);
        }
    }

    function applyFilters() {
        const keyword = keywordFilter.value.trim().toLowerCase();
        const method = methodFilter.value;
        const status = statusFilter.value;
        const dateFrom = dateFromFilter.value;
        const dateTo = dateToFilter.value;

        const filtered = historyItems.filter(item => {
            const occurredDate = item.occurredAt?.slice(0, 10) || "";
            const matchesKeyword = !keyword
                || String(item.memberName || "").toLowerCase().includes(keyword)
                || String(item.productName || "").toLowerCase().includes(keyword);
            const matchesMethod = !method || item.paymentMethod === method;
            const matchesStatus = !status || item.status === status;
            const matchesFrom = !dateFrom || occurredDate >= dateFrom;
            const matchesTo = !dateTo || occurredDate <= dateTo;
            return matchesKeyword && matchesMethod && matchesStatus && matchesFrom && matchesTo;
        });

        renderHistory(filtered);
        updateSummary(filtered);
    }

    function renderHistory(items) {
        historyCount.textContent = `결제·환불 ${items.length}건`;
        if (!items.length) {
            historyBody.innerHTML = '<tr><td class="history-loading" colspan="8">조건에 맞는 결제·환불 내역이 없습니다.</td></tr>';
            return;
        }

        historyBody.innerHTML = items.map(item => {
            const refund = item.transactionType === "REFUND";
            const refundable = !refund && ["COMPLETED", "PARTIALLY_REFUNDED"].includes(item.status);
            return `<tr>
                <td>${formatDateTime(item.occurredAt)}</td>
                <td><strong class="member-name">${escapeHtml(item.memberName || "-")}</strong></td>
                <td>${refund ? "환불" : "결제"}</td>
                <td>${escapeHtml(item.productName || "-")}</td>
                <td>${paymentMethodLabel(item.paymentMethod)}</td>
                <td class="${refund ? "amount-refund" : "amount-positive"}">${refund ? "-" : ""}${formatPrice(item.amount)}</td>
                <td><span class="table-status ${statusClass(item.status)}">${statusLabel(item.status)}</span></td>
                <td>${refundable ? `<button class="table-action" type="button" data-refund-payment-id="${item.paymentId}">환불 관리</button>` : "-"}</td>
            </tr>`;
        }).join("");
    }

    function updateSummary(items) {
        const payments = items.filter(item => item.transactionType === "PAYMENT");
        const refunds = items.filter(item => item.transactionType === "REFUND" && item.status === "COMPLETED");
        const paidAmount = payments
            .filter(item => ["COMPLETED", "PARTIALLY_REFUNDED", "REFUNDED"].includes(item.status))
            .reduce((sum, item) => sum + Number(item.amount || 0), 0);
        const refundedAmount = refunds.reduce((sum, item) => sum + Number(item.amount || 0), 0);
        const partialCount = payments.filter(item => item.status === "PARTIALLY_REFUNDED").length;

        document.querySelector("#summary-count").textContent = `${items.length}건`;
        document.querySelector("#summary-paid").textContent = formatPrice(paidAmount);
        document.querySelector("#summary-refunded").textContent = formatPrice(refundedAmount);
        document.querySelector("#summary-partial").textContent = `${partialCount}건`;
    }

    async function handleRefund(event) {
        const button = event.target.closest("[data-refund-payment-id]");
        if (!button || processing) return;
        const amount = window.prompt("환불 금액을 숫자로 입력해 주세요.");
        if (amount === null) return;
        const normalizedAmount = amount.replaceAll(",", "").trim();
        if (!normalizedAmount || Number(normalizedAmount) <= 0) {
            showFeedback("올바른 환불 금액을 입력해 주세요.", true);
            return;
        }
        const reason = window.prompt("환불 사유를 입력해 주세요. (선택)") ?? "";

        setProcessing(true);
        try {
            const result = await request(`/api/payments/${button.dataset.refundPaymentId}/refunds`, {
                method: "POST",
                body: JSON.stringify({amount: normalizedAmount, reason})
            });
            showFeedback(result.message, false);
            await loadHistory();
        } catch (error) {
            showFeedback(error.message, true);
        } finally {
            setProcessing(false);
        }
    }

    function resetFilters() {
        keywordFilter.value = "";
        methodFilter.value = "";
        statusFilter.value = "";
        dateFromFilter.value = "";
        dateToFilter.value = "";
        applyFilters();
    }

    async function request(url, options = {}) {
        const headers = {Accept: "application/json", ...(options.headers || {})};
        if (options.body) headers["Content-Type"] = "application/json";
        if (options.method && options.method !== "GET") {
            if (!csrfToken || !csrfHeader) throw new Error("보안 토큰을 확인할 수 없습니다. 페이지를 새로고침해 주세요.");
            headers[csrfHeader] = csrfToken;
        }
        const response = await fetch(url, {...options, headers});
        const result = await readJson(response);
        if (!response.ok) throw new Error(result?.error?.detail || result?.message || "요청을 처리하지 못했습니다.");
        return result;
    }

    function updateUrlContext() {
        const params = new URLSearchParams({view: "history"});
        if (memberFilter.value) params.set("memberId", memberFilter.value);
        window.history.replaceState(null, "", `/admin/memberships?${params}`);
    }

    function setProcessing(value) {
        processing = value;
        page.setAttribute("aria-busy", String(value));
    }

    function showFeedback(message, isError) {
        feedback.textContent = message;
        feedback.className = `payment-page-feedback ${isError ? "is-error" : "is-success"}`;
        feedback.hidden = false;
        feedback.focus();
    }

    function formatDateTime(value) {
        return value ? value.slice(0, 16).replace("T", " ").replaceAll("-", ".") : "-";
    }

    function formatPrice(value) {
        return `${Number(value || 0).toLocaleString("ko-KR")}원`;
    }

    function paymentMethodLabel(value) {
        return {CARD: "카드", CASH: "현금", TRANSFER: "계좌이체"}[value] || value || "-";
    }

    function statusLabel(value) {
        return {
            COMPLETED: "완료", PARTIALLY_REFUNDED: "부분 환불",
            REFUNDED: "전액 환불", CANCELLED: "취소", PENDING: "대기",
            REJECTED: "거절"
        }[value] || value;
    }

    function statusClass(value) {
        if (value === "COMPLETED") return "completed";
        if (value === "PARTIALLY_REFUNDED") return "partial";
        if (value === "REFUNDED") return "refunded";
        return "neutral";
    }

    function escapeHtml(value) {
        const element = document.createElement("div");
        element.textContent = value ?? "";
        return element.innerHTML;
    }

    async function readJson(response) {
        try { return await response.json(); } catch (error) { return null; }
    }
});
