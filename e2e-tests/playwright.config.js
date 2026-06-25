// @ts-check
const { defineConfig, devices } = require('@playwright/test');

/**
 * CakeShop 项目端到端测试配置
 * @see https://playwright.dev/docs/test-configuration
 */
module.exports = defineConfig({
  testDir: './tests',
  /* 测试超时时间 */
  timeout: 60000,
  expect: {
    timeout: 10000
  },
  /* 测试运行完全失败后停止 */
  maxFailures: 0,
  /* 报告配置 */
  reporter: [
    ['html', { outputFolder: 'test-report', open: 'never' }],
    ['json', { outputFile: 'test-report/test-results.json' }],
    ['list']
  ],
  /* 全局配置 */
  use: {
    /* 应用基础URL */
    baseURL: 'http://localhost:8090',
    /* 追踪信息配置 */
    trace: 'retain-on-failure',
    /* 截图配置 */
    screenshot: 'only-on-failure',
    /* 视频录制 */
    video: 'retain-on-failure',
  },

  /* 项目配置 */
  projects: [
    {
      name: 'chromium',
      use: {
        ...devices['Desktop Chrome'],
        viewport: { width: 1920, height: 1080 },
        locale: 'zh-CN',
      },
    },
  ],

  /* 启动本地Web服务器 */
  webServer: {
    command: 'cd c:\\1\\CakeShop && mvn spring-boot:run',
    url: 'http://localhost:8090/login',
    reuseExistingServer: true,
    timeout: 120000,
  },
});
