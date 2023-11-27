package application.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import application.alert.AlertMessage;
import application.entity.TankCard;
import application.util.FileHandler;
import application.util.SearchUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CompanyUpdateFrameController implements Initializable{
	
	private List<TankCard> companies;
	private int companyId;
	private CompanyPaneController companyPaneController;
	private TankCard selectedCompany;
	private SearchUtil srcUtilObj = new SearchUtil();
	private AlertMessage alertMessage = new AlertMessage();
	
	@FXML
    private TextField tfName;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;
    
    @FXML
    private Button btnDelete;

    public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
    
    @FXML
    void deleteCompany(ActionEvent event) {
    	if (alertMessage.isConfirmedDelete()) {
			String todayStr = LocalDate.now().toString();
			companies.get(companyId -1).deleteTankCard(true, todayStr);
			FileHandler fhObj = new FileHandler();
			fhObj.writeTankCardToFile(companies);
			Stage stage = (Stage) btnDelete.getScene().getWindow();
			stage.close();
			companyPaneController.fillTableCompanyData();
    	}
    }

    @FXML
    void close(ActionEvent event) {
    	Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
    }

    @FXML
    void updateCompany(ActionEvent event) {
    	if (checkTextFieldFilled()) {
			try {
				companies.get(companyId -1).updateTankCard(tfName.getText());
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

	private boolean checkTextFieldFilled() {
		boolean filled = true;
		if (tfName.getText().isEmpty()) {
			filled = false;
		}
		if (!filled) {
			alertMessage.emptyTextFieldAlert();
		}
		return filled;
	}

	public void loadSelectedCompany() {
		selectedCompany = srcUtilObj.getTankCardById(companies, companyId);
		tfName.setText(selectedCompany.getCompany());
	}

	public void setCompanyPaneController(CompanyPaneController controller) {
		companyPaneController = controller;		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		companies = MainFrameController.getTankCards();
	}

}
