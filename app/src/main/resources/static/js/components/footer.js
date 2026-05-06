// Function to render a static footer on every page
function renderFooter() {

  // Get footer container from DOM
  const footer = document.getElementById("footer");

  // If footer element does not exist, stop execution
  if (!footer) return;

  // Inject footer HTML content
  footer.innerHTML = `
    <footer class="footer">

      <!-- Branding Section -->
      <div class="footer-branding">
        <h4>Hospital System</h4>
        <p>© Copyright 2026. All rights reserved.</p>
      </div>

      <!-- Footer Links Container -->
      <div class="footer-links">

        <!-- Company Section -->
        <div class="footer-column">
          <h4>Company</h4>
          <a href="#">About</a>
          <a href="#">Careers</a>
          <a href="#">Press</a>
        </div>

        <!-- Support Section -->
        <div class="footer-column">
          <h4>Support</h4>
          <a href="#">Account</a>
          <a href="#">Help Center</a>
          <a href="#">Contact</a>
        </div>

        <!-- Legal Section -->
        <div class="footer-column">
          <h4>Legal</h4>
          <a href="#">Terms</a>
          <a href="#">Privacy Policy</a>
          <a href="#">Licensing</a>
        </div>

      </div>
    </footer>
  `;

}

// Call function when script loads
renderFooter();