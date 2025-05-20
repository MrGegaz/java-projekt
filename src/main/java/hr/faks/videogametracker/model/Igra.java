package hr.faks.videogametracker.model;

public class Igra {
    private String naslovIgre;
    private String platforma;
    private String zanrIgre;
    private String datumIzlaska;
    // TODO:
    // brSatiIgranja;
    // ocjenaIgre;

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
}
