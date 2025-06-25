package hr.faks.videogametracker.model;

public abstract class Igra {
    private String naslovIgre;
    private String platforma;
    private String zanrIgre;
    private String datumIzlaska;
    private boolean digitalnaIgra; // Je li igra digitalna ili fizička
    private boolean instalirana;

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public Igra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska, boolean digitalnaIgra, boolean instalirana) {
        this.naslovIgre = naslovIgre;
        this.platforma = platforma;
        this.zanrIgre = zanrIgre;
        this.datumIzlaska = datumIzlaska;
        this.digitalnaIgra = digitalnaIgra;
        this.instalirana = instalirana;
    }

    /* ------------------------------ SETTERI ------------------------------ */

    public void setNaslovIgre(String naslovIgre) {
        this.naslovIgre = naslovIgre;
    }

    public void setPlatforma(String platforma) {
        this.platforma = platforma;
    }

    public void setZanrIgre(String zanrIgre) {
        this.zanrIgre = zanrIgre;
    }

    public void setDatumIzlaska(String datumIzlaska) {
        this.datumIzlaska = datumIzlaska;
    }

    public void setInstalirana(boolean instalirana) {
        this.instalirana = instalirana;
    }

    /* ------------------------------ GETTERI ------------------------------ */

    public String getNaslovIgre() {
        return naslovIgre;
    }

    public String getPlatforma() {
        return platforma;
    }

    public String getZanrIgre() {
        return zanrIgre;
    }

    public String getDatumIzlaska() {
        return datumIzlaska;
    }

    public boolean isDigitalnaIgra() { return digitalnaIgra; }

    public boolean isInstalirana() {
        return instalirana;
    }

    /* ------------------------------ METODE ------------------------------ */

    @Override
    public String toString() {
        return "Naslov igre: " + naslovIgre + '\n' +
                "Platforma: " + platforma + '\n' +
                "Žanr igre: " + zanrIgre + '\n' +
                "Datum izlaska: " + datumIzlaska + '\n' +
                "Digitalna kopija: " + digitalnaIgra + '\n' +
                "Instalirana: " + instalirana + '\n';
    }

    // TODO: Dodati još neke metode ako je potrebno
}
