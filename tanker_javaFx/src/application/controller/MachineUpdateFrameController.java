package application.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import application.alert.AlertMessage;
import application.entity.Machine;
import application.util.FileHandler;
import application.util.SearchUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MachineUpdateFrameController implements Initializable {

	private List<Machine> machines;
	private int machineId;
	private Machine selectedMachine;
	private MachinePaneController machinePaneController;
	private AlertMessage alertMessage = new AlertMessage();
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField tfLicensePlate;

	@FXML
	private TextField tfType;

	@FXML
	private TextField tfStartMileage;

	@FXML
	private CheckBox cbPrivateVehicle;

	@FXML
	private CheckBox cbHourlyConsumption;

	@FXML
	private Label lblStartMileage;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	@FXML
	private Button btnDelete;

	public void setMachinePaneController(MachinePaneController controller) {
		machinePaneController = controller;
	}

	public void setMachineId(int machineId) {
		this.machineId = machineId;
	}

	public void loadSelectedMachine() {
		selectedMachine = new SearchUtil().getMachineById(machines, machineId);
		tfLicensePlate.setText(selectedMachine.getLicensePlate());
		tfType.setText(selectedMachine.getType());
		tfStartMileage.setText(selectedMachine.getStartMileage() + "");
		cbPrivateVehicle.setSelected(selectedMachine.isPrivateVehicle());
		cbHourlyConsumption.setSelected(selectedMachine.isHourlyConsumption());
		cbPrivateInitialize();
	}

	@FXML
	void close(ActionEvent event) {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	@FXML
	void deleteMachine(ActionEvent event) {
		String todayStr = LocalDate.now().toString();
		machines.get(machineId - 1).deleteMachine(true, todayStr);
		FileHandler fhObj = new FileHandler();
		fhObj.writeMachinesToFile(machines);
		Stage stage = (Stage) btnDelete.getScene().getWindow();
		stage.close();
		machinePaneController.fillTableMachineData();
	}

	@FXML
	void updateMachine(ActionEvent event) {
		if (filledAllTextField()) {
			machines.get(machineId - 1).updateMachine(stringFormatter(tfLicensePlate.getText()),
					Integer.parseInt(tfStartMileage.getText()), stringFormatter(tfType.getText()), cbPrivateVehicle.isSelected(),
					cbHourlyConsumption.isSelected());
			FileHandler fhObj = new FileHandler();
			fhObj.writeMachinesToFile(machines);
			Stage stage = (Stage) btnSave.getScene().getWindow();
			stage.close();
			machinePaneController.fillTableMachineData();
		}
	}

	private boolean filledAllTextField() {
		boolean filled = true;
		if (tfLicensePlate.getText().isEmpty() || tfType.getText().isEmpty()) {
			filled = false;
			alertMessage.emptyTextFieldAlert();
		} else if (!cbPrivateVehicle.isSelected() && tfStartMileage.getText().isEmpty()) {
			filled = false;
			alertMessage.emptyStartMileageTextFieldAlert();
		}
		return filled;
	}

	@FXML
	void selectPrivate(ActionEvent event) {
		cbPrivateInitialize();
	}

	private void cbPrivateInitialize() {
		if (cbPrivateVehicle.isSelected()) {
			lblStartMileage.setText("Kezdő óraállás:");
			tfStartMileage.setPromptText("Nem kötelező");
		} else {
			lblStartMileage.setText("Kezdő óraállás:*");
			tfStartMileage.setPromptText("pl.: 123456");
		}
	}

	@FXML
	void textChanged(KeyEvent event) {
		String character = event.getCharacter();
		if (!isValidInput(character)) {
			event.consume();
		}
	}

	private boolean isValidInput(String character) {
		return character.matches("[0-9]");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.machines = MainFrameController.getMachines();
	}
	
	private String stringFormatter(String text) {
		text = text.replace(";", ",");
		return text.equals("") ? "0" : text;
	}

}
