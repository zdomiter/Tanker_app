# Tanker

**English** | [Magyar](README.hu.md)

Tanker is a JavaFX desktop application for tracking diesel refuelings at a company that operates trucks and construction machinery. It keeps records of the fuel dispensed from the on-site diesel tank, of the deliveries into that tank, and of refuelings at public fuel stations, then produces monthly reports and a printable PDF ledger.

The application was built as a practice project. Its user interface is in Hungarian.

## Screenshots

<p align="center">
  <img src="tanker_javaFx/docs/screenshots/refuelings.png" alt="Refuelings page" width="800">
  <br>
  <em>Refuelings list with vehicle and year filters, and the tank level gauge in the side menu</em>
</p>

<p align="center">
  <img src="tanker_javaFx/docs/screenshots/edit-refueling.png" alt="Editing a refueling" width="600">
  <br>
  <em>Editing a refueling, with the calculated distance and average consumption</em>
</p>

<p align="center">
  <img src="tanker_javaFx/docs/screenshots/statement.png" alt="Reports page" width="800">
  <br>
  <em>Monthly report with tank balance, daily tank level chart and per-vehicle consumption</em>
</p>

## Features

The application has five pages, each reachable from the side menu:

| Page | Hungarian label | What it does |
| --- | --- | --- |
| Refuelings | Tankolások | Create, edit and delete refuelings. Filter the list by vehicle and by year. |
| Vehicles | Járművek | Manage trucks, machines and private vehicles. |
| Tank | Tartály | Record deliveries into the on-site tank (date, quantity, unit price, supplier). |
| Fuel stations | Benzinkutak | Manage the fuel stations / fuel card providers a vehicle can be refuelled at. |
| Reports | Kimutatások | Tank balance and per-vehicle consumption summary for a chosen period, with PDF export. |

### Fuel cost calculated by FIFO

The on-site tank is modelled as a queue of deliveries, each with its own quantity and unit price. When fuel is taken from the tank, it is consumed from the oldest delivery first, and the refueling's price is the weighted average of the batches it used. For example, if 100 litres remain from a delivery at 690 Ft/l and a truck takes 150 litres, the first 100 litres are priced at 690 Ft/l and the remaining 50 at the price of the next delivery.

For refuelings at a public station, the unit price is entered manually.

### Distance and average consumption

When a refueling is recorded with its odometer (or operating hour) reading, the app calculates the distance since the previous refueling. For refuelings marked as a full tank, it calculates average consumption using the full-to-full method, so partial refuelings between two full ones are added together. Consumption is shown in l/100 km for road vehicles and in l/hour for machines measured in operating hours. Private vehicles do not need a mileage reading and have no consumption figure.

### Other details

- A level gauge in the side menu always shows the current quantity in the tank and its fill percentage (the tank capacity is 1,100 litres).
- AdBlue quantities can be recorded with each refueling and are totalled in the reports.
- Deletions are soft deletes: records are flagged as deleted with the date of deletion rather than being removed from the data files.

### Reports

The Reports page defaults to the previous calendar month and shows the following for the selected period:

- Opening quantity, total delivered into the tank, total dispensed from the tank, change over the period, closing quantity and current quantity
- Total AdBlue dispensed
- Dashboard gauges for the opening and closing quantity
- A line chart of the daily tank level
- A per-vehicle table with quantity refuelled, distance, average consumption and total cost

The **Letöltés** (Download) button saves a PDF of daily closings ("Napi zárások") to the user's `Downloads` folder. It lists every tank delivery and every refueling from the tank day by day, with a running tank balance.

## Technology

- Java 8
- JavaFX (FXML views, CSS styling)
- Maven
- [Medusa 8.3](https://github.com/HanSolo/Medusa) for the gauges
- iText 2.1.7 (`com.lowagie`) for PDF generation
- Semicolon-separated UTF-8 CSV files for data storage

The class diagram of the data model is in [`Tanker.drawio.png`](Tanker.drawio.png).

## Getting started

### Requirements

- A **JDK 8 that includes JavaFX**, for example Oracle JDK 8, Liberica JDK 8 "Full" or Azul Zulu 8 with JavaFX. The project targets Java 1.8 and does not declare JavaFX as a Maven dependency.
- Maven 3

### Running from an IDE

The project was created in Eclipse (with e(fx)clipse), and the Eclipse project files are included.

1. Import `tanker_javaFx` as an existing Maven project.
2. Make sure the project uses a JDK 8 with JavaFX.
3. Run `application.Main`.

The working directory must be the `tanker_javaFx` folder, because the data files are read from the relative path `data/`.

### Running with Maven

```bash
cd tanker_javaFx
mvn compile exec:java -Dexec.mainClass=application.Main
```

## Data files

The data lives in `tanker_javaFx/data/`. The repository contains sample data from 2023.

| File | Content | Columns |
| --- | --- | --- |
| `Machines.csv` | Vehicles and machines | `id;licensePlate;startMileage;type;privateVehicle;hourlyConsumption;deleted;delDate` |
| `ReFuelings.csv` | Refuelings | `id;date;machineId;tankId;quantity;amount;mileage;fuelPrice;note;full;adBlue;deleted;delDate` |
| `TankReFills.csv` | Deliveries into the tank | `id;date;quantity;price;company;note;deleted;delDate` |
| `TankCard.csv` | Fuel stations / card providers | `id;company;deleted;delDate` |

Boolean values are stored as `1`/`0` and dates as `yyyy-MM-dd`. The entry with id `1` in `TankCard.csv` (`Tartály`) stands for the on-site tank, and only refuelings with this source are deducted from the tank.

## Project structure

```
tanker_javaFx/
├── data/                  CSV data files
├── pom.xml
└── src/
    ├── application/
    │   ├── Main.java      Entry point
    │   ├── alert/         Dialog messages
    │   ├── controller/    JavaFX controllers for pages and dialogs
    │   ├── entity/        Domain classes (Machine, Refueling, Tank, TankReFill, TankCard) and table row models
    │   ├── frame/         FXML dialogs for adding and editing records
    │   ├── pane/          FXML pages
    │   ├── pdf/           PDF report generation
    │   ├── util/          CSV handling, lookups, chart data
    │   ├── images/        Menu icons
    │   └── style.css
    └── test/              Unit tests (JUnit)
```

