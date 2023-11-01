package pl.edu.wat.sr.ricart_agrawala;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class RadsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pl.edu.wat.sr.ricart_agrawala.strings", Locale.getDefault());

        FXMLLoader loader = new FXMLLoader(RadsApplication.class.getResource("main-view.fxml"));
        loader.setResources(resourceBundle);

        Scene scene = new Scene(loader.load());

        stage.setTitle(RadsConfig.TITLE);
        stage.setWidth(RadsConfig.WINDOW_WIDTH);
        stage.setHeight(RadsConfig.WINDOW_HEIGHT);
        stage.setResizable(RadsConfig.IS_RESIZABLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}