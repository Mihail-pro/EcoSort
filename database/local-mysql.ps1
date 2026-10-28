# EcoSort — instanță MySQL locală, separată (pentru cine nu are parola root a serverului existent)
# Folosește mysqld-ul deja instalat, cu date proprii în %LOCALAPPDATA%\EcoSort\mysql,
# accesibilă doar de pe acest calculator (127.0.0.1).
#
#     powershell -ExecutionPolicy Bypass -File database\local-mysql.ps1            # pornește instanța
#     powershell -ExecutionPolicy Bypass -File database\local-mysql.ps1 -Install   # + rulează install.ps1
#
# La prima rulare se generează o parolă root aleatorie, salvată în root-password.txt din folderul instanței.

param(
    [int]$Port = 3307,
    [switch]$Install
)

$ErrorActionPreference = 'Stop'
$inst = Join-Path $env:LOCALAPPDATA 'EcoSort\mysql'
$dataDir = Join-Path $inst 'data'
$ini = Join-Path $inst 'my.ini'
$rootFile = Join-Path $inst 'root-password.txt'
$mysqld = (Get-Command mysqld -ErrorAction SilentlyContinue).Source
if (-not $mysqld) { throw 'mysqld nu este în PATH. Adăugați folderul bin al MySQL Server în PATH.' }

function Test-Ruleaza {
    $c = New-Object Net.Sockets.TcpClient
    try { $c.Connect('127.0.0.1', $Port); $true } catch { $false } finally { $c.Dispose() }
}

if (-not (Test-Path $dataDir)) {
    Write-Host "Inițializez instanța în $inst (port $Port)..."
    New-Item -ItemType Directory -Force $inst | Out-Null
    $d = $dataDir.Replace('\', '/')
    @"
[mysqld]
datadir=$d
port=$Port
bind-address=127.0.0.1
mysqlx=OFF
character-set-server=utf8mb4
"@ | Set-Content -Encoding ascii $ini
    & $mysqld --defaults-file=$ini --initialize-insecure --console
    if ($LASTEXITCODE -ne 0) { throw 'Inițializarea MySQL a eșuat.' }
}

if (-not (Test-Ruleaza)) {
    Write-Host 'Pornesc MySQL...'
    Start-Process -FilePath $mysqld -ArgumentList "--defaults-file=`"$ini`"" -WindowStyle Hidden
    for ($i = 0; $i -lt 30 -and -not (Test-Ruleaza); $i++) { Start-Sleep -Seconds 1 }
    if (-not (Test-Ruleaza)) { throw "MySQL nu a pornit pe portul $Port. Vezi jurnalul din $dataDir." }
}

if (-not (Test-Path $rootFile)) {
    $parola = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 24 | ForEach-Object { [char]$_ })
    # Întâi se salvează parola, apoi se aplică — altfel o eroare ar bloca accesul la instanță.
    [IO.File]::WriteAllText($rootFile, $parola)
    & mysql --host=127.0.0.1 --port=$Port --user=root --skip-password -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$parola';"
    if ($LASTEXITCODE -ne 0) { Remove-Item $rootFile; throw 'Nu am putut seta parola root.' }
    Write-Host "Parola root a instanței: $rootFile"
}

Write-Host "MySQL rulează pe 127.0.0.1:$Port" -ForegroundColor Green

if ($Install) {
    $env:MYSQL_PWD = [IO.File]::ReadAllText($rootFile)
    try { & (Join-Path $PSScriptRoot 'install.ps1') -DbHost 127.0.0.1 -Port $Port -AdminUser root }
    finally { Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue }
}
