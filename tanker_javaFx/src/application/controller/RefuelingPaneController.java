package application.controller;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import application.entity.Machine;
import application.entity.Refueling;
import application.entity.TableData;
import application.entity.TankCard;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RefuelingPaneController implements Initializable {

	private List<Machine> machines;
	private List<TankCard> tankCards;
	private List<Refueling> refuelings;

	private DecimalFormat df;

	@FXML
	private VBox paneRefueling;

	@FXML
	private Button btnAdd;

	@FXML
	private Button btnFilterRefueling;

	@FXML
	private ComboBox<String> cmbMachine;

	@FXML
	private ComboBox<String> cmbYear;

	@FXML
	private TableView<TableData> tableRefueling;

	@FXML
	void filterRefueling(ActionEvent event) {
		fillTableData();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.machines = MainFrameController.getMachines();
		this.tankCards = MainFrameController.getTankCards();
		this.refuelings = MainFrameController.getRefuelings();
		comboBoxYearFilterFillData();
		comboBoxMachineFilterFillData();
		fillTableData();
		tableRefueling.setRowFactory(tv -> {
			TableRow<TableData> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					TableData rowData = row.getItem();
					IntegerProperty selectedId = rowData.getId();
					openRefuelingUpdateFrame(selectedId.getValue().intValue());
				}
			});
			return row;
		});

	}

	@SuppressWarnings("unchecked")
	public void fillTableData() {
		tableRefueling.getColumns().clear();
		tableRefueling.getItems().clear();

		ObservableList<TableData> data = FXCollections.observableArrayList();

		for (Refueling refueling : refuelings) {
			if (filterWithTable(refueling)) {
				Machine machine = machines.stream().filter(m -> m.getId() == refueling.getMachineId()).findFirst()
						.orElse(null);

				TankCard tankCard = tankCards.stream().filter(t -> t.getId() == refueling.getTankId()).findFirst()
						.orElse(null);

				data.add(new TableData(refueling.getId(), refueling.getDate(),
						machine != null ? machine.getLicensePlate() : "", machine != null ? machine.getType() : "",
						tankCard != null ? tankCard.getCompany() : "", refueling.getQuantity(), refueling.getAmount(),
						refueling.getMileage(), calculateDistance(refueling),
						calculateAverageFuelConsumption(refueling),
						(refueling.getNote()!="0")? refueling.getNote(): "",
						refueling.isFull() ? "Tele" : "Nincs tele", refueling.getAdBlue()));
			}
		}
		TableColumn<TableData, Integer> idColumn = new TableColumn<>("Id");
		idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());
		idColumn.setPrefWidth(0);

		TableColumn<TableData, LocalDate> dateColumn = new TableColumn<>("Dátum");
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDate());

		TableColumn<TableData, String> licensePlateColumn = new TableColumn<>("Rendszám/Név");
		licensePlateColumn.setCellValueFactory(cellData -> cellData.getValue().getLicensePlate());

		TableColumn<TableData, String> typeColumn = new TableColumn<>("Típus");
		typeColumn.setCellValueFactory(cellData -> cellData.getValue().getType());

		TableColumn<TableData, String> companyColumn = new TableColumn<>("Tartály/Kártya");
		companyColumn.setCellValueFactory(cellData -> cellData.getValue().getCompany());

		TableColumn<TableData, Double> quantityColumn = new TableColumn<>("Mennyiség");
		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().getQuantity().asObject());

		TableColumn<TableData, Double> adBlueColumn = new TableColumn<>("AdBlue");
		adBlueColumn.setCellValueFactory(cellData -> cellData.getValue().getAdBlue().asObject());

		TableColumn<TableData, Integer> amountColumn = new TableColumn<>("Összeg");
		amountColumn.setCellValueFactory(cellData -> cellData.getValue().getAmount().asObject());

		TableColumn<TableData, Integer> mileageColumn = new TableColumn<>("Km/óra állás");
		mileageColumn.setCellValueFactory(cellData -> cellData.getValue().getMileage().asObject());

		TableColumn<TableData, Integer> distanceColumn = new TableColumn<>("Megtett km/óra");
		distanceColumn.setCellValueFactory(cellData -> cellData.getValue().getDistance().asObject());

		TableColumn<TableData, String> fullColumn = new TableColumn<>("Teletank");
		fullColumn.setCellValueFactory(cellData -> cellData.getValue().getFull());

		TableColumn<TableData, String> avgFuelConsumptionColumn = new TableColumn<>("Átlagfogyasztás");
		avgFuelConsumptionColumn.setCellValueFactory(cellData -> cellData.getValue().getAvgFuelConsumption());

		TableColumn<TableData, String> noteColumn = new TableColumn<>("Megjegyzés");
		noteColumn.setCellValueFactory(cellData -> cellData.getValue().getNote());

		tableRefueling.getColumns().addAll(idColumn, dateColumn, licensePlateColumn, typeColumn, companyColumn,
				quantityColumn, adBlueColumn, amountColumn, mileageColumn, distanceColumn, fullColumn,
				avgFuelConsumptionColumn, noteColumn);

		tableRefueling.setItems(data);
		int lastIndex = tableRefueling.getItems().size() - 1;
		tableRefueling.scrollTo(lastIndex);
	}

	private boolean filterWithTable(Refueling refueling) {
		boolean filter = false;
		int selectedMachineId = machines.stream()
				.filter(machine -> machine.getLicensePlate().equals(cmbMachine.getValue())).map(Machine::getId)
				.findFirst().orElse(0);

		String SelectedYear = cmbYear.getValue();

		if (!refueling.isDeleted() && (selectedMachineId == refueling.getMachineId() || selectedMachineId == 0)
				&& (SelectedYear.equals(refueling.getDate().getYear() + "") || SelectedYear.equals("Mind"))) {
			filter = true;
		}
		return filter;
	}

	private int calculateDistance(Refueling currentRefueling) {
		return currentRefueling.getMileage() - getPreFullMileage(currentRefueling);
	}

	private int getPreFullMileage(Refueling currentRefueling) {
		return refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> x.isFull())
				.filter(x -> x.getMachineId() == currentRefueling.getMachineId())
				.filter(x -> x.getMileage() < currentRefueling.getMileage()).mapToInt(Refueling::getMileage).max()
				.orElse(machines.stream().filter(x -> x.getId() == currentRefueling.getMachineId()).findFirst().get()
						.getStartMileage());
	}

	private double calculateQuantity(Refueling currentRefueling) {
		double preQuantity = refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> !x.isFull())
				.filter(x -> x.getMachineId() == currentRefueling.getMachineId())
				.filter(x -> x.getMileage() > getPreFullMileage(currentRefueling))
				.filter(x -> x.getMileage() < currentRefueling.getMileage()).mapToDouble(Refueling::getQuantity).sum();
		return preQuantity + currentRefueling.getQuantity();
	}

	private String calculateAverageFuelConsumption(Refueling lastRefueling) {
		boolean privateMachine = machines.get(lastRefueling.getMachineId() - 1).isPrivateVehicle();

		if (lastRefueling.isFull() && !privateMachine) {
			df = new DecimalFormat("0.00");
			int distance = calculateDistance(lastRefueling);
			double consumption = calculateQuantity(lastRefueling) / distance * 100;
			if (isHourlyConsumption(lastRefueling.getMachineId())) {
				consumption = consumption / 100;
			}
			return df.format(consumption);
		} else {
			return "-";
		}

	}

	private boolean isHourlyConsumption(int machineId) {
		return machines.stream().filter(x -> x.getId() == machineId).findFirst().map(Machine::isHourlyConsumption)
				.orElse(false);
	}

	public void comboBoxMachineFilterFillData() {
		ObservableList<String> items = FXCollections.observableArrayList("Mind");
		for (Machine machine : machines) {
			items.add(machine.getLicensePlate());
		}
		cmbMachine.setItems(items);
		cmbMachine.getSelectionModel().select(0);

	}

	public void comboBoxYearFilterFillData() {
		ObservableList<String> distinctYears = FXCollections.observableArrayList("Mind");
		distinctYears.addAll(refuelings.stream().map(Refueling::getDate).map(date -> String.valueOf(date.getYear()))
				.distinct().sorted().collect(Collectors.toList()));
		cmbYear.setItems(distinctYears);
		cmbYear.getSelectionModel().selectLast();

	}

	@FXML
	void refuelingFrameOpen(ActionEvent event) {
		Stage newRefuelingStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../RefuelingNewFrame.fxml"));
			GridPane root = loader.load();
			RefuelingNewFrameController controller = loader.getController();
			controller.setRefuelingPaneController(this);
			Scene scene = new Scene(root, 800, 600);
			newRefuelingStage.setScene(scene);
			newRefuelingStage.setTitle("Tankolás hozzáadása");
			newRefuelingStage.setX(150);
			newRefuelingStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void openRefuelingUpdateFrame(int id) {
		Stage refuelingUpdateStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../RefuelingUpdateFrame.fxml"));
			GridPane root = loader.load();
			RefuelingUpdateFrameController controller = loader.getController();
			controller.setRefuelingId(id);
			controller.loadSelectedRefueling();
			controller.setRefuelingPaneController(this);
			Scene scene = new Scene(root, 800, 600);
			refuelingUpdateStage.setScene(scene);
			refuelingUpdateStage.setTitle("Tankolás szerkesztése");
			refuelingUpdateStage.setX(150);
			refuelingUpdateStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
