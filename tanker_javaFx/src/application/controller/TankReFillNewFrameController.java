package application.controller;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import application.alert.AlertMessage;
import application.entity.Tank;
import application.entity.TankReFill;
import application.util.FileHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class TankReFillNewFrameController implements Initializable {

	private List<TankReFill> tankReFills;
	private Tank tank;
	private TankPaneController tankPaneController;
	private AlertMessage alertMessage = new AlertMessage();
	private DecimalFormat df = new DecimalFormat("#,###.##");

	@FXML
	private TextField tfQuantity;
	
	@FXML
	private TextField tfPrice;

	@FXML
	private TextField tfCompany;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	@FXML
	private DatePicker dpDate;

	@FXML
	private TextField tfNote;

	public void setTankPaneController(TankPaneController controller) {
		tankPaneController = controller;
	}

	@FXML
	void close(ActionEvent event) {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	@FXML
	void saveNewTankReFill(ActionEvent event) {
		if (checkTextFieldFilled()) {
			try {
				tankReFills.add(new TankReFill(tankReFills.size() + 1,
						dpDate.getValue(), doubleNumberFormater(tfQuantity.getText()),
						doubleNumberFormater(tfPrice.getText()), stringFormatter(tfCompany.getText()),
						stringFormatter(tfNote.getText()), false, null));
				FileHandler fhObj = new FileHandler();
				fhObj.writeTankReFillsToFile(tankReFills);
				tank.fillTank(doubleNumberFormater(tfQuantity.getText()), doubleNumberFormater(tfPrice.getText()));
				Stage stage = (Stage) btnSave.getScene().getWindow();
				stage.close();
				tankPaneController.fillTableTankData();
			} catch (Exception e) {
				alertMessage.wrongFormatAlert();
			}
		}
	}
	private boolean checkTextFieldFilled() {
		boolean filled = true;
		if (dpDate.getValue() == null) {
			filled = false;
		}
		if (tfQuantity.getText().isEmpty() || doubleNumberFormater(tfQuantity.getText())<=0) {
			filled = false;
		}
		if (!filled) {
			alertMessage.emptyTextFieldAlert();
		}
		return filled;
	}
	private String stringFormatter(String text) {
		return text.equals("") ? "0" : text;
	}
	
	private double doubleNumberFormater(String numberStr) {
		NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
		double d = 0;
		try {
			Number number = format.parse(numberStr);
			d = number.doubleValue();
		} catch (ParseException e) {
			alertMessage.numberFormatAlert();
		}
		return d;
	}
	
	@FXML
	void doubleTyped(KeyEvent event) {
		String character = event.getCharacter();
		if (!isValidInput(character)) {
			event.consume();
		}
	}

	private boolean isValidInput(String character) {
		return character.matches("[0-9,]");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.tankReFills = MainFrameController.getTankReFills();
		this.tank = MainFrameController.getTank();
	}

}
