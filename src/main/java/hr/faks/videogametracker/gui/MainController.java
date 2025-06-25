package hr.faks.videogametracker.gui;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import hr.faks.videogametracker.model.PcIgra;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private ObservableList<Igra> listaIgara;

    @FXML
    private Button btnDodaj;

    @FXML
    private Button btnObrisi;

    @FXML
    private Button btnUredi;

    @FXML
    private TableColumn<Igra, String> colNaslov;

    @FXML
    private TableColumn<Igra, String> colPlatforma;

    @FXML
    private TableColumn<Igra, String> colZanr;

    @FXML
    private TableColumn<Igra, String> colGodina;

    @FXML
    private Label lblUkupno;

    @FXML
    private TableView<Igra> tableViewIgre;

    public void initialize() {
        // Spajanje stupaca s atributima
        colNaslov.setCellValueFactory(new PropertyValueFactory<>("naslovIgre"));
        colPlatforma.setCellValueFactory(new PropertyValueFactory<>("platforma"));
        colZanr.setCellValueFactory(new PropertyValueFactory<>("zanrIgre"));
        colGodina.setCellValueFactory(new PropertyValueFactory<>("datumIzlaska"));

        // Punjenje tablice podacima
        ucitajTestnePodatke();
        tableViewIgre.setItems(listaIgara);

        // Postavljanje broja igara u label
        azurirajBrojIgara();

        // Postavljanje event handlera za gumbe
        postaviEventHandlere();

        System.out.println("Broj igara: " + listaIgara.size());
    }

    private void postaviEventHandlere() {
        btnDodaj.setOnAction(event -> otvoriDodajIgruProzor());
        btnUredi.setOnAction(event -> urediIgru());
        btnObrisi.setOnAction(event -> obrisiIgru());
    }

    private void otvoriDodajIgruProzor() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/faks/videogametracker/fxml/dodaj-igru.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Dodaj novu igru");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            DodajIgruController controller = fxmlLoader.getController();
            controller.setMainController(this);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void urediIgru() {
        Igra odabranaIgra = tableViewIgre.getSelectionModel().getSelectedItem();
        if (odabranaIgra != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/faks/videogametracker/fxml/dodaj-igru.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setTitle("Uredi igru");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);

                DodajIgruController controller = fxmlLoader.getController();
                controller.setMainController(this);
                controller.postaviIgruZaUredivanje(odabranaIgra);

                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Nema odabrane igre, prikaži poruku
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Informacija");
            alert.setHeaderText("Nije odabrana igra");
            alert.setContentText("Molimo odaberite igru koju želite urediti.");
            alert.showAndWait();
        }
    }

    private void obrisiIgru() {
        Igra odabranaIgra = tableViewIgre.getSelectionModel().getSelectedItem();
        if (odabranaIgra != null) {
            listaIgara.remove(odabranaIgra);
            azurirajBrojIgara();
        }
    }

    public void dodajIgru(Igra igra) {
        listaIgara.add(igra);
        azurirajBrojIgara();
    }

    public void azurirajIgru() {
        // Obavijesti TableView da su podaci promijenjeni
        tableViewIgre.refresh();
    }

    private void azurirajBrojIgara() {
        lblUkupno.setText(String.valueOf(listaIgara.size()));
    }

    // Metoda sa podacima za testiranje
    private void ucitajTestnePodatke() {
        // Testni podaci
        listaIgara = FXCollections.observableArrayList();

        listaIgara.add(new PcIgra("Cyberpunk 2077", "PC", "RPG", "2020-12-10", true, true, true, "specifikacija", true));
        listaIgara.add(new PcIgra("The Witcher 3", "PC", "RPG", "2015-05-19", true, true, true, "specifikacija", false));
        listaIgara.add(new KonzolnaIgra("God of War", "Playstation", "Action", "2018-04-20", true, false, true, "PS4"));
        listaIgara.add(new KonzolnaIgra("Halo Infinite", "Xbox", "Shooter", "2021-12-08", true, false, true, "Xbox Series X"));
    }
}
