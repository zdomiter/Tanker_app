package application.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableMachineData {

	private final IntegerProperty id;
    private final StringProperty licensePlate;
    private final StringProperty type;
    private final StringProperty privateVehicle;
    private final StringProperty hourlyConsumption;
    private final IntegerProperty startMileage;
    
	public TableMachineData(int id, String licensePlate, String type,
			String privateVehicle, String hourlyConsumption, int startMileage) {
		this.id = new SimpleIntegerProperty(id);
		this.licensePlate = new SimpleStringProperty(licensePlate);
		this.type = new SimpleStringProperty(type);
		this.privateVehicle = new SimpleStringProperty(privateVehicle);
		this.hourlyConsumption = new SimpleStringProperty(hourlyConsumption);
		this.startMileage = new SimpleIntegerProperty(startMileage);
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

	public IntegerProperty getStartMileage() {
		return startMileage;
	}
   
}
