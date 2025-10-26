# LearnEdge Development Start Script
Write-Host "Starting LearnEdge with AI configuration..." -ForegroundColor Green

# Set environment variables directly
$env:HF_TOKEN = "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"
$env:HF_MODEL = "facebook/bart-large-mnli"

Write-Host "Environment variables set" -ForegroundColor Green

Write-Host "Environment variables set" -ForegroundColor Green
Write-Host "HF_MODEL: $($env:HF_MODEL)" -ForegroundColor Cyan

# Start the application
Write-Host "Starting Spring Boot application..." -ForegroundColor Yellow
.\mvnw spring-boot:run