package application.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.entity.Machine;
import application.entity.Refueling;
import application.entity.TankCard;
import application.entity.TankReFill;

public class FileHandler {

	private static String headerMachine = "";
	private static String headerTankCard = "";
	private static String headerTankReFill = "";
	private static String headerRefueling = "";


//	private static List<Machine> machines = new ArrayList<>();
//	private static List<TankCard> tankCards = new ArrayList<>();
//	private static List<TankReFill> tankReFills = new ArrayList<>();
//	private static List<Refueling> refuelings = new ArrayList<>();

	public List<Machine> readMachinesFromFile() {
		List<Machine> machines = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("data/Machines.csv"), "UTF-8"));
			headerMachine = br.readLine();
			while (br.ready()) {
				String[] rowData = br.readLine().split(";");
				boolean privateTemp = (rowData[4].equals("1"));
				boolean hourlyTemp = (rowData[5].equals("1"));
				boolean deleteTemp = (rowData[6].equals("1"));
				machines.add(new Machine(Integer.parseInt(rowData[0]), rowData[1], Integer.parseInt(rowData[2]),
						rowData[3], privateTemp, hourlyTemp, deleteTemp, rowData[7]));
			}
			br.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println("Nem megfelelő formátum!");
			;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return machines;
	}

	public List<TankCard> readTankCardFromFile() {
		List<TankCard> tankCards = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("data/TankCard.csv"), "UTF-8"));
			headerTankCard = br.readLine();
			while (br.ready()) {
				String[] rowData = br.readLine().split(";");
				boolean deletedTemp = rowData[2].equals("1");
				TankCard newTankCard = new TankCard(Integer.parseInt(rowData[0]), rowData[1], deletedTemp, rowData[3]);
				tankCards.add(newTankCard);
			}
			br.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println("Nem megfelelő formátum!");
			;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tankCards;
	}

	public List<TankReFill> readTankReFillsFromFile() {
		List<TankReFill> tankReFills = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("data/TankReFills.csv"), "UTF-8"));
			headerTankReFill = br.readLine();
			while (br.ready()) {
				String[] rowData = br.readLine().split(";");
				boolean deletedTemp = rowData[6].equals("1");
				TankReFill newtankReFill = new TankReFill(Integer.parseInt(rowData[0]), LocalDate.parse(rowData[1]),
						Double.parseDouble(rowData[2]), Double.parseDouble(rowData[3]), rowData[4], rowData[5],
						deletedTemp, rowData[7]);
				tankReFills.add(newtankReFill);
			}
			br.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println("Nem megfelelő formátum!");
			;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tankReFills;
	}

	public List<Refueling> readRefuelingsFromFile() {
		List<Refueling> refuelings = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("data/Refuelings.csv"), "UTF-8"));
			headerRefueling = br.readLine();
			while (br.ready()) {
				String[] rowData = br.readLine().split(";");
				boolean deletedTemp = rowData[11].equals("1");
				boolean fullTemp = rowData[9].equals("1");
				Refueling newReFueling = new Refueling(Integer.parseInt(rowData[0]), LocalDate.parse(rowData[1]),
						Integer.parseInt(rowData[2]), Integer.parseInt(rowData[3]), Double.parseDouble(rowData[4]),
						Integer.parseInt(rowData[5]), Integer.parseInt(rowData[6]), Double.parseDouble(rowData[7]),
						rowData[8], fullTemp, Double.parseDouble(rowData[10]), deletedTemp, rowData[12]);
				refuelings.add(newReFueling);
			}
			br.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println("Nem megfelelő formátum!");
			;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return refuelings;
	}

	public void writeMachinesToFile(List<Machine> list) {
		try {
			FileOutputStream fs = new FileOutputStream("data/Machines.csv");
			OutputStreamWriter out = new OutputStreamWriter(fs, "UTF-8");

			out.write(headerMachine);
			out.write("\r\n");
			for (Machine machine : list) {
				out.write(machine.getDataRowToFile());
			}
			out.close();
			fs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeTankCardToFile(List<TankCard> list) {
		try {
			FileOutputStream fs = new FileOutputStream("data/TankCard.csv");
			OutputStreamWriter out = new OutputStreamWriter(fs, "UTF-8");

			out.write(headerTankCard);
			out.write("\r\n");
			for (TankCard tankCard : list) {
				out.write(tankCard.getDataRowToFile());
			}
			out.close();
			fs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeTankReFillsToFile(List<TankReFill> list) {
		try {
			FileOutputStream fs = new FileOutputStream("data/TankReFills.csv");
			OutputStreamWriter out = new OutputStreamWriter(fs, "UTF-8");

			out.write(headerTankReFill);
			out.write("\r\n");
			for (TankReFill tankReFill : list) {
				out.write(tankReFill.getDataRowToFile());
			}
			out.close();
			fs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeRefuelingsToFile(List<Refueling> list) {
		try {
			FileOutputStream fs = new FileOutputStream("data/ReFuelings.csv");
			OutputStreamWriter out = new OutputStreamWriter(fs, "UTF-8");

			out.write(headerRefueling);
			out.write("\r\n");
			for (Refueling Refueling : list) {
				out.write(Refueling.getDataRowToFile());
			}
			out.close();
			fs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
