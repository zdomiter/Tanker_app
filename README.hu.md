# Tanker

[English](README.md) | **Magyar**

A Tanker egy JavaFX asztali alkalmazás, amely egy teherautókat és munkagépeket üzemeltető cég gázolaj-tankolásait tartja nyilván. Rögzíti a telephelyi tartályból kiadott üzemanyagot, a tartályba történő betöltéseket és a benzinkutas tankolásokat, majd ezekből havi kimutatást és nyomtatható PDF-et készít.

Az alkalmazás gyakorló projektként készült. A felhasználói felület magyar nyelvű.

## Képernyőképek

<p align="center">
  <img src="docs/screenshots/refuelings.png" alt="Tankolások oldal" width="800">
  <br>
  <em>A tankolások listája jármű- és évszűrővel, az oldalsó menüben a tartály szintjelzőjével</em>
</p>

<p align="center">
  <img src="docs/screenshots/edit-refueling.png" alt="Tankolás szerkesztése" width="600">
  <br>
  <em>Tankolás szerkesztése a kiszámolt megtett távolsággal és átlagfogyasztással</em>
</p>

<p align="center">
  <img src="docs/screenshots/statement.png" alt="Kimutatások oldal" width="800">
  <br>
  <em>Havi kimutatás a tartálymérleggel, a napi tartályszint diagramjával és a járművenkénti fogyasztással</em>
</p>

## Funkciók

Az alkalmazás öt oldalból áll, ezek az oldalsó menüből érhetők el:

| Oldal | Mire szolgál |
| --- | --- |
| Tankolások | Tankolások felvétele, szerkesztése és törlése. A lista járműre és évre szűrhető. |
| Járművek | Teherautók, munkagépek és magánjárművek kezelése. |
| Tartály | A telephelyi tartályba történt betöltések rögzítése (dátum, mennyiség, egységár, szállító). |
| Benzinkutak | Azoknak a töltőállomásoknak / tankkártya-szolgáltatóknak a kezelése, ahol tankolni lehet. |
| Kimutatások | Tartálymérleg és járművenkénti fogyasztási összesítő egy kiválasztott időszakra, PDF-exporttal. |

### Üzemanyagár FIFO-elv alapján

A telephelyi tartályt az alkalmazás betöltések sorozataként kezeli, mindegyiknek saját mennyisége és egységára van. A tartályból kiadott üzemanyag mindig a legrégebbi betöltésből fogy először, a tankolás egységára pedig a felhasznált tételek súlyozott átlaga. Ha például egy 690 Ft/l-es betöltésből 100 liter maradt, és egy teherautó 150 litert tankol, akkor az első 100 liter 690 Ft/l-es áron, a maradék 50 liter a következő betöltés árán számolódik.

Benzinkutas tankolásnál az egységárat kézzel kell megadni.

### Megtett távolság és átlagfogyasztás

Ha a tankolásnál meg van adva a km-óra (vagy üzemóra) állás, az alkalmazás kiszámolja az előző tankolás óta megtett távolságot. Teletankként jelölt tankolásnál a „teletanktól teletankig” módszerrel számolja az átlagfogyasztást, vagyis a két teletankolás közötti részleges tankolásokat is összeadja. A fogyasztás közúti járműveknél l/100 km-ben, üzemórával mért gépeknél l/órában jelenik meg. Magánjárműnél a km-óra állás nem kötelező, és fogyasztást sem számol a program.

### Egyéb részletek

- Az oldalsó menüben egy szintjelző mindig mutatja a tartály aktuális mennyiségét és telítettségét (a tartály kapacitása 1100 liter).
- Minden tankoláshoz AdBlue-mennyiség is rögzíthető, ezt a kimutatás összesíti.
- A törlés logikai törlés: a rekord nem tűnik el az adatfájlból, csak törölt jelölést kap a törlés dátumával.

### Kimutatások

A Kimutatások oldal alapértelmezetten az előző naptári hónapot mutatja, és a kiválasztott időszakra a következőket jeleníti meg:

