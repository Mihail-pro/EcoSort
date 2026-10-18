package md.ecosort.ecosort;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.function.Consumer;

public class LoginView extends StackPane {

    // Datele administratorului (le poți schimba aici)
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PAROLA = "admin123";

    // Se execută după autentificare reușită și primește utilizatorul logat
    private final Consumer<Utilizator> laLoginReusit;
    private TextField txtUser = new TextField();
    private PasswordField txtParola = new PasswordField();
    private Label lblEroare = new Label();
    private VBox card = new VBox(8);

    // Culorile folosite în pagină
    private final Color VERDE = Color.web("#10b981");
    private final Color VERDE_INCHIS = Color.web("#065f46");

    public LoginView(Consumer<Utilizator> laLoginReusit) {
        this.laLoginReusit = laLoginReusit;

        // Fundalul
        LinearGradient degrade = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#022c22")),
                new Stop(1, Color.web("#059669")));
        setBackground(new Background(new BackgroundFill(degrade, null, null)));

        // LOGO
        Circle cerc = new Circle(32, VERDE);
        Label simbol = new Label("♻");
        simbol.setFont(Font.font(32));
        simbol.setTextFill(Color.WHITE);
        StackPane logo = new StackPane(cerc, simbol);

        Label titlu = new Label("EcoSort");
        titlu.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titlu.setTextFill(VERDE_INCHIS);

        Label subtitlu = new Label("AUTENTIFICARE ADMINISTRATOR");
        subtitlu.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        subtitlu.setTextFill(VERDE);

        VBox antet = new VBox(6, logo, titlu, subtitlu);
        antet.setAlignment(Pos.CENTER);
        antet.setPadding(new Insets(0, 0, 15, 0));

        // Câmpurile de logare
        txtUser.setPromptText("Introdu numele de utilizator");
        txtParola.setPromptText("Introdu parola");

        formeazaCamp(txtUser);
        formeazaCamp(txtParola);

        // Mesaj de eroare
        lblEroare.setTextFill(Color.RED);
        lblEroare.setWrapText(true);

        // BUTON
        Button btnLogin = new Button("Autentificare  →");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnLogin.setTextFill(Color.WHITE);
        btnLogin.setPadding(new Insets(12));
        culoareButon(btnLogin, VERDE);

        // Când mouse-ul trece peste buton se schimbă culoarea
        btnLogin.setOnMouseEntered(e -> culoareButon(btnLogin, VERDE_INCHIS));
        btnLogin.setOnMouseExited(e -> culoareButon(btnLogin, VERDE));

        btnLogin.setOnAction(e -> login());
        txtUser.setOnAction(e -> txtParola.requestFocus());
        txtParola.setOnAction(e -> login());

        // Cardul alb din mijloc
        card.setMaxWidth(380);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setPadding(new Insets(30));
        card.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(20), null)));
        card.setEffect(new DropShadow(30, Color.rgb(0, 0, 0, 0.4)));

        card.getChildren().addAll(
                antet,
                eticheta("NUME UTILIZATOR"), txtUser,
                eticheta("PAROLĂ"), txtParola,
                lblEroare,
                btnLogin
        );

        // Fundalul animat merge primul (în spate), cardul deasupra
        getChildren().addAll(creeazaFundalAnimat(), card);

        aparitieCard();
    }

    // Cercuri verzi transparente care plutesc în fundal
    private Pane creeazaFundalAnimat() {
        Pane fundal = new Pane();
        fundal.setMouseTransparent(true);

        for (int i = 0; i < 15; i++) {
            Circle c = new Circle(20 + Math.random() * 60);
            c.setFill(Color.rgb(52, 211, 153, 0.10 + Math.random() * 0.15));

            // Poziția depinde de mărimea ferestrei (între 0% și 100%)
            c.centerXProperty().bind(widthProperty().multiply(Math.random()));
            c.centerYProperty().bind(heightProperty().multiply(Math.random()));

            // Mișcări lente
            TranslateTransition mutare = new TranslateTransition(Duration.seconds(5 + Math.random() * 6), c);
            mutare.setByY(-100 + Math.random() * 200);
            mutare.setByX(-60 + Math.random() * 120);
            mutare.setAutoReverse(true);
            mutare.setCycleCount(Animation.INDEFINITE);
            mutare.play();

            fundal.getChildren().add(c);
        }
        return fundal;
    }

    // Cardul apare treptat și urcă puțin
    private void aparitieCard() {
        FadeTransition fade = new FadeTransition(Duration.millis(900), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        TranslateTransition urcare = new TranslateTransition(Duration.millis(900), card);
        urcare.setFromY(40);
        urcare.setToY(0);
        urcare.play();
    }

    // Cardul se scutură stânga-dreapta
    private void scutura() {
        TranslateTransition t = new TranslateTransition(Duration.millis(60), card);
        t.setFromX(-10);
        t.setToX(10);
        t.setCycleCount(6);
        t.setAutoReverse(true);
        t.setOnFinished(e -> card.setTranslateX(0));
        t.play();
    }

    // Creează o etichetă mică deasupra unui câmp
    private Label eticheta(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.GRAY);
        l.setPadding(new Insets(8, 0, 0, 0));
        return l;
    }


    private void formeazaCamp(Control camp) {
        camp.setBackground(new Background(new BackgroundFill(Color.web("#f0fdf4"), new CornerRadii(10), null)));
        camp.setPrefHeight(40);
        camp.setBorder(margine(VERDE, 1.5));

        camp.focusedProperty().addListener((obs, vechi, nou) -> {
            if (nou) {
                camp.setBorder(margine(VERDE_INCHIS, 2.5));
            } else {
                camp.setBorder(margine(VERDE, 1.5));
            }
        });
    }

    private Border margine(Color culoare, double grosime) {
        return new Border(new BorderStroke(culoare, BorderStrokeStyle.SOLID,
                new CornerRadii(10), new BorderWidths(grosime)));
    }

    private void culoareButon(Button b, Color culoare) {
        b.setBackground(new Background(new BackgroundFill(culoare, new CornerRadii(10), null)));
    }


    private void login() {
        String user = txtUser.getText().trim();
        String parola = txtParola.getText();

        if (user.isEmpty() || parola.isEmpty()) {
            lblEroare.setText("Completează toate câmpurile!");
            scutura();
        } else if (!user.equals(ADMIN_USER) || !parola.equals(ADMIN_PAROLA)) {
            lblEroare.setText("Nume de utilizator sau parolă incorectă!");
            scutura();
        } else {
            // Autentificare reușită -> trimitem administratorul către dashboard
            laLoginReusit.accept(new Utilizator(user, "Administrator"));
        }
    }
}