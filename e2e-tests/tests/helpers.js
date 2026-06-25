/**
 * CakeShop E2E 测试通用辅助函数
 */

/**
 * 生成唯一用户名（基于时间戳）
 */
function generateUniqueUsername() {
  const timestamp = Date.now();
  return `test_user_${timestamp}`;
}

/**
 * 生成唯一邮箱
 */
function generateUniqueEmail() {
  const timestamp = Date.now();
  return `test_${timestamp}@example.com`;
}

/**
 * 生成唯一手机号
 */
function generateUniquePhone() {
  const timestamp = Date.now().toString().slice(-8);
  return `138${timestamp}`;
}

/**
 * 测试用户数据
 */
const TEST_USER = {
  username: 'test_e2e',
  password: '123456',
  email: 'test_e2e@example.com',
  phone: '13800138000',
};

/**
 * 管理员登录信息
 */
const ADMIN_USER = {
  username: 'admin',
  password: 'admin123',
};

/**
 * 注册新用户
 */
async function registerUser(page, userData) {
  await page.goto('/register.html');
  await page.waitForSelector('form');
  
  // 填写注册表单
  await page.fill('input[name="username"]', userData.username || generateUniqueUsername());
  await page.fill('input[name="password"]', userData.password || '123456');
  await page.fill('input[name="email"]', userData.email || generateUniqueEmail());
  await page.fill('input[name="phone"]', userData.phone || generateUniquePhone());
  
  // 提交表单
  await page.click('button[type="submit"]');
  
  // 等待跳转
  await page.waitForTimeout(2000);
}

/**
 * 用户登录
 */
async function loginUser(page, username, password) {
  await page.goto('/login');
  await page.waitForSelector('form');
  
  await page.fill('input[name="username"]', username);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');
  
  // 等待登录完成
  await page.waitForTimeout(2000);
}

/**
 * 管理员登录
 */
async function loginAdmin(page) {
  await page.goto('/admin/login');
  await page.waitForSelector('form');
  
  await page.fill('input[name="username"]', ADMIN_USER.username);
  await page.fill('input[name="password"]', ADMIN_USER.password);
  await page.click('button[type="submit"]');
  
  await page.waitForTimeout(2000);
}

/**
 * 截图辅助
 */
async function takeScreenshot(page, name) {
  await page.screenshot({ 
    path: `test-report/screenshots/${name}.png`, 
    fullPage: true 
  });
}

module.exports = {
  generateUniqueUsername,
  generateUniqueEmail,
  generateUniquePhone,
  TEST_USER,
  ADMIN_USER,
  registerUser,
  loginUser,
  loginAdmin,
  takeScreenshot,
};
