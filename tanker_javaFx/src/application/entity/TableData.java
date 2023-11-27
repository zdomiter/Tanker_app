package application.entity;

import java.time.LocalDate;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableData {

	private final IntegerProperty id;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty licensePlate;
    private final StringProperty type;
    private final StringProperty company;
    private final DoubleProperty quantity;
    private final IntegerProperty amount;
    private final IntegerProperty mileage;
    private final IntegerProperty distance;
    private final StringProperty avgFuelConsumption;
    private final StringProperty note;
    private final StringProperty full;
    private final DoubleProperty adBlue;
    
    

    public TableData(int id, LocalDate date, String licensePlate, String type, String company,
            double quantity, int amount, int mileage, int distance, String avgFuelConsumption, String note,
            String full, double adBlue) {
        this.id = new SimpleIntegerProperty(id);
        this.date = new SimpleObjectProperty<>(date);
        this.licensePlate = new SimpleStringProperty(licensePlate);
        this.type = new SimpleStringProperty(type);
        this.company = new SimpleStringProperty(company);
        this.quantity = new SimpleDoubleProperty(quantity);
        this.amount = new SimpleIntegerProperty(amount);
        this.mileage = new SimpleIntegerProperty(mileage);
        this.distance = new SimpleIntegerProperty(distance);
        this.avgFuelConsumption = new SimpleStringProperty(avgFuelConsumption);
        this.note = new SimpleStringProperty(note);
        this.full = new SimpleStringProperty(full);
        this.adBlue = new SimpleDoubleProperty(adBlue);
    }

	public IntegerProperty getId() {
		return id;
	}

	public ObjectProperty<LocalDate> getDate() {
		return date;
	}

	public StringProperty getLicensePlate() {
		return licensePlate;
	}

	public StringProperty getType() {
		return type;
	}

	public StringProperty getCompany() {
		return company;
	}

	public DoubleProperty getQuantity() {
		return quantity;
	}

	public IntegerProperty getAmount() {
		return amount;
	}

	public IntegerProperty getMileage() {
		return mileage;
	}

	public IntegerProperty getDistance() {
		return distance;
	}

	public StringProperty getAvgFuelConsumption() {
		return avgFuelConsumption;
	}

	public StringProperty getNote() {
		return note;
	}

	public StringProperty getFull() {
		return full;
	}
	
	public DoubleProperty getAdBlue() {
		return adBlue;
	}
}

