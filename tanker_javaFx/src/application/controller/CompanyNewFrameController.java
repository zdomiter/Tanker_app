package application.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.alert.AlertMessage;
import application.entity.TankCard;
import application.util.FileHandler;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CompanyNewFrameController implements Initializable {

	private List<TankCard> companies;
	private AlertMessage alertMessage = new AlertMessage();
	private CompanyPaneController companyPaneController;

	@FXML
	private TextField tfName;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	public void setCompanyPaneController(CompanyPaneController controller) {
		companyPaneController = controller;
	}

	@FXML
	void close(ActionEvent event) {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	@FXML
	void saveNewCompany(ActionEvent event) {
		if (filledAllTextField()) {
			try {
				companies.add(new TankCard(companies.size() + 1, tfName.getText(), false, null));
				FileHandler fhObj = new FileHandler();
				fhObj.writeTankCardToFile(companies);
				Stage stage = (Stage) btnSave.getScene().getWindow();
				stage.close();
				companyPaneController.fillTableCompanyData();
			} catch (Exception e) {
				alertMessage.wrongFormatAlert();
			}
		}

	}

	private boolean filledAllTextField() {
		boolean filled = true;
		if (tfName.getText().isEmpty()) {
			filled = false;
			alertMessage.emptyTextFieldAlert();
		}
		return filled;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.companies = MainFrameController.getTankCards();

	}

}
