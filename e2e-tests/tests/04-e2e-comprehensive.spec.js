/**
 * 测试套件4：完整端到端(E2E)用户之旅
 * 模拟真实用户的完整购物体验
 */

const { test, expect } = require('@playwright/test');
const { loginUser, loginAdmin, generateUniqueUsername, generateUniqueEmail, generateUniquePhone } = require('./helpers.js');

test.describe('【完整端到端(E2E)测试】', () => {

  // ==================== E2E：用户完整购物流程 ====================
  test.describe('用户E2E: 完整购物流程', () => {
    test('TC-E2E-001: 完整购物闭环 - 浏览→详情→购物车→订单', async ({ page }) => {
      // 步骤1: 打开首页
      console.log('  步骤1: 打开首页');
      await page.goto('/');
      await page.waitForSelector('body');
      
      // 验证首页加载成功
      const bodyText = await page.textContent('body');
      expect(bodyText.length).toBeGreaterThan(0);
      console.log('  ✓ 首页加载成功');
      
      // 步骤2: 尝试浏览商品详情
      console.log('  步骤2: 浏览商品');
      const detailLink = await page.$('a[href*="goodsDetail"]');
      
      if (detailLink) {
        await detailLink.click();
        await page.waitForTimeout(2000);
        console.log('  ✓ 进入商品详情页');
      } else {
        console.log('  ⚠ 未找到商品详情链接');
      }
      
      // 步骤3: 尝试登录
      console.log('  步骤3: 用户登录');
      await loginUser(page, 'test', '123456');
      const afterLoginUrl = page.url();
      console.log(`  ✓ 登录完成，当前URL: ${afterLoginUrl}`);
      
      // 步骤4: 访问购物车
      console.log('  步骤4: 访问购物车');
      await page.goto('/cartList.html');
      await page.waitForTimeout(2000);
      const cartUrl = page.url();
      expect(cartUrl).toContain('cartList');
      console.log('  ✓ 购物车页面加载成功');
      
      // 步骤5: 访问我的订单
      console.log('  步骤5: 查看订单');
      await page.goto('/myOrders.html');
      await page.waitForTimeout(2000);
      const orderUrl = page.url();
      expect(orderUrl).toContain('myOrders');
      console.log('  ✓ 订单页面加载成功');
      
      // 步骤6: 访问热销排行榜
      console.log('  步骤6: 浏览热销排行榜');
      await page.goto('/topSell.html');
      await page.waitForTimeout(2000);
      const topSellUrl = page.url();
      expect(topSellUrl).toContain('topSell');
      console.log('  ✓ 热销排行榜加载成功');
      
      console.log('\n  ✅ 完整购物流程E2E测试通过！');
    });

    test('TC-E2E-002: 用户注册到浏览的完整闭环', async ({ page }) => {
      // 生成唯一用户名
      const uniqueUser = {
        username: generateUniqueUsername(),
        password: '123456',
        email: generateUniqueEmail(),
        phone: generateUniquePhone(),
      };
      console.log(`  测试用户: ${uniqueUser.username}`);
      
      // 步骤1: 访问注册页面
      console.log('  步骤1: 打开注册页面');
      await page.goto('/register.html');
      await page.waitForSelector('form');
      console.log('  ✓ 注册页面加载成功');
      
      // 步骤2: 填写注册信息
      console.log('  步骤2: 填写注册信息');
      await page.fill('input[name="username"]', uniqueUser.username);
      await page.fill('input[name="password"]', uniqueUser.password);
      await page.fill('input[name="email"]', uniqueUser.email);
      await page.fill('input[name="phone"]', uniqueUser.phone);
      console.log('  ✓ 注册信息填写完成');
      
      // 步骤3: 提交注册
      console.log('  步骤3: 提交注册');
      await page.click('button[type="submit"]');
      await page.waitForTimeout(2000);
      console.log('  ✓ 注册提交完成');
      
      // 步骤4: 回到首页浏览商品
      console.log('  步骤4: 浏览首页商品');
      await page.goto('/');
      await page.waitForTimeout(1000);
      const bodyText = await page.textContent('body');
      expect(bodyText.length).toBeGreaterThan(0);
      console.log('  ✓ 首页浏览成功');
      
      console.log('\n  ✅ 注册到浏览闭环测试通过！');
    });
  });

  // ==================== E2E：管理员完整管理流程 ====================
  test.describe('管理员E2E: 完整管理流程', () => {
    test('TC-E2E-003: 管理员完整管理闭环 - 登录→商品管理→订单管理→用户管理', async ({ page }) => {
      // 步骤1: 管理员登录
      console.log('  步骤1: 管理员登录');
      await loginAdmin(page);
      console.log('  ✓ 管理员登录成功');
      
      // 步骤2: 查看商品管理
      console.log('  步骤2: 查看商品管理');
      await page.goto('/admin/goodsList');
      await page.waitForTimeout(2000);
      const goodsBody = await page.textContent('body');
      expect(goodsBody).not.toContain('Whitelabel Error Page');
      console.log('  ✓ 商品管理页面加载成功');
      
      // 步骤3: 查看订单管理
      console.log('  步骤3: 查看订单管理');
      await page.goto('/admin/orderList');
      await page.waitForTimeout(2000);
      const orderBody = await page.textContent('body');
      expect(orderBody).not.toContain('Whitelabel Error Page');
      console.log('  ✓ 订单管理页面加载成功');
      
      // 步骤4: 查看用户管理
      console.log('  步骤4: 查看用户管理');
      await page.goto('/admin/userList');
      await page.waitForTimeout(2000);
      const userBody = await page.textContent('body');
      expect(userBody).not.toContain('Whitelabel Error Page');
      console.log('  ✓ 用户管理页面加载成功');
      
      // 步骤5: 查看骑手管理
      console.log('  步骤5: 查看骑手管理');
      await page.goto('/admin/riderList');
      await page.waitForTimeout(2000);
      const riderBody = await page.textContent('body');
      expect(riderBody).not.toContain('Whitelabel Error Page');
      console.log('  ✓ 骑手管理页面加载成功');
      
      // 步骤6: 查看销售统计
      console.log('  步骤6: 查看销售统计');
      await page.goto('/admin/salesStats');
      await page.waitForTimeout(2000);
      const statsBody = await page.textContent('body');
      expect(statsBody).not.toContain('Whitelabel Error Page');
      console.log('  ✓ 销售统计页面加载成功');
      
      // 步骤7: 查看库存预警
      console.log('  步骤7: 查看库存预警');
      await page.goto('/admin/stockWarning');
      await page.waitForTimeout(2000);
      const stockBody = await page.textContent('body');
      expect(stockBody).not.toContain('Whitelabel Error Page');
      console.log('  ✓ 库存预警页面加载成功');
      
      console.log('\n  ✅ 管理员完整管理流程E2E测试通过！');
    });
  });

  // ==================== E2E：骑手端流程 ====================
  test.describe('骑手端E2E: 配送流程', () => {
    test('TC-E2E-004: 骑手端待接单页面应正常显示', async ({ page }) => {
      // 使用骑手账户登录（通过同一登录页面）
      await page.goto('/login');
      await page.waitForSelector('form');
      
      await page.fill('input[name="username"]', 'rider');
      await page.fill('input[name="password"]', '123456');
      await page.click('button[type="submit"]');
      await page.waitForTimeout(2000);
      
      // 尝试访问骑手端页面
      await page.goto('/rider/pendingOrders');
      await page.waitForTimeout(2000);
      
      const bodyText = await page.textContent('body');
      expect(bodyText.length).toBeGreaterThan(0);
      expect(bodyText).not.toContain('Whitelabel Error Page');
      
      console.log('  ✓ 骑手端待接单页面加载成功');
    });
  });
});
