// index.js

const modalOverlay = document.getElementById("modalOverlay");
const closeModalBtn = document.getElementById("closeModalBtn");

if (closeModalBtn && modalOverlay) {
    closeModalBtn.addEventListener("click", () => {
        modalOverlay.hidden = true;
    });

    modalOverlay.addEventListener("click", (event) => {
        if (event.target === modalOverlay) {
            modalOverlay.hidden = true;
        }
    });
}

