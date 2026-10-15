# ==============================================================
# start-backend.ps1 — CareerPilot Backend Dev Launcher
# Runs Spring Boot with H2 in-memory DB (no Docker required)
# ==============================================================

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  CareerPilot Backend — Dev Local Mode" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Profile  : dev-local (H2 in-memory DB, no Docker needed)" -ForegroundColor Yellow
Write-Host "Port     : 8081" -ForegroundColor Yellow
Write-Host "H2 UI    : http://localhost:8081/h2-console" -ForegroundColor Yellow
Write-Host "Health   : http://localhost:8081/actuator/health" -ForegroundColor Yellow
Write-Host ""

# Load .env file if present
$envFile = Join-Path $ProjectRoot ".env"
if (Test-Path $envFile) {
    Write-Host "Loading .env..." -ForegroundColor Gray
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $key = $matches[1].Trim()
            $val = $matches[2].Trim()
            [System.Environment]::SetEnvironmentVariable($key, $val, "Process")
        }
    }
}

# Build
Write-Host "Building project (skipping tests)..." -ForegroundColor Green
Push-Location $ProjectRoot
try {
    mvn -q clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build FAILED. Fix compilation errors above." -ForegroundColor Red
        exit 1
    }

    $jar = Get-ChildItem "target\*.jar" | Where-Object { $_.Name -notmatch "original" } | Select-Object -First 1
    if (-not $jar) {
        Write-Host "No JAR found in target/. Build may have failed." -ForegroundColor Red
        exit 1
    }

    Write-Host ""
    Write-Host "Build SUCCESS. Starting backend on port 8081..." -ForegroundColor Green
    Write-Host "Press Ctrl+C to stop." -ForegroundColor Gray
    Write-Host ""

    java `
        "-Dspring.profiles.active=dev-local" `
        "-DGEMINI_API_KEY=$env:GEMINI_API_KEY" `
        "-DJWT_SECRET=$env:JWT_SECRET" `
        -jar $jar.FullName
} finally {
    Pop-Location
}
