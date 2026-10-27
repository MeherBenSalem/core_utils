// Renders the Reel cover (1080x1920) from edit/cover.html.
const { chromium } = require('playwright');
const E = '/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel/edit';
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1080, height: 1920 } });
  await p.goto('file://' + E + '/cover.html');
  await p.evaluate(() => document.fonts.ready);
  await p.screenshot({ path: E + '/cover.png' });
  await b.close();
})();
