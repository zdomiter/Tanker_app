package application.entity;
import java.time.LocalDate;

public class Refueling {

	private int id;
	private LocalDate date;
	private int machineId;
	private int tankId;
	private double quantity;
	private int amount;
	private int mileage;
	private double fuelPrice;
	private String note;
	private boolean full;
	private double adBlue;
	private boolean deleted;
	private String delDate;
	
	

	public Refueling(int id, LocalDate date, int machineId, int tankId, double quantity, int amount, int mileage,
			double fuelPrice, String note, boolean full, double adBlue, boolean deleted, String delDate) {
		super();
		this.id = id;
		this.date = date;
		this.machineId = machineId;
		this.tankId = tankId;
		this.quantity = quantity;
		this.amount = amount;
		this.mileage = mileage;
		this.fuelPrice = fuelPrice;
		this.note = note;
		this.full = full;
		this.adBlue = adBlue;
		this.deleted = deleted;
		this.delDate = delDate;
	}


	public int getId() {
		return id;
	}

	public LocalDate getDate() {
		return date;
	}

	public int getMachineId() {
		return machineId;
	}

	public int getTankId() {
		return tankId;
	}

	public double getQuantity() {
		return quantity;
	}

	public int getAmount() {
		return amount;
	}

	
	public void setAmount() {
		this.amount = (int) (quantity * fuelPrice);
	}


	public int getMileage() {
		return mileage;
	}

	public double getFuelPrice() {
		return fuelPrice;
	}
	
	public void setFuelPrice(double fuelPrice) {
		this.fuelPrice = fuelPrice;
	}

	public String getNote() {
		return note;
	}
	
	public boolean isFull() {
		return full;
	}
	
	public double getAdBlue() {
		return adBlue;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public String getDelDate() {
		return delDate;
	}
	

	
	public void updateRefueling(LocalDate date, int machineId, int tankId, double quantity, int amount, int mileage,
			double fuelPrice, String note, boolean full, double adBlue) {
		this.date = date;
		this.machineId = machineId;
		this.tankId = tankId;
		this.quantity = quantity;
		this.amount = amount;
		this.mileage = mileage;
		this.fuelPrice = fuelPrice;
		this.note = note;
		this.full = full;
		this.adBlue = adBlue;
	}
	
	public void deleteRefueling(boolean deleted, String delDate) {
		this.deleted = deleted;
		this.delDate = delDate;
	}

	@Override
	public String toString() {
		return "Refueling [id=" + id + ", date=" + date + ", machineId=" + machineId + ", tankId=" + tankId
				+ ", quantity=" + quantity + ", amount=" + amount + ", mileage=" + mileage + ", fuelPrice=" + fuelPrice
				+ ", note=" + note + ", full=" + full + ", adBlue=" + adBlue + ", deleted=" + deleted + ", delDate="
				+ delDate + "]";
	}

	public String getDataRowToFile() {
		return id + ";" + date + ";" + machineId + ";" + tankId + ";" + quantity + ";" + amount + ";"
				+ mileage + ";" + fuelPrice + ";" + note + ";" + (full ? "1" : "0") + ";" + adBlue + ";" + (deleted ? "1" : "0") + ";"
				+ delDate + "\r\n";
	}

}
