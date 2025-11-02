# Script to set up Java 21 LTS permanently
# Run this script as Administrator to set system-wide environment variables

Write-Host "Setting up Java 21 LTS environment..." -ForegroundColor Green

# Set JAVA_HOME to Java 21
$java21Path = "C:\Program Files\Java\jdk-21"

# Check if Java 21 is installed
if (Test-Path $java21Path) {
    Write-Host "Java 21 found at: $java21Path" -ForegroundColor Green
    
    # Set JAVA_HOME for current user
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $java21Path, "User")
    Write-Host "JAVA_HOME set to: $java21Path" -ForegroundColor Green
    
    # Update PATH for current user
    $currentUserPath = [Environment]::GetEnvironmentVariable("PATH", "User")
    $java21BinPath = "$java21Path\bin"
    
    # Remove any existing Java paths from PATH
    $pathItems = $currentUserPath -split ";"
    $cleanPath = $pathItems | Where-Object { $_ -notmatch "\\Java\\|\\jdk" }
    $newPath = ($cleanPath + $java21BinPath) -join ";"
    
    [Environment]::SetEnvironmentVariable("PATH", $newPath, "User")
    Write-Host "PATH updated to include Java 21 bin directory" -ForegroundColor Green
    
    # Set environment variables for current session
    $env:JAVA_HOME = $java21Path
    $env:PATH = "$java21BinPath;$env:PATH"
    
    Write-Host "`nEnvironment setup complete!" -ForegroundColor Green
    Write-Host "Please restart your terminal or IDE to use the new Java version." -ForegroundColor Yellow
    
    # Verify installation
    Write-Host "`nVerifying Java installation:" -ForegroundColor Cyan
    & "$java21Path\bin\java.exe" -version
    
} else {
    Write-Host "Error: Java 21 not found at $java21Path" -ForegroundColor Red
    Write-Host "Please ensure Java 21 is properly installed." -ForegroundColor Red
}