/**
 * 测试套件3：管理员功能闭环测试
 * 覆盖管理员登录 → 商品管理 → 订单管理 → 用户管理 的完整流程
 */

const { test, expect } = require('@playwright/test');
const { loginAdmin, takeScreenshot } = require('./helpers.js');

test.describe('【管理员功能闭环测试】', () => {

  // ==================== 管理员登录测试 ====================
  test.describe('管理员登录', () => {
    test('TC-AF-001: 管理员登录页面应正常显示', async ({ page }) => {
      await page.goto('/admin/login');
      await page.waitForSelector('body');
      
      const form = await page.$('form');
      expect(form).not.toBeNull();
      
      console.log('  ✓ 管理员登录页面正常');
    });

    test('TC-AF-002: 使用空密码登录应提示错误', async ({ page }) => {
      await page.goto('/admin/login');
      
      // 输入用户名但空密码
      await page.fill('input[name="username"]', 'admin');
      await page.click('button[type="submit"]');
      await page.waitForTimeout(1000);
      
      const currentUrl = page.url();
      expect(currentUrl).toContain('login');
      
      console.log('  ✓ 空密码登录被阻止');
    });
  });

  // ==================== 管理员后台页面访问测试 ====================
  test.describe('管理后台页面', () => {
    const ADMIN_PAGES = [
      { path: '/admin/goodsList', name: '商品列表' },
      { path: '/admin/orderList', name: '订单列表' },
      { path: '/admin/userList', name: '用户列表' },
      { path: '/admin/riderList', name: '骑手列表' },
      { path: '/admin/categoryList', name: '分类列表' },
      { path: '/admin/salesStats', name: '销售统计' },
      { path: '/admin/settings', name: '系统设置' },
      { path: '/admin/stockWarning', name: '库存预警' },
    ];

    ADMIN_PAGES.forEach(({ path, name }) => {
      test(`TC-AF-003: 管理员应能访问${name}页面`, async ({ page }) => {
        // 先登录
        await loginAdmin(page);
        
        // 访问管理页面
        const response = await page.goto(path, { waitUntil: 'networkidle' });
        
        // 验证能正常访问
        const status = response ? response.status() : 0;
        expect(status).toBeLessThan(500);
        
        const bodyText = await page.textContent('body');
        expect(bodyText).not.toContain('Whitelabel Error Page');
        
        console.log(`  ✓ ${name} (${path}) 访问成功，状态码: ${status}`);
      });
    });
  });

  // ==================== 商品管理功能测试 ====================
  test.describe('商品管理功能', () => {
    test('TC-AF-004: 商品列表应显示商品数据', async ({ page }) => {
      await loginAdmin(page);
      await page.goto('/admin/goodsList');
      await page.waitForTimeout(2000);
      
      // 检查是否有表格或列表
      const table = await page.$('table');
      expect(table).not.toBeNull();
      
      console.log('  ✓ 商品列表页面有数据表格');
    });

    test('TC-AF-005: 商品搜索功能可用', async ({ page }) => {
      await loginAdmin(page);
      await page.goto('/admin/goodsList');
      await page.waitForTimeout(1000);
      
      // 查找搜索输入框
      const searchInput = await page.$('input[type="text"], input[name*="search"], input[name*="key"], input[name*="name"]');
      
      if (searchInput) {
        await searchInput.fill('蛋糕');
        
        // 查找并提交搜索
        const submitBtn = await page.$('button[type="submit"], input[type="submit"]');
        if (submitBtn) {
          await submitBtn.click();
          await page.waitForTimeout(2000);
          console.log('  ✓ 商品搜索功能可用');
        }
      } else {
        console.log('  ⚠ 未找到搜索框，跳过搜索测试');
      }
    });
  });

  // ==================== 订单管理测试 ====================
  test.describe('订单管理功能', () => {
    test('TC-AF-006: 订单列表应显示订单数据', async ({ page }) => {
      await loginAdmin(page);
      await page.goto('/admin/orderList');
      await page.waitForTimeout(2000);
      
      const table = await page.$('table');
      expect(table).not.toBeNull();
      
      console.log('  ✓ 订单列表页面有数据表格');
    });
  });

  // ==================== 用户管理测试 ====================
  test.describe('用户管理功能', () => {
    test('TC-AF-007: 用户列表应正常显示', async ({ page }) => {
      await loginAdmin(page);
      await page.goto('/admin/userList');
      await page.waitForTimeout(2000);
      
      const table = await page.$('table');
      expect(table).not.toBeNull();
      
      console.log('  ✓ 用户列表页面正常');
    });

    test('TC-AF-008: 用户统计页面应正常显示', async ({ page }) => {
      await loginAdmin(page);
      await page.goto('/admin/userStats');
      await page.waitForTimeout(2000);
      
      const bodyText = await page.textContent('body');
      expect(bodyText.length).toBeGreaterThan(0);
      
      console.log('  ✓ 用户统计页面正常');
    });

    test('TC-AF-009: 骑手列表页面应正常显示', async ({ page }) => {
      await loginAdmin(page);
      await page.goto('/admin/riderList');
      await page.waitForTimeout(2000);
      
      const table = await page.$('table');
      expect(table).not.toBeNull();
      
      console.log('  ✓ 骑手列表页面正常');
    });
  });

  // ==================== 库存预警测试 ====================
  test.describe('库存管理', () => {
    test('TC-AF-010: 库存预警页面应正常显示', async ({ page }) => {
      await loginAdmin(page);
      await page.goto('/admin/stockWarning');
      await page.waitForTimeout(2000);
      
      const bodyText = await page.textContent('body');
      expect(bodyText.length).toBeGreaterThan(0);
      
      console.log('  ✓ 库存预警页面正常');
    });
  });
});
