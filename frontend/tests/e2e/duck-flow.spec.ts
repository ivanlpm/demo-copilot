import { test, expect } from '@playwright/test';

test('user can discover a duck', async ({ page }) => {
  // We expect the local dev server to be running or use a specific baseURL
  await page.goto('/');

  // Check initial state
  await expect(page.locator('h1')).toContainText('Duck Gallery');

  // Verify the card appears (using text that survives the loading state)
  await expect(page.locator('h3')).toContainText('Duck Discovery');

  // Click refresh button
  const button = page.getByRole('button', { name: /Get Another Duck/i });
  await button.click();

  // Verification of loading state and subsequent data
  await expect(button).toBeDisabled();
  await expect(button).toBeEnabled();
});
