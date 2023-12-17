package application.entity;

import java.util.LinkedList;

public class Tank {

	private LinkedList<TankReFill> tankReFills;
	private static int fullCapacity;

	public Tank(int fullCapacity) {
		this.tankReFills = new LinkedList<>();
		Tank.fullCapacity = fullCapacity;
	}

	public void fillTank(double quantity, double price) {
		TankReFill newTankReFill = new TankReFill(quantity, price);
		tankReFills.addLast(newTankReFill);
	}

	public double getQuantityLiter() {
		return tankReFills.stream().mapToDouble(x->x.getQuantity()).sum();
	}
	
	public static int getFullCapacity() {
		return fullCapacity;
	}
	
	public double getCapacityPercentage() {
	return getQuantityLiter()/fullCapacity*100;
}

	public double getPrice() {
		return tankReFills.getFirst().getPrice();	
	}

	public double refuelFromTank(double quantity) {
    	double restQuantity= quantity;
        double refuel = 0.0;

        while (restQuantity > 0 && !tankReFills.isEmpty()) {
            TankReFill firstTankReFill = tankReFills.getFirst();
            double firstTankReFillQuantity = firstTankReFill.getQuantity();
            double firstTankReFillPrice = firstTankReFill.getPrice();

            if (firstTankReFillQuantity <= restQuantity) {
                tankReFills.removeFirst();
                refuel += firstTankReFillQuantity * firstTankReFillPrice;
                restQuantity -= firstTankReFillQuantity;
            } else {
                firstTankReFill.setQuantity(firstTankReFillQuantity - restQuantity);
                refuel += restQuantity * firstTankReFillPrice;
                restQuantity = 0;
            }
        } 
        return refuel/quantity;
	}


}
