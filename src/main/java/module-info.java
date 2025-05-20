module hr.faks.videogametracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens hr.faks.videogametracker to javafx.fxml;
    exports hr.faks.videogametracker;
}