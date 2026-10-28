package md.ecosort.ecosort;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import md.ecosort.ecosort.model.Utilizator;
import md.ecosort.ecosort.session.Sesiune;

public class EcoSortApplication extends Application {
    @Override
    public void start(Stage stage) {
        deschideLogin(stage);
    }

    private void deschideLogin(Stage stage) {
        LoginView loginView = new LoginView(utilizator -> deschideDashboard(stage, utilizator));

        Scene scene = new Scene(loginView, 1920, 1080);
        stage.setTitle("EcoSort - Autentificare");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void deschideDashboard(Stage loginStage, Utilizator utilizator) {
        Stage stage = new Stage();

        // La deconectare: se închide sesiunea și se revine la ecranul de autentificare.
        Runnable deconectare = () -> {
            Sesiune.inchide();
            deschideLogin(new Stage());
            stage.close();
        };

        Scene scene = new Scene(new DashboardView(utilizator, deconectare), 1920, 1080);
        stage.setTitle("EcoSort Application");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        loginStage.close();
    }
}
