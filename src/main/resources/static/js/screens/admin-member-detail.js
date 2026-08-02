document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("#member-basic-information-form");
    const editButton = document.querySelector("#edit-member-basic-information");
    const cancelButton = document.querySelector("#cancel-member-basic-information");
    const saveButton = document.querySelector("#save-member-basic-information");
    const message = document.querySelector("#member-basic-information-message");

    if (!form || !editButton || !cancelButton || !saveButton || !message) {
        return;
    }

    const fields = {
        name: form.elements.namedItem("name"),
        phone: form.elements.namedItem("phone"),
        birthDate: form.elements.namedItem("birthDate"),
        gender: form.elements.namedItem("gender"),
        status: form.elements.namedItem("status")
    };
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    const phonePattern = /^010-\d{4}-\d{4}$/;
    const originalValues = {};
    let editing = false;
    let submitting = false;

    setMaximumBirthDate();
    form.addEventListener("submit", submitBasicInformation);
    editButton.addEventListener("click", startEditing);
    cancelButton.addEventListener("click", cancelEditing);

    Object.values(fields).forEach(field => {
        const eventName = field instanceof HTMLSelectElement ? "change" : "input";
        field?.addEventListener(eventName, () => clearFieldError(field.name));
    });

    fields.phone?.addEventListener("input", event => {
        event.target.value = formatPhone(event.target.value);
    });

    function startEditing() {
        if (editing || submitting) {
            return;
        }

        Object.entries(fields).forEach(([name, field]) => {
            originalValues[name] = field.value;
            if (field instanceof HTMLSelectElement) {
                field.disabled = false;
            } else {
                field.readOnly = false;
            }
        });

        editing = true;
        editButton.hidden = true;
        cancelButton.hidden = false;
        saveButton.hidden = false;
        clearMessages();
        fields.name?.focus();
    }

    function cancelEditing() {
        if (!editing || submitting) {
            return;
        }

        Object.entries(fields).forEach(([name, field]) => {
            field.value = originalValues[name] ?? "";
        });
        finishEditing();
        clearMessages();
    }

    async function submitBasicInformation(event) {
        event.preventDefault();
        if (!editing || submitting) {
            return;
        }

        clearMessages();
        const payload = readPayload();
        if (!validate(payload)) {
            form.querySelector('[aria-invalid="true"]')?.focus();
            return;
        }

        if (payload.status === "WITHDRAWN" && originalValues.status !== "WITHDRAWN") {
            const confirmed = window.confirm(
                "탈퇴 상태로 변경하면 회원이 로그인할 수 없습니다. 계속하시겠습니까?"
            );
            if (!confirmed) {
                return;
            }
        }

        if (!csrfToken || !csrfHeader) {
            showMessage("보안 토큰을 확인할 수 없습니다. 페이지를 새로고침해 주세요.", true);
            return;
        }

        setSubmitting(true);
        try {
            const memberId = form.dataset.memberId;
            const response = await fetch(`/api/members/${memberId}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(payload)
            });
            const result = await readJson(response);

            if (response.status === 401) {
                window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname)}`;
                return;
            }
            if (response.status === 400) {
                showMessage(getErrorMessage(result, "입력값을 다시 확인해 주세요."), true);
                return;
            }
            if (response.status === 403) {
                showMessage("회원 정보를 수정할 권한이 없거나 보안 토큰이 만료되었습니다.", true);
                return;
            }
            if (response.status === 404) {
                showMessage("수정할 회원을 찾을 수 없습니다.", true);
                return;
            }
            if (!response.ok) {
                showMessage(getErrorMessage(result, "회원 정보 수정에 실패했습니다."), true);
                return;
            }

            Object.entries(fields).forEach(([name, field]) => {
                originalValues[name] = field.value;
            });
            finishEditing();
            showMessage("회원 기본 정보가 수정되었습니다.", false);
        } catch (error) {
            showMessage("수정 요청 중 오류가 발생했습니다. 네트워크 상태를 확인해 주세요.", true);
        } finally {
            setSubmitting(false);
        }
    }

    function readPayload() {
        return {
            name: fields.name.value.trim(),
            phone: fields.phone.value.trim(),
            birthDate: fields.birthDate.value || null,
            gender: fields.gender.value || null,
            status: fields.status.value
        };
    }

    function validate(payload) {
        let valid = true;
        if (!payload.name) {
            showFieldError("name", "회원 이름을 입력해 주세요.");
            valid = false;
        } else if (payload.name.length > 100) {
            showFieldError("name", "회원 이름은 100자 이하로 입력해 주세요.");
            valid = false;
        }
        if (!phonePattern.test(payload.phone)) {
            showFieldError("phone", "연락처를 010-0000-0000 형식으로 입력해 주세요.");
            valid = false;
        }
        if (payload.birthDate && !isPastDate(payload.birthDate)) {
            showFieldError("birthDate", "생년월일은 오늘보다 이전이어야 합니다.");
            valid = false;
        }
        if (payload.gender && !["MALE", "FEMALE"].includes(payload.gender)) {
            showMessage("올바른 성별을 선택해 주세요.", true);
            valid = false;
        }
        if (!["ACTIVE", "SUSPENDED", "WITHDRAWN"].includes(payload.status)) {
            showMessage("올바른 회원 상태를 선택해 주세요.", true);
            valid = false;
        }
        return valid;
    }

    function finishEditing() {
        Object.values(fields).forEach(field => {
            if (field instanceof HTMLSelectElement) {
                field.disabled = true;
            } else {
                field.readOnly = true;
            }
        });
        editing = false;
        editButton.hidden = false;
        cancelButton.hidden = true;
        saveButton.hidden = true;
        clearFieldErrors();
    }

    function setSubmitting(value) {
        submitting = value;
        saveButton.disabled = value;
        cancelButton.disabled = value;
        saveButton.textContent = value ? "저장 중..." : "저장";
        form.setAttribute("aria-busy", String(value));
    }

    function showFieldError(fieldName, text) {
        const field = fields[fieldName];
        const error = form.querySelector(`[data-error-for="${fieldName}"]`);
        field?.setAttribute("aria-invalid", "true");
        field?.closest(".field")?.classList.add("has-error");
        if (error) {
            error.textContent = text;
        }
    }

    function clearFieldError(fieldName) {
        const field = fields[fieldName];
        const error = form.querySelector(`[data-error-for="${fieldName}"]`);
        field?.removeAttribute("aria-invalid");
        field?.closest(".field")?.classList.remove("has-error");
        if (error) {
            error.textContent = "";
        }
    }

    function clearFieldErrors() {
        Object.keys(fields).forEach(clearFieldError);
    }

    function clearMessages() {
        clearFieldErrors();
        message.hidden = true;
        message.textContent = "";
        message.classList.remove("is-error", "is-success");
    }

    function showMessage(text, isError) {
        message.textContent = text;
        message.classList.toggle("is-error", isError);
        message.classList.toggle("is-success", !isError);
        message.hidden = false;
        message.focus();
    }

    function formatPhone(value) {
        const digits = value.replace(/\D/g, "").slice(0, 11);
        if (digits.length <= 3) {
            return digits;
        }
        if (digits.length <= 7) {
            return `${digits.slice(0, 3)}-${digits.slice(3)}`;
        }
        return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
    }

    function setMaximumBirthDate() {
        const yesterday = new Date();
        yesterday.setDate(yesterday.getDate() - 1);
        fields.birthDate.max = formatLocalDate(yesterday);
    }

    function isPastDate(value) {
        const selected = new Date(`${value}T00:00:00`);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return !Number.isNaN(selected.getTime()) && selected < today;
    }

    function formatLocalDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        return `${year}-${month}-${day}`;
    }

    function getErrorMessage(result, fallback) {
        return result?.error?.detail || result?.message || fallback;
    }

    async function readJson(response) {
        try {
            return await response.json();
        } catch (error) {
            return null;
        }
    }
});
