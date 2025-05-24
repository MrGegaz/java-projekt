package hr.faks.videogametracker.model;

public class PcIgra extends Igra{
    private String zahtjevi;
    private boolean imaDRM; // Ima li zaštitu poput Denuvo ili Steam itd.

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public PcIgra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska, boolean kupljena, boolean instalirana, String zahtjevi, boolean imaDRM) {
        super(naslovIgre, platforma, zanrIgre, datumIzlaska, kupljena, instalirana);
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

    // TODO: Dodati override metode za toString() i ostale potrebne metode
}
