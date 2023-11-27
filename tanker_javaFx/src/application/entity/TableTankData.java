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

public class TableTankData {
	
	private final IntegerProperty id;
    private final ObjectProperty<LocalDate> date;
    private final DoubleProperty quantity;
    private final DoubleProperty price;    
    private final StringProperty company;
    private final StringProperty note;
    
	public TableTankData(int id, LocalDate date, double quantity, double price,
			String company, String note) {
		this.id = new SimpleIntegerProperty(id);
		this.date = new SimpleObjectProperty<LocalDate>(date);
		this.quantity = new SimpleDoubleProperty(quantity);
		this.price = new SimpleDoubleProperty(price);
		this.company = new SimpleStringProperty(company);
		this.note = new SimpleStringProperty(note);
	}

	public IntegerProperty getId() {
		return id;
	}

	public ObjectProperty<LocalDate> getDate() {
		return date;
	}

	public DoubleProperty getQuantity() {
		return quantity;
	}
	public DoubleProperty getPrice() {
		return price;
	}

	public StringProperty getCompany() {
		return company;
	}

	public StringProperty getNote() {
		return note;
	}
    
    
}
