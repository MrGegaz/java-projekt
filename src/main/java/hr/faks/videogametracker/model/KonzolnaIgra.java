package hr.faks.videogametracker.model;

public class KonzolnaIgra extends Igra {
    private String platformaKonzole; // Platforma konzole (npr. PS5, Xbox Series X, Nintendo Switch itd.)
    private boolean digitalnaIgra; // Je li igra digitalna ili fizička

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public KonzolnaIgra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska, boolean kupljena, boolean instalirana) {
        super(naslovIgre, platforma, zanrIgre, datumIzlaska, kupljena, instalirana);
    }

    /* ------------------------------ SETTERI ------------------------------ */

    public void setPlatformaKonzole(String platformaKonzole) {
        this.platformaKonzole = platformaKonzole;
    }

    public void setDigitalnaIgra(boolean digitalnaIgra) {
        this.digitalnaIgra = digitalnaIgra;
    }

    /* ------------------------------ GETTERI ------------------------------ */

    public String getPlatformaKonzole() {
        return platformaKonzole;
    }

    public boolean isDigitalnaIgra() {
        return digitalnaIgra;
    }

    /* ------------------------------ METODE ------------------------------ */

    // TODO: Dodati override metode za toString() i ostale potrebne metode
}
