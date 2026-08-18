document.querySelectorAll('.portal-header').forEach(header => {
    const toggle = header.querySelector('.member-menu-toggle');
    if (!toggle) return;

    toggle.addEventListener('click', () => {
        const open = header.classList.toggle('menu-open');
        toggle.setAttribute('aria-expanded', String(open));
        toggle.setAttribute('aria-label', open ? '메뉴 닫기' : '메뉴 열기');
    });

    header.querySelectorAll('nav a').forEach(link => link.addEventListener('click', () => {
        header.classList.remove('menu-open');
        toggle.setAttribute('aria-expanded', 'false');
        toggle.setAttribute('aria-label', '메뉴 열기');
    }));
});
