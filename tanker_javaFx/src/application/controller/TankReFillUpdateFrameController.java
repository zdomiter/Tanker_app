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
import application.entity.Refueling;
import application.entity.Tank;
import application.entity.TankReFill;
import application.util.FileHandler;
import application.util.SearchUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class TankReFillUpdateFrameController implements Initializable{

	private List<TankReFill> tankReFills;
	private List<Refueling> refuelings;
	private Tank tank;
	private int tankReFillId;
	private TankReFill selectedTankReFill;
	private SearchUtil srcUtilObj = new SearchUtil();
	private TankPaneController tankPaneController;
	private AlertMessage alertMessage = new AlertMessage();
	private DecimalFormat df = new DecimalFormat("#,###.##");

	@FXML
    private DatePicker dpDate;

    @FXML
    private TextField tfQuantity;

    @FXML
    private TextField tfPrice;

    @FXML
    private TextField tfCompany;

    @FXML
    private TextField tfNote;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;

	public void setTankPaneController(TankPaneController controller) {
		tankPaneController = controller;
	}
	
	public void setTankReFillId(int tankReFillId) {
		this.tankReFillId = tankReFillId;
	}
	
    @FXML
	void close(ActionEvent event) {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

    public void loadSelectedTankReFill() {
    	selectedTankReFill = srcUtilObj.getTankReFillById(tankReFills, tankReFillId);
    	dpDate.setValue(selectedTankReFill.getDate());
    	tfQuantity.setText(df.format(selectedTankReFill.getQuantity()));
    	tfPrice.setText(df.format(selectedTankReFill.getPrice()));
    	tfCompany.setText(selectedTankReFill.getCompany());
    	tfNote.setText(selectedTankReFill.getNote());
	}
    
    @FXML
    void delete(ActionEvent event) {
    	if (alertMessage.isConfirmedDelete()) {
    		String todayStr = LocalDate.now().toString();
    		tankReFills.get(tankReFillId-1).deleteTankReFill(true, todayStr);
    		FileHandler fhObj = new FileHandler();
    		fhObj.writeTankReFillsToFile(tankReFills);
    		tank.refuelFromTank(selectedTankReFill.getQuantity());
    		Stage stage = (Stage) btnDelete.getScene().getWindow();
			stage.close();
			tankPaneController.fillTableTankData();
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

    @FXML
    void saveUpdateTankReFill(ActionEvent event) {
    	if (checkTextFieldFilled()) {
			try {
				tankReFills.get(tankReFillId-1).updateTankReFill(dpDate.getValue(),
						doubleNumberFormater(tfQuantity.getText()),
						doubleNumberFormater(tfPrice.getText()),
						stringFormatter(tfCompany.getText()), 
						stringFormatter(tfNote.getText()));
				FileHandler fhObj = new FileHandler();
				fhObj.writeTankReFillsToFile(tankReFills);
				
				MainFrameController.loadTank();
				
				fhObj.writeRefuelingsToFile(refuelings);
				Stage stage = (Stage) btnSave.getScene().getWindow();
				stage.close();
				tankPaneController.fillTableTankData();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.tankReFills = MainFrameController.getTankReFills();
		this.refuelings = MainFrameController.getRefuelings();
		this.tank = MainFrameController.getTank();
	}

}
