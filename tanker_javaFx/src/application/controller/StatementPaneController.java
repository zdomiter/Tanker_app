package application.controller;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ResourceBundle;

import application.entity.Machine;
import application.entity.Refueling;
import application.entity.TableStatementData;
import application.entity.Tank;
import application.entity.TankReFill;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class StatementPaneController implements Initializable {

	private List<Machine> machines;
	private List<TankReFill> tankReFills;
	private List<Refueling> refuelings;
	private DecimalFormat df = new DecimalFormat("#,###.00");
	
	@FXML
	private VBox paneStatement;

	@FXML
	private DatePicker dpStartDate;

	@FXML
	private DatePicker dpEndDate;

	@FXML
	private Button btnLoad;

	@FXML
	private Label lblOpenQuantity;

	@FXML
	private Label lblSumTankFill;

	@FXML
	private Label lblSumRefuelings;

	@FXML
	private Label lblChangeQuantity;

	@FXML
	private Label lblCloseQuantity;

	@FXML
	private Label lblCurrentQuantity;

	@FXML
	private TableView<TableStatementData> tableStatement;

	@FXML
	private TableColumn<TableStatementData, Integer> idColumn;

	@FXML
	private TableColumn<TableStatementData, String> licensePlateColumn;

	@FXML
	private TableColumn<TableStatementData, String> typeColumn;

	@FXML
	private TableColumn<TableStatementData, String> privateVehicleColumn;

	@FXML
	private TableColumn<TableStatementData, String> hourlyConsumptionColumn;

	@FXML
	private TableColumn<TableStatementData, Double> refuelQuantityColumn;

	@FXML
	private TableColumn<TableStatementData, Integer> distanceColumn;

	@FXML
	private TableColumn<TableStatementData, Double> averageConsumptionColumn;

	@FXML
	void LoadStatement(ActionEvent event) {
		fillTableStatementDate();
		calculateStatementDatas();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.machines = MainFrameController.getMachines();
		this.refuelings = MainFrameController.getRefuelings();
		this.tankReFills = MainFrameController.getTankReFills();
		setDatePickers();
		calculateStatementDatas();
		fillTableStatementDate();
	}



	@SuppressWarnings("unchecked")
	public void fillTableStatementDate() {
		tableStatement.getColumns().clear();
		tableStatement.getItems().clear();

		ObservableList<TableStatementData> data = FXCollections.observableArrayList();

		for (Machine machine : machines) {
			if (!machine.isDeleted()) {
				int id = machine.getId();
				data.add(new TableStatementData(id, machine.getLicensePlate(), machine.getType(),
						machine.isPrivateVehicle() ? "magán" : "céges",
						machine.isHourlyConsumption() ? "üzemóra" : "km", calculateRefuelQuantity(id), calculateDistance(id),
						calculateAverageConsumption(id, machine.isHourlyConsumption())));
			}
		}

		idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());

		licensePlateColumn.setCellValueFactory(cellData -> cellData.getValue().getLicensePlate());

		typeColumn.setCellValueFactory(cellData -> cellData.getValue().getType());

		privateVehicleColumn.setCellValueFactory(cellData -> cellData.getValue().isPrivateVehicle());

		hourlyConsumptionColumn.setCellValueFactory(cellData -> cellData.getValue().isHourlyConsumption());
		
		refuelQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().getRefuelQuantity().asObject());
		
		distanceColumn.setCellValueFactory(cellData -> cellData.getValue().getDistance().asObject());

		averageConsumptionColumn.setCellValueFactory(cellData -> cellData.getValue().getAverageConsumption().asObject());
		
		tableStatement.getColumns().addAll(idColumn, licensePlateColumn, typeColumn, privateVehicleColumn,
				hourlyConsumptionColumn, refuelQuantityColumn, distanceColumn, averageConsumptionColumn);
		
		tableStatement.setItems(data);
	}

	private double calculateAverageConsumption(int id, boolean isHourlyConsumption) {
		double quantity = calculateRefuelQuantity(id);
		int distance = calculateDistance(id);
		if (distance!=0) {
			double avg = quantity/distance;
			if (!isHourlyConsumption) {
				avg = avg * 100;
			}			
			return (double)(Math.round(avg*100))/100;
		}else {
			return 0;
		}
	}

	private int calculateDistance(int id) {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		int startMileage = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x ->x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x ->x.getDate().isBefore(endDate.plusDays(1)))
				.filter(x ->x.getMachineId()==id)
				.mapToInt(Refueling::getMileage).min().orElse(0);
		int endMileage = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x ->x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x ->x.getDate().isBefore(endDate.plusDays(1)))
				.filter(x ->x.getMachineId()==id)
				.mapToInt(Refueling::getMileage).max().orElse(0);
		return endMileage - startMileage;
	}

	private double calculateRefuelQuantity(int id) {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		return refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x ->x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x ->x.getDate().isBefore(endDate.plusDays(1)))
				.filter(x ->x.getMachineId()==id)
				.mapToDouble(Refueling::getQuantity).sum();
	}

	private void setDatePickers() {
		LocalDate currentDate = LocalDate.now();

		LocalDate firstDayOfPreviousMonth = currentDate.minusMonths(1).withDayOfMonth(1);
		dpStartDate.setValue(firstDayOfPreviousMonth);
		
    	LocalDate lastDayOfPreviousMonth = YearMonth.from(dpStartDate.getValue()).atEndOfMonth();
		dpEndDate.setValue(lastDayOfPreviousMonth);

	}
	

    @FXML
    private void setDpEndValue(ActionEvent event) {
    	LocalDate lastDayOfPreviousMonth = YearMonth.from(dpStartDate.getValue()).atEndOfMonth();
		dpEndDate.setValue(lastDayOfPreviousMonth);
    }
    
	private void calculateStatementDatas() {
		lblOpenQuantity.setText(df.format(getTankStartLevel())+" liter");
		lblSumTankFill.setText(df.format(getTankReFills())+" liter");
		lblSumRefuelings.setText(df.format(getAllRefuelins())+" liter");
		lblCloseQuantity.setText(df.format(getTankEndLevel())+" liter");
		lblChangeQuantity.setText(df.format(getTankChange())+" liter");
		
		Tank tank = MainFrameController.getTank();
		lblCurrentQuantity.setText(df.format(tank.getQuantityLiter())+" liter");
	}
    



	private double getTankStartLevel() {
    	LocalDate startDate = dpStartDate.getValue();
		double level = tankReFills.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isBefore(startDate))
				.mapToDouble(TankReFill::getQuantity).sum();
		double refuels = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getTankId()==1)
				.filter(x -> x.getDate().isBefore(startDate))
				.mapToDouble(Refueling::getQuantity).sum();
		level -= refuels;
		return level;
	}
    
	private double getTankReFills() {
		LocalDate startDate = dpStartDate.getValue();
    	LocalDate endDate = dpEndDate.getValue();
		double level = tankReFills.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1)))
				.mapToDouble(TankReFill::getQuantity).sum();
		return level;
	}
	
    private double getAllRefuelins() {
    	LocalDate startDate = dpStartDate.getValue();
    	LocalDate endDate = dpEndDate.getValue();
    	double refuels = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1)))
				.mapToDouble(Refueling::getQuantity).sum();
		return refuels;
	}
    
	private double getTankEndLevel() {
		LocalDate endDate = dpEndDate.getValue();
		double level = tankReFills.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1)))
				.mapToDouble(TankReFill::getQuantity).sum();
		double refuels = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getTankId()==1)
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1)))
				.mapToDouble(Refueling::getQuantity).sum();
		level -= refuels;
		return level;
	}
	

	private double getTankChange() {
		return getTankEndLevel()-getTankStartLevel();
	}
}
