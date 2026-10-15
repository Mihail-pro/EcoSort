package md.ecosort.ecosort;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

public class InteractiveMap extends BorderPane {

    private static final double VIRTUAL_WIDTH = 1280;
    private static final double VIRTUAL_HEIGHT = 720;

    private final Pane bloc_A1, bloc_B1, bloc_C1, bloc_D1, bloc_E1;
    private final Pane bloc_A2, bloc_B2, bloc_C2, bloc_D2, bloc_E2;
    private final Pane bloc_A3, bloc_B3, bloc_C3, bloc_D3, bloc_E3;


    private final Pane etaj1;
    private final Pane etaj2;
    private final Pane etaj3;
    private final StackPane containerHarta;
    private final Label lblEtaj;
    private final Scale mapScale = new Scale(1, 1);

    public InteractiveMap() {
        bloc_A1 = new Pane();
        bloc_B1 = new Pane();
        bloc_C1 = new Pane();
        bloc_D1 = new Pane();
        bloc_E1 = new Pane();

        bloc_A2 = new Pane();
        bloc_B2 = new Pane();
        bloc_C2 = new Pane();
        bloc_D2 = new Pane();
        bloc_E2 = new Pane();

        bloc_A3 = new Pane();
        bloc_B3 = new Pane();
        bloc_C3 = new Pane();
        bloc_D3 = new Pane();
        bloc_E3 = new Pane();

        etaj1 = new Pane();
        etaj2 = new Pane();
        etaj3 = new Pane();

        etaj1.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj1.setMaxSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj1.setBackground(new Background(new BackgroundFill(Color.rgb(242, 240, 239), CornerRadii.EMPTY, Insets.EMPTY)));
        etaj1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        etaj2.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj2.setMaxSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj2.setBackground(new Background(new BackgroundFill(Color.rgb(242, 240, 239), CornerRadii.EMPTY, Insets.EMPTY)));
        etaj2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        etaj3.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj3.setMaxSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj3.setBackground(new Background(new BackgroundFill(Color.rgb(242, 240, 239), CornerRadii.EMPTY, Insets.EMPTY)));
        etaj3.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        // Blocuri
        // Construire bloc A
        contureazaZona(bloc_A1, "Blocul A", Color.WHITE, Color.BLACK,
                340, 387,
                340, 176,
                368, 176,
                368, 169,
                379, 169,
                379, 176,
                431, 176,
                431, 387
                );

        // Construire Bloc A1
        adaugaIncapere(bloc_A1, null, "A doua scară, Etaj 1, A", 340, 176, 39, 19);
        adaugaIncaperePoligon(bloc_A1, "WC", "WC-B-A1", Color.WHITE, Color.BLACK, 340, 195, 379, 195, 379, 206, 364, 206, 364, 214, 340, 214);
        adaugaIncaperePoligon(bloc_A1, null, "WC-Persoane-cu-necesități-speciale-A1", Color.WHITE, Color.BLACK, 364, 206, 379, 206, 379, 216, 364, 216);
        adaugaIncaperePoligon(bloc_A1, "WC", "WC-F-A1", Color.WHITE, Color.BLACK, 340, 214, 364, 214, 364, 216, 379, 216, 379, 232, 340, 232);
        adaugaIncapere(bloc_A1, "124", "Matematică", 340, 232, 39, 51);
        adaugaIncapere(bloc_A1, "122", "Matematică", 340, 283, 39, 51);
        adaugaIncapere(bloc_A1, "120", "Metodist Olga Jumbei", 340, 335, 39, 33);
        adaugaIncapere(bloc_A1, null, "Prima scară, Etaj 1, A", 340, 368, 39, 19);
        adaugaIncapere(bloc_A1, "123", "Matematică", 393, 176, 39, 54);
        adaugaIncapere(bloc_A1, "121", "Limba și Literatura Română", 393, 230, 39, 52);
        adaugaIncapere(bloc_A1, "119", "Istorie", 393, 282, 39, 54);
        adaugaIncapere(bloc_A1, "117", "Istorie", 393, 336, 39, 51);

        contureazaZona(bloc_A2, "Blocul A",
                340, 387,
                340, 176,
                368, 176,
                368, 169,
                379, 169,
                379, 176,
                431, 176,
                431, 387
        );

        // Construire bloc A2
        adaugaIncapere(bloc_A2, null, "A doua scară, Etaj 2, A", 340, 176, 39, 19);
        adaugaIncaperePoligon(bloc_A2, "WC", "WC-B-A2", Color.WHITE, Color.BLACK, 340, 195, 379, 195, 379, 206, 364, 206, 364, 214, 340, 214);
        adaugaIncaperePoligon(bloc_A2, null, "WC-Persoane-cu-necesități-speciale-A2", Color.WHITE, Color.BLACK, 364, 206, 379, 206, 379, 216, 364, 216);
        adaugaIncaperePoligon(bloc_A2, "WC", "WC-F-A2", Color.WHITE, Color.BLACK, 340, 214, 364, 214, 364, 216, 379, 216, 379, 232, 340, 232);
        adaugaIncapere(bloc_A2, "224", "Limba și Literatura Română", 340, 232, 39, 51);
        adaugaIncapere(bloc_A2, "222", "Limba și Literatura Română", 340, 283, 39, 51);
        adaugaIncapere(bloc_A2, "220", "Limba franceză", 340, 335, 39, 33);
        adaugaIncapere(bloc_A2, null, "Prima scară, Etaj 2, A", 340, 368, 39, 19);
        adaugaIncapere(bloc_A2, "226", "Limba engleză", 393, 176, 39, 38);
        adaugaIncapere(bloc_A2, "225", "Servicii admnistrative", 393, 214, 39, 51);
        adaugaIncapere(bloc_A2, "223", "Secția didactică 4 (Șef: Vasile Melnic)", 393, 265, 39, 17);
        adaugaIncapere(bloc_A2, "221", "Servicii administrative", 393, 282, 39, 53);
        adaugaIncapere(bloc_A2, "219", "Istorie", 393, 335, 39, 52);

        contureazaZona(bloc_A3, "Blocul A",
                340, 387,
                340, 176,
                368, 176,
                368, 169,
                379, 169,
                379, 176,
                431, 176,
                431, 387
        );

        adaugaIncapere(bloc_A3, null, "A doua scară, Etaj 3, A", 340, 176, 39, 19);
        adaugaIncapere(bloc_A3, "308", "Catedra Științe reale", 340, 194, 39, 38);
        adaugaIncapere(bloc_A3, "306", "Chimie", 340, 232, 39, 51);
        adaugaIncapere(bloc_A3, "304", "Limba și Literatura Română", 340, 283, 39, 51);
        adaugaIncapere(bloc_A3, "302", "Limba engleză", 340, 335, 39, 33);
        adaugaIncapere(bloc_A3, null, "Prima scară, Etaj 3, A", 340, 368, 39, 19);
        adaugaIncapere(bloc_A3, "309", "Limba engleză", 393, 176, 39, 38);
        adaugaIncapere(bloc_A3, "307", "Laboratorul de chimie", 393, 214, 39, 16);
        adaugaIncapere(bloc_A3, "305", "Geografie", 393, 230, 39, 52);
        adaugaIncapere(bloc_A3, "303", "Geografie", 393, 282, 39, 53);
        adaugaIncapere(bloc_A3, "301", "Limba și Literatura Română", 393, 335, 39, 52);

        // Construire bloc B
        contureazaZona(bloc_B1, "Blocul B",
                376, 387,
                431, 387,
                431, 394,
                466, 394,
                466, 447,
                484, 447,
                484, 467,
                571, 467,
                571, 423,
                580, 423,
                580, 370,
                574, 370,
                574, 275,
                571, 275,
                571, 199,
                684, 199,
                684, 254,
                700, 254,
                700, 270,
                684, 270,
                684, 399,
                688, 399,
                688, 414,
                684, 414,
                684, 429,
                768, 429,
                768, 272,
                751, 272,
                751, 251,
                869, 251,
                869, 454,
                810, 454,
                810, 462,
                845, 462,
                845, 534,
                765, 534,
                765, 545,
                725, 545,
                725, 534,
                636, 534,
                636, 545,
                598, 545,
                598, 533,
                520, 533,
                520, 545,
                483, 545,
                483, 534,
                400, 534,
                400, 489,
                376, 489
                );

        // Construire Bloc B1
        // adaugaIncapere(bloc_B1, "101", "Laborator de informatica 1", 50, 50, 150, 100, Color.WHITE, Color.BLACK);

        contureazaZona(bloc_B2, "Blocul B",
                376, 387,
                431, 387,
                431, 394,
                466, 394,
                466, 447,
                484, 447,
                484, 467,
                571, 467,
                571, 423,
                580, 423,
                580, 370,
                574, 370,
                574, 275,
                571, 275,
                571, 199,
                684, 199,
                684, 254,
                700, 254,
                700, 270,
                684, 270,
                684, 399,
                688, 399,
                688, 414,
                684, 414,
                684, 429,
                768, 429,
                768, 272,
                751, 272,
                751, 251,
                869, 251,
                869, 454,
                810, 454,
                810, 462,
                845, 462,
                845, 534,
                765, 534,
                765, 545,
                725, 545,
                725, 534,
                636, 534,
                636, 545,
                598, 545,
                598, 533,
                520, 533,
                520, 545,
                483, 545,
                483, 534,
                400, 534,
                400, 489,
                376, 489
        );

        // Construire Bloc B2
        // adaugaIncapere(bloc_B2, "202", "Formare continuă și resurse educaționale", 200, 80, 300, 100, Color.WHITE, Color.BLACK);

        contureazaZona(bloc_B3, "Blocul B",
                376, 387,
                431, 387,
                431, 394,
                466, 394,
                466, 447,
                484, 447,
                484, 467,
                571, 467,
                571, 423,
                580, 423,
                580, 370,
                574, 370,
                574, 275,
                571, 275,
                571, 199,
                684, 199,
                684, 254,
                700, 254,
                700, 270,
                684, 270,
                684, 399,
                688, 399,
                688, 414,
                684, 414,
                684, 429,
                768, 429,
                768, 272,
                751, 272,
                751, 251,
                869, 251,
                869, 454,
                810, 454,
                810, 462,
                845, 462,
                845, 534,
                765, 534,
                765, 545,
                725, 545,
                725, 534,
                636, 534,
                636, 545,
                598, 545,
                598, 533,
                520, 533,
                520, 545,
                483, 545,
                483, 534,
                400, 534,
                400, 489,
                376, 489
        );

        // Construire Bloc B3
        // adaugaIncapere(bloc_B3, "310", "Laborator de informatică 16", 80, 80, 120, 80, Color.WHITE, Color.BLACK);

        // Construire bloc C
        contureazaZona(bloc_C1, "Blocul C",
                769, 247,
                869, 247,
                869, 122,
                884, 122,
                884, 5,
                660, 5,
                660, 154,
                769, 154
                );

        contureazaZona(bloc_C2, "Blocul C",
                769, 247,
                869, 247,
                869, 122,
                884, 122,
                884, 5,
                660, 5,
                660, 154,
                769, 154
        );

        contureazaZona(bloc_C3, "Blocul C",
                769, 247,
                869, 247,
                869, 122,
                884, 122,
                884, 5,
                660, 5,
                660, 154,
                769, 154
        );

        // Construire bloc D
        contureazaZona(bloc_D1, "Blocul D",
                235, 421,
                235, 240,
                238, 240,
                255, 127,
                272, 127,
                263, 209,
                276, 239,
                276, 421
                );

        contureazaZona(bloc_D2, "Blocul D",
                235, 421,
                235, 240,
                238, 240,
                255, 127,
                272, 127,
                263, 209,
                276, 239,
                276, 421
        );

        contureazaZona(bloc_D3, "Blocul D",
                235, 421,
                235, 240,
                238, 240,
                255, 127,
                272, 127,
                263, 209,
                276, 239,
                276, 421
        );


        // Construire bloc E
        contureazaZona(bloc_E1, "Blocul E",
                660, 125,
                660, 18,
                620, 18,
                620, 25,
                583, 25,
                583, 37,
                552, 37,
                552, 80,
                550, 80,
                550, 87,
                438, 87,
                438, 89,
                349, 89,
                349, 125,
                409, 125,
                409, 134,
                459, 134,
                459, 125,
                493, 125,
                493, 134,
                531, 134,
                531, 125,
                550, 125,
                550, 134,
                570, 134,
                570, 125,
                620, 125,
                620, 110,
                646, 110,
                646, 125
                );

        contureazaZona(bloc_E2, "Blocul E",
                660, 125,
                660, 18,
                620, 18,
                620, 25,
                583, 25,
                583, 37,
                552, 37,
                552, 80,
                550, 80,
                550, 87,
                438, 87,
                438, 89,
                349, 89,
                349, 125,
                409, 125,
                409, 134,
                459, 134,
                459, 125,
                493, 125,
                493, 134,
                531, 134,
                531, 125,
                550, 125,
                550, 134,
                570, 134,
                570, 125,
                620, 125,
                620, 110,
                646, 110,
                646, 125
        );

        contureazaZona(bloc_E3, "Blocul E",
                660, 125,
                660, 18,
                620, 18,
                620, 25,
                583, 25,
                583, 37,
                552, 37,
                552, 80,
                550, 80,
                550, 87,
                438, 87,
                438, 89,
                349, 89,
                349, 125,
                409, 125,
                409, 134,
                459, 134,
                459, 125,
                493, 125,
                493, 134,
                531, 134,
                531, 125,
                550, 125,
                550, 134,
                570, 134,
                570, 125,
                620, 125,
                620, 110,
                646, 110,
                646, 125
        );

        // Etaje
        // Construire etaj 1
        etaj1.getChildren().addAll(bloc_A1, bloc_B1, bloc_C1, bloc_D1, bloc_E1);

        // Construire etaj 2
        etaj2.getChildren().addAll(bloc_A2, bloc_B2, bloc_C2, bloc_D2, bloc_E2);

        // Construire etaj 3
        etaj3.getChildren().addAll(bloc_A3, bloc_B3, bloc_C3, bloc_D3, bloc_E3);

        containerHarta = new StackPane();
        containerHarta.getChildren().addAll(etaj1, etaj2, etaj3);
        containerHarta.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        containerHarta.setMaxSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        StackPane map = new StackPane(containerHarta);
        map.setAlignment(Pos.CENTER);

        lblEtaj = new Label("Etajul 1");

        // Inițial este afișat etajul 1
        afiseazaEtaj(1);

        // Butoane de navigare între etaje
        Button btnEtaj1 = new Button("Etaj 1");
        Button btnEtaj2 = new Button("Etaj 2");
        Button btnEtaj3 = new Button("Etaj 3");

        btnEtaj1.setOnAction(e -> afiseazaEtaj(1));
        btnEtaj2.setOnAction(e -> afiseazaEtaj(2));
        btnEtaj3.setOnAction(e -> afiseazaEtaj(3));

        HBox navigare = new HBox(10, btnEtaj1, btnEtaj2, btnEtaj3, lblEtaj);
        navigare.setAlignment(Pos.CENTER);
        navigare.setPadding(new Insets(10));

        this.setTop(navigare);
        this.setCenter(map);

        map.widthProperty().addListener((obs, veche, noua) -> redemensioneazaHarta(map.getWidth(), map.getHeight()));
        map.heightProperty().addListener((obs, veche, noua) -> redemensioneazaHarta(map.getWidth(), map.getHeight()));
    }

