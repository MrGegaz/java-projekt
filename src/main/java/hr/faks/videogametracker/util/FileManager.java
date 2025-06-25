package hr.faks.videogametracker.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import hr.faks.videogametracker.model.PcIgra;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
 * Klasa za upravljanje spremanjem i učitavanjem podataka
 * u/iz JSON datoteke
 */
public class FileManager {

    private static final String DATA_FOLDER = "podaci";
    private static final String FILE_NAME = "igre.json";
    private static final String FILE_PATH = DATA_FOLDER + File.separator + FILE_NAME;

    private static final Gson gson = createGson();

    /*
     * Kreira Gson instancu s potrebnim adapterima za naslijeđivanje
     */
    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Igra.class, new IgraAdapter())
                .create();
    }

    /*
     * Sprema listu igara u JSON datoteku
     * @param igre lista igara za spremanje
     * @throws IOException ako postoji problem pri pisanju u datoteku
     */
    public static void spremiIgre(ObservableList<Igra> igre) throws IOException {
        // Osiguraj da postoji direktorij
        Path direktorij = Paths.get(DATA_FOLDER);
        if (!Files.exists(direktorij)) {
            Files.createDirectories(direktorij);
        }

        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(igre, writer);
            System.out.println("Igre uspješno spremljene u " + FILE_PATH);
        }
    }

    /*
     * Učitava listu igara iz JSON datoteke
     * @return ObservableList s učitanim igrama ili praznu listu ako datoteka ne postoji
     * @throws IOException ako postoji problem pri čitanju iz datoteke
     */
    public static ObservableList<Igra> ucitajIgre() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("Datoteka " + FILE_PATH + " ne postoji. Vraćam praznu listu.");
            return FXCollections.observableArrayList();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Igra>>(){}.getType();
            List<Igra> igre = gson.fromJson(reader, listType);
            System.out.println("Učitano " + igre.size() + " igara iz datoteke " + FILE_PATH);
            return FXCollections.observableArrayList(igre);
        }
    }

    /*
     * Provjerava postoji li datoteka s igrama
     * @return true ako datoteka postoji
     */
    public static boolean postojeDatoteka() {
        return new File(FILE_PATH).exists();
    }

    /*
     * Adapter za serijalizaciju/deserijalizaciju apstraktne klase Igra
     * i njenih podklasa
     */
    private static class IgraAdapter implements JsonSerializer<Igra>, JsonDeserializer<Igra> {

        // Konstante za tipove igara u JSON-u
        private static final String TYPE = "tip";
        private static final String PC_IGRA = "PC";
        private static final String KONZOLNA_IGRA = "KONZOLNA";

        @Override
        public JsonElement serialize(Igra igra, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            // Dodajmo zajednička svojstva iz apstraktne klase Igra
            jsonObject.addProperty("naslovIgre", igra.getNaslovIgre());
            jsonObject.addProperty("platforma", igra.getPlatforma());
            jsonObject.addProperty("zanrIgre", igra.getZanrIgre());
            jsonObject.addProperty("datumIzlaska", igra.getDatumIzlaska());
            jsonObject.addProperty("kupljena", igra.isKupljena());
            jsonObject.addProperty("digitalnaIgra", igra.isDigitalnaIgra());
            jsonObject.addProperty("instalirana", igra.isInstalirana());

            // Dodaj specifične podatke za podklase
            if (igra instanceof PcIgra) {
                jsonObject.addProperty(TYPE, PC_IGRA);
                PcIgra pcIgra = (PcIgra) igra;
                jsonObject.addProperty("zahtjevi", pcIgra.getZahtjevi());
                jsonObject.addProperty("imaDRM", pcIgra.isImaDRM());
            } else if (igra instanceof KonzolnaIgra) {
                jsonObject.addProperty(TYPE, KONZOLNA_IGRA);
                KonzolnaIgra konzolnaIgra = (KonzolnaIgra) igra;
                jsonObject.addProperty("platformaKonzole", konzolnaIgra.getPlatformaKonzole());
            }

            return jsonObject;
        }

        @Override
        public Igra deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            if (!jsonObject.has(TYPE)) {
                throw new JsonParseException("Nepoznat tip igre: nedostaje polje 'tip'");
            }

            String tip = jsonObject.get(TYPE).getAsString();

            String naslovIgre = jsonObject.get("naslovIgre").getAsString();
            String platforma = jsonObject.get("platforma").getAsString();
            String zanrIgre = jsonObject.get("zanrIgre").getAsString();
            String datumIzlaska = jsonObject.get("datumIzlaska").getAsString();
            boolean kupljena = jsonObject.get("kupljena").getAsBoolean();
            boolean digitalnaIgra = jsonObject.get("digitalnaIgra").getAsBoolean();
            boolean instalirana = jsonObject.get("instalirana").getAsBoolean();

            if (PC_IGRA.equals(tip)) {
                String zahtjevi = jsonObject.get("zahtjevi").getAsString();
                boolean imaDRM = jsonObject.get("imaDRM").getAsBoolean();
                return new PcIgra(naslovIgre, platforma, zanrIgre, datumIzlaska, kupljena,
                                 digitalnaIgra, instalirana, zahtjevi, imaDRM);
            } else if (KONZOLNA_IGRA.equals(tip)) {
                String platformaKonzole = jsonObject.get("platformaKonzole").getAsString();
                return new KonzolnaIgra(naslovIgre, platforma, zanrIgre, datumIzlaska, kupljena,
                                       digitalnaIgra, instalirana, platformaKonzole);
            }

            throw new JsonParseException("Nepoznat tip igre: " + tip);
        }
    }
}
