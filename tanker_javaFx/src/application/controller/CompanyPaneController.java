package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.entity.TableCompanyData;
import application.entity.TankCard;
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

public class CompanyPaneController implements Initializable {

	private List<TankCard> companies;

	@FXML
	private VBox paneCompany;

	@FXML
	private Button btnAdd;

	@FXML
	private TableView<TableCompanyData> tableCompany;

	@FXML
	private TableColumn<TableCompanyData, Integer> idColumn;

	@FXML
	private TableColumn<TableCompanyData, String> nameColumn;

	@FXML
	void companyNewFrameOpen(ActionEvent event) {
		Stage newCompanyStage = new Stage();
		try {
			GridPane root = (GridPane) FXMLLoader.load(getClass().getResource("../CompanyNewFrame.fxml"));
			Scene scene = new Scene(root, 600, 400);
			newCompanyStage.setScene(scene);
			newCompanyStage.setTitle("Üzemanyagtársaság hozzáadása");
			newCompanyStage.setX(350);
			newCompanyStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.companies = MainFrameController.getTankCards();
		fillTableCompanyData();
		tableCompany.setRowFactory(tv -> {
			TableRow<TableCompanyData> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					TableCompanyData rowData = row.getItem();
					IntegerProperty selectedId = rowData.getId();
					openCompanyUpdateFrame(selectedId.getValue().intValue());

				}
			});
			return row;
		});

	}

	private void openCompanyUpdateFrame(int id) {
		Stage companyUpdateStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../CompanyUpdateFrame.fxml"));
			GridPane root = loader.load();
			CompanyUpdateFrameController controller = loader.getController();
			controller.setCompanyId(id);
			controller.loadSelectedCompany();
			controller.setCompanyPaneController(this);
			Scene scene = new Scene(root, 600, 400);
			companyUpdateStage.setScene(scene);
			companyUpdateStage.setTitle("Üzemanyagcég szerkesztése");
			companyUpdateStage.setX(350);
			companyUpdateStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void fillTableCompanyData() {
		tableCompany.getColumns().clear();
		tableCompany.getItems().clear();

		ObservableList<TableCompanyData> data = FXCollections.observableArrayList();

		for (TankCard company : companies) {
			if (!company.isDeleted()) {
				data.add(new TableCompanyData(company.getId(), company.getCompany()));
			}
		}

		idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());

		nameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());

		tableCompany.getColumns().addAll(idColumn, nameColumn);

		tableCompany.setItems(data);

	}

}
