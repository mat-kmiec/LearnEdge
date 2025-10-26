# LearnEdge Development Start Script
Write-Host "Starting LearnEdge with AI configuration..." -ForegroundColor Green

# Load environment variables from .env file
if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match "^([^=]+)=(.*)$") {
            Set-Item -Path "env:$($matches[1])" -Value $matches[2]
        }
    }
    Write-Host "Environment variables loaded from .env file" -ForegroundColor Green
} else {
    Write-Host "Warning: .env file not found. Please create it with HF_TOKEN and HF_MODEL" -ForegroundColor Yellow
}

Write-Host "Environment variables set" -ForegroundColor Green
Write-Host "HF_MODEL: $($env:HF_MODEL)" -ForegroundColor Cyan

# Start the application
Write-Host "Starting Spring Boot application..." -ForegroundColor Yellow
.\mvnw spring-boot:run