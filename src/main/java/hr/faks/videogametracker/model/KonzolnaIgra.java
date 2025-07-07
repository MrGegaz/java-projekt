package hr.faks.videogametracker.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class KonzolnaIgra extends Igra implements Igrivo {
    private String platformaKonzole; // Platforma konzole (npr. PS5, Xbox Series X, Nintendo Switch itd.)

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public KonzolnaIgra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska,
                        boolean digitalnaIgra, boolean instalirana, String platformaKonzole) {
        super(naslovIgre, platforma, zanrIgre, datumIzlaska, digitalnaIgra, instalirana);
        this.platformaKonzole = platformaKonzole;
    }

    /* ------------------------------ SETTERI ------------------------------ */

    public void setPlatformaKonzole(String platformaKonzole) {
        this.platformaKonzole = platformaKonzole;
    }

    /* ------------------------------ GETTERI ------------------------------ */

    public String getPlatformaKonzole() {
        return platformaKonzole;
    }

    /* ------------------------------ METODE ------------------------------ */

    public String toString() {
        return "Konzolna Igra {\n" +
                super.toString() +
                "Platforma konzole: " + platformaKonzole + '\n' +
                "}" + '\n';
    }

    @Override
    public void pokreniIgru() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Konzolna igra " + getNaslovIgre() + " je pokrenuta", ButtonType.OK);
        alert.setTitle("Pokretanje konzolne igre");
        alert.showAndWait();
    }

    @Override
    public void zaustaviIgru() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Konzolna igra " + getNaslovIgre() + " je zaustavljena", ButtonType.OK);
        alert.setTitle("Zaustavljanje konzolne igre");
        alert.showAndWait();
    }


}
