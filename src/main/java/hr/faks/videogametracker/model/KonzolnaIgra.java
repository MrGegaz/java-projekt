package hr.faks.videogametracker.model;

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

    // TODO: Dodati ostale potrebne metode

    public String toString() {
        return "Konzolna Igra {\n" +
                super.toString() +
                "Platforma konzole: " + platformaKonzole + '\n' +
                "}" + '\n';
    }

    @Override
    public void pokreniIgru() {
        // TODO: Implementirati logiku pokretanja igre na konzoli
    }

    @Override
    public void zaustaviIgru() {
        // TODO: Implementirati logiku zaustavljanja igre na konzoli
    }
}
