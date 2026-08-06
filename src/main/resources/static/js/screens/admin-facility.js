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
})();
