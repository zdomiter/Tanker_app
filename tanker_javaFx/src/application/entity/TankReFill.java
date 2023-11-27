package application.entity;
import java.time.LocalDate;

public class TankReFill {
	
	private int id;
	private LocalDate date;
	private double quantity;
	private double price;
	private String company;
	private String note;
	private boolean deleted;
	private String delDate;
	
	
	
	public TankReFill(int id, LocalDate date, double quantity, double price,
			String company, String note, boolean deleted, String delDate) {
		this.id = id;
		this.date = date;
		this.quantity = quantity;
		this.price = price;
		this.company = company;
		this.note = note;
		this.deleted = deleted;
		this.delDate = delDate;
	}
	
	

	public TankReFill(double quantity, double price) {
		this.quantity = quantity;
		this.price = price;
	}



	public int getId() {
		return id;
	}

	public LocalDate getDate() {
		return date;
	}

	public double getQuantity() {
		return quantity;
	}

	public String getCompany() {
		return company;
	}
	
	public double getPrice() {
		return price;
	}

	public String getNote() {
		return note;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public String getDelDate() {
		return delDate;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}



	@Override
	public String toString() {
		return "TankReFill [id=" + id + ", date=" + date + ", quantity=" + quantity + ", price=" + price + ", company="
				+ company + ", note=" + note + ", deleted=" + deleted + ", delDate=" + delDate + "]";
	}
	
	

	public void updateTankReFill(LocalDate date, double quantity, double price, String company, String note) {
		this.date = date;
		this.quantity = quantity;
		this.price = price;
		this.company = company;
		this.note = note;
	}

	public String getDataRowToFile() {
		return id + ";" + date + ";" + quantity + ";" + price + ";" + company + ";"
				+ note + ";" + (deleted ? "1" : "0") + ";" + delDate + "\r\n";
	}

	public void deleteTankReFill(boolean deleted, String delDate) {
		this.deleted = deleted;
		this.delDate = delDate;		
	}
	
	
	
	
}