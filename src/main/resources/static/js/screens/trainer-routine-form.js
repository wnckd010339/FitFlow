document.addEventListener("DOMContentLoaded", () => {
    const list = document.querySelector("#exercise-list");
    const template = document.querySelector("#exercise-template");
    const addButton = document.querySelector("#add-exercise");
    if (!list || !template || !addButton) return;

    const reindex = () => {
        list.querySelectorAll(".exercise-card").forEach((card, index) => {
            card.querySelector(".exercise-title strong").textContent = `운동 ${index + 1}`;
            card.querySelectorAll("[name]").forEach(input => {
                input.name = input.name.replace(/exercises\[\d+\]/, `exercises[${index}]`);
            });
        });
        list.querySelectorAll(".remove-exercise").forEach(button => {
            button.disabled = list.querySelectorAll(".exercise-card").length === 1;
        });
    };

    addButton.addEventListener("click", () => {
        const index = list.querySelectorAll(".exercise-card").length;
        const html = template.innerHTML.replaceAll("__INDEX__", String(index));
        list.insertAdjacentHTML("beforeend", html);
        reindex();
        list.lastElementChild?.querySelector("input")?.focus();
    });

    list.addEventListener("click", event => {
        const button = event.target.closest(".remove-exercise");
        if (!button || list.querySelectorAll(".exercise-card").length === 1) return;
        button.closest(".exercise-card").remove();
        reindex();
    });
    reindex();
});
