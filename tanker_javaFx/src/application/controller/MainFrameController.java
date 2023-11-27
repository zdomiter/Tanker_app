package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.entity.Machine;
import application.entity.Refueling;
import application.entity.Tank;
import application.entity.TankCard;
import application.entity.TankReFill;
import application.util.FileHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class MainFrameController implements Initializable {

	private static List<Machine> machines = new ArrayList<>();
	private static List<TankCard> tankCards = new ArrayList<>();
	private static List<Refueling> refuelings = new ArrayList<>();
	private static List<TankReFill> tankReFills = new ArrayList<>();
	private static Tank tank;

	@FXML
	private BorderPane root;

	@FXML
	private ImageView menuImageView;

	@FXML
	private Button btnRefueling;

	@FXML
	private Button btnMachine;

	@FXML
	private Button btnTank;

	@FXML
	private Button btnCompany;

	@FXML
	private Button btnStatement;

	@FXML
	private Label lblHeader;

	@FXML
	private Label lblTankQuantity;

	@FXML
	private Label lblTankPercentage;

	@FXML
	private Label lblTankQuantityCheck;

	@FXML
	private Button btnTankLabel;

	public static List<Machine> getMachines() {
		return machines;
	}

	public static List<TankCard> getTankCards() {
		return tankCards;
	}

	public static List<Refueling> getRefuelings() {
		return refuelings;
	}

	public static List<TankReFill> getTankReFills() {
		return tankReFills;
	}

	public static Tank getTank() {
		return tank;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		readDataFromFile();
		loadTank();
		paneRefuelingInitialize();
		tankLabel();
	}

	public static void loadTank() {
		tank = new Tank(1200);
		for (TankReFill tankReFill : tankReFills) {
			if (!tankReFill.isDeleted()) {
				tank.fillTank(tankReFill.getQuantity(), tankReFill.getPrice());
			}
		}
		int tankId = tankCards.stream().filter(t -> t.getCompany().equals("Tartály")).findFirst().orElse(null).getId();

		for (Refueling refueling : refuelings) {
			if (!refueling.isDeleted() && refueling.getTankId() == tankId) {
				refueling.setFuelPrice(tank.refuelFromTank(refueling.getQuantity()));
				refueling.setAmount();
			}
		}
	}

	private void loadTankCheck() {
//		double amount = tankReFills.stream().filter(x -> !x.isDeleted())
//				.mapToDouble(x -> x.getQuantity() * x.getPrice()).sum();
		double allLoadQuantity = tankReFills.stream().filter(x -> !x.isDeleted()).mapToDouble(TankReFill::getQuantity)
				.sum();
//		double avgPrice = amount / allLoadQuantity;

		double allRefuelQuantity = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x->x.getTankId()==1).mapToDouble(Refueling::getQuantity)
				.sum();
		double tankQauntityLiter = allLoadQuantity - allRefuelQuantity;

		lblTankQuantityCheck.setText(tankQauntityLiter + "");
	}

	@FXML
	void tankLabel() {
		loadTankCheck();
		setTankLabels();
	}

	public void setTankLabels() {
		lblTankQuantity.setText(tank.getQuantityLiter() + "");
		lblTankPercentage.setText(tank.getCapacityPercentage() + "");

	}

	@FXML
	void handleClicks(ActionEvent e) {
		lblHeader.setText(btnRefueling.getText());
		if (e.getSource() == btnRefueling) {
			paneRefuelingInitialize();
		}
		if (e.getSource() == btnMachine) {
			paneMachineInitialize();
		}
		if (e.getSource() == btnCompany) {
			paneCompanyInitialize();
		}
		if (e.getSource() == btnTank) {
			paneTankInitialize();
		}
		if (e.getSource() == btnStatement) {
			paneStatementInitialize();
		}
	}

	private void paneStatementInitialize() {
		loadPaneByFxml("../pane/StatementPane.fxml");
		lblHeader.setText("Kimutatások");
		setMenuImage("../images/analytics.png");
	}

	private void paneTankInitialize() {
		loadPaneByFxml("../pane/TankPane.fxml");
		lblHeader.setText("Tartály");
		setMenuImage("../images/oil-barrel.png");
	}

	private void paneCompanyInitialize() {
		loadPaneByFxml("../pane/CompanyPane.fxml");
		lblHeader.setText("Benzinkutak");
		setMenuImage("../images/gas-station.png");
	}

	private void paneMachineInitialize() {
		loadPaneByFxml("../pane/MachinePane.fxml");
		lblHeader.setText("Járművek");
		setMenuImage("../images/excavator.png");
	}

	public void paneRefuelingInitialize() {
		loadPaneByFxml("../pane/RefuelingPane.fxml");
		lblHeader.setText("Tankolások");
		setMenuImage("../images/gas-pump.png");

	}

	private void setMenuImage(String imagePath) {
		Image image = new Image(getClass().getResourceAsStream(imagePath));
		menuImageView.setImage(image);

	}

	private void loadPaneByFxml(String paneFxmlPath) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(paneFxmlPath));
			Node Pane = loader.load();
			root.setCenter(Pane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readDataFromFile() {
		FileHandler fhObj = new FileHandler();
		machines = fhObj.readMachinesFromFile();
		tankCards = fhObj.readTankCardFromFile();
		refuelings = fhObj.readRefuelingsFromFile();
		tankReFills = fhObj.readTankReFillsFromFile();
	}

}
