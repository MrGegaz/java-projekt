package hr.faks.videogametracker.model;

public abstract class Igra {
    private String naslovIgre;
    private String platforma;
    private String zanrIgre;
    private String datumIzlaska;
    private boolean kupljena;
    private boolean instalirana;
    // TODO:
    // brSatiIgranja;
    // ocjenaIgre;

    /* ------------------------------ KONSTRUKTORI ------------------------------ */

    public Igra(String naslovIgre, String platforma, String zanrIgre, String datumIzlaska, boolean kupljena, boolean instalirana) {
        this.naslovIgre = naslovIgre;
        this.platforma = platforma;
        this.zanrIgre = zanrIgre;
        this.datumIzlaska = datumIzlaska;
        this.kupljena = kupljena;
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

    public void setKupljena(boolean kupljena) {
        this.kupljena = kupljena;
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

    public boolean isKupljena() {
        return kupljena;
    }

    public boolean isInstalirana() {
        return instalirana;
    }

    /* ------------------------------ METODE ------------------------------ */

    @Override
    public String toString() {
        return "Igra{" +
                "naslovIgre='" + naslovIgre + '\'' +
                ", platforma='" + platforma + '\'' +
                ", zanrIgre='" + zanrIgre + '\'' +
                ", datumIzlaska='" + datumIzlaska + '\'' +
                '}';
    }

    // TODO: Dodati još neke metode ako je potrebno
}
