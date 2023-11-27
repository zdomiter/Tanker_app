package application.util;

import java.util.List;

import application.entity.Machine;
import application.entity.Refueling;
import application.entity.TankCard;
import application.entity.TankReFill;

public class SearchUtil {

	public int getMachineIdByLicensePlate(List<Machine> machines, String plate) {
		Machine machine = machines.stream().filter(m -> m.getLicensePlate() == plate).findFirst().orElse(null);
		return machine.getId();
	}

	public int getTankCardIdByCompany(List<TankCard> tankCards, String company) {
		TankCard tankCard = tankCards.stream().filter(t -> t.getCompany() == company).findFirst().orElse(null);
		return tankCard.getId();
	}

	public String getMachineLicensePlateById(List<Machine> machines, int id) {
		Machine machine = machines.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
		return machine.getLicensePlate();
	}

	public String getTankCardCompanyById(List<TankCard> tankCards, int id) {
		TankCard tankCard = tankCards.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
		return tankCard.getCompany();
	}
	
	public TankCard getTankCardById(List<TankCard> tankCards, int id) {
		TankCard tankCard = tankCards.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
		return tankCard;
	}

	public int getLastMileageByLicensePlate(List<Refueling> refuelings, List<Machine> machines, String plate) {
		int machineId = getMachineIdByLicensePlate(machines, plate);
		return refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> x.getMachineId() == machineId)
				.mapToInt(Refueling::getMileage).max()
				.orElse(machines.stream().filter(x -> x.getId() == machineId).findFirst().get().getStartMileage());
	}

	public int getPreviousMileage(List<Refueling> refuelings, List<Machine> machines, Refueling currentRefueling) {
		int machineId = currentRefueling.getMachineId();
		int mileage = currentRefueling.getMileage();
		return refuelings.stream().filter(x -> !x.isDeleted()).filter(x -> x.getMachineId() == machineId).filter(x -> x.getMileage() < mileage)
				.mapToInt(Refueling::getMileage).max()
				.orElse(machines.stream().filter(x -> x.getId() == machineId).findFirst().get().getStartMileage());
	}
	
	
	public Refueling getRefuelingById(List<Refueling> refuelings, int id) {
		return refuelings.stream().filter(x -> x.getId() == id).findFirst().get();

	}
	
	public Machine getMachineById(List<Machine> machines, int id) {
		return machines.stream().filter(x -> x.getId() == id).findFirst().get();
	}
	
	public int calculateDistance(List<Refueling> refuelings, List<Machine> machines, Refueling lastRefueling) {
		int preMileage = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getMachineId() == lastRefueling.getMachineId())
				.filter(x -> x.getMileage() < lastRefueling.getMileage()).mapToInt(Refueling::getMileage).max()
				.orElse(machines.stream().filter(x -> x.getId() == lastRefueling.getMachineId()).findFirst().get()
						.getStartMileage());
		return lastRefueling.getMileage() - preMileage;
	}

	public TankReFill getTankReFillById(List<TankReFill> tankReFills, int id) {
		return tankReFills.stream().filter(x->x.getId()==id).findFirst().get();
	}
}
