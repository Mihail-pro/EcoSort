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
import md.ecosort.ecosort.model.Utilizator;
import md.ecosort.ecosort.service.AuthService;

import java.util.Arrays;
import java.util.function.Consumer;

public class LoginView extends StackPane {

    private final AuthService auth = new AuthService();

    // Se execută după autentificare reușită și primește utilizatorul logat
    private final Consumer<Utilizator> laLoginReusit;
    private TextField txtUser = new TextField();
    private PasswordField txtParola = new PasswordField();
    private Label lblEroare = new Label();
    private VBox card = new VBox(8);

    // Câmpuri afișate doar la prima pornire, când trebuie creat primul administrator
    private final TextField txtNume = new TextField();
    private final PasswordField txtConfirmare = new PasswordField();
    private final Label subtitlu = new Label("AUTENTIFICARE");
    private final Button btnLogin = new Button("Autentificare  →");
    private boolean modConfigurare = false;

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

        subtitlu.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        subtitlu.setTextFill(VERDE);

        VBox antet = new VBox(6, logo, titlu, subtitlu);
        antet.setAlignment(Pos.CENTER);
        antet.setPadding(new Insets(0, 0, 15, 0));

        // Câmpurile de logare
        txtUser.setPromptText("Introdu numele de utilizator");
        txtParola.setPromptText("Introdu parola");
        txtNume.setPromptText("Numele și prenumele");
        txtConfirmare.setPromptText("Repetă parola");

        formeazaCamp(txtUser);
        formeazaCamp(txtParola);
        formeazaCamp(txtNume);
        formeazaCamp(txtConfirmare);

        // Mesaj de eroare
        lblEroare.setTextFill(Color.RED);
        lblEroare.setWrapText(true);

        // BUTON
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
        verificaConfigurare();
    }

    // La prima pornire (niciun administrator în baza de date) formularul devine "creare administrator".
    private void verificaConfigurare() {
        btnLogin.setDisable(true);
        Async.executa(auth::necesitaConfigurare, necesita -> {
            btnLogin.setDisable(false);
            if (necesita) activeazaModConfigurare();
        }, mesaj -> {
            btnLogin.setDisable(false);
            lblEroare.setText(mesaj);
        });
    }

    private void activeazaModConfigurare() {
        modConfigurare = true;
        subtitlu.setText("PRIMA PORNIRE — CREEAZĂ CONTUL DE ADMINISTRATOR");
        btnLogin.setText("Creează administratorul  →");
        // Ordinea câmpurilor: utilizator, nume complet, parolă, confirmare parolă.
        card.getChildren().addAll(card.getChildren().indexOf(txtUser) + 1,
                java.util.List.of(eticheta("NUME COMPLET"), txtNume));
        card.getChildren().addAll(card.getChildren().indexOf(txtParola) + 1,
                java.util.List.of(eticheta("CONFIRMĂ PAROLA"), txtConfirmare));
        txtUser.setOnAction(e -> txtNume.requestFocus());
        txtNume.setOnAction(e -> txtParola.requestFocus());
        txtParola.setOnAction(e -> txtConfirmare.requestFocus());
        txtConfirmare.setOnAction(e -> login());
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
        if (btnLogin.isDisabled()) return;

        String user = txtUser.getText().trim();
        String nume = txtNume.getText();
        char[] parola = txtParola.getText().toCharArray();
        char[] confirmare = txtConfirmare.getText().toCharArray();

        if (user.isEmpty() || parola.length == 0 || (modConfigurare && (nume.isBlank() || confirmare.length == 0))) {
            eroare("Completează toate câmpurile!");
            return;
        }

        lblEroare.setText("");
        btnLogin.setDisable(true);
        boolean configurare = modConfigurare;
        Async.executa(() -> {
            try {
                // Verificarea Argon2id durează intenționat ~0,1 s: rulează pe firul de fundal.
                return configurare
                        ? auth.creeazaPrimulAdmin(user, nume, parola, confirmare)
                        : auth.autentifica(user, parola);
            } finally {
                Arrays.fill(parola, '\0');
                Arrays.fill(confirmare, '\0');
            }
        }, utilizator -> {
            txtParola.clear();
            txtConfirmare.clear();
            laLoginReusit.accept(utilizator);
        }, mesaj -> {
            btnLogin.setDisable(false);
            txtParola.clear();
            txtConfirmare.clear();
            eroare(mesaj);
        });
    }

    private void eroare(String mesaj) {
        lblEroare.setText(mesaj);
        scutura();
    }
}
