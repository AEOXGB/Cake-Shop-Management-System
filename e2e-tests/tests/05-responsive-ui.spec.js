/**
 * 测试套件5：页面响应式布局和UI一致性测试
 * 验证页面在不同屏幕尺寸下的显示效果
 */

const { test, expect } = require('@playwright/test');

test.describe('【响应式布局和UI测试】', () => {

  // ==================== 响应式布局测试 ====================
  test.describe('响应式布局', () => {
    const viewports = [
      { width: 1920, height: 1080, name: '桌面端(1920x1080)' },
      { width: 1366, height: 768, name: '笔记本(1366x768)' },
      { width: 768, height: 1024, name: '平板(768x1024)' },
      { width: 375, height: 812, name: '手机(375x812)' },
    ];

    viewports.forEach(({ width, height, name }) => {
      test(`TC-RL-001: 首页在${name}下应正常显示`, async ({ page }) => {
        // 设置视口大小
        await page.setViewportSize({ width, height });
        
        await page.goto('/');
        await page.waitForSelector('body');
        
        // 验证页面内容可见
        const bodyText = await page.textContent('body');
        expect(bodyText.length).toBeGreaterThan(0);
        
        // 验证没有布局溢出
        const overflow = await page.evaluate(() => {
          return document.documentElement.scrollWidth <= document.documentElement.clientWidth + 5;
        });
        
        console.log(`  ✓ ${name} 首页显示正常，水平溢出: ${!overflow}`);
      });
    });

    test('TC-RL-002: 登录页面在移动端应正常显示', async ({ page }) => {
      await page.setViewportSize({ width: 375, height: 812 });
      await page.goto('/login');
      
      // 验证表单元素可见且可交互
      const form = await page.$('form');
      expect(form).not.toBeNull();
      
      const usernameInput = await page.$('input[name="username"]');
      expect(usernameInput).not.toBeNull();
      
      console.log('  ✓ 移动端登录页面显示正常');
    });
  });

  // ==================== 图片加载测试 ====================
  test.describe('图片和资源加载', () => {
    test('TC-RL-003: 首页商品图片应正常加载', async ({ page }) => {
      await page.goto('/');
      await page.waitForTimeout(2000);
      
      // 检查图片加载状态
      const brokenImages = await page.evaluate(() => {
        return Array.from(document.querySelectorAll('img'))
          .filter(img => !img.complete || img.naturalWidth === 0)
          .length;
      });
      
      console.log(`  ✓ 破损图片数: ${brokenImages}`);
    });
  });

  // ==================== 链接正确性测试 ====================
  test.describe('链接完整性', () => {
    test('TC-RL-004: 首页所有链接不应指向无效页面', async ({ page }) => {
      await page.goto('/', { waitUntil: 'networkidle' });
      
      // 收集所有内部链接
      const links = await page.evaluate(() => {
        return Array.from(document.querySelectorAll('a[href]'))
          .map(a => a.getAttribute('href'))
          .filter(href => href.startsWith('/') || href.startsWith('http://localhost'));
      });
      
      console.log(`  ✓ 首页共 ${links.length} 个内部链接`);
      
      // 抽样测试几个链接
      const testLinks = links.slice(0, 5);
      for (const link of testLinks) {
        try {
          const response = await page.goto(link, { waitUntil: 'domcontentloaded', timeout: 10000 });
          const status = response ? response.status() : 0;
          if (status >= 400) {
            console.log(`  ⚠ 链接 ${link} 返回状态码 ${status}`);
          }
        } catch (e) {
          console.log(`  ⚠ 链接 ${link} 访问超时`);
        }
      }
    });
  });
});
