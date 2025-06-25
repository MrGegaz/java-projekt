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

 // Klasa za upravljanje spremanjem i učitavanjem podataka u i iz JSON datoteke
public class FileManager {

    private static final String DATA_FOLDER = "data";
    private static final String FILE_NAME = "igre.json";
    private static final String FILE_PATH = DATA_FOLDER + File.separator + FILE_NAME;

    private static final Gson gson = createGson();

    // Kreiranje Gson instance s potrebnim adapterima za naslijeđivanje
    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Igra.class, new IgraAdapter())
                .create();
    }

    // Spremanje liste igara u JSON datoteku
    public static void spremiIgre(ObservableList<Igra> igre) throws IOException {
        // Direktorij postoji?
        Path direktorij = Paths.get(DATA_FOLDER);
        if (!Files.exists(direktorij)) {
            Files.createDirectories(direktorij);
        }

        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(igre, writer);
            System.out.println("Igre uspješno spremljene u " + FILE_PATH);
        }
    }

    // Sprema listu igara u JSON datoteku koristeći direktno formatiranje
    public static void spremiIgreDirektno(ObservableList<Igra> igre) throws IOException {
        // Direktorij postoji?
        Path direktorij = Paths.get(DATA_FOLDER);
        if (!Files.exists(direktorij)) {
            Files.createDirectories(direktorij);
        }

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            // Početak JSON polja
            writer.write("[\n");

            int size = igre.size();
            for (int i = 0; i < size; i++) {
                Igra igra = igre.get(i);

                writer.write("  {\n");

                // Dodajemo polje "tip" koje je ključno za deserijalizaciju
                if (igra instanceof PcIgra) {
                    writer.write("    \"tip\": \"PC\",\n");
                } else if (igra instanceof KonzolnaIgra) {
                    writer.write("    \"tip\": \"KONZOLNA\",\n");
                }

                // Zajednička svojstva
                writer.write("    \"naslovIgre\": \"" + igra.getNaslovIgre() + "\",\n");
                writer.write("    \"platforma\": \"" + igra.getPlatforma() + "\",\n");
                writer.write("    \"zanrIgre\": \"" + igra.getZanrIgre() + "\",\n");
                writer.write("    \"datumIzlaska\": \"" + igra.getDatumIzlaska() + "\",\n");
                writer.write("    \"digitalnaIgra\": " + igra.isDigitalnaIgra() + ",\n");
                writer.write("    \"instalirana\": " + igra.isInstalirana() + ",\n");

                // Specifična svojstva
                if (igra instanceof PcIgra) {
                    PcIgra pcIgra = (PcIgra) igra;
                    writer.write("    \"zahtjevi\": \"" + pcIgra.getZahtjevi() + "\",\n");
                    writer.write("    \"imaDRM\": " + pcIgra.isImaDRM() + "\n");
                } else if (igra instanceof KonzolnaIgra) {
                    KonzolnaIgra konzolnaIgra = (KonzolnaIgra) igra;
                    writer.write("    \"platformaKonzole\": \"" + konzolnaIgra.getPlatformaKonzole() + "\"\n");
                }

                // Zatvori objekt sa zarezom ako nije posljednji
                if (i < size - 1) {
                    writer.write("  },\n");
                } else {
                    writer.write("  }\n");
                }
            }

            // Kraj JSON polja
            writer.write("]");

            System.out.println("Igre uspješno spremljene direktnom metodom u " + FILE_PATH);
        }
    }

    /*
    * Učitavanje liste igara iz JSON datoteke
    * @return ObservableList s učitanim igrama ili praznu listu ako datoteka ne postoji
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

    // Datoteka s igrama postoji?
    public static boolean postojeDatoteka() {
        return new File(FILE_PATH).exists();
    }

    // Adapter za serijalizaciju/deserijalizaciju apstraktne klase Igra i podklasa
    private static class IgraAdapter implements JsonSerializer<Igra>, JsonDeserializer<Igra> {

        // Konstante za tipove igara u JSON-u
        private static final String TYPE = "tip";
        private static final String PC_IGRA = "PC";
        private static final String KONZOLNA_IGRA = "KONZOLNA";

        @Override
        public JsonElement serialize(Igra igra, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            // Polje "tip" koje je ključno za deserijalizaciju
            if (igra instanceof PcIgra) {
                jsonObject.addProperty(TYPE, PC_IGRA);
            } else if (igra instanceof KonzolnaIgra) {
                jsonObject.addProperty(TYPE, KONZOLNA_IGRA);
            }

            // Dodavanje zajedničkih svojstava iz apstraktne klase Igra
            jsonObject.addProperty("naslovIgre", igra.getNaslovIgre());
            jsonObject.addProperty("platforma", igra.getPlatforma());
            jsonObject.addProperty("zanrIgre", igra.getZanrIgre());
            jsonObject.addProperty("datumIzlaska", igra.getDatumIzlaska());
            jsonObject.addProperty("digitalnaIgra", igra.isDigitalnaIgra());
            jsonObject.addProperty("instalirana", igra.isInstalirana());

            // Dodavanje specifičnih podataka za podklase
            if (igra instanceof PcIgra) {
                PcIgra pcIgra = (PcIgra) igra;
                jsonObject.addProperty("zahtjevi", pcIgra.getZahtjevi());
                jsonObject.addProperty("imaDRM", pcIgra.isImaDRM());
            } else if (igra instanceof KonzolnaIgra) {
                KonzolnaIgra konzolnaIgra = (KonzolnaIgra) igra;
                jsonObject.addProperty("platformaKonzole", konzolnaIgra.getPlatformaKonzole());
            }

            return jsonObject;
        }

        @Override
        public Igra deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            String tip;
            if (jsonObject.has(TYPE)) {
                tip = jsonObject.get(TYPE).getAsString();
            } else if (jsonObject.has("zahtjevi")) {
                tip = PC_IGRA;
            } else if (jsonObject.has("platformaKonzole")) {
                tip = KONZOLNA_IGRA;
            } else {
                throw new JsonParseException("Nepoznat tip igre: nedostaju ključna polja");
            }

            String naslovIgre = jsonObject.has("naslovIgre") ? jsonObject.get("naslovIgre").getAsString() : "Nepoznat naslov";
            String platforma = jsonObject.has("platforma") ? jsonObject.get("platforma").getAsString() : "Nepoznata platforma";
            String zanrIgre = jsonObject.has("zanrIgre") ? jsonObject.get("zanrIgre").getAsString() : "Nepoznat žanr";
            String datumIzlaska = jsonObject.has("datumIzlaska") ? jsonObject.get("datumIzlaska").getAsString() : "Nepoznat datum";
            boolean digitalnaIgra = jsonObject.has("digitalnaIgra") && jsonObject.get("digitalnaIgra").getAsBoolean();
            boolean instalirana = jsonObject.has("instalirana") && jsonObject.get("instalirana").getAsBoolean();

            if (PC_IGRA.equals(tip)) {
                String zahtjevi = jsonObject.has("zahtjevi") ? jsonObject.get("zahtjevi").getAsString() : "Nepoznat zahtjev";
                boolean imaDRM = jsonObject.has("imaDRM") && jsonObject.get("imaDRM").getAsBoolean();
                return new PcIgra(naslovIgre, platforma, zanrIgre, datumIzlaska, false,
                                 digitalnaIgra, instalirana, zahtjevi, imaDRM);
            } else if (KONZOLNA_IGRA.equals(tip)) {
                String platformaKonzole = jsonObject.has("platformaKonzole") ? jsonObject.get("platformaKonzole").getAsString() : "Nepoznata konzola";
                return new KonzolnaIgra(naslovIgre, platforma, zanrIgre, datumIzlaska,
                                       digitalnaIgra, instalirana, platformaKonzole);
            }

            throw new JsonParseException("Nepoznat tip igre: " + tip);
        }
    }
}
