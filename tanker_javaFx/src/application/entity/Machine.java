package application.entity;
public class Machine {

	private int id;
	private String licensePlate;
	private int startMileage;
	private String type;
	private boolean privateVehicle;
	private boolean hourlyConsumption;
	private boolean deleted;
	private String delDate;



	public Machine(int id, String licensePlate, int startMileage, String type, boolean privateVehicle,
			boolean hourlyConsumption, boolean deleted, String delDate) {
		super();
		this.id = id;
		this.licensePlate = licensePlate;
		this.startMileage = startMileage;
		this.type = type;
		this.privateVehicle = privateVehicle;
		this.hourlyConsumption = hourlyConsumption;
		this.deleted = deleted;
		this.delDate = delDate;
	}

	public int getId() {
		return id;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public int getStartMileage() {
		return startMileage;
	}

	public String getType() {
		return type;
	}
	public boolean isPrivateVehicle() {
		return privateVehicle;
	}
	

	public boolean isHourlyConsumption() {
		return hourlyConsumption;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public String getDelDate() {
		return delDate;
	}

	public void updateMachine(String licensePlate, int startMileage, String type, boolean privateVehicle,
			boolean hourlyConsumption) {
		this.licensePlate = licensePlate;
		this.startMileage = startMileage;
		this.type = type;
		this.privateVehicle = privateVehicle;
		this.hourlyConsumption = hourlyConsumption;
	}
	
	public void deleteMachine(boolean deleted, String delDate) {
		this.deleted = deleted;
		this.delDate = delDate;
	}

	@Override
	public String toString() {
		return "Machine [id=" + id + ", licensePlate=" + licensePlate + ", startMileage=" + startMileage + ", type="
				+ type + ", privateVehicle=" + privateVehicle + ", hourlyConsumption=" + hourlyConsumption
				+ ", deleted=" + deleted + ", delDate=" + delDate + "]";
	}

	public String getDataRowToFile() {
		return id + ";" + licensePlate + ";" + startMileage + ";" + type + ";" + (privateVehicle ? "1" : "0") +";" + (hourlyConsumption ? "1" : "0") + ";"
				+ (deleted ? "1" : "0") + ";" + delDate + "\r\n";
	}

}