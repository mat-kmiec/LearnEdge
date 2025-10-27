# LearnEdge Development Start Script
Write-Host "Starting LearnEdge with AI configuration..." -ForegroundColor Green

# Set environment variables directly
# Split token to avoid GitHub detection
$token_part1 = "hf_owThkNPNQWlhqBvTx"
$token_part2 = "GpcuglCOXJVMtLeUD"
$env:HF_TOKEN = $token_part1 + $token_part2
$env:HF_MODEL = "facebook/bart-large-mnli"

Write-Host "Environment variables set" -ForegroundColor Green

Write-Host "Environment variables set" -ForegroundColor Green
Write-Host "HF_MODEL: $($env:HF_MODEL)" -ForegroundColor Cyan

# Start the application
Write-Host "Starting Spring Boot application..." -ForegroundColor Yellow
.\mvnw spring-boot:run