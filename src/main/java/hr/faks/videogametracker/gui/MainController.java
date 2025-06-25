package hr.faks.videogametracker.gui;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import hr.faks.videogametracker.model.PcIgra;
import hr.faks.videogametracker.util.FileManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
    private ObservableList<Igra> listaIgara = FXCollections.observableArrayList();

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
        ucitajPodatke();
        tableViewIgre.setItems(listaIgara);

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
            // Nema odabrane igre za uredjivanje, prikaži poruku
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
            spremiPodatke();
        }
    }

    public void dodajIgru(Igra igra) {
        listaIgara.add(igra);
        azurirajBrojIgara();
        spremiPodatke();
    }

    public void azurirajIgru() {
        tableViewIgre.refresh();
        spremiPodatke();
    }

    private void azurirajBrojIgara() {
        lblUkupno.setText(String.valueOf(listaIgara.size()));
    }

    // Metoda za učitavanje podataka iz JSON datoteke
    private void ucitajPodatke() {
        // Učitavanje podataka u novom threadu
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (FileManager.postojeDatoteka()) {
                    try {
                        listaIgara = FileManager.ucitajIgre();
                        System.out.println("Podaci učitani iz JSON datoteke.");
                    } catch (IOException e) {
                        System.err.println("Greška pri učitavanju podataka: " + e.getMessage());
                        e.printStackTrace();
                        obrisiIPonovoKreirajDatoteku();
                    } catch (Exception e) {
                        System.err.println("Greška pri parsiranju JSON datoteke: " + e.getMessage());
                        System.out.println("Učitavam testne podatke umjesto neispravne JSON datoteke.");
                        obrisiIPonovoKreirajDatoteku();
                    }
                } else {
                    System.out.println("JSON datoteka ne postoji. Učitavam testne podatke.");
                    ucitajTestnePodatke();
                    // Nakon prvog pokretanja, spremi testne podatke
                    spremiPodatke();
                }

                if (listaIgara == null) {
                    listaIgara = FXCollections.observableArrayList();
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                tableViewIgre.setItems(listaIgara);
                azurirajBrojIgara();
            }

            @Override
            protected void failed() {
                super.failed();
                System.err.println("Greška pri učitavanju podataka: " + getException().getMessage());
                getException().printStackTrace();
            }
        };

        // Pokretanje task-a u novom threadu
        new Thread(task).start();
    }

    // Metoda koja briše postojeću datoteku i kreira novu s testnim podacima
    private void obrisiIPonovoKreirajDatoteku() {
        try {
            // Direktorij postoji?
            java.io.File direktorij = new java.io.File("data");
            if (!direktorij.exists()) {
                boolean kreirano = direktorij.mkdirs();
                if (kreirano) {
                    System.out.println("Direktorij data/ uspješno kreiran");
                } else {
                    System.err.println("Problem pri kreiranju data/ direktorija");
                }
            }

            // Brisanje postojeće datoteke
            java.io.File datoteka = new java.io.File("data/igre.json");
            if (datoteka.exists()) {
                // Zatvaranje svih veza s datotekom
                System.gc(); // Garbage collector

                boolean obrisano = datoteka.delete();
                if (obrisano) {
                    System.out.println("Neispravna datoteka uspješno obrisana.");
                } else {
                    System.err.println("Nije moguće obrisati neispravnu datoteku! Pokušavam alternativnu metodu...");
                    // Alternativna metoda brisanja
                    try {
                        java.nio.file.Files.delete(datoteka.toPath());
                        System.out.println("Neispravna datoteka obrisana alternativnom metodom.");
                    } catch (Exception ex) {
                        System.err.println("Ni alternativna metoda brisanja nije uspjela: " + ex.getMessage());
                    }
                }
            } else {
                System.out.println("Datoteka ne postoji, ništa za brisati.");
            }

            ucitajTestnePodatke();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Ignore
            }

            // Spremanje podataka u novu datoteku
            try {
                FileManager.spremiIgre(listaIgara);
                System.out.println("Nova datoteka s testnim podacima uspješno kreirana.");
            } catch (IOException e) {
                System.err.println("Problem pri kreiranju nove datoteke: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Greška pri regeneriranju datoteke: " + e.getMessage());
            e.printStackTrace();
            // Učitamo testne podatke u memoriju ako dođe do greške
            ucitajTestnePodatke();
        }
    }

    // Metoda za spremanje podataka u JSON datoteku
    private void spremiPodatke() {
        try {
            FileManager.spremiIgreDirektno(listaIgara);
            System.out.println("Podaci uspješno spremljeni.");
        } catch (IOException e) {
            System.err.println("Greška pri spremanju podataka: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metoda sa podacima za testiranje
    private void ucitajTestnePodatke() {
        listaIgara = FXCollections.observableArrayList();

        listaIgara.add(new PcIgra("Cyberpunk 2077", "PC", "RPG", "2020-12-10", true, true, true, "specifikacija", true));
        listaIgara.add(new PcIgra("The Witcher 3", "PC", "RPG", "2015-05-19", true, true, true, "specifikacija", false));
        listaIgara.add(new KonzolnaIgra("God of War", "Playstation", "Action", "2018-04-20", true, false, "PS4"));
        listaIgara.add(new KonzolnaIgra("Halo Infinite", "Xbox", "Shooter", "2021-12-08", true, false, "Xbox Series X"));
    }
}
