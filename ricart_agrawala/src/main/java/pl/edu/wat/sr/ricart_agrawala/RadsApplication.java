package pl.edu.wat.sr.ricart_agrawala;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class RadsApplication extends Application {
    private static final String TITLE = "Ricart-Agrawala";
    private static  final int WINDOW_WIDTH = 1366;
    private static  final int WINDOW_HEIGHT = 768;
    private static final boolean IS_RESIZABLE = false;

    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                StringsResourceController.RESOURCE_STRINGS_PATH,
                Locale.getDefault());

        FXMLLoader loader = new FXMLLoader(RadsApplication.class.getResource("main-view.fxml"));
        loader.setResources(resourceBundle);

        Scene scene = new Scene(loader.load());

        stage.setTitle(TITLE);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setResizable(IS_RESIZABLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}