package hr.faks.videogametracker.gui;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.PcIgra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainController {
    private ObservableList<Igra> listaIgara;

    // TODO: Implementirati logiku glavnog kontrolera GUI aplikacije
    /* ------------------------------ TABLE VIEW ------------------------------ */
    @FXML
    private Button btnDodaj;

    @FXML
    private Button btnObrisi;

    @FXML
    private Button btnUredi;

    @FXML
    private TableColumn<?, ?> colGodina;

    @FXML
    private TableColumn<?, ?> colNaslov;

    @FXML
    private TableColumn<?, ?> colPlatforma;

    @FXML
    private TableColumn<?, ?> colZanr;

    @FXML
    private Label lblUkupno;

    @FXML
    private TableView<?> tableViewIgre;

    public void initialize() {
        // Spajanje stupaca s atributima
        colNaziv.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("naslovIgre"));
        colPlatforma.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("platforma"));
        colZanr.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("zanrIgre"));
        colDatumIzlaska.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("datumIzlaska"));

        // Punjenje tablice podacima
        ucitajTestnePodatke();
        tableViewIgre.setItems(listaIgara);

        System.out.println("Broj igara: " + listaIgara.size());
    }

    // Metoda sa podacima za testiranje
    private void ucitajTestnePodatke() {
        // Testni podaci
        listaIgara = FXCollections.observableArrayList();

        listaIgara.add(new PcIgra("Cyberpunk 2077", "PC", "RPG", "2020-12-10", true, true, true, "specifikacija", true) {});
        listaIgara.add(new PcIgra("The Witcher 3", "PC", "RPG", "2015-05-19", true, true, true, "specifikacija", false) {});
        listaIgara.add(new KonzolnaIgra("God of War", "Playstation", "Action", "2018-04-20", true, false, true, "PS4") {});
        listaIgara.add(new KonzolnaIgra("Halo Infinite", "Xbox", "Shooter", "2021-12-08", true, false, true, "Xbox Series X") {});
    }
}