    private void redemensioneazaHarta(double latimeDisponibila, double inaltimeDisponibila) {
        if (latimeDisponibila <= 0 || inaltimeDisponibila <= 0) return;

        double scaleX = latimeDisponibila / VIRTUAL_WIDTH;
        double scaleY = inaltimeDisponibila / VIRTUAL_HEIGHT;

        double scale = Math.min(scaleX, scaleY);
        mapScale.setX(scale);
        mapScale.setY(scale);
    }

    private void contureazaZona(Pane zona, String nume, double... puncte) {
        contureazaZona(zona, nume, Color.WHITE, Color.BLACK, puncte);
    }

    private void contureazaZona(Pane zona, String nume, Color culoareFundal, Color culoareBordura, double... puncte) {
        Polygon polygon = new Polygon(puncte);
        polygon.setFill(culoareFundal);
        polygon.setStrokeWidth(2.5);
        polygon.setStroke(culoareBordura);

        zona.getChildren().add(polygon);
    }

    private void adaugaHol(Pane bloc, String nume, double coordonataX, double coordonataY, double... puncte) {
        adaugaHol(bloc, nume, coordonataX, coordonataY, Color.WHITE, Color.BLACK, puncte);
    }

    private void adaugaHol(Pane bloc, String nume, double coordonataX, double coordonataY, Color culoareFundal, Color culoareBordura, double... puncte) {
        Polygon hol = new Polygon(puncte);
        hol.setFill(culoareFundal);
        hol.setStroke(culoareBordura);
        hol.setStrokeWidth(1.0);

        hol.setOnMouseEntered(e -> {
            hol.setFill(culoareFundal.darker());
            bloc.setCursor(Cursor.HAND);
        });

        hol.setOnMouseExited(e -> {
            hol.setFill(culoareFundal);
            bloc.setCursor(Cursor.DEFAULT);
        });

        hol.setOnMouseClicked(e -> {
            System.out.println("A fost selectat " + nume);
        });

        bloc.getChildren().add(hol);
    }

