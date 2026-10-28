# EcoSort Mobile (Beta)

Aplicație Android nativă (Kotlin + Jetpack Compose) pentru lucrătorii care colectează deșeurile selective din CEITI. Este un prototip demo: nu folosește backend, toate datele sunt simulate local.

## Funcționalități

- Autentificare cu conturi de test (`sofer1` / `parola123`, `sofer2` / `parola123`) sau autentificare rapidă demo.
- Hartă interactivă pe etaje (Etaj 1–3), construită pe schițele reale ale colegiului: zoom, coșuri colorate după categorie, puls pe coșurile pline, filtre.
- Golirea unui coș: nivelul de umplere și greutatea sunt măsurate automat de senzorii coșului; lucrătorul doar scanează codul QR (simulat) și confirmă.
- Istoric și statistici pe sesiune: total kg, distribuție pe categorii, lista colectărilor.
- Profil cu instrumente demo: senzori live, umplere aleatorie a coșurilor, resetare.

## Rulare

1. Deschide folderul `mobile/` în Android Studio (nu rădăcina repo-ului).
2. Așteaptă sincronizarea Gradle.
3. Alege un emulator sau un telefon și apasă Run.

Cerințe: Android Studio recent, SDK 37, minSdk 26 (Android 8.0+).

## Structură

```
app/src/main/java/md/ceiti/ecosort/
  EcoSortViewModel.kt      starea aplicației (StateFlow)
  data/                    modele și date simulate
  ui/login                 ecran de autentificare
  ui/map                   hartă, listă coșuri, fereastra de golire
  ui/history               istoric și statistici
  ui/profile               profil și instrumente demo
  ui/theme                 culori și tipografie (paleta EcoSort Web)
```

Coordonatele coșurilor pe hartă și datele de test se modifică în `data/MockData.kt`.
