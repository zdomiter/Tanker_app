package application.entity;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableStatementData {

	private final IntegerProperty id;
    private final StringProperty licensePlate;
    private final StringProperty type;
    private final StringProperty privateVehicle;
    private final StringProperty hourlyConsumption;
    private final DoubleProperty refuelQuantity;
    private final IntegerProperty distance;
    private final DoubleProperty averageConsumption;

    
	public TableStatementData(int id, String licensePlate, String type, String privateVehicle, 
			String hourlyConsumption, double refuelQuantity, int distance, double averageConsumption) {
		this.id = new SimpleIntegerProperty(id);
		this.licensePlate = new SimpleStringProperty(licensePlate);
		this.type = new SimpleStringProperty(type);
		this.privateVehicle = new SimpleStringProperty(privateVehicle);
		this.hourlyConsumption = new SimpleStringProperty(hourlyConsumption);
		this.refuelQuantity = new SimpleDoubleProperty(refuelQuantity);
		this.distance = new SimpleIntegerProperty(distance);
		this.averageConsumption = new SimpleDoubleProperty(averageConsumption);
	}


	public IntegerProperty getId() {
		return id;
	}


	public StringProperty getLicensePlate() {
		return licensePlate;
	}


	public StringProperty getType() {
		return type;
	}


	public StringProperty isPrivateVehicle() {
		return privateVehicle;
	}


	public StringProperty isHourlyConsumption() {
		return hourlyConsumption;
	}


	public DoubleProperty getRefuelQuantity() {
		return refuelQuantity;
	}


	public IntegerProperty getDistance() {
		return distance;
	}


	public DoubleProperty getAverageConsumption() {
		return averageConsumption;
	}


}