- nyitó mennyiség, tartályba betöltött összes mennyiség, tartályból kiadott mennyiség, időszaki változás, záró mennyiség és aktuális mennyiség
- kiadott AdBlue összesen
- műszerfal-kijelzők a nyitó és a záró mennyiséghez
- vonaldiagram a tartály napi szintjéről
- járművenkénti táblázat a tankolt mennyiséggel, megtett távolsággal, átlagfogyasztással és összköltséggel

A **Letöltés** gomb a felhasználó `Letöltések` (`Downloads`) mappájába menti a „Napi zárások” PDF-et. Ez napról napra felsorolja a tartályba történt betöltéseket és a tartályból történt tankolásokat, a tartály futó egyenlegével együtt.

## Technológiák

- Java 8
- JavaFX (FXML nézetek, CSS stílusok)
- Maven
- [Medusa 8.3](https://github.com/HanSolo/Medusa) a kijelzőkhöz
- iText 2.1.7 (`com.lowagie`) a PDF-készítéshez
- Pontosvesszővel tagolt, UTF-8 kódolású CSV-fájlok az adattároláshoz

Az adatmodell osztálydiagramja a [`Tanker.drawio.png`](Tanker.drawio.png) fájlban található.

## Indítás

### Követelmények

- **JavaFX-et tartalmazó JDK 8**, például Oracle JDK 8, Liberica JDK 8 „Full” vagy Azul Zulu 8 JavaFX-szel. A projekt Java 1.8-ra készült, és a JavaFX-et nem Maven-függőségként húzza be.
- Maven 3

### Futtatás fejlesztőkörnyezetből

A projekt Eclipse-ben (e(fx)clipse-szel) készült, az Eclipse projektfájlok a repóban vannak.

1. Importáld a `tanker_javaFx` mappát meglévő Maven-projektként.
2. Állítsd be, hogy a projekt JavaFX-es JDK 8-at használjon.
3. Futtasd az `application.Main` osztályt.

A munkakönyvtárnak a `tanker_javaFx` mappának kell lennie, mert a program a `data/` relatív útvonalról olvassa az adatfájlokat.

### Futtatás Mavennel

```bash
cd tanker_javaFx
mvn compile exec:java -Dexec.mainClass=application.Main
```

## Adatfájlok

Az adatok a `tanker_javaFx/data/` mappában vannak. A repó 2023-as mintaadatokat tartalmaz.

| Fájl | Tartalom | Oszlopok |
| --- | --- | --- |
| `Machines.csv` | Járművek és munkagépek | `id;licensePlate;startMileage;type;privateVehicle;hourlyConsumption;deleted;delDate` |
| `ReFuelings.csv` | Tankolások | `id;date;machineId;tankId;quantity;amount;mileage;fuelPrice;note;full;adBlue;deleted;delDate` |
| `TankReFills.csv` | Tartálybetöltések | `id;date;quantity;price;company;note;deleted;delDate` |
| `TankCard.csv` | Benzinkutak / kártyaszolgáltatók | `id;company;deleted;delDate` |

A logikai értékek `1`/`0` formában, a dátumok `éééé-HH-nn` formában tárolódnak. A `TankCard.csv` 1-es azonosítójú sora (`Tartály`) a telephelyi tartályt jelöli, és csak az ilyen forrású tankolások csökkentik a tartály készletét.

## Projektszerkezet

```
tanker_javaFx/
├── data/                  CSV adatfájlok
├── pom.xml
└── src/
    ├── application/
    │   ├── Main.java      Belépési pont
    │   ├── alert/         Figyelmeztető és megerősítő üzenetek
    │   ├── controller/    Az oldalak és ablakok JavaFX controllerei
    │   ├── entity/        Domain-osztályok (Machine, Refueling, Tank, TankReFill, TankCard) és táblázatsor-modellek
    │   ├── frame/         FXML ablakok rekordok felvételéhez és szerkesztéséhez
    │   ├── pane/          FXML oldalak
    │   ├── pdf/           PDF-kimutatás készítése
    │   ├── util/          CSV-kezelés, keresések, diagramadatok
    │   ├── images/        Menüikonok
    │   └── style.css
    └── test/              Egységtesztek (JUnit)
```

