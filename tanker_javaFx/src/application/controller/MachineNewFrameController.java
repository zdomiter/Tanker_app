package application.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.alert.AlertMessage;
import application.entity.Machine;
import application.util.FileHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class MachineNewFrameController implements Initializable{

	private List<Machine> machines;
	private AlertMessage alertMessage = new AlertMessage();
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
    private Button btnSave;

    @FXML
    private Button btnCancel;
    
    @FXML
    private Label lblStartMileage;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.machines = MainFrameController.getMachines();
		
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
	
	@FXML
    void selectPrivate(ActionEvent event) {
		if (cbPrivateVehicle.isSelected()) {
			lblStartMileage.setText("Kezdő óraállás:");
			tfStartMileage.setPromptText("Nem kötelező");
		}else {
			lblStartMileage.setText("Kezdő óraállás:*");
			tfStartMileage.setPromptText("pl.: 123456");
		}
    }
	
	@FXML
	private void close(ActionEvent event) {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

    @FXML
    void saveNewMachine(ActionEvent event) {
    	if (filledAllTextField()) {
			machines.add(new Machine(machines.size()+1, tfLicensePlate.getText(), 
					tfStartMileage.getText().isEmpty() ? 0 : Integer.parseInt(tfStartMileage.getText()), 
					tfType.getText(), cbPrivateVehicle.isSelected(), 
					cbHourlyConsumption.isSelected(), false, null));
			FileHandler fhObj = new FileHandler();
			fhObj.writeMachinesToFile(machines);
			Stage stage = (Stage) btnSave.getScene().getWindow();
			stage.close();
		}
    	
    	
    }

	private boolean filledAllTextField() {
		boolean filled = true;
		if (tfLicensePlate.getText().isEmpty() || tfType.getText().isEmpty()) {
			filled = false;
			alertMessage.emptyTextFieldAlert();
		}else if (!cbPrivateVehicle.isSelected() && tfStartMileage.getText().isEmpty()){
			filled = false;
			alertMessage.emptyStartMileageTextFieldAlert();
		}
		return filled;
	}


}

