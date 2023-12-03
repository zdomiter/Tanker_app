package application.controller;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import application.alert.AlertMessage;
import application.entity.Machine;
import application.entity.Refueling;
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

public class RefuelingUpdateFrameController implements Initializable {

	private List<Machine> machines;
	private List<TankCard> tankCards;
	private List<Refueling> refuelings;
	private int refuelingId;
	private RefuelingPaneController refuelingPaneController;
	private Refueling selectedRefueling;
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

	public void setRefuelingId(int refuelingId) {
		this.refuelingId = refuelingId;
	}

	public void setRefuelingPaneController(RefuelingPaneController controller) {
		refuelingPaneController = controller;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		machines = MainFrameController.getMachines();
		tankCards = MainFrameController.getTankCards();
		refuelings = MainFrameController.getRefuelings();
		comboBoxMachineFillData();
		comboBoxTankCardFillData();
	}

	public void loadSelectedRefueling() {
		selectedRefueling = srcUtilObj.getRefuelingById(refuelings, refuelingId);
		dpDate.setValue(selectedRefueling.getDate());
		String licencePlate = srcUtilObj.getMachineLicensePlateById(machines, selectedRefueling.getMachineId());
		cmbMachine.setValue(licencePlate);
		String tankCard = srcUtilObj.getTankCardCompanyById(tankCards, selectedRefueling.getTankId());
		cmbTankCard.setValue(tankCard);
		tfMileAge.setText(selectedRefueling.getMileage() + "");
		tfAdBlue.setText(selectedRefueling.getAdBlue() + "");
		tfQuantity.setText(df.format(selectedRefueling.getQuantity()));
		tfPrice.setText(df.format(selectedRefueling.getFuelPrice()));
		lblAmount.setText((selectedRefueling.getAmount() + " Ft"));
		tfNote.setText(selectedRefueling.getNote());
		cbFull.setSelected(selectedRefueling.isFull());
		calculateDistance();
		calculateAverageFuelConsumption();
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
		setAmountLabelText();
	}
	
	private void setAmountLabelText() {
		double price = doubleNumberFormater(tfPrice.getText());
		double quantity = doubleNumberFormater(tfQuantity.getText());
		if (price > 0 && quantity > 0) {
			lblAmount.setText(df.format((int)price*quantity) + " Ft");
		}
	}

	private void calculateDistance() {
		if (!cmbMachine.getValue().equals("Válassz!") && !tfMileAge.getText().equals("")) {
			int preMileage = srcUtilObj.getPreviousMileage(refuelings, machines, selectedRefueling);
			int currentMileage = Integer.parseInt(tfMileAge.getText());
			if (currentMileage > preMileage) {
				lblCalculateDistance.setText(currentMileage - preMileage + "");
			} else {
				lblCalculateDistance.setText("0");
			}
		}
	}

	private void calculateAverageFuelConsumption() {
		if (!lblCalculateDistance.getText().equals("0") && !tfQuantity.getText().equals("") && cbFull.isSelected()) {
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
			Number number = format.parse(numberStr);
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
	private void deleteRefueling(ActionEvent event) {
		if (alertMessage.isConfirmedDelete()) {
			String todayStr = LocalDate.now().toString();
			refuelings.get(refuelingId - 1).deleteRefueling(true, todayStr);
			FileHandler fhObj = new FileHandler();
			fhObj.writeRefuelingsToFile(refuelings);
			MainFrameController.loadTank();
			Stage stage = (Stage) btnDelete.getScene().getWindow();
			stage.close();
			refuelingPaneController.fillTableData();
			MainFrameController.setGaugeLevelValue();
		}
	}

	@FXML
	public void updateRefueling(ActionEvent event) {
		if (checkTextFieldFilled()) {
			try {
				int machineId = srcUtilObj.getMachineIdByLicensePlate(machines, cmbMachine.getValue());
				int tankCardId = srcUtilObj.getTankCardIdByCompany(tankCards, cmbTankCard.getValue());
				double price = doubleNumberFormater(tfPrice.getText());
				double quantity = doubleNumberFormater(tfQuantity.getText());
				int amount = (int) (quantity * price);
				
				refuelings.get(refuelingId - 1).updateRefueling(dpDate.getValue(), machineId, tankCardId,
						quantity, amount, Integer.parseInt(tfMileAge.getText()), price,
						stringFormatter(tfNote.getText()), cbFull.isSelected(),
						doubleNumberFormater(tfAdBlue.getText()));
				
				MainFrameController.loadTank();
				
				FileHandler fhObj = new FileHandler();
				fhObj.writeRefuelingsToFile(refuelings);

				Stage stage = (Stage) btnSave.getScene().getWindow();
				stage.close();
				
				refuelingPaneController.fillTableData();
				MainFrameController.setGaugeLevelValue();
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
