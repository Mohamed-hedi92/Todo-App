/*import { test, expect } from '@playwright/test';

test('Page has title and adds a todo', async ({ page }) => {
  await page.goto('http://localhost:3000');

  await expect(page).toHaveTitle(/Todo/);


  await page.fill('input[name="new-todo"]', 'Playwright Test Todo');
  await page.click('button#add-todo');
  await page.waitForTimeout(2000);

 const todo = page.locator('text=Playwright Test Todo').first();
 await expect(todo).toBeVisible();
});


test('Todo can be added and then deleted', async ({ page }) => {
  await page.goto('http://localhost:3000');

  const todoText = 'Playwright Delete Test ' + Date.now();

  await page.fill('input[name="new-todo"]', todoText);
  await page.click('button#add-todo');

  const todoItem = page.locator('li', { hasText: todoText }).first();
  await expect(todoItem).toBeVisible();

  await todoItem.getByRole('button', { name: 'Löschen' }).click();
  await page.waitForTimeout(2000);
  await expect(todoItem).toHaveCount(0);
});

*/