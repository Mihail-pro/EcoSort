package md.ecosort.ecosort;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EcoSortApplication extends Application {
    @Override
    public void start(Stage stage) {
        LoginView loginView = new LoginView(utilizator -> deschideDashboard(stage, utilizator));

        Scene scene = new Scene(loginView, 1920, 1080);
        stage.setTitle("EcoSort - Autentificare");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void deschideDashboard(Stage loginStage, Utilizator utilizator) {
        Stage stage = new Stage();

        Scene scene = new Scene(new DashboardView(utilizator), 1920, 1080);
        stage.setTitle("EcoSort Application");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        loginStage.close();
    }
}