    private void adaugaIncapere(Pane bloc, String cod, String nume, double coordonataX, double coordonataY, double lungime, double latime) {
        adaugaIncapere(bloc, cod, nume, coordonataX, coordonataY, lungime, latime, Color.WHITE, Color.BLACK);
    }

    private void adaugaIncapere(Pane bloc, String cod, String nume, double coordonataX, double coordonataY, double lungime, double latime, Color culoareFundal, Color culoareBordura) {
        Rectangle rectangle = new Rectangle(lungime, latime);
        rectangle.setX(coordonataX);
        rectangle.setY(coordonataY);
        rectangle.setFill(culoareFundal);
        rectangle.setStroke(culoareBordura);
        rectangle.setStrokeWidth(1.5);
        rectangle.setArcWidth(0);
        rectangle.setArcHeight(0);

        // Numele încăperii (de ex: 112, 123, 118, 213, 250, Sala Sportivă, Bufet, Sala de Lectură)
        Text txtNume = new Text(cod);
        txtNume.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        txtNume.setFill(Color.BLACK);

        // Centrarea textului
        txtNume.setX(coordonataX + (lungime - txtNume.getLayoutBounds().getWidth()) / 2);
        txtNume.setY(coordonataY + (latime - txtNume.getLayoutBounds().getHeight() / 2) / 2 + 10);

        // Grupare figura geometrica cu text
        Group grupIncapere = new Group(rectangle, txtNume);

        grupIncapere.setOnMouseEntered(e -> {
            rectangle.setFill(culoareFundal.darker());
            bloc.setCursor(Cursor.HAND);
        });

        grupIncapere.setOnMouseExited(e -> {
            rectangle.setFill(culoareFundal);
            bloc.setCursor(Cursor.DEFAULT);
        });

        grupIncapere.setOnMouseClicked(e -> {
            System.out.println("A fost selectat " + nume + " - " + cod);
        });

        bloc.getChildren().add(grupIncapere);
    }

