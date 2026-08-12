document.addEventListener("DOMContentLoaded", () => {
    const button = document.querySelector("#admin-alert-button");
    const panel = document.querySelector("#admin-alert-panel");
    if (!button || !panel) return;
    button.addEventListener("click", () => {
        const open = panel.hidden;
        panel.hidden = !open;
        button.setAttribute("aria-expanded", String(open));
        if (open) panel.querySelector("a")?.focus();
    });
});
