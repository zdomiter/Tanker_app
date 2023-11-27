package application.controller;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import application.alert.AlertMessage;
import application.entity.Machine;
import application.entity.Refueling;
import application.entity.Tank;
import application.entity.TankCard;
import application.util.FileHandler;
import application.util.SearchUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RefuelingNewFrameController implements Initializable {

	private List<Machine> machines;
	private List<TankCard> tankCards;
	private List<Refueling> refuelings;
	private Tank tank;
	private RefuelingPaneController refuelingPaneController;
	private SearchUtil srcUtilObj = new SearchUtil();
	private AlertMessage alertMessage = new AlertMessage();
	private DecimalFormat df = new DecimalFormat("#,###.##");

	@FXML
	private TextField tfQuantity;

	@FXML
	private TextField tfNote;

	@FXML
	private TextField tfMileAge;

	@FXML
	private Label lblAmount;

	@FXML
	private TextField tfAdBlue;

	@FXML
	private TextField tfPrice;

	@FXML
	public ComboBox<String> cmbMachine;

	@FXML
	public ComboBox<String> cmbTankCard;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	@FXML
	private Button btnDelete;

	@FXML
	private Label lblCalculateDistance;

	@FXML
	private DatePicker dpDate;

	@FXML
	private Label lblCalculateAverageFuelConsumption;

	@FXML
	private CheckBox cbFull;

	public void setRefuelingPaneController(RefuelingPaneController controller) {
		refuelingPaneController = controller;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.machines = MainFrameController.getMachines();
		this.tankCards = MainFrameController.getTankCards();
		this.refuelings = MainFrameController.getRefuelings();
		this.tank = MainFrameController.getTank();
		comboBoxMachineFillData();
		comboBoxTankCardFillData();
		setPriceLabelText();
	}

	private void setPriceLabelText() {
		if (cmbTankCard.getValue().equals("Tartály")) {
			tfPrice.setText(df.format(tank.getPrice()));
			tfPrice.setEditable(false);
		}else {
			tfPrice.setEditable(true);
		}

	}

	public void comboBoxMachineFillData() {
		ObservableList<String> items = FXCollections.observableArrayList("Válassz!");
		for (Machine machine : machines) {
			items.add(machine.getLicensePlate());
		}
		cmbMachine.setItems(items);
		cmbMachine.getSelectionModel().select(0);

	}

	public void comboBoxTankCardFillData() {
		ObservableList<String> items = FXCollections.observableArrayList();
		for (TankCard tankCard : tankCards) {
			items.add(tankCard.getCompany());
			cmbTankCard.setItems(items);
			cmbTankCard.getSelectionModel().select(0);
		}
	}

	@FXML
	void textChanged(Event event) {
		calculateDistance();
		calculateAverageFuelConsumption();
		setPriceLabelText();
		setAmountLabelText();
	}

	private void setAmountLabelText() {
		if (!tfPrice.getText().isEmpty() && !tfQuantity.getText().isEmpty()) {
			double price = doubleNumberFormater(tfPrice.getText());
			double quantity = doubleNumberFormater(tfQuantity.getText());
			if (price > 0 && quantity > 0) {
				lblAmount.setText(df.format((int)price*quantity) + " Ft");
			}
		}
	}

	private void calculateDistance() {
		if (!cmbMachine.getValue().equals("Válassz!") && !tfMileAge.getText().equals("")) {
			int preMileage = srcUtilObj.getLastMileageByLicensePlate(refuelings, machines, cmbMachine.getValue());
			int currentMileage = Integer.parseInt(tfMileAge.getText());
			if (currentMileage > preMileage) {
				lblCalculateDistance.setText(currentMileage - preMileage + "");
			} else {
				lblCalculateDistance.setText("0");
			}
		}
	}

	private void calculateAverageFuelConsumption() {
		if (!lblCalculateDistance.getText().equals("0") && !tfQuantity.getText().isEmpty() && cbFull.isSelected()) {
			double distance = doubleNumberFormater(lblCalculateDistance.getText());
			double quantity = doubleNumberFormater(tfQuantity.getText());
			String averageFuel = df.format(quantity / (distance / 100));
			lblCalculateAverageFuelConsumption.setText(averageFuel);
		} else {
			lblCalculateAverageFuelConsumption.setText("0");
		}
	}

	private double doubleNumberFormater(String numberStr) {
		NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
		double d = 0;
		try {
			numberStr = numberStr.replaceAll(" ", "");
			Number number = format.parse(numberStr.trim());
			d = number.doubleValue();
		} catch (ParseException e) {
			alertMessage.numberFormatAlert();
		}
		return d;
	}

	@FXML
	private void close(ActionEvent event) {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void saveNewRefueling(ActionEvent event) {
		if (checkTextFieldFilled()) {
			try {
				int machineId = srcUtilObj.getMachineIdByLicensePlate(machines, cmbMachine.getValue());
				int tankCardId = srcUtilObj.getTankCardIdByCompany(tankCards, cmbTankCard.getValue());
				double price = tfPrice.getText().isEmpty() ? 0.0 : doubleNumberFormater(tfPrice.getText());
				double adBlue = tfAdBlue.getText().isEmpty() ? 0.0 : doubleNumberFormater(tfAdBlue.getText());
				double quantity = doubleNumberFormater(tfQuantity.getText());

				if (cmbTankCard.getValue().equals("Tartály")) {
					price = tank.refuelFromTank(doubleNumberFormater(tfQuantity.getText()));
				}
				int amount = (int) (price * quantity);
				
				refuelings.add(new Refueling(refuelings.size() + 1, dpDate.getValue(), machineId, tankCardId,
						quantity, amount, Integer.parseInt(tfMileAge.getText()),
						price, stringFormatter(tfNote.getText()), cbFull.isSelected(), adBlue, false, null));
				
				FileHandler fhObj = new FileHandler();
				fhObj.writeRefuelingsToFile(refuelings);
				
				Stage stage = (Stage) btnSave.getScene().getWindow();
				stage.close();
				
				refuelingPaneController.fillTableData();
			} catch (Exception e) {
				alertMessage.wrongFormatAlert();
			}
		}
	}

	private String stringFormatter(String text) {
		return text.equals("") ? "0" : text;
	}

	private boolean checkTextFieldFilled() {
		boolean filled = true;
		if (dpDate.getValue() == null) {
			filled = false;
		}
		if (cmbMachine.getValue().equals("Válassz!")) {
			filled = false;
		}
		if (tfQuantity.getText().isEmpty() || doubleNumberFormater(tfQuantity.getText()) <= 0) {
			filled = false;
		}
		if (!filled) {
			alertMessage.emptyTextFieldAlert();
		}
		return filled;
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

	@FXML
	void intTyped(KeyEvent event) {
		String character = event.getCharacter();
		if (!isValidIntInput(character)) {
			event.consume();
		}
	}

	private boolean isValidIntInput(String character) {
		return character.matches("[0-9]");
	}

}