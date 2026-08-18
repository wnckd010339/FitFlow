document.querySelectorAll('.sidebar-toggle').forEach((toggle) => {
    toggle.addEventListener('click', () => {
        const header = toggle.closest('.portal-header');
        const isOpen = header.classList.toggle('menu-open');
        toggle.setAttribute('aria-expanded', String(isOpen));
        toggle.setAttribute('aria-label', isOpen ? '메뉴 닫기' : '메뉴 열기');
    });
});
document.querySelectorAll('form[data-confirm]').forEach((form) => {
    form.addEventListener('submit', (event) => {
        if (!window.confirm(form.dataset.confirm)) event.preventDefault();
    });
});
