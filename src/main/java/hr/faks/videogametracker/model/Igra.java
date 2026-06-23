package hr.faks.videogametracker.model;

public abstract class Igra implements Igrivo {
    private Integer id;
    private String naslovIgre;
    private String platforma;
    private String zanrIgre;
    private String datumIzlaska;
    private boolean digitalnaIgra;
    private boolean instalirana;

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public Igra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska, boolean digitalnaIgra, boolean instalirana) {
        this.id = null; // Novo kreirana igra nema ID
        this.naslovIgre = naslovIgre;
        this.platforma = platforma;
        this.zanrIgre = zanrIgre;
        this.datumIzlaska = datumIzlaska;
        this.digitalnaIgra = digitalnaIgra;
        this.instalirana = instalirana;
    }

    /* ------------------------------ SETTERI ------------------------------ */

    public void setId(Integer id) {
        this.id = id;
    }

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

    public void setDigitalnaIgra(boolean digitalnaIgra) { this.digitalnaIgra = digitalnaIgra; }

    public void setInstalirana(boolean instalirana) {
        this.instalirana = instalirana;
    }

    /* ------------------------------ GETTERI ------------------------------ */

    public Integer getId() {
        return id;
    }

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

    @Override
    public void pokreniIgru() {}

    @Override
    public void zaustaviIgru() {}
}
