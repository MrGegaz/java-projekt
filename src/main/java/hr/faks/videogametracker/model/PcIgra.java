package hr.faks.videogametracker.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PcIgra extends Igra implements Igrivo {
    private String zahtjevi;
    private boolean imaDRM; // Ima li zaštitu poput Denuvo ili Steam itd.

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public PcIgra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska,
                  boolean digitalnaIgra, boolean instalirana, String zahtjevi, boolean imaDRM) {
        super(naslovIgre, platforma, zanrIgre, datumIzlaska, digitalnaIgra, instalirana);
        this.zahtjevi = zahtjevi;
        this.imaDRM = imaDRM;
    }

    /* ------------------------------ SETTERI ------------------------------ */

    public void setZahtjevi(String zahtjevi) {
        this.zahtjevi = zahtjevi;
    }

    public void setImaDRM(boolean imaDRM) {
        this.imaDRM = imaDRM;
    }

    /* ------------------------------ GETTERI ------------------------------ */

    public String getZahtjevi() {
        return zahtjevi;
    }

    public boolean isImaDRM() {
        return imaDRM;
    }

    /* ------------------------------ METODE ------------------------------ */

    public String toString() {
        return "PC Igra {\n" +
                super.toString() +
                "Zahtjevi: " + zahtjevi + '\n' +
                "DRM: " + imaDRM + '\n' +
                "}" + '\n';
    }

    @Override
    public void pokreniIgru() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pokretanje PC igre");
        alert.setHeaderText("PC igra " + getNaslovIgre() + " se pokreće");
        alert.setContentText(
            "Sistemski zahtjevi: " + zahtjevi + "\n" +
            "DRM zaštita: " + (imaDRM ? "Da" : "Ne")
        );
        alert.showAndWait();
    }

    @Override
    public void zaustaviIgru() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Zaustavljanje PC igre");
        alert.setHeaderText("PC igra " + getNaslovIgre() + " se zaustavlja");
        alert.showAndWait();
    }
}
