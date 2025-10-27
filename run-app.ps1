# Build and run the Spring Boot application, then wait until http://localhost:8080 is available
param(
    [switch]$Build
)

if ($Build) {
    Write-Host "Building project..."
    .\mvnw.cmd -DskipTests package
}

$jar = Get-ChildItem -Path target -Filter "*-SNAPSHOT.jar" | Select-Object -First 1
if (-not $jar) {
    Write-Host "No jar found in target/. Run with -Build to build first or verify build artifacts." -ForegroundColor Yellow
    exit 1
}

Write-Host "Starting $($jar.Name)..."
$proc = Start-Process -FilePath "java" -ArgumentList "-jar", "$($jar.FullName)" -PassThru

# Wait for port 8080
$timeout = 60
$elapsed = 0
while ($elapsed -lt $timeout) {
    try {
        $r = Invoke-WebRequest -Uri http://localhost:8080/login -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
        if ($r.StatusCode -eq 200) {
            Write-Host "Application is up and /login returned HTTP 200"
            exit 0
        }
    } catch {
        Start-Sleep -Seconds 1
        $elapsed++
    }
}

Write-Host "Timed out waiting for application to start after $timeout seconds." -ForegroundColor Red
exit 2
