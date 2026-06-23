package hr.faks.videogametracker.gui;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.util.DatabaseManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {
    private ObservableList<Igra> listaIgara = FXCollections.observableArrayList();
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
    private TableColumn<Igra, String> colInstalirana;

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
        colInstalirana.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isInstalirana() ? "Da" : "Ne"));

        // Punjenje tablice podacima
        ucitajPodatke();
        tableViewIgre.setItems(listaIgara);

        // Inicijalizacija combo boxova
        inicijalizirajFiltere();

        azurirajBrojIgara();

        // Postavljanje event handlera za gumbe
        postaviEventHandlere();

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
        Alert alert = new Alert(AlertType.CONFIRMATION);
        if (odabranaIgra != null) {
            alert.setTitle("Obriši igru");
            alert.setHeaderText("Igra: " + odabranaIgra.getNaslovIgre());
            alert.setContentText("Ovaj postupak se ne može poništiti naknadno!\n\nSigurno želite obrisati ovu igru?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Obriši iz baze podataka
                if (odabranaIgra.getId() != null) {
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

    // Metoda za učitavanje podataka iz baze podataka
    private void ucitajPodatke() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                dbManager = DatabaseManager.getInstance();
                if (dbManager.connect()) {
                    // Inicijaliziraj tablice u bazi ako ne postoje
                    dbManager.initializeDatabase();

                    // Učitaj podatke iz baze
                    ObservableList<Igra> igreIzBaze = dbManager.loadGames();
                    if (igreIzBaze != null) {
                        listaIgara = igreIzBaze;
                    }
                    System.out.println("Podaci uspješno učitani iz baze podataka.");
                } else {
                    System.err.println("Nije moguće povezati se s bazom podataka.");
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

                if (dbManager != null && dbManager.isConnected()) {
                    System.out.println("STATUS: Koristi se baza podataka");
                } else {
                    prikaziGreskuBaze();
                }
            }

            @Override
            protected void failed() {
                super.failed();
                System.err.println("Greška pri učitavanju podataka: " + getException().getMessage());
                prikaziGreskuBaze();
            }
        };

        new Thread(task).start();
    }

    // Prikazuje poruku korisniku da baza nije dostupna
    private void prikaziGreskuBaze() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Greška");
        alert.setHeaderText("Baza podataka nije dostupna");
        alert.setContentText("Nije moguće povezati se s bazom podataka. Tablica je prazna.");
        alert.showAndWait();
    }

    // Spremanje podataka u bazu podataka
    private void spremiPodatke() {
        try {
            if (dbManager != null && dbManager.isConnected()) {
                for (Igra igra : listaIgara) {
                    dbManager.saveOrUpdateGame(igra);
                }
                System.out.println("Podaci uspješno spremljeni u bazu podataka.");
            } else {
                System.err.println("Nije moguće spremiti podatke: baza podataka nije dostupna.");
            }
        } catch (Exception e) {
            System.err.println("Greška pri spremanju u bazu: " + e.getMessage());
        }
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
