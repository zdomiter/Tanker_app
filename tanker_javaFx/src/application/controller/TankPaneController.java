package application.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import application.entity.TableTankData;
import application.entity.TankReFill;
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


public class TankPaneController  implements Initializable {
	
	private List<TankReFill> tankReFills;

    @FXML
    private VBox paneTank;

    @FXML
    private Button btnAdd;

    @FXML
    private TableView<TableTankData> tableTank;

    @FXML
    private TableColumn<TableTankData, Integer> idColumn;

    @FXML
    private TableColumn<TableTankData, LocalDate> dateColumn;

    @FXML
    private TableColumn<TableTankData, Double> quantityColumn;
    
    @FXML
    private TableColumn<TableTankData, Double> priceColumn;

    @FXML
    private TableColumn<TableTankData, String> companyColumn;

    @FXML
    private TableColumn<TableTankData, String> noteColumn;

    @FXML
    void tankFrameOpen(ActionEvent event) {
    	Stage newTankReFillStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../TankReFillNewFrame.fxml"));
			GridPane root = loader.load();
			TankReFillNewFrameController controller = loader.getController();
			controller.setTankPaneController(this);
			Scene scene = new Scene(root, 600, 350);
			newTankReFillStage.setScene(scene);
			newTankReFillStage.setTitle("Tartáy feltöltés hozzáadása");
			newTankReFillStage.setX(150);
			newTankReFillStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.tankReFills = MainFrameController.getTankReFills();
		fillTableTankData();
		tableTank.setRowFactory(tv -> {
			TableRow<TableTankData> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					TableTankData rowData = row.getItem();
					IntegerProperty selectedId = rowData.getId();
					openTankUpdateFrame(selectedId.getValue().intValue());
				}
			});
			return row;
		});
			
	}
	
	@SuppressWarnings("unchecked")
	public void fillTableTankData() {
		tableTank.getColumns().clear();
		tableTank.getItems().clear();

		ObservableList<TableTankData> data = FXCollections.observableArrayList();

		for (TankReFill tankReFill : tankReFills) {
			if (!tankReFill.isDeleted()) {
				data.add(new TableTankData(tankReFill.getId(), tankReFill.getDate(), tankReFill.getQuantity(),
						tankReFill.getPrice(), tankReFill.getCompany(), tankReFill.getNote()));
			}
		}

		idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());

		dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDate());

		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().getQuantity().asObject());
		
		priceColumn.setCellValueFactory(cellData -> cellData.getValue().getPrice().asObject());

		companyColumn.setCellValueFactory(cellData -> cellData.getValue().getCompany());

		noteColumn.setCellValueFactory(cellData -> cellData.getValue().getNote());
		
		tableTank.getColumns().addAll(idColumn, dateColumn, quantityColumn, priceColumn, companyColumn, noteColumn);

		tableTank.setItems(data);
		
		int lastIndex = tableTank.getItems().size()-1;
		tableTank.scrollTo(lastIndex);
	}

	private void openTankUpdateFrame(int id) {
		Stage tankReFillUpdateStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../TankReFillUpdateFrame.fxml"));
			GridPane root = loader.load();
			TankReFillUpdateFrameController controller = loader.getController();
			controller.setTankReFillId(id);
			controller.loadSelectedTankReFill();
			controller.setTankPaneController(this);
			Scene scene = new Scene(root, 600, 350);
			tankReFillUpdateStage.setScene(scene);
			tankReFillUpdateStage.setTitle("Tartáy feltöltés szerkesztése");
			tankReFillUpdateStage.setX(350);
			tankReFillUpdateStage.show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}


}
