module hr.faks.videogametracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens hr.faks.videogametracker.gui to javafx.fxml;
    opens hr.faks.videogametracker.app to javafx.graphics;
    opens hr.faks.videogametracker.model to javafx.base;

    exports hr.faks.videogametracker.app;
    exports hr.faks.videogametracker.gui;
}