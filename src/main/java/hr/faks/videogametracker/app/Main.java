package hr.faks.videogametracker.app;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import hr.faks.videogametracker.model.PcIgra;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/hr/faks/videogametracker/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        stage.setTitle("Video Game Tracker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

/*        // ZA TESTIRANJE
        List<Igra> igre = new ArrayList<>();

        PcIgra pcIgra1 = new PcIgra("Cyberpunk 2077", "PC", "RPG", "2020-12-10", true, true, true, "specc", true) {};

        PcIgra pcIgra2 = new PcIgra("The Witcher 3", "PC", "RPG", "2015-05-19", true, true, true, "specifikacija", false) {};

        KonzolnaIgra konzolnaIgra1 = new KonzolnaIgra("God of War", "Playstation", "Action", "2018-04-20", true, false, true, "PS4") {};

        igre.add(pcIgra1);
        igre.add(pcIgra2);
        igre.add(konzolnaIgra1);
        // Ispis svih igara
        for (Igra igra : igre) {
            System.out.println(igra);
        }*/
    }
}
