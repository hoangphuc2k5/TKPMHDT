import { renderHeader, bindHeaderEvents } from "./components/header.js";
import { renderFooter } from "./components/footer.js";

export function mountLayout({ activeNav = "home" } = {}) {
  const headerHost = document.getElementById("app-header");
  const footerHost = document.getElementById("app-footer");

  if (headerHost) headerHost.innerHTML = renderHeader(activeNav);
  if (footerHost) footerHost.innerHTML = renderFooter();

  bindHeaderEvents(document);
}