    private void adaugaIncaperePoligon(Pane bloc, String cod, String nume, double... puncte) {
        adaugaIncaperePoligon(bloc, cod, nume, Color.WHITE, Color.BLACK, puncte);
    }

    private void adaugaIncaperePoligon(Pane bloc, String cod, String nume, Color culoareFundal, Color culoareBordura, double... puncte) {
        Polygon polygon = new Polygon(puncte);
        polygon.setFill(culoareFundal);
        polygon.setStroke(culoareBordura);
        polygon.setStrokeWidth(1.5);

        Text txtNume = new Text(cod != null ? cod : "");
        txtNume.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        txtNume.setFill(Color.BLACK);

        // Centrarea textului
        double centerX = polygon.getLayoutBounds().getCenterX();
        double centerY = polygon.getLayoutBounds().getCenterY();

        txtNume.setX(centerX - txtNume.getLayoutBounds().getWidth() / 2 - 5);
        txtNume.setY(centerY + txtNume.getLayoutBounds().getHeight() / 4);

        // Grup de încăpere în formă de poligon cu nume
        Group grupIncapere = new Group(polygon, txtNume);

        grupIncapere.setOnMouseEntered(e -> {
            polygon.setFill(culoareFundal.darker());
            grupIncapere.setCursor(Cursor.HAND);
        });

        grupIncapere.setOnMouseExited(e -> {
            polygon.setFill(culoareFundal);
            polygon.setCursor(Cursor.DEFAULT);
        });

        grupIncapere.setOnMouseClicked(e -> {
            System.out.println("A fost selectat + " + nume + " - " + (cod != null ? cod : ""));
        });

        bloc.getChildren().add(grupIncapere);
    }

    private void adaugaUsa(Pane bloc, double startX, double startY, double endX, double endY) {
        Line usa = new Line(startX, startY, endX, endY);
        usa.setFill(Color.WHITE);
        usa.setStroke(Color.TRANSPARENT);
        usa.setStrokeWidth(4);
    }

    private void afiseazaEtaj(int etaj) {
        etaj1.setVisible(etaj == 1);
        etaj1.setManaged(etaj == 1);

        etaj2.setVisible(etaj == 2);
        etaj2.setManaged(etaj == 2);

        etaj3.setVisible(etaj == 3);
        etaj3.setManaged(etaj == 3);

        lblEtaj.setText("Etajul " + etaj);
    }
}
