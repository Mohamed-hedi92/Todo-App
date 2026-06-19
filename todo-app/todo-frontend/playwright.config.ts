import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  use: {
    baseURL: 'http://localhost:3000',
    // Headless in CI, sichtbar bei lokaler Entwicklung
    // Setze HEADED=true für sichtbare Browser (z. B. lokal beim Debuggen)
    headless: process.env.HEADED !== 'true',
  },
  // In CI: keine Videos, keine Screenshots bei Erfolg (spart Zeit und Speicher)
  retries: process.env.CI ? 2 : 0,
  reporter: process.env.CI ? 'line' : 'list',
});