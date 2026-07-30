const fs = require("node:fs");
const path = require("node:path");
const { chromium } = require("playwright");

(async () => {
  const root = path.resolve("design/generated");
  const files = fs.readdirSync(root).filter((name) => name.endsWith(".svg"));
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1440, height: 1024 }, deviceScaleFactor: 1 });

  for (const file of files) {
    const source = path.join(root, file);
    const target = path.join(root, file.replace(/\.svg$/, ".png"));
    await page.goto(`file:///${source.replaceAll("\\", "/")}`);
    await page.screenshot({ path: target, fullPage: false });
  }

  await browser.close();
  console.log(`Rendered ${files.length} PNG previews`);
})();
