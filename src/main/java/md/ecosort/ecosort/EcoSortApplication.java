package md.ecosort.ecosort;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class EcoSortApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1920, 1080);
        stage.setTitle("EcoSort Application");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
