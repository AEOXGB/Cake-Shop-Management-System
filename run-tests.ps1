$started = Get-Process -Name "java" -ErrorAction SilentlyContinue
Start-Process -NoNewWindow -FilePath "mvn.cmd" -ArgumentList "clean compile" -WorkingDirectory "C:\1\CakeShop" -RedirectStandardOutput "C:\1\CakeShop\compile-out.txt" -RedirectStandardError "C:\1\CakeShop\compile-err.txt" -Wait
Write-Host "Compile exit code: $LASTEXITCODE"
Start-Process -NoNewWindow -FilePath "mvn.cmd" -ArgumentList "test-compile" -WorkingDirectory "C:\1\CakeShop" -RedirectStandardOutput "C:\1\CakeShop\test-compile-out.txt" -RedirectStandardError "C:\1\CakeShop\test-compile-err.txt" -Wait
Write-Host "Test-compile exit code: $LASTEXITCODE"
Start-Process -NoNewWindow -FilePath "mvn.cmd" -ArgumentList "surefire:test" -WorkingDirectory "C:\1\CakeShop" -RedirectStandardOutput "C:\1\CakeShop\test-out.txt" -RedirectStandardError "C:\1\CakeShop\test-err.txt" -Wait
Write-Host "Test exit code: $LASTEXITCODE"
