document.addEventListener("DOMContentLoaded", () => {
    const list = document.querySelector("#workout-exercise-list");
    const template = document.querySelector("#workout-exercise-template");
    const addButton = document.querySelector("#add-workout-exercise");
    const memberSelect = document.querySelector("#memberId");
    const routineSelect = document.querySelector("#routineId");
    if (!list || !template || !addButton) return;

    const reindex = () => {
        const cards = list.querySelectorAll(".workout-exercise-card");
        cards.forEach((card, index) => {
            card.querySelector(".workout-exercise-title strong").textContent = String(index + 1);
            card.querySelectorAll("[name]").forEach(input => {
                input.name = input.name.replace(/exercises\[\d+]/, `exercises[${index}]`);
            });
        });
        list.querySelectorAll(".remove-workout-exercise").forEach(button => button.disabled = cards.length === 1);
    };

    addButton.addEventListener("click", () => {
        const index = list.querySelectorAll(".workout-exercise-card").length;
        list.insertAdjacentHTML("beforeend", template.innerHTML.replaceAll("__INDEX__", String(index)));
        reindex();
        list.lastElementChild?.querySelector("input")?.focus();
    });
    list.addEventListener("click", event => {
        const button = event.target.closest(".remove-workout-exercise");
        if (!button || list.querySelectorAll(".workout-exercise-card").length === 1) return;
        button.closest(".workout-exercise-card").remove();
        reindex();
    });

    const filterRoutines = () => {
        if (!memberSelect || !routineSelect) return;
        const memberId = memberSelect.value;
        const selectedOption = routineSelect.selectedOptions[0];
        routineSelect.querySelectorAll("option[data-member-id]").forEach(option => {
            const available = memberId !== "" && option.dataset.memberId === memberId;
            option.hidden = !available;
            option.disabled = !available;
        });
        if (selectedOption?.dataset.memberId && selectedOption.dataset.memberId !== memberId) {
            routineSelect.value = "";
        }
    };

    memberSelect?.addEventListener("change", filterRoutines);
    filterRoutines();
    reindex();
});
