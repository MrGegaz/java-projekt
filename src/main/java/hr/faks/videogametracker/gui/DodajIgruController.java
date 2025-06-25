package hr.faks.videogametracker.gui;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import hr.faks.videogametracker.model.PcIgra;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DodajIgruController {

    private MainController mainController;
    private Igra igraZaUredivanje; // Igra koja se uređuje (null ako je nova igra)
    private boolean uredivanjeMod = false;

    @FXML
    private Button btnSpremi;

    @FXML
    private Button btnOdustani;

    @FXML
    private ComboBox<String> cbPlatforma;

    @FXML
    private TextField tfNaslov;

    @FXML
    private DatePicker dpDatumIzlaska;

    @FXML
    private TextField tfZanr;

    public void initialize() {
        // Inicijalizacija platformi
        cbPlatforma.getItems().addAll("PC", "Playstation", "Xbox", "Nintendo Switch");

        // Postavljanje današnjeg datuma kao default
        dpDatumIzlaska.setValue(LocalDate.now());

        // Event handler za gumb spremi
        btnSpremi.setOnAction(event -> spremiIgru());

        // Event handler za gumb odustani
        btnOdustani.setOnAction(event -> zatvoriProzor());
    }

    /**
     * Postavlja kontroler u mod za uređivanje postojeće igre
     * @param igra Igra koja se uređuje
     */
    public void postaviIgruZaUredivanje(Igra igra) {
        if (igra != null) {
            this.igraZaUredivanje = igra;
            this.uredivanjeMod = true;

            // Popuni polja s podacima igre
            tfNaslov.setText(igra.getNaslovIgre());
            tfZanr.setText(igra.getZanrIgre());
            cbPlatforma.setValue(igra.getPlatforma());

            // Postavi datum
            try {
                LocalDate datum = LocalDate.parse(igra.getDatumIzlaska(), DateTimeFormatter.ISO_DATE);
                dpDatumIzlaska.setValue(datum);
            } catch (Exception e) {
                // U slučaju nepravilnog formata datuma, koristi današnji datum
                dpDatumIzlaska.setValue(LocalDate.now());
            }
        }
    }

    private void spremiIgru() {
        String naslov = tfNaslov.getText();
        String platforma = cbPlatforma.getValue();
        String zanr = tfZanr.getText();
        LocalDate datumIzlaska = dpDatumIzlaska.getValue();

        // Provjera jesu li sva polja popunjena
        if (!naslov.isEmpty() && platforma != null && !zanr.isEmpty() && datumIzlaska != null) {
            // Formatiranje datuma u željeni format (npr. YYYY-MM-DD)
            String formatiranDatum = datumIzlaska.format(DateTimeFormatter.ISO_DATE);

            if (uredivanjeMod && igraZaUredivanje != null) {
                // Ažuriranje postojeće igre
                igraZaUredivanje.setNaslovIgre(naslov);
                igraZaUredivanje.setPlatforma(platforma);
                igraZaUredivanje.setZanrIgre(zanr);
                igraZaUredivanje.setDatumIzlaska(formatiranDatum);

                if (mainController != null) {
                    mainController.azurirajIgru();
                    System.out.println("Ažurirana igra: " + naslov + " (datum izlaska: " + formatiranDatum + ")");
                }
            } else {
                // Stvaranje nove igre
                Igra novaIgra;

                if (platforma.equalsIgnoreCase("PC")) {
                    novaIgra = new PcIgra(naslov, platforma, zanr, formatiranDatum, true, true, true, "Standardne specifikacije", false);
                } else {
                    novaIgra = new KonzolnaIgra(naslov, platforma, zanr, formatiranDatum, true, false, true, platforma);
                }

                // Dodaj igru u listu u MainController-u
                if (mainController != null) {
                    mainController.dodajIgru(novaIgra);
                    System.out.println("Dodana nova igra: " + naslov + " (datum izlaska: " + formatiranDatum + ")");
                } else {
                    System.err.println("MainController nije postavljen!");
                }
            }

            // Zatvori prozor
            zatvoriProzor();
        } else {
            // Prikaži upozorenje ako nisu sva polja popunjena
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Upozorenje");
            alert.setHeaderText("Nepotpuni podaci");
            alert.setContentText("Molimo popunite sva polja!");
            alert.showAndWait();

            System.err.println("Sva polja moraju biti popunjena!");
        }
    }

    private void zatvoriProzor() {
        Stage stage = (Stage) btnSpremi.getScene().getWindow();
        stage.close();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
