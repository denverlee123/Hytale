# PowerShell script to discover Hytale APIs
# Usage: .\find_apis.ps1

$jarPath = "libraries\HytaleServer.jar"

if (-not (Test-Path $jarPath)) {
    Write-Host "Error: HytaleServer.jar not found in libraries folder"
    exit 1
}

Write-Host "=== Finding Command Argument APIs ===" -ForegroundColor Cyan
jar -tf $jarPath | Select-String -Pattern "command.*arg" -CaseSensitive:$false | Select-String -Pattern "\.class$"

Write-Host "`n=== Finding Teleport APIs ===" -ForegroundColor Cyan
jar -tf $jarPath | Select-String -Pattern "teleport" -CaseSensitive:$false | Select-String -Pattern "\.class$"

Write-Host "`n=== Finding Death/Damage APIs ===" -ForegroundColor Cyan
jar -tf $jarPath | Select-String -Pattern "(death|damage)" -CaseSensitive:$false | Select-String -Pattern "\.class$" | Select-Object -First 20

Write-Host "`n=== Finding Block Event APIs ===" -ForegroundColor Cyan
jar -tf $jarPath | Select-String -Pattern "(block.*event|event.*block)" -CaseSensitive:$false | Select-String -Pattern "\.class$"

Write-Host "`n=== Finding Notification/Message APIs ===" -ForegroundColor Cyan
jar -tf $jarPath | Select-String -Pattern "(notif|notification)" -CaseSensitive:$false | Select-String -Pattern "\.class$" | Select-Object -First 20

Write-Host "`n=== Finding Entity Stat/Health APIs ===" -ForegroundColor Cyan
jar -tf $jarPath | Select-String -Pattern "(stat|health)" -CaseSensitive:$false | Select-String -Pattern "\.class$" | Select-Object -First 20

Write-Host "`n=== Finding Item/Inventory APIs ===" -ForegroundColor Cyan
jar -tf $jarPath | Select-String -Pattern "item.*metadata" -CaseSensitive:$false | Select-String -Pattern "\.class$"

Write-Host "`nDone! Please copy all the output above and share it." -ForegroundColor Green
