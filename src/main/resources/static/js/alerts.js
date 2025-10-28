
    document.addEventListener('DOMContentLoaded', function () {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
    setTimeout(() => {
    const bsAlert = new bootstrap.Alert(alert);
    bsAlert.close();
}, 3000);
});
});

    document.addEventListener("DOMContentLoaded", () => {
        const msg = sessionStorage.getItem("successMessage");
        if (msg) {
            const container = document.querySelector(".alert-container") || document.body;
            const alertDiv = document.createElement("div");
            alertDiv.className = "alert alert-success alert-dismissible fade show mt-3 shadow-sm";
            alertDiv.role = "alert";
            alertDiv.innerHTML = `
      <i class="bi bi-check-circle me-2"></i>${msg}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
            container.prepend(alertDiv);
            sessionStorage.removeItem("successMessage");

            setTimeout(() => {
                alertDiv.classList.remove("show");
                alertDiv.classList.add("hide");
                setTimeout(() => alertDiv.remove(), 500);
            }, 3000);
        }
    });
