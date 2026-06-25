const { execSync } = require('child_process');
const path = require('path');

// 设置工作目录
const testDir = path.resolve(__dirname);

console.log('Starting Playwright E2E tests...\n');

try {
  // 运行Playwright测试
  const result = execSync('npx playwright test --reporter=html,json,list', {
    cwd: testDir,
    stdio: 'pipe',
    timeout: 300000,
    env: { ...process.env, CI: 'true' }
  });
  
  console.log(result.stdout.toString());
  console.log('\nTest output captured successfully!');
} catch (error) {
  if (error.stdout) console.log(error.stdout.toString());
  if (error.stderr) console.error(error.stderr.toString());
  console.error(`\nTests finished with exit code: ${error.status}`);
}

// 检查报告
const fs = require('fs');
const reportDir = path.join(testDir, 'test-report');
if (fs.existsSync(reportDir)) {
  console.log(`\nTest report generated at: ${reportDir}`);
  const files = fs.readdirSync(reportDir, { recursive: true });
  files.forEach(f => console.log(`  - ${f}`));
} else {
  console.log('\nTest report directory not found - checking for test-results...');
  const resultsDir = path.join(testDir, 'test-results');
  if (fs.existsSync(resultsDir)) {
    console.log(`Test results at: ${resultsDir}`);
    const files = fs.readdirSync(resultsDir, { recursive: true });
    files.forEach(f => console.log(`  - ${f}`));
  }
}
