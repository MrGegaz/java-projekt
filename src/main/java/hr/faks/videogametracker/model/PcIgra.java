package hr.faks.videogametracker.model;

public class PcIgra extends Igra implements Igrivo {
    private String zahtjevi;
    private boolean imaDRM; // Ima li zaštitu poput Denuvo ili Steam itd.

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public PcIgra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska, boolean kupljena,
                  boolean digitalnaIgra, boolean instalirana, String zahtjevi, boolean imaDRM) {
        super(naslovIgre, platforma, zanrIgre, datumIzlaska, kupljena, digitalnaIgra, instalirana);
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

    @Override
    public void pokreniIgru() {
        // TODO: Implementirati logiku pokretanja igre na PC-u
    }

    @Override
    public void jeIgriva() {
        // TODO: Implementirati logiku provjere je li igra igriva na PC-u
    }
}
