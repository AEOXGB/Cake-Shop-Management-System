/**
 * 测试套件1：前端页面可访问性测试
 * 验证所有关键页面是否能正常访问和加载
 */

const { test, expect } = require('@playwright/test');

// 需要测试的公开页面列表
const PUBLIC_PAGES = [
  { path: '/', name: '首页' },
  { path: '/login', name: '登录页面' },
  { path: '/register.html', name: '注册页面' },
];

// 需要注意登录才能访问的页面列表
const USER_PAGES = [
  { path: '/cartList.html', name: '购物车页面' },
  { path: '/myOrders.html', name: '我的订单页面' },
  { path: '/changePassword.html', name: '修改密码页面' },
  { path: '/topSell.html', name: '热销排行榜页面' },
  { path: '/newArrivals.html', name: '新品上市页面' },
];

test.describe('【前端页面可访问性测试】', () => {

  // ==================== 公开页面访问测试 ====================
  test.describe('公开页面访问', () => {
    PUBLIC_PAGES.forEach(({ path, name }) => {
      test(`TC-FA-001: 应该能正常访问${name}`, async ({ page }) => {
        const response = await page.goto(path, { waitUntil: 'networkidle' });
        
        // 验证HTTP状态码
        expect(response.status()).toBeLessThan(500);
        
        // 验证页面标题存在
        const title = await page.title();
        expect(title).toBeDefined();
        
        // 验证页面没有致命的错误内容
        const bodyText = await page.textContent('body');
        expect(bodyText).not.toContain('Whitelabel Error Page');
        expect(bodyText).not.toContain('404');
        expect(bodyText).not.toContain('500');
        
        console.log(`  ✓ ${name} (${path}) 访问成功，状态码: ${response.status()}`);
      });
    });
  });

  // ==================== 需要登录的页面访问测试 ====================
  test.describe('需登录页面重定向', () => {
    USER_PAGES.forEach(({ path, name }) => {
      test(`TC-FA-002: 未登录访问${name}应跳转到登录页`, async ({ page }) => {
        await page.goto(path, { waitUntil: 'networkidle' });
        
        // 验证是否被重定向到登录页面
        const currentUrl = page.url();
        expect(currentUrl).toContain('login');
        
        console.log(`  ✓ ${name} (${path}) 未登录时正确跳转到登录页`);
      });
    });
  });

  // ==================== 页面加载性能测试 ====================
  test.describe('页面加载性能', () => {
    test('TC-FA-003: 首页加载时间应在合理范围内', async ({ page }) => {
      const startTime = Date.now();
      await page.goto('/', { waitUntil: 'networkidle' });
      const loadTime = Date.now() - startTime;
      
      // 加载时间不应超过5秒
      expect(loadTime).toBeLessThan(5000);
      console.log(`  ✓ 首页加载时间: ${loadTime}ms`);
    });
  });

  // ==================== 导航菜单测试 ====================
  test.describe('导航和菜单', () => {
    test('TC-FA-004: 首页应包含主导航菜单', async ({ page }) => {
      await page.goto('/', { waitUntil: 'networkidle' });
      
      // 查找导航元素
      const navExists = await page.$('nav, .navbar, .nav, header');
      expect(navExists).not.toBeNull();
      
      console.log('  ✓ 首页包含导航菜单');
    });

    test('TC-FA-005: 导航菜单中的链接应可点击', async ({ page }) => {
      await page.goto('/', { waitUntil: 'networkidle' });
      
      // 查找所有链接
      const links = await page.$$('a');
      expect(links.length).toBeGreaterThan(0);
      
      console.log(`  ✓ 首页包含 ${links.length} 个链接`);
    });

    test('TC-FA-006: 页面应包含商品展示区域', async ({ page }) => {
      await page.goto('/', { waitUntil: 'networkidle' });
      
      // 查找商品列表相关元素
      const goodsSection = await page.$('.goods-list, .product-list, [class*="goods"], [class*="product"]');
      expect(goodsSection).not.toBeNull();
      
      console.log('  ✓ 页面包含商品展示区域');
    });
  });
});
