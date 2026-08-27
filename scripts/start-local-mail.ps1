param(
    [string]$DatabasePassword = $env:DB_PASSWORD
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$mailpit = Join-Path $root "tools\mailpit\mailpit.exe"
$maven = Join-Path $root ".m2\wrapper\dists\apache-maven-3.9.9-bin\33b4b2b4\apache-maven-3.9.9\bin\mvn.cmd"

if (-not (Test-Path $mailpit)) {
    throw "Mailpit is missing from tools\mailpit. Run the project setup first."
}
if ([string]::IsNullOrWhiteSpace($DatabasePassword)) {
    throw "Set DB_PASSWORD before running this script."
}

if (-not (Get-NetTCPConnection -LocalPort 1025 -State Listen -ErrorAction SilentlyContinue)) {
    Start-Process -FilePath $mailpit -ArgumentList "--database", (Join-Path $root "mailpit-data\mailpit.db") -WorkingDirectory $root -WindowStyle Hidden
}

$env:DB_URL = "jdbc:postgresql://localhost:5432/unihub"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = $DatabasePassword
$env:SPRING_PROFILES_ACTIVE = "local-mail"

& $maven "-Dmaven.repo.local=$root\.m2\repository" spring-boot:run
