# EcoSort — pornire cu un singur dublu-click (prin porneste.bat).
# 1. verifică Java și Docker  2. creează .env la prima rulare  3. pornește baza de date  4. pornește aplicația
# Erorile programelor externe se verifică prin $LASTEXITCODE.

Set-Location $PSScriptRoot

function Pas($text) { Write-Host "`n>> $text" -ForegroundColor Green }
function Opreste($text) { Write-Host "`n[!] $text" -ForegroundColor Red; exit 1 }

Write-Host '=== EcoSort ===' -ForegroundColor Green

# --- 1. Programele necesare ---
Pas 'Verific programele necesare...'
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Opreste 'Java nu este instalat. Instalează-l de pe https://adoptium.net (vezi README, Pasul 1).'
}
# mvnw.cmd are nevoie de JAVA_HOME; multe instalări de Java nu îl setează, așa că îl deducem.
if (-not $env:JAVA_HOME) {
    $linie = (& java -XshowSettings:properties -version 2>&1 | Out-String) -split "`n" |
             Where-Object { $_ -match '^\s*java\.home\s*=' } | Select-Object -First 1
    if ($linie) { $env:JAVA_HOME = ($linie -split '=', 2)[1].Trim() }
}
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Opreste 'Docker Desktop nu este instalat. Instalează-l de pe https://www.docker.com/products/docker-desktop (vezi README, Pasul 1).'
}

# Docker Desktop trebuie să ruleze; dacă e închis, îl pornim și așteptăm.
docker info *> $null
if ($LASTEXITCODE -ne 0) {
    Pas 'Pornesc Docker Desktop (poate dura până la un minut)...'
    $exe = @("$env:ProgramFiles\Docker\Docker\Docker Desktop.exe",
             "$env:LOCALAPPDATA\Programs\DockerDesktop\Docker Desktop.exe") |
           Where-Object { Test-Path $_ } | Select-Object -First 1
    if ($exe) { Start-Process $exe }
    for ($i = 0; $i -lt 60; $i++) {
        Start-Sleep -Seconds 3
        docker info *> $null
        if ($LASTEXITCODE -eq 0) { break }
    }
    if ($LASTEXITCODE -ne 0) { Opreste 'Docker Desktop nu a pornit. Deschide-l manual, așteaptă "Engine running" și rulează din nou.' }
}

# --- 2. Configurarea (.env), doar la prima rulare ---
if (-not (Test-Path .env)) {
    Pas 'Prima rulare: creez fișierul de configurare .env...'
    $octeti = New-Object byte[] 18
    [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($octeti)
    $parola = [Convert]::ToBase64String($octeti).Replace('+', 'A').Replace('/', 'B')

    # Dacă 3306 e ocupat (MySQL instalat pe calculator), folosim alt port.
    $port = 3306
    $c = New-Object Net.Sockets.TcpClient
    try { $c.Connect('127.0.0.1', 3306); $port = 3310 } catch { } finally { $c.Dispose() }

    $linii = Get-Content .env.example -Encoding UTF8 | ForEach-Object {
        $_ -replace '^ECOSORT_DB_PORT=.*', "ECOSORT_DB_PORT=$port" `
           -replace '^ECOSORT_DB_PASSWORD=.*', "ECOSORT_DB_PASSWORD=$parola"
    }
    [IO.File]::WriteAllLines((Join-Path $PSScriptRoot '.env'), [string[]]$linii)   # UTF-8 fără BOM
}

# --- 3. Baza de date ---
Pas 'Pornesc baza de date (prima dată durează 1-2 minute)...'
docker compose up -d --wait
if ($LASTEXITCODE -ne 0) {
    Opreste 'Baza de date nu a pornit. Vezi secțiunea "Dacă ceva nu merge" din README.'
}

# --- 4. Aplicația ---
Pas 'Pornesc aplicația (prima dată se descarcă bibliotecile, câteva minute)...'
& .\mvnw.cmd -q javafx:run
if ($LASTEXITCODE -ne 0) { Opreste 'Aplicația s-a oprit cu o eroare (vezi mesajele de mai sus).' }
