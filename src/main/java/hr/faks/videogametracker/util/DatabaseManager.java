package hr.faks.videogametracker.util;

import hr.faks.videogametracker.model.Igra;
import hr.faks.videogametracker.model.KonzolnaIgra;
import hr.faks.videogametracker.model.PcIgra;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Utility class for managing database operations
 * Uses properties file for secure credential storage
 */
public class DatabaseManager {
    private static final String PROPERTIES_FILE = "database.properties";
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    // Singleton instance
    private static DatabaseManager instance;
    private Connection connection;
    private boolean isConnected = false;

    static {
        loadDatabaseProperties();
    }

    private DatabaseManager() {
        // Private constructor for singleton pattern
    }

    /**
     * Load database connection properties from the properties file
     */
    private static void loadDatabaseProperties() {
        Properties props = new Properties();

        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + PROPERTIES_FILE);
                return;
            }

            // Load the properties file
            props.load(input);

            // Get the database connection properties
            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");

        } catch (IOException ex) {
            System.err.println("Error loading database properties: " + ex.getMessage());
        }
    }

    /**
     * Get the singleton instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Check if database is connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Connect to the PostgreSQL database
     */
    public boolean connect() {
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Try to establish the database connection
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            isConnected = true;

            System.out.println("Successfully connected to PostgreSQL database");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found: " + e.getMessage());
            isConnected = false;
            return false;
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            isConnected = false;
            return false;
        }
    }

    /**
     * Close the database connection
     */
    public boolean disconnect() {
        if (connection != null) {
            try {
                connection.close();
                isConnected = false;
                System.out.println("Database connection closed");
                return true;
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Save or update a game in the database
     * Uses INSERT for new games (id == null) and UPDATE for existing games
     */
    public boolean saveOrUpdateGame(Igra igra) {
        if (igra.getId() == null) {
            // Nova igra - koristi INSERT
            return insertNewGame(igra);
        } else {
            // Postojeća igra - koristi UPDATE
            return updateGame(igra, igra.getId());
        }
    }

    /**
     * Insert a new game into the database and set its ID
     */
    private boolean insertNewGame(Igra igra) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            connection.setAutoCommit(false);

            // First, insert into the main igre table
            String sql = "INSERT INTO igre (naslov, platforma, zanr, datum_izlaska, digitalna_kopija, instalirana, tip) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

            int gameId;
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, igra.getNaslovIgre());
                pstmt.setString(2, igra.getPlatforma());
                pstmt.setString(3, igra.getZanrIgre());
                pstmt.setString(4, igra.getDatumIzlaska());
                pstmt.setBoolean(5, igra.isDigitalnaIgra());
                pstmt.setBoolean(6, igra.isInstalirana());

                // Determine game type
                if (igra instanceof PcIgra) {
                    pstmt.setString(7, "pc");
                } else {
                    pstmt.setString(7, "konzola");
                }

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    gameId = rs.getInt(1);
                    igra.setId(gameId); // Postavi ID u objekt
                } else {
                    connection.rollback();
                    System.err.println("Failed to get generated game ID");
                    return false;
                }
            }

            // Now insert into the specific game type table
            if (igra instanceof PcIgra) {
                PcIgra pcIgra = (PcIgra) igra;
                sql = "INSERT INTO pc_igre (igra_id, zahtjevi, drm) VALUES (?, ?, ?)";

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, gameId);
                    pstmt.setString(2, pcIgra.getZahtjevi());
                    pstmt.setBoolean(3, pcIgra.isImaDRM());
                    pstmt.executeUpdate();
                }
            } else if (igra instanceof KonzolnaIgra) {
                KonzolnaIgra konzolnaIgra = (KonzolnaIgra) igra;
                sql = "INSERT INTO konzolne_igre (igra_id, platforma_konzole) VALUES (?, ?)";

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, gameId);
                    pstmt.setString(2, konzolnaIgra.getPlatformaKonzole());
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            System.out.println("New game inserted to database: " + igra.getNaslovIgre() + " (ID: " + gameId + ")");
            return true;

        } catch (SQLException e) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error during transaction rollback: " + ex.getMessage());
            }

            System.err.println("Error inserting game to database: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    /**
     * Load all games from the database
     */
    public ObservableList<Igra> loadGames() {
        ObservableList<Igra> games = FXCollections.observableArrayList();

        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            String sql =
                "SELECT i.id, i.naslov, i.platforma, i.zanr, i.datum_izlaska, " +
                "i.digitalna_kopija, i.instalirana, i.tip, " +
                "pc.zahtjevi, pc.drm, " +
                "k.platforma_konzole " +
                "FROM igre i " +
                "LEFT JOIN pc_igre pc ON i.id = pc.igra_id " +
                "LEFT JOIN konzolne_igre k ON i.id = k.igra_id";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    String tip = rs.getString("tip");
                    int id = rs.getInt("id");

                    if ("pc".equals(tip)) {
                        PcIgra pcIgra = new PcIgra(
                            rs.getString("naslov"),
                            rs.getString("platforma"),
                            rs.getString("zanr"),
                            rs.getString("datum_izlaska"),
                            rs.getBoolean("digitalna_kopija"),
                            rs.getBoolean("instalirana"),
                            rs.getString("zahtjevi"),
                            rs.getBoolean("drm")
                        );
                        pcIgra.setId(id); // Postavi ID iz baze
                        games.add(pcIgra);
                    } else {
                        KonzolnaIgra konzolnaIgra = new KonzolnaIgra(
                            rs.getString("naslov"),
                            rs.getString("platforma"),
                            rs.getString("zanr"),
                            rs.getString("datum_izlaska"),
                            rs.getBoolean("digitalna_kopija"),
                            rs.getBoolean("instalirana"),
                            rs.getString("platforma_konzole")
                        );
                        konzolnaIgra.setId(id); // Postavi ID iz baze
                        games.add(konzolnaIgra);
                    }
                }
            }

            System.out.println("Loaded " + games.size() + " games from database");
            return games;

        } catch (SQLException e) {
            System.err.println("Error loading games from database: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Update an existing game in the database
     */
    public boolean updateGame(Igra igra, int id) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            connection.setAutoCommit(false);

            // First, update the main igre table
            String sql = "UPDATE igre SET naslov = ?, platforma = ?, zanr = ?, datum_izlaska = ?, " +
                        "digitalna_kopija = ?, instalirana = ? WHERE id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, igra.getNaslovIgre());
                pstmt.setString(2, igra.getPlatforma());
                pstmt.setString(3, igra.getZanrIgre());
                pstmt.setString(4, igra.getDatumIzlaska());
                pstmt.setBoolean(5, igra.isDigitalnaIgra());
                pstmt.setBoolean(6, igra.isInstalirana());
                pstmt.setInt(7, id);
                pstmt.executeUpdate();
            }

            // Then, update the type-specific table
            if (igra instanceof PcIgra) {
                PcIgra pcIgra = (PcIgra) igra;
                sql = "UPDATE pc_igre SET zahtjevi = ?, drm = ? WHERE igra_id = ?";

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, pcIgra.getZahtjevi());
                    pstmt.setBoolean(2, pcIgra.isImaDRM());
                    pstmt.setInt(3, id);
                    pstmt.executeUpdate();
                }
            } else if (igra instanceof KonzolnaIgra) {
                KonzolnaIgra konzolnaIgra = (KonzolnaIgra) igra;
                sql = "UPDATE konzolne_igre SET platforma_konzole = ? WHERE igra_id = ?";

                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, konzolnaIgra.getPlatformaKonzole());
                    pstmt.setInt(2, id);
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            System.out.println("Game updated in database: " + igra.getNaslovIgre());
            return true;

        } catch (SQLException e) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error during transaction rollback: " + ex.getMessage());
            }

            System.err.println("Error updating game in database: " + e.getMessage());
            return false;
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    /**
     * Delete a game from the database
     */
    public boolean deleteGame(int id) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            // Due to foreign key constraints with ON DELETE CASCADE,
            // we only need to delete from the main table
            String sql = "DELETE FROM igre WHERE id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsDeleted = pstmt.executeUpdate();

                System.out.println("Game deleted from database (ID: " + id + ")");
                return rowsDeleted > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting game from database: " + e.getMessage());
            return false;
        }
    }

    /**
     * Initialize database tables if they don't exist
     */
    public boolean initializeDatabase() {
        try {
            // Check if connection is established
            if (connection == null || connection.isClosed()) {
                connect();
            }

            // Create the main igre table if it doesn't exist
            String createIgreTable =
                "CREATE TABLE IF NOT EXISTS public.igre (" +
                "id SERIAL PRIMARY KEY, " +
                "naslov VARCHAR(100) NOT NULL, " +
                "platforma VARCHAR(50) NOT NULL, " +
                "zanr VARCHAR(50) NOT NULL, " +
                "datum_izlaska VARCHAR(20) NOT NULL, " +
                "digitalna_kopija BOOLEAN NOT NULL, " +
                "instalirana BOOLEAN NOT NULL, " +
                "tip VARCHAR(20) NOT NULL" +
                ")";

            // Create PC specific table
            String createPcTable =
                "CREATE TABLE IF NOT EXISTS public.pc_igre (" +
                "igra_id INTEGER PRIMARY KEY REFERENCES igre(id) ON DELETE CASCADE, " +
                "zahtjevi TEXT, " +
                "drm BOOLEAN NOT NULL" +
                ")";

            // Create console specific table
            String createKonzoleTable =
                "CREATE TABLE IF NOT EXISTS public.konzolne_igre (" +
                "igra_id INTEGER PRIMARY KEY REFERENCES igre(id) ON DELETE CASCADE, " +
                "platforma_konzole VARCHAR(50) NOT NULL" +
                ")";

            // Execute the CREATE TABLE statements
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createIgreTable);
                stmt.execute(createPcTable);
                stmt.execute(createKonzoleTable);
                System.out.println("Database tables check completed (IF NOT EXISTS)");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
            return false;
        }
    }
}
