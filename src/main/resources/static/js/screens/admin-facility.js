(() => {
  const modal = document.getElementById('equipment-modal');
  if (!modal) return;
  const open = () => modal.showModal();
  const close = () => modal.close();
  document.getElementById('open-equipment-modal')?.addEventListener('click', open);
  document.getElementById('close-equipment-modal')?.addEventListener('click', close);
  document.getElementById('cancel-equipment-modal')?.addEventListener('click', close);
  modal.addEventListener('click', event => { if (event.target === modal) close(); });
  if (modal.dataset.open === 'true') open();

  const facilityActions = [...document.querySelectorAll('.facility-action')];
  facilityActions.forEach(action => action.addEventListener('toggle', () => {
    if (!action.open) return;
    facilityActions.forEach(other => {
      if (other !== action) other.open = false;
    });
    requestAnimationFrame(() => positionActionForm(action));
  }));

  document.addEventListener('click', event => {
    if (event.target.closest('.facility-action')) return;
    facilityActions.forEach(action => action.open = false);
  });
  window.addEventListener('resize', repositionOpenAction);
  window.addEventListener('scroll', repositionOpenAction, true);
  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') facilityActions.forEach(action => action.open = false);
  });

  function repositionOpenAction() {
    const opened = facilityActions.find(action => action.open);
    if (opened) positionActionForm(opened);
  }

  function positionActionForm(action) {
    const summary = action.querySelector('summary');
    const form = action.querySelector('.inline-management-form');
    if (!summary || !form) return;
    const anchor = summary.getBoundingClientRect();
    const gap = 8;
    const edge = 16;
    const width = form.offsetWidth;
    const height = form.offsetHeight;
    const left = Math.max(edge, Math.min(anchor.right - width, window.innerWidth - width - edge));
    const below = anchor.bottom + gap;
    const top = below + height <= window.innerHeight - edge
      ? below
      : Math.max(edge, anchor.top - height - gap);
    form.style.left = `${left}px`;
    form.style.top = `${top}px`;
  }
})();
