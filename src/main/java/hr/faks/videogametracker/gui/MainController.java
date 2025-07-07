package hr.faks.videogametracker.gui;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import hr.faks.videogametracker.model.PcIgra;
import hr.faks.videogametracker.util.DatabaseManager;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MainController {
    private ObservableList<Igra> listaIgara = FXCollections.observableArrayList();
    private ObservableList<Igra> filtriranaListaIgara = FXCollections.observableArrayList();
    private boolean koristiBazu = false; // Prati koristimo li bazu ili JSON datoteku
    private DatabaseManager dbManager;

    @FXML
    private Button btnDodaj;

    @FXML
    private Button btnObrisi;

    @FXML
    private Button btnUredi;

    @FXML
    private Button btnPokreni;

    @FXML
    private Button btnFiltriraj;

    @FXML
    private Button btnResetiraj;

    @FXML
    private TextField txtPretraga;

    @FXML
    private ComboBox<String> cmbPlatforma;

    @FXML
    private ComboBox<String> cmbGodina;

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

        // Inicijalizacija combo boxova
        inicijalizirajFiltere();

        azurirajBrojIgara();

        // Postavljanje event handlera za gumbe
        postaviEventHandlere();

        System.out.println("Broj igara: " + listaIgara.size());

        btnPokreni.setVisible(false);

        tableViewIgre.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.isInstalirana()) {
                btnPokreni.setVisible(true);
            } else {
                btnPokreni.setVisible(false);
            }
        });

        // Dodaj listener za real-time pretragu kada se promijeni tekst u txtPretraga
        txtPretraga.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filtrirajIgre();
            }
        });
    }

    // Metoda za inicijalizaciju filter komponenti
    private void inicijalizirajFiltere() {
        Platform.runLater(() -> {
            // Dohvati sve jedinstvene platforme
            Set<String> platforme = listaIgara.stream()
                    .map(Igra::getPlatforma)
                    .collect(Collectors.toSet());

            // Dohvati sve jedinstvene godine iz datuma izlaska
            Set<String> godine = listaIgara.stream()
                    .map(igra -> igra.getDatumIzlaska().split("-")[0])
                    .collect(Collectors.toSet());

            // Dodaj "Sve platforme" na početak liste platformi
            List<String> listaPlatformi = new ArrayList<>(platforme);
            listaPlatformi.sort(String::compareTo);
            listaPlatformi.add(0, "Sve platforme");

            // Dodaj "Sve godine" na početak liste godina i sortiraj
            List<String> listaGodina = new ArrayList<>(godine);
            listaGodina.sort(String::compareTo);
            listaGodina.add(0, "Sve godine");
            listaGodina.add("Najstarije prvo");
            listaGodina.add("Najnovije prvo");

            // Postavi podatke u ComboBoxove
            cmbPlatforma.setItems(FXCollections.observableArrayList(listaPlatformi));
            cmbPlatforma.setValue("Sve platforme");

            cmbGodina.setItems(FXCollections.observableArrayList(listaGodina));
            cmbGodina.setValue("Sve godine");
        });
    }

    private void postaviEventHandlere() {
        btnDodaj.setOnAction(event -> otvoriDodajIgruProzor());
        btnUredi.setOnAction(event -> urediIgru());
        btnObrisi.setOnAction(event -> obrisiIgru());
        btnPokreni.setOnAction(event -> pokreniIgru());
        btnFiltriraj.setOnAction(event -> filtrirajIgre());
        btnResetiraj.setOnAction(event -> resetirajFilter());
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
            // Obriši iz baze podataka ako je to aktivni izvor podataka
            if (koristiBazu && odabranaIgra.getId() != null) {
                try {
                    if (dbManager != null && dbManager.isConnected()) {
                        dbManager.deleteGame(odabranaIgra.getId());
                    }
                } catch (Exception e) {
                    System.err.println("Greška pri brisanju iz baze: " + e.getMessage());
                }
            }

            // Obriši iz memorije
            listaIgara.remove(odabranaIgra);
            azurirajBrojIgara();

            // Spremi promjene (samo ako koristimo JSON)
            if (!koristiBazu) {
                spremiPodatke();
            }
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

    // Metoda za učitavanje podataka - prvo pokuša iz baze, a onda iz JSON datoteke
    private void ucitajPodatke() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Prvo pokušaj učitati podatke iz baze podataka
                dbManager = DatabaseManager.getInstance();
                if (dbManager.connect()) {
                    try {
                        // Inicijaliziraj tablice u bazi ako ne postoje
                        dbManager.initializeDatabase();

                        // Učitaj podatke iz baze
                        ObservableList<Igra> igreIzBaze = dbManager.loadGames();
                        if (igreIzBaze != null && !igreIzBaze.isEmpty()) {
                            listaIgara = igreIzBaze;
                            koristiBazu = true;
                            System.out.println("Podaci uspješno učitani iz baze podataka.");
                            return null;
                        } else {
                            System.out.println("Nema podataka u bazi. Pokušat ću učitati iz JSON datoteke.");
                            dbManager.disconnect();
                            koristiBazu = false;
                        }
                    } catch (Exception e) {
                        System.err.println("Problem s bazom podataka: " + e.getMessage());
                        e.printStackTrace();
                        // Prekini vezu s bazom ako je uspostavljena
                        dbManager.disconnect();
                        koristiBazu = false;
                    }
                } else {
                    System.out.println("Nije moguće povezati se s bazom. Učitavam podatke iz JSON datoteke.");
                    koristiBazu = false;
                }

                // Ako nismo uspjeli učitati iz baze, idemo koristiti JSON datoteku
                if (!koristiBazu) {
                    if (FileManager.postojeDatoteka()) {
                        try {
                            listaIgara = FileManager.ucitajIgre();
                            System.out.println("Podaci učitani iz JSON datoteke.");
                        } catch (IOException e) {
                            System.err.println("Greška pri učitavanju podataka iz JSON datoteke: " + e.getMessage());
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

                // Dodaj statusnu poruku o tome odakle su podaci učitani
                if (koristiBazu) {
                    System.out.println("STATUS: Koristi se baza podataka");
                } else {
                    System.out.println("STATUS: Koristi se JSON datoteka");
                }
            }

            @Override
            protected void failed() {
                super.failed();
                System.err.println("Greška pri učitavanju podataka: " + getException().getMessage());
                getException().printStackTrace();
            }
        };

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

    // Metoda za spremanje podataka ovisno o izvoru podataka (baza ili JSON)
    private void spremiPodatke() {
        if (koristiBazu) {
            try {
                if (dbManager != null && dbManager.isConnected()) {
                    for (Igra igra : listaIgara) {
                        dbManager.saveOrUpdateGame(igra);
                    }
                    System.out.println("Podaci uspješno spremljeni u bazu podataka.");
                } else {
                    // Ako je došlo do problema s bazom, prebaci se na JSON
                    System.out.println("Nije moguće spremiti u bazu. Spremam u JSON datoteku.");
                    FileManager.spremiIgreDirektno(listaIgara);
                    koristiBazu = false;
                }
            } catch (Exception e) {
                System.err.println("Greška pri spremanju u bazu: " + e.getMessage());
                // Ako spremanje u bazu ne uspije, pokušaj spremiti u JSON
                try {
                    FileManager.spremiIgreDirektno(listaIgara);
                    System.out.println("Podaci spremljeni u JSON datoteku kao rezervna opcija.");
                    koristiBazu = false;
                } catch (IOException ioe) {
                    System.err.println("Nije moguće spremiti ni u JSON: " + ioe.getMessage());
                }
            }
        } else {
            // Spremanje u JSON datoteku
            try {
                FileManager.spremiIgreDirektno(listaIgara);
                System.out.println("Podaci uspješno spremljeni u JSON datoteku.");
            } catch (IOException e) {
                System.err.println("Greška pri spremanju podataka u JSON: " + e.getMessage());
                e.printStackTrace();
            }
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

    @FXML
    private void pokreniIgru() {
        Igra odabranaIgra = tableViewIgre.getSelectionModel().getSelectedItem();
        if (odabranaIgra != null && odabranaIgra.isInstalirana()) {
            // Pokretanje igre pozivom metode iz interface-a Igrivo
            odabranaIgra.pokreniIgru();

            // Kreiranje novog prozora za zaustavljanje igre
            Stage gameControlStage = new Stage();
            gameControlStage.setTitle("Igra pokrenuta");

            Button stopButton = new Button("Zaustavi igru");
            stopButton.setOnAction(e -> {
                odabranaIgra.zaustaviIgru();
                gameControlStage.close();
            });

            // Kreiranje layout-a za prozor
            javafx.scene.layout.VBox layout = new javafx.scene.layout.VBox(10);
            layout.setPadding(new javafx.geometry.Insets(20));
            layout.setAlignment(javafx.geometry.Pos.CENTER);

            Label gameRunningLabel = new Label("Igra " + odabranaIgra.getNaslovIgre() + " je pokrenuta!");
            layout.getChildren().addAll(gameRunningLabel, stopButton);

            Scene scene = new Scene(layout, 300, 150);
            gameControlStage.setScene(scene);
            gameControlStage.initModality(Modality.WINDOW_MODAL);
            gameControlStage.initOwner(btnPokreni.getScene().getWindow());
            gameControlStage.show();
        }
    }

    private void filtrirajIgre() {
        String pretragaTekst = txtPretraga.getText().toLowerCase();
        String odabranaPlatforma = cmbPlatforma.getValue();
        String odabranaGodina = cmbGodina.getValue();

        // Korištenje Stream API-ja i lambda izraza za filtriranje igara
        ObservableList<Igra> rezultat = listaIgara.stream()
            .filter(igra -> {
                // Filtriranje po naslovu ako je unesen tekst za pretragu
                if (pretragaTekst != null && !pretragaTekst.isEmpty()) {
                    return igra.getNaslovIgre().toLowerCase().contains(pretragaTekst);
                }
                return true;
            })
            .filter(igra -> {
                // Filtriranje po platformi ako je odabrana specifična platforma
                if (odabranaPlatforma != null && !odabranaPlatforma.equals("Sve platforme")) {
                    return igra.getPlatforma().equals(odabranaPlatforma);
                }
                return true;
            })
            .filter(igra -> {
                // Filtriranje po godini ako je odabrana specifična godina
                if (odabranaGodina != null) {
                    if (odabranaGodina.equals("Najstarije prvo")) {
                        return true;
                    } else if (odabranaGodina.equals("Najnovije prvo")) {
                        return true;
                    } else if (!odabranaGodina.equals("Sve godine")) {
                        // Izdvajanje godine iz datuma (format: "yyyy-MM-dd")
                        String godinaIzlaska = igra.getDatumIzlaska().split("-")[0];
                        return godinaIzlaska.equals(odabranaGodina);
                    }
                }
                return true;
            })
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Sortiranje rezultata ako je odabrana opcija za sortiranje
        if (odabranaGodina != null) {
            if (odabranaGodina.equals("Najstarije prvo")) {
                rezultat.sort(Comparator.comparing(igra -> igra.getDatumIzlaska()));
            } else if (odabranaGodina.equals("Najnovije prvo")) {
                rezultat.sort((igra1, igra2) -> igra2.getDatumIzlaska().compareTo(igra1.getDatumIzlaska()));
            }
        }

        tableViewIgre.setItems(rezultat);

        lblUkupno.setText(String.valueOf(rezultat.size()));
    }

    private void resetirajFilter() {
        txtPretraga.clear();
        cmbPlatforma.setValue("Sve platforme");
        cmbGodina.setValue("Sve godine");

        tableViewIgre.setItems(listaIgara);
        azurirajBrojIgara();
    }
}
