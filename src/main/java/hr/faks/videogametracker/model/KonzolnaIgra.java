package hr.faks.videogametracker.model;

public class KonzolnaIgra extends Igra implements Igrivo {
    private String platformaKonzole; // Platforma konzole (npr. PS5, Xbox Series X, Nintendo Switch itd.)

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public KonzolnaIgra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska, boolean kupljena,
                        boolean digitalnaIgra, boolean instalirana, String platformaKonzole) {
        super(naslovIgre, platforma, zanrIgre, datumIzlaska, kupljena, digitalnaIgra, instalirana);
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

    // TODO: Dodati override metode za toString() i ostale potrebne metode

    @Override
    public void pokreniIgru() {
        // TODO: Implementirati logiku pokretanja igre na konzoli
    }

    @Override
    public void jeIgriva() {
        // TODO: Implementirati logiku provjere je li igra igriva na konzoli
    }
}
