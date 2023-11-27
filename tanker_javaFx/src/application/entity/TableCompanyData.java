package application.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableCompanyData {
	
	private final IntegerProperty id;
	private final StringProperty name;
	
	public TableCompanyData(int id, String name) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(name);
	}

	public IntegerProperty getId() {
		return id;
	}

	public StringProperty getName() {
		return name;
	}
	
	
	
	

}
