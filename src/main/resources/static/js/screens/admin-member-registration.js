document.addEventListener("DOMContentLoaded", () => {
    const openButton = document.querySelector("#open-member-registration");
    const modal = document.querySelector("#member-registration-modal");
    const dialog = modal?.querySelector(".member-registration-modal");
    const closeButton = document.querySelector("#close-member-registration");
    const cancelButton = document.querySelector("#cancel-member-registration");
    const form = document.querySelector("#member-registration-form");
    const submitButton = document.querySelector("#submit-member-registration");
    const formError = document.querySelector("#member-registration-error");
    const nameInput = document.querySelector("#registration-name");
    const birthDateInput = document.querySelector("#registration-birth-date");

    if (!openButton || !modal || !dialog || !form || !submitButton || !formError) {
        return;
    }

    const csrfToken = document
        .querySelector('meta[name="_csrf"]')
        ?.getAttribute("content");

    const csrfHeader = document
        .querySelector('meta[name="_csrf_header"]')
        ?.getAttribute("content");

    const phonePattern = /^01[016789]-?\d{3,4}-?\d{4}$/;
    const loginIdPattern = /^[a-zA-Z0-9._-]+$/;
    const submitButtonText = submitButton.textContent.trim();

    let previouslyFocusedElement = null;
    let submitting = false;

    setMaximumBirthDate();
    openButton.setAttribute("aria-expanded", "false");

    openButton.addEventListener("click", openModal);
    closeButton?.addEventListener("click", closeModal);
    cancelButton?.addEventListener("click", closeModal);
    form.addEventListener("submit", handleSubmit);

    modal.addEventListener("click", event => {
        if (event.target === modal && !submitting) {
            closeModal();
        }
    });

    document.addEventListener("keydown", event => {
        if (modal.hidden) {
            return;
        }

        if (event.key === "Escape" && !submitting) {
            event.preventDefault();
            closeModal();
            return;
        }

        if (event.key === "Tab") {
            keepFocusInsideModal(event);
        }
    });

    form.querySelectorAll("input, select").forEach(field => {
        const eventName = field.tagName === "SELECT" || field.type === "checkbox"
            ? "change"
            : "input";

        field.addEventListener(eventName, () => {
            clearFieldError(field.name);
        });
    });

    function openModal() {
        previouslyFocusedElement = document.activeElement;

        form.reset();
        clearErrors();
        setSubmitting(false);

        modal.hidden = false;
        document.body.classList.add("modal-open");
        openButton.setAttribute("aria-expanded", "true");

        window.requestAnimationFrame(() => {
            nameInput?.focus();
        });
    }

    function closeModal() {
        if (submitting) {
            return;
        }

        modal.hidden = true;
        document.body.classList.remove("modal-open");
        openButton.setAttribute("aria-expanded", "false");

        if (previouslyFocusedElement instanceof HTMLElement) {
            previouslyFocusedElement.focus();
        } else {
            openButton.focus();
        }
    }

    async function handleSubmit(event) {
        event.preventDefault();

        if (submitting) {
            return;
        }

        clearErrors();

        const payload = readPayload();

        if (!validate(payload)) {
            focusFirstInvalidField();
            return;
        }

        if (!csrfToken || !csrfHeader) {
            showFormError("보안 토큰을 확인할 수 없습니다. 페이지를 새로고침해 주세요.");
            return;
        }

        setSubmitting(true);

        try {
            const response = await fetch("/api/members", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify(payload)
            });

            const result = await readJson(response);

            if (response.status === 401) {
                window.location.href = "/login?redirect=%2Fadmin%2Fmembers";
                return;
            }

            if (response.status === 409) {
                showFieldError(
                    "loginId",
                    getErrorMessage(result, "이미 사용 중인 로그인 ID입니다.")
                );
                focusFirstInvalidField();
                return;
            }

            if (response.status === 400) {
                showFormError(
                    getErrorMessage(result, "입력값을 다시 확인해 주세요.")
                );
                return;
            }

            if (response.status === 403) {
                showFormError(
                    "요청 권한이 없거나 보안 토큰이 만료되었습니다. 페이지를 새로고침해 주세요."
                );
                return;
            }

            if (!response.ok) {
                showFormError(
                    getErrorMessage(result, "회원 등록에 실패했습니다. 잠시 후 다시 시도해 주세요.")
                );
                return;
            }

            modal.hidden = true;
            document.body.classList.remove("modal-open");
            window.location.reload();
        } catch (error) {
            showFormError("회원 등록 요청 중 오류가 발생했습니다. 네트워크 상태를 확인해 주세요.");
        } finally {
            setSubmitting(false);
        }
    }

    function readPayload() {
        const formData = new FormData(form);

        return {
            name: stringValue(formData.get("name")).trim(),
            phone: stringValue(formData.get("phone")).trim(),
            birthDate: nullableValue(formData.get("birthDate")),
            gender: nullableValue(formData.get("gender")),
            loginId: stringValue(formData.get("loginId")).trim(),
            initialPassword: stringValue(formData.get("initialPassword")),
            trainerRequested: formData.get("trainerRequested") === "on"
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

        if (!payload.phone) {
            showFieldError("phone", "연락처를 입력해 주세요.");
            valid = false;
        } else if (!phonePattern.test(payload.phone)) {
            showFieldError("phone", "올바른 휴대전화 번호를 입력해 주세요.");
            valid = false;
        }

        if (payload.birthDate && !isPastDate(payload.birthDate)) {
            showFieldError("birthDate", "생년월일은 오늘보다 이전이어야 합니다.");
            valid = false;
        }

        if (payload.gender && !["MALE", "FEMALE"].includes(payload.gender)) {
            showFieldError("gender", "올바른 성별을 선택해 주세요.");
            valid = false;
        }

        if (!payload.loginId) {
            showFieldError("loginId", "로그인 ID를 입력해 주세요.");
            valid = false;
        } else if (payload.loginId.length < 4 || payload.loginId.length > 100) {
            showFieldError("loginId", "로그인 ID는 4자 이상 100자 이하로 입력해 주세요.");
            valid = false;
        } else if (!loginIdPattern.test(payload.loginId)) {
            showFieldError(
                "loginId",
                "로그인 ID는 영문, 숫자, 마침표, 밑줄, 하이픈만 사용할 수 있습니다."
            );
            valid = false;
        }

        if (!payload.initialPassword) {
            showFieldError("initialPassword", "초기 비밀번호를 입력해 주세요.");
            valid = false;
        } else if (payload.initialPassword.length < 8) {
            showFieldError("initialPassword", "초기 비밀번호는 8자 이상 입력해 주세요.");
            valid = false;
        } else if (payload.initialPassword.length > 100) {
            showFieldError("initialPassword", "초기 비밀번호는 100자 이하로 입력해 주세요.");
            valid = false;
        }

        return valid;
    }

    function showFieldError(fieldName, message) {
        const field = form.elements.namedItem(fieldName);
        const errorElement = form.querySelector(`[data-error-for="${fieldName}"]`);

        if (field instanceof HTMLElement) {
            field.setAttribute("aria-invalid", "true");
            field.closest(".member-registration-field")?.classList.add("has-error");
        }

        if (errorElement) {
            errorElement.textContent = message;
        }
    }

    function clearFieldError(fieldName) {
        const field = form.elements.namedItem(fieldName);
        const errorElement = form.querySelector(`[data-error-for="${fieldName}"]`);

        if (field instanceof HTMLElement) {
            field.removeAttribute("aria-invalid");
            field.closest(".member-registration-field")?.classList.remove("has-error");
        }

        if (errorElement) {
            errorElement.textContent = "";
        }
    }

    function clearErrors() {
        form.querySelectorAll("[data-error-for]").forEach(element => {
            element.textContent = "";
        });

        form.querySelectorAll("[aria-invalid]").forEach(element => {
            element.removeAttribute("aria-invalid");
        });

        form.querySelectorAll(".has-error").forEach(element => {
            element.classList.remove("has-error");
        });

        formError.textContent = "";
        formError.hidden = true;
    }

    function showFormError(message) {
        formError.textContent = message;
        formError.hidden = false;
        formError.focus?.();
    }

    function focusFirstInvalidField() {
        form.querySelector('[aria-invalid="true"]')?.focus();
    }

    function setSubmitting(value) {
        submitting = value;
        submitButton.disabled = value;
        submitButton.textContent = value ? "등록 중..." : submitButtonText;
        form.setAttribute("aria-busy", String(value));
    }

    function setMaximumBirthDate() {
        if (!birthDateInput) {
            return;
        }

        const yesterday = new Date();
        yesterday.setDate(yesterday.getDate() - 1);
        birthDateInput.max = formatLocalDate(yesterday);
    }

    function keepFocusInsideModal(event) {
        const focusableElements = Array.from(
            dialog.querySelectorAll(
                'button:not([disabled]), input:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])'
            )
        ).filter(element => !element.hidden);

        if (focusableElements.length === 0) {
            event.preventDefault();
            dialog.focus();
            return;
        }

        const firstElement = focusableElements[0];
        const lastElement = focusableElements[focusableElements.length - 1];

        if (event.shiftKey && document.activeElement === firstElement) {
            event.preventDefault();
            lastElement.focus();
        } else if (!event.shiftKey && document.activeElement === lastElement) {
            event.preventDefault();
            firstElement.focus();
        }
    }

    function isPastDate(value) {
        const selectedDate = new Date(`${value}T00:00:00`);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        return !Number.isNaN(selectedDate.getTime()) && selectedDate < today;
    }

    function formatLocalDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");

        return `${year}-${month}-${day}`;
    }

    function stringValue(value) {
        return typeof value === "string" ? value : "";
    }

    function nullableValue(value) {
        const normalized = stringValue(value).trim();
        return normalized || null;
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
