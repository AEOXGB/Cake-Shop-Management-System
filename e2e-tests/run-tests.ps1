# 运行Playwright测试脚本
Write-Host "Starting Playwright E2E tests..." -ForegroundColor Green

Set-Location $PSScriptRoot

# 运行测试
$env:CI = "true"
npx.cmd playwright test --reporter=html,json

# 检查运行结果
if ($LASTEXITCODE -eq 0) {
    Write-Host "All tests completed successfully!" -ForegroundColor Green
} else {
    Write-Host "Some tests failed with exit code: $LASTEXITCODE" -ForegroundColor Yellow
}

# 确认报告生成
$reportDir = Join-Path $PSScriptRoot "test-report"
if (Test-Path $reportDir) {
    Write-Host "Test report generated at: $reportDir" -ForegroundColor Green
    Get-ChildItem -Path $reportDir -Recurse | Select-Object Name, Length | Format-Table -AutoSize
} else {
    Write-Host "Test report directory not found!" -ForegroundColor Red
}
