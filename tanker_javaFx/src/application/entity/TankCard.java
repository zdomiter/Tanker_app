package application.entity;
public class TankCard {

	private int id;
	private String company;
	private boolean deleted;
	private String delDate;

	public TankCard(int id, String company, boolean deleted, String delDate) {
		this.id = id;
		this.company = company;
		this.deleted = deleted;
		this.delDate = delDate;
	}

	public int getId() {
		return id;
	}

	public String getCompany() {
		return company;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public String getDelDate() {
		return delDate;
	}
	
	public void updateTankCard(String company) {
		this.company = company;
	}

	public void deleteTankCard(boolean deleted, String delDate) {
		this.deleted = deleted;
		this.delDate = delDate;
	}
	@Override
	public String toString() {
		return "Tank [id=" + id + ", company=" + company + ", deleted=" + deleted + ", delDate=" + delDate + "]";
	}

	public String getDataRowToFile() {
		return id + ";" + company + ";" + (deleted ? "1" : "0") + ";" + delDate + "\r\n";
	}
}
