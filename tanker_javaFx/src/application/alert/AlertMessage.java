package application.alert;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertMessage {

	public void numberFormatAlert() {
		Alert a = new Alert(AlertType.NONE,"Ebbe a mezőbe csak számot lehet megadni!!!",ButtonType.OK);
		a.setTitle("Hibás adat!!!");
		a.show();
	}
	public void wrongFormatAlert() {
		Alert a = new Alert(AlertType.NONE,"Valamelyik mező hibásan lett kitöltve!!!",ButtonType.OK);
		a.setTitle("Hibás adat!!!");
		a.show();
	}
	
	public void emptyTextFieldAlert() {
		Alert a = new Alert(AlertType.NONE, "Nem lett minden kötelező mező kitöltve!", ButtonType.OK);
		a.setTitle("Hiányzó adat!!!");
		a.show();
	}
	
	public void emptyStartMileageTextFieldAlert() {
		Alert a = new Alert(AlertType.NONE, "Céges jármű esetén kötelező megadni a kezdő óraállást!", ButtonType.OK);
		a.setTitle("Hiányzó adat!!!");
		a.show();
	}
	
	public boolean isConfirmedDelete() {
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setTitle("Törlés");
		a.setHeaderText(null);
		a.setContentText("Biztosan törlöd?");
		Optional<ButtonType> action = a.showAndWait();
		return action.get() == ButtonType.OK ? true : false;
	}
}
