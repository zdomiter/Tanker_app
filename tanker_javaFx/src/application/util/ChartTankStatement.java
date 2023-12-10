package application.util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import application.controller.MainFrameController;
import application.entity.Refueling;
import application.entity.TankReFill;

public class ChartTankStatement {
	
	private static List<TankReFill> tankReFills = MainFrameController.getTankReFills();
	private static List<Refueling> refuelings = MainFrameController.getRefuelings();
	private LocalDate startDate;
	private LocalDate endDate;

	public ChartTankStatement(LocalDate startDate, LocalDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Map<LocalDate, Double> getTankLevelsForChart() {
		double startLevel = getTankStartLevel();
		
		Map<LocalDate, Double> tankLevels = new TreeMap<LocalDate, Double>();
		tankLevels.put(startDate.minusDays(1), startLevel);
		
		double level = startLevel;
		
		for (LocalDate	date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
			level += getLevelByDate(date);
			tankLevels.put(date, level);
		}
		return tankLevels;
	}
	
	private double getLevelByDate(LocalDate date) {
		double refills = tankReFills.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().equals(date))
				.mapToDouble(TankReFill::getQuantity).sum();
		double refuels = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getTankId()==1)
				.filter(x -> x.getDate().equals(date))
				.mapToDouble(Refueling::getQuantity).sum();
		return refills - refuels ;
	}

	private double getTankStartLevel() {
		double level = tankReFills.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getDate().isBefore(startDate))
				.mapToDouble(TankReFill::getQuantity).sum();
		double refuels = refuelings.stream().filter(x -> !x.isDeleted())
				.filter(x -> x.getTankId()==1)
				.filter(x -> x.getDate().isBefore(startDate))
				.mapToDouble(Refueling::getQuantity).sum();
		level -= refuels;
		return level;
	}

}
