package application.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import application.alert.AlertMessage;
import application.controller.MainFrameController;
import application.entity.Machine;
import application.entity.Refueling;
import application.entity.TankReFill;
import application.util.SearchUtil;

public class PdfStatement {

	private List<Refueling> refuelings;
	private List<TankReFill> tankReFills;
	private List<Machine> machines;
	private SearchUtil srcObj = new SearchUtil();
	private DecimalFormat df = new DecimalFormat("#,##0.00");
	private DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy.MM.dd.");
	private LocalDate startDate;
	private LocalDate endDate;
	private double startLevel;

	public PdfStatement(LocalDate startDate, LocalDate endDate, double startLevel) {
		this.refuelings = MainFrameController.getRefuelings();
		this.tankReFills = MainFrameController.getTankReFills();
		this.machines = MainFrameController.getMachines();
		this.startDate = startDate;
		this.endDate = endDate;
		this.startLevel = startLevel;
	}

	public void createPdf(String fileName) {

		try {

			Document document = new Document();

			String home = System.getProperty("user.home");
			OutputStream outputStream = new FileOutputStream(new File(home + "/Downloads/" + fileName + ".pdf"));

			PdfWriter.getInstance(document, outputStream);

			document.open();
			
			Font titleFont = new Font(Font.HELVETICA, 20);
            Paragraph title = new Paragraph("NAPI ZÁRÁSOK", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(18f);
            document.add(title);
            
 
            Paragraph leftText = new Paragraph();
            leftText.add("Tartály kimutatás intervalluma: \n");
            leftText.add(startDate.format(datePattern) + " - " + endDate.format(datePattern));
            leftText.setSpacingAfter(18f);
            document.add(leftText);
            
			PdfPTable pdfPTable = new PdfPTable(new float[] {20,22,60,20,20});
			pdfPTable.setWidthPercentage(100);

			pdfPTable.addCell(createStyledCell("Dátum", Element.ALIGN_LEFT, true, 10, 1));
			pdfPTable.addCell(createStyledCell("Név", Element.ALIGN_CENTER, true, 10,1));
			pdfPTable.addCell(createStyledCell("Típus", Element.ALIGN_CENTER, true, 10,1));
			pdfPTable.addCell(createStyledCell("Menny.", Element.ALIGN_RIGHT, true, 10,1));
			pdfPTable.addCell(createStyledCell("Ár", Element.ALIGN_RIGHT, true, 10,1));
			
			pdfPTable.addCell(createStyledCell(startDate+"", Element.ALIGN_LEFT, true, 10,1));
			pdfPTable.addCell(createStyledCell("Nyitó mennyiség", Element.ALIGN_CENTER, false, 10,2));
			pdfPTable.addCell(createStyledCell(df.format(startLevel), Element.ALIGN_RIGHT, true, 10,1));
			pdfPTable.addCell(createStyledCell(" ", 1, false, 10, 1));

			pdfPTable.addCell(createStyledCell(" ", 1, false,10, pdfPTable.getNumberOfColumns()));

			double dateQuantity = startLevel;
			boolean emptyDate;
			for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
				emptyDate = true;
				for (TankReFill tankReFill : tankReFills) {
					if (tankReFill.getDate().equals(date)) {
						if (!tankReFill.isDeleted()) {
							pdfPTable.addCell(createStyledCell(tankReFill.getDate().format(datePattern), Element.ALIGN_LEFT,false, 10, 1));
							pdfPTable.addCell(createStyledCell("Tank feltöltés", Element.ALIGN_LEFT, false,10,1));
							pdfPTable.addCell(createStyledCell(tankReFill.getCompany(), Element.ALIGN_LEFT, false,10,1));
							pdfPTable.addCell(createStyledCell(df.format(tankReFill.getQuantity()), Element.ALIGN_RIGHT, false,10,1));
							pdfPTable.addCell(createStyledCell(df.format(tankReFill.getPrice()), Element.ALIGN_RIGHT, false, 10,1));	
							dateQuantity += tankReFill.getQuantity();
							emptyDate = false;
						}
					}
				}

				for (Refueling refueling : refuelings) {
					if (refueling.getDate().equals(date)) {
						if (!refueling.isDeleted() && refueling.getTankId()==1) {
							pdfPTable.addCell(createStyledCell(refueling.getDate().format(datePattern), Element.ALIGN_LEFT,false, 10, 1));
							pdfPTable.addCell(createStyledCell(
									srcObj.getMachineLicensePlateById(machines, refueling.getMachineId()), Element.ALIGN_LEFT,false, 10, 1));
							pdfPTable.addCell(createStyledCell(
									srcObj.getMachineTypeById(machines, refueling.getMachineId()), Element.ALIGN_LEFT,false, 10, 1));
							pdfPTable.addCell(createStyledCell(df.format(refueling.getQuantity()), Element.ALIGN_RIGHT, false,10,1));
							pdfPTable.addCell(createStyledCell(df.format(refueling.getFuelPrice()), Element.ALIGN_RIGHT, false,10,1));	
							dateQuantity -= refueling.getQuantity();
							emptyDate = false;
						}
					}
				}
				if (!emptyDate) {
					pdfPTable.addCell(createStyledCell(date.format(datePattern) + "", Element.ALIGN_LEFT, true,10,3));
					pdfPTable.addCell(createStyledCell(df.format(dateQuantity), Element.ALIGN_RIGHT, true,10,1));
					pdfPTable.addCell(new PdfPCell(new Paragraph("")));

					pdfPTable.addCell(createStyledCell(" ", 1, false,10, pdfPTable.getNumberOfColumns()));
				}

			}
			document.add(pdfPTable);

			document.close();
			outputStream.close();

			new AlertMessage().successDownload();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private PdfPCell createStyledCell(String content, int alignment, boolean bold, int size, int colspan) {
	    PdfPCell cell = new PdfPCell(new Paragraph(content, new Font(Font.HELVETICA, size)));
	    cell.setHorizontalAlignment(alignment);
	    cell.setColspan(colspan);

	    if (bold) {
	        cell.setPhrase(new Phrase(content, FontFactory.getFont(FontFactory.HELVETICA_BOLD, size)));
	    }

	    return cell;
	}

}