package application.controller;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import application.entity.Machine;
import application.entity.Refueling;
import application.entity.TableStatementData;
import application.entity.Tank;
import application.entity.TankReFill;
import application.pdf.PdfStatement;
import application.util.ChartTankStatement;
import application.util.SearchUtil;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.LedType;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.Gauge.ScaleDirection;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.LcdDesign;
import eu.hansolo.medusa.LcdFont;
import eu.hansolo.medusa.TickLabelLocation;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.TickMarkType;
import eu.hansolo.medusa.skins.LevelSkin;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StatementPaneController implements Initializable {

	private List<Machine> machines;
	private List<TankReFill> tankReFills;
	private List<Refueling> refuelings;
	private DecimalFormat df = new DecimalFormat("#,##0.00");
	private int tankFullCapcity;
	private Gauge gaugeStart;
	private Gauge gaugeEnd;

	@FXML
	private VBox paneStatement;

	@FXML
	private DatePicker dpStartDate;

	@FXML
	private DatePicker dpEndDate;

	@FXML
	private Button btnLoad;

	@FXML
	private Button btnDownload;

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
	private Label lblAdBlue;

	@FXML
	private VBox vBoxDashboardStart;

	@FXML
	private VBox vBoxDashboardEnd;

	@FXML
	private LineChart<String, Number> chartTank;

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
	private TableColumn<TableStatementData, String> amountColumn;

	@FXML
	void LoadStatement(ActionEvent event) {
		fillTableStatementDate();
		calculateStatementDatas();
		setChartTankLevel();
		setDasboardSkins();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.machines = MainFrameController.getMachines();
		this.refuelings = MainFrameController.getRefuelings();
		this.tankReFills = MainFrameController.getTankReFills();
		this.tankFullCapcity = Tank.getFullCapacity();
		setDatePickers();
		calculateStatementDatas();
		fillTableStatementDate();
		setChartTankLevel();
		createDasboardSkins();
		setDasboardSkins();

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
						machine.isHourlyConsumption() ? "üzemóra" : "km", calculateRefuelQuantity(id),
						calculateDistance(id), calculateAverageConsumption(id, machine.isHourlyConsumption()),
						df.format(calculateAmount(id)) + " Ft"));
			}
		}

		idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());

		licensePlateColumn.setCellValueFactory(cellData -> cellData.getValue().getLicensePlate());

		typeColumn.setCellValueFactory(cellData -> cellData.getValue().getType());

		privateVehicleColumn.setCellValueFactory(cellData -> cellData.getValue().isPrivateVehicle());

		hourlyConsumptionColumn.setCellValueFactory(cellData -> cellData.getValue().isHourlyConsumption());

		refuelQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().getRefuelQuantity().asObject());

		distanceColumn.setCellValueFactory(cellData -> cellData.getValue().getDistance().asObject());

		averageConsumptionColumn
				.setCellValueFactory(cellData -> cellData.getValue().getAverageConsumption().asObject());

		amountColumn.setCellValueFactory(cellData -> cellData.getValue().getAmountColumn());

		tableStatement.getColumns().addAll(idColumn, licensePlateColumn, typeColumn, privateVehicleColumn,
				hourlyConsumptionColumn, refuelQuantityColumn, distanceColumn, averageConsumptionColumn, amountColumn);

		tableStatement.setItems(data);
	}

	private double calculateAmount(int id) {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		return refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1))).filter(x -> x.getMachineId() == id)
				.mapToDouble(Refueling::getAmount).sum();
	}

	private double calculateAverageConsumption(int id, boolean isHourlyConsumption) {
		if (isFullFirstAndLastRefueling(id)) {
			double quantity = calculateRefuelQuantity(id);
			int distance = calculateDistance(id);
			if (distance != 0) {
				double avg = quantity / distance;
				if (!isHourlyConsumption) {
					avg = avg * 100;
				}
				return (double) (Math.round(avg * 100)) / 100;
			} else {
				return 0;
			}
		}else {
			return -1;
		}
		
	}
	
	private boolean isFullFirstAndLastRefueling(int id) {
		boolean isFull = true;
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		Refueling lastRefueling = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1)))
				.filter(x -> x.getMachineId() == id)
				.max(java.util.Comparator.comparingInt(Refueling::getMileage)).orElse(null);
		
		if (lastRefueling!=null && !lastRefueling.isFull()) {
			isFull = false;
		}
		Refueling preFirstRefueling = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isBefore(startDate))
				.filter(x -> x.getMachineId() == id)
				.max(java.util.Comparator.comparingInt(Refueling::getMileage)).orElse(null);
		if (preFirstRefueling!=null && !preFirstRefueling.isFull()) {
			isFull = false;
		}	
		return isFull;
	}
	
	private Refueling firstRefueling(int id) {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		return refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1)))
				.filter(x -> x.getMachineId() == id)
				.min(java.util.Comparator.comparingInt(Refueling::getMileage)).orElse(null);
	}

	private int calculateDistance(int id) {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		
		int startMileage = 0;
		if (firstRefueling(id)!=null) {
			SearchUtil srcObj = new SearchUtil();
			startMileage = srcObj.getPreviousMileage(refuelings, machines, firstRefueling(id));
		}		
		
		int endMileage = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1)))
				.filter(x -> x.getMachineId() == id)
				.mapToInt(Refueling::getMileage).max().orElse(0);
		return endMileage - startMileage;
	}

	private double calculateRefuelQuantity(int id) {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		return refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1))).filter(x -> x.getMachineId() == id)
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
		lblOpenQuantity.setText(df.format(getTankStartLevel()) + " liter");
		lblSumTankFill.setText(df.format(getTankReFills()) + " liter");
		lblSumRefuelings.setText(df.format(getAllRefuelins()) + " liter");
		lblCloseQuantity.setText(df.format(getTankEndLevel()) + " liter");
		lblChangeQuantity.setText(df.format(getTankChange()) + " liter");

		Tank tank = MainFrameController.getTank();
		lblCurrentQuantity.setText(df.format(tank.getQuantityLiter()) + " liter");
		lblAdBlue.setText(df.format(getAllAdBlueQauntity()) + " liter");
	}

	private double getAllAdBlueQauntity() {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		return refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1))).mapToDouble(Refueling::getAdBlue).sum();
	}

	private double getTankStartLevel() {
		LocalDate startDate = dpStartDate.getValue();
		double level = tankReFills.stream().filter(x -> !x.isDeleted()).filter(x -> x.getDate().isBefore(startDate))
				.mapToDouble(TankReFill::getQuantity).sum();
		double refuels = refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> x.getTankId() == 1)
				.filter(x -> x.getDate().isBefore(startDate)).mapToDouble(Refueling::getQuantity).sum();
		level -= refuels;
		return level;
	}

	private double getTankReFills() {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		double level = tankReFills.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1))).mapToDouble(TankReFill::getQuantity).sum();
		return level;
	}

	private double getAllRefuelins() {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		double refuels = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getTankId() == 1)
				.filter(x -> x.getDate().isAfter(startDate.minusDays(1)))
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1))).mapToDouble(Refueling::getQuantity).sum();
		return refuels;
	}

	private double getTankEndLevel() {
		LocalDate endDate = dpEndDate.getValue();
		double level = tankReFills.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1))).mapToDouble(TankReFill::getQuantity).sum();
		double refuels = refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> x.getTankId() == 1)
				.filter(x -> x.getDate().isBefore(endDate.plusDays(1))).mapToDouble(Refueling::getQuantity).sum();
		level -= refuels;
		return level;
	}

	private double getTankChange() {
		return getTankEndLevel() - getTankStartLevel();
	}

	private void setChartTankLevel() {
		try {
			chartTank.getData().clear();
			LocalDate startDate = dpStartDate.getValue();
			LocalDate endDate = dpEndDate.getValue();
			Map<LocalDate, Double> tankLevels = new ChartTankStatement(startDate, endDate).getTankLevelsForChart();

			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName("Tank");

			for (Map.Entry<LocalDate, Double> entry : tankLevels.entrySet()) {
				String day = (entry.getKey() + "").replace("-", "").substring(2, 8);
				series.getData().add(new XYChart.Data<>(day, entry.getValue()));
			}

			chartTank.getData().add(series);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void createDasboardSkins() {
		gaugeStart = GaugeBuilder.create().skinType(SkinType.DASHBOARD).prefSize(150, 150)
				.barColor(Color.rgb(243, 98, 45)).title("").unit("liter").unitColor(Color.BLACK).valueColor(Color.BLACK)
				.decimals(0).minValue(0).maxValue(tankFullCapcity).animated(true).animationDuration(500).build();
		vBoxDashboardStart.getChildren().add(gaugeStart);

		gaugeEnd = GaugeBuilder.create().skinType(SkinType.DASHBOARD).prefSize(150, 150)
				.barColor(Color.rgb(243, 98, 45)).title("").unit("liter").unitColor(Color.BLACK).valueColor(Color.BLACK)
				.decimals(0).minValue(0).maxValue(tankFullCapcity).animated(true).animationDuration(500).build();
		vBoxDashboardEnd.getChildren().add(gaugeEnd);
	}

	private void setDasboardSkins() {
		gaugeStart.setValue(getTankStartLevel());
		gaugeEnd.setValue(getTankEndLevel());
	}

	@FXML
	void downloadTable(ActionEvent event) {
		LocalDate startDate = dpStartDate.getValue();
		LocalDate endDate = dpEndDate.getValue();
		PdfStatement pdfsObj = new PdfStatement(startDate,endDate, getTankStartLevel());
		String fileName = startDate + " - " + endDate + " napi zárások";
		pdfsObj.createPdf(fileName);
	}
}
