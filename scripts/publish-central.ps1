[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$PrivateKeyFile,

    [Parameter(Mandatory = $true)]
    [string]$SigningPassword,

    [Parameter(Mandatory = $true)]
    [string]$CentralUsername,

    [Parameter(Mandatory = $true)]
    [string]$CentralPassword,

    [switch]$DryRun
)

$ErrorActionPreference = "Stop"

if (!(Test-Path $PrivateKeyFile)) {
    throw "Private key file not found: $PrivateKeyFile"
}

$privateKey = Get-Content $PrivateKeyFile -Raw
if ([string]::IsNullOrWhiteSpace($privateKey)) {
    throw "Private key file is empty: $PrivateKeyFile"
}

if ($privateKey -notmatch "BEGIN PGP PRIVATE KEY BLOCK") {
    throw "Private key file does not look like an armored PGP private key."
}

$env:ORG_GRADLE_PROJECT_signingInMemoryKey = $privateKey
$env:ORG_GRADLE_PROJECT_signingInMemoryKeyPassword = $SigningPassword
$env:ORG_GRADLE_PROJECT_mavenCentralUsername = $CentralUsername
$env:ORG_GRADLE_PROJECT_mavenCentralPassword = $CentralPassword

$task = if ($DryRun) { ":curvify:publishToMavenCentral" } else { ":curvify:publishAndReleaseToMavenCentral" }

Write-Host "Running Gradle task: $task"
& .\gradlew.bat $task --no-configuration-cache
