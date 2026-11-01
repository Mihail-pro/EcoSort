# EcoSort — instalare completă a bazei de date (Windows PowerShell)
# Rulează toate scripturile în ordine și creează contul aplicației din .env
# (ECOSORT_DB_USER / ECOSORT_DB_PASSWORD), cu drept doar de EXECUTE.
#
# Utilizare (din rădăcina proiectului):
#     powershell -ExecutionPolicy Bypass -File database\install.ps1
# Parola contului administrativ MySQL (root) se cere interactiv și nu se salvează
# (sau se ia din $env:MYSQL_PWD, dacă e deja setată — vezi local-mysql.ps1).

param(
    [string]$DbHost,
    [string]$Port,
    [string]$AdminUser
)

$ErrorActionPreference = 'Stop'
$dbDir = $PSScriptRoot
$envFile = Join-Path (Split-Path $dbDir -Parent) '.env'

$cfg = @{}
if (Test-Path $envFile) {
    foreach ($linie in Get-Content $envFile) {
        if ($linie -match '^\s*([A-Z_]+)\s*=\s*(.*?)\s*$') { $cfg[$Matches[1]] = $Matches[2] }
    }
}
foreach ($cheie in 'ECOSORT_DB_USER', 'ECOSORT_DB_PASSWORD') {
    if (-not $cfg[$cheie]) { throw "Lipsește $cheie din .env (vezi .env.example)." }
}
$dbHost = if ($DbHost) { $DbHost } elseif ($cfg['ECOSORT_DB_HOST']) { $cfg['ECOSORT_DB_HOST'] } else { 'localhost' }
$dbPort = if ($Port) { $Port } elseif ($cfg['ECOSORT_DB_PORT']) { $cfg['ECOSORT_DB_PORT'] } else { '3306' }

$adminUser = $AdminUser
if (-not $adminUser) { $adminUser = Read-Host 'Cont administrativ MySQL [root]' }
if (-not $adminUser) { $adminUser = 'root' }
if (-not $env:MYSQL_PWD) {
    $sec = Read-Host "Parola pentru $adminUser" -AsSecureString
    $env:MYSQL_PWD = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec))
}

function Invoke-Sql([string]$sql, [string]$pas) {
    & mysql --host=$dbHost --port=$dbPort --user=$adminUser --default-character-set=utf8mb4 -e $sql
    # Nu afișăm $sql: poate conține parola contului aplicației.
    if ($LASTEXITCODE -ne 0) { throw "MySQL a eșuat la pasul: $pas" }
}

try {
    $fisiere = @(
        '01_schema.sql', '02_constraints_indexes.sql',
        '03_procedures/utilizatori.sql', '03_procedures/categorii.sql', '03_procedures/puncte.sql',
        '03_procedures/inregistrari.sql', '03_procedures/obiective.sql', '03_procedures/rapoarte.sql',
        '06_iot_schema.sql', '03_procedures/masuratori.sql',
        '04_seed.sql'
    )
    foreach ($f in $fisiere) {
        Write-Host "-> $f"
        Invoke-Sql ("source " + (Join-Path $dbDir $f).Replace('\', '/')) $f
    }

    Write-Host "-> 05_app_user.sql (cont: $($cfg['ECOSORT_DB_USER']))"
    $q = { param($s) $s.Replace('\', '\\').Replace("'", "''") }
    # `source` este comandă a clientului mysql și trebuie să fie pe o linie separată.
    Invoke-Sql ("SET @app_user='" + (& $q $cfg['ECOSORT_DB_USER']) + "';`n" +
                "SET @app_password='" + (& $q $cfg['ECOSORT_DB_PASSWORD']) + "';`n" +
                "source " + (Join-Path $dbDir '05_app_user.sql').Replace('\', '/')) '05_app_user.sql'

    Write-Host 'Baza de date ecosort este gata.' -ForegroundColor Green
}
finally {
    Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue
}
