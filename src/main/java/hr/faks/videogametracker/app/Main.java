package hr.faks.videogametracker.app;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import hr.faks.videogametracker.model.PcIgra;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // ZA TESTIRANJE
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
        }
    }
}
