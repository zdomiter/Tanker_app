package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.entity.Machine;
import application.entity.TableMachineData;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MachinePaneController implements Initializable {
	
	private List<Machine> machines;

	@FXML
	private VBox paneMachine;

	@FXML
	private Button btnAdd;

	@FXML
	private TableView<TableMachineData> tableMachine;

	@FXML
	private TableColumn<TableMachineData, Integer> idColumn;

	@FXML
	private TableColumn<TableMachineData, String> licensePlateColumn;

	@FXML
	private TableColumn<TableMachineData, String> typeColumn;

	@FXML
	private TableColumn<TableMachineData, String> privateVehicleColumn;

	@FXML
	private TableColumn<TableMachineData, String> hourlyConsumptionColumn;

	@FXML
	private TableColumn<TableMachineData, Integer> startMileageColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.machines = MainFrameController.getMachines();
		fillTableMachineData();
		tableMachine.setRowFactory(tv -> {
			TableRow<TableMachineData> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					TableMachineData rowData = row.getItem();
					IntegerProperty selectedId = rowData.getId();
					openMachineUpdateFrame(selectedId.getValue().intValue());
				}
			});
			return row;
		});

	}

	@SuppressWarnings("unchecked")
	public void fillTableMachineData() {
		tableMachine.getColumns().clear();
		tableMachine.getItems().clear();

		ObservableList<TableMachineData> data = FXCollections.observableArrayList();

		for (Machine machine : machines) {
			if (!machine.isDeleted()) {
				data.add(new TableMachineData(machine.getId(), machine.getLicensePlate(), machine.getType(),
						machine.isPrivateVehicle()? "magán" : "céges", 
						machine.isHourlyConsumption()? "üzemóra" : "km",	
						machine.getStartMileage()));
			}
		}

		idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());

		licensePlateColumn.setCellValueFactory(cellData -> cellData.getValue().getLicensePlate());

		typeColumn.setCellValueFactory(cellData -> cellData.getValue().getType());

		privateVehicleColumn.setCellValueFactory(cellData -> cellData.getValue().isPrivateVehicle());

		hourlyConsumptionColumn.setCellValueFactory(cellData -> cellData.getValue().isHourlyConsumption());

		startMileageColumn.setCellValueFactory(cellData -> cellData.getValue().getStartMileage().asObject());

		tableMachine.getColumns().addAll(idColumn, licensePlateColumn, typeColumn, privateVehicleColumn,
				hourlyConsumptionColumn, startMileageColumn);

		tableMachine.setItems(data);

	}

	@FXML
	void machineFrameOpen(ActionEvent event) {
		Stage newMachineStage = new Stage();
		try {
			GridPane root = (GridPane) FXMLLoader.load(getClass().getResource("../MachineNewFrame.fxml"));
			Scene scene = new Scene(root, 600, 400);
			newMachineStage.setScene(scene);
			newMachineStage.setTitle("Jármű hozzáadása");
			newMachineStage.setX(350);
			newMachineStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void openMachineUpdateFrame(int id) {
		Stage machineUpdateStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../MachineUpdateFrame.fxml"));
			GridPane root = loader.load();
			MachineUpdateFrameController controller = loader.getController();
			controller.setMachineId(id);
			controller.loadSelectedMachine();
			controller.setMachinePaneController(this);
			Scene scene = new Scene(root, 600, 400);
			machineUpdateStage.setScene(scene);
			machineUpdateStage.setTitle("Jármű szerkesztése");
			machineUpdateStage.setX(350);
			machineUpdateStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
