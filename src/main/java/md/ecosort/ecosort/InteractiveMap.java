package md.ecosort.ecosort;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import java.util.ArrayList;
import java.util.List;

public class InteractiveMap extends BorderPane {

    private static final double VIRTUAL_WIDTH = 1280;
    private static final double VIRTUAL_HEIGHT = 720;
    private static final int numar_etaje = 3;

    private static final double[][] Coordonate_bloc_A = {
            {340, 387, 340, 176, 368, 176, 368, 169, 379, 169, 379, 176, 431, 176, 431, 387},
            {340, 387, 340, 176, 368, 176, 368, 169, 379, 169, 379, 176, 431, 176, 431, 387},
            {340, 387, 340, 176, 368, 176, 368, 169, 379, 169, 379, 176, 431, 176, 431, 387}
    };
    private static final double[][] Coordonate_bloc_B = {
            {376, 387, 431, 387, 431, 394, 466, 394, 466, 447, 484, 447, 484, 467, 571, 467, 571, 423, 580, 423, 580, 370, 574, 370, 574, 275, 571, 275, 571, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 399, 688, 399, 688, 414, 684, 414, 684, 429, 768, 429, 768, 307, 869, 307, 869, 456, 827, 456, 827, 462, 845, 462, 845, 534, 765, 534, 765, 545, 725, 545, 725, 534, 636, 534, 636, 545, 598, 545, 598, 533, 520, 533, 520, 545, 483, 545, 483, 534, 400, 534, 400, 489, 376, 489},
            {376, 387, 431, 387, 431, 394, 466, 394, 466, 447, 484, 447, 484, 467, 571, 467, 571, 423, 580, 423, 580, 370, 574, 370, 574, 275, 571, 275, 571, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 399, 688, 399, 688, 414, 684, 414, 684, 429, 768, 429, 768, 307, 869, 307, 869, 456, 827, 456, 827, 462, 845, 462, 845, 534, 765, 534, 765, 545, 725, 545, 725, 534, 636, 534, 636, 545, 598, 545, 598, 533, 520, 533, 520, 545, 483, 545, 483, 534, 400, 534, 400, 489, 376, 489},
            {376, 387, 431, 387, 431, 394, 466, 394, 466, 447, 484, 447, 484, 467, 571, 467, 571, 423, 580, 423, 580, 370, 574, 370, 574, 275, 571, 275, 571, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 399, 688, 399, 688, 414, 684, 414, 684, 429, 768, 429, 768, 272, 751, 272, 751, 251, 869, 251, 869, 454, 810, 454, 810, 462, 845, 462, 845, 534, 765, 534, 765, 545, 725, 545, 725, 534, 636, 534, 636, 545, 598, 545, 598, 533, 520, 533, 520, 545, 483, 545, 483, 534, 400, 534, 400, 489, 376, 489}
    };
    private static final double[][] Coordonate_bloc_C = {
            {769, 247, 869, 247, 869, 122, 884, 122, 884, 5, 660, 5, 660, 154, 769, 154},
            {769, 247, 869, 247, 869, 122, 884, 122, 884, 5, 660, 5, 660, 154, 769, 154},
            {769, 247, 869, 247, 869, 122, 884, 122, 884, 5, 660, 5, 660, 154, 769, 154}
    };
    private static final double[][] Coordonate_bloc_D = {
            {235, 421, 235, 240, 238, 240, 255, 127, 272, 127, 263, 209, 276, 239, 276, 421},
            {235, 421, 235, 240, 238, 240, 255, 127, 272, 127, 263, 209, 276, 239, 276, 421},
            {235, 421, 235, 240, 238, 240, 255, 127, 272, 127, 263, 209, 276, 239, 276, 421}
    };
    private static final double[][] Coordonate_bloc_E = {
            {660, 125, 660, 18, 620, 18, 620, 25, 583, 25, 583, 37, 552, 37, 552, 80, 550, 80, 550, 87, 438, 87, 438, 89, 349, 89, 349, 125, 409, 125, 409, 134, 459, 134, 459, 125, 493, 125, 493, 134, 531, 134, 531, 125, 550, 125, 550, 134, 570, 134, 570, 125, 620, 125, 620, 110, 646, 110, 646, 125},
            {660, 125, 660, 18, 620, 18, 620, 25, 583, 25, 583, 37, 552, 37, 552, 80, 550, 80, 550, 87, 438, 87, 438, 89, 349, 89, 349, 125, 409, 125, 409, 134, 459, 134, 459, 125, 493, 125, 493, 134, 531, 134, 531, 125, 550, 125, 550, 134, 570, 134, 570, 125, 620, 125, 620, 110, 646, 110, 646, 125},
            {660, 125, 660, 18, 620, 18, 620, 25, 583, 25, 583, 37, 552, 37, 552, 80, 550, 80, 550, 87, 438, 87, 438, 89, 349, 89, 349, 125, 409, 125, 409, 134, 459, 134, 459, 125, 493, 125, 493, 134, 531, 134, 531, 125, 550, 125, 550, 134, 570, 134, 570, 125, 620, 125, 620, 110, 646, 110, 646, 125}
    };


    private final List<Pane> etaje = new ArrayList<>();
    private final Label lblEtaj = new Label();
    private final BorderPane toolBar = new BorderPane();
    private final Scale mapScale = new Scale(1, 1);

    public InteractiveMap() {
        Pane[] bloc_A = new Pane[numar_etaje];
        Pane[] bloc_B = new Pane[numar_etaje];
        Pane[] bloc_C = new Pane[numar_etaje];
        Pane[] bloc_D = new Pane[numar_etaje];
        Pane[] bloc_E = new Pane[numar_etaje];

        for (int i = 0; i < numar_etaje; i++) {
            Pane etaj = creeazaEtaj();
            etaje.add(etaj);

            bloc_A[i] = creeazaBloc(etaj, "Blocul A", Coordonate_bloc_A[i]);
            bloc_B[i] = creeazaBloc(etaj, "Blocul B", Coordonate_bloc_B[i]);
            bloc_C[i] = creeazaBloc(etaj, "Blocul C", Coordonate_bloc_C[i]);
            bloc_D[i] = creeazaBloc(etaj, "Blocul D", Coordonate_bloc_D[i]);
            bloc_E[i] = creeazaBloc(etaj, "Blocul E", Coordonate_bloc_E[i]);

            if (i == 0) {
                asambleazaBlocA1(bloc_A[i]);
                asambleazaBlocB1(bloc_B[i]);
            } else if (i == 1) {
                asambleazaBlocA2(bloc_A[i]);
            } else {
                asambleazaBlocA3(bloc_A[i]);
            }
        }

        StackPane containerHarta = new StackPane();
        containerHarta.getChildren().addAll(etaje);
        containerHarta.setMinSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        containerHarta.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        containerHarta.setMaxSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        containerHarta.getTransforms().add(mapScale);

        StackPane map = new StackPane(new Group(containerHarta));
        map.setAlignment(Pos.CENTER);
        map.setMinSize(0, 0);

        map.layoutBoundsProperty().addListener((obs, veche, noua) -> redimensioneazaHarta(noua.getWidth(), noua.getHeight()));
        HBox navigare = new HBox(10);
        for (int i = 0; i < numar_etaje; i++) {
            final  int nr_etaj = i + 1;
            Button btn = new Button("Etaj: " + nr_etaj);
            btn.setOnAction(e -> afiseazaEtaj(nr_etaj));
            navigare.getChildren().add(btn);
        }
        navigare.getChildren().add(lblEtaj);
        navigare.setAlignment(Pos.CENTER);
        navigare.setPadding(new Insets(10, 0, 10, 0));

        toolBar.setPadding(new Insets(10));

        ObservableList<String> listaIncaperi = FXCollections.observableArrayList("", "", "", "");
        ComboBox comboIncaperi = new ComboBox<>(listaIncaperi);
        comboIncaperi.setPromptText("Caută în colegiu ...");
        comboIncaperi.setEditable(true);
        comboIncaperi.setFocusTraversable(false);

        Button btnZoom = new Button("Zoom");

        Button btnAdaugaPunctColectare = new Button("+ Punct colectare");
        HBox unelteEco = new HBox(10, btnAdaugaPunctColectare);
        unelteEco.setAlignment(Pos.CENTER);

        HBox unelteCautare = new HBox(10, comboIncaperi, btnZoom);
        unelteCautare.setAlignment(Pos.CENTER);
        toolBar.setCenter(unelteCautare);
        toolBar.setRight(navigare);
        toolBar.setLeft(unelteEco);

        VBox mapController = new VBox();
        map.setAlignment(Pos.TOP_CENTER);
        mapController.getChildren().addAll(toolBar, map);
        mapController.setAlignment(Pos.TOP_CENTER);
        mapController.setSpacing(0);
        mapController.setPadding(new Insets(25));
        mapController.setAlignment(Pos.CENTER);
        VBox.setVgrow(map, Priority.ALWAYS);
        this.setCenter(mapController);

        afiseazaEtaj(1);
    }
        private void asambleazaBlocA1(Pane bloc) {
            adaugaIncapere(bloc, null, "A doua scară, Etaj 1, A", 340, 176, 39, 19);
            adaugaIncaperePoligon(bloc, "WC", "WC-B-A1", Color.WHITE, Color.BLACK, 340, 195, 379, 195, 379, 206, 364, 206, 364, 214, 340, 214);
            adaugaIncaperePoligon(bloc, null, "WC-Persoane-cu-necesități-speciale-A1", Color.WHITE, Color.BLACK, 364, 206, 379, 206, 379, 216, 364, 216);
            adaugaIncaperePoligon(bloc, "WC", "WC-F-A1", Color.WHITE, Color.BLACK, 340, 214, 364, 214, 364, 216, 379, 216, 379, 232, 340, 232);
            adaugaIncapere(bloc, "124", "Matematică", 340, 232, 39, 51);
            adaugaIncapere(bloc, "122", "Matematică", 340, 283, 39, 51);
            adaugaIncapere(bloc, "120", "Metodist Olga Jumbei", 340, 335, 39, 33);
            adaugaIncapere(bloc, null, "Prima scară, Etaj 1, A", 340, 368, 39, 19);
            adaugaIncapere(bloc, "123", "Matematică", 393, 176, 39, 54);
            adaugaIncapere(bloc, "121", "Limba și Literatura Română", 393, 230, 39, 52);
            adaugaIncapere(bloc, "119", "Istorie", 393, 282, 39, 54);
            adaugaIncapere(bloc, "117", "Istorie", 393, 336, 39, 51);
        }

        private void asambleazaBlocA2(Pane bloc) {
            adaugaIncapere(bloc, null, "A doua scară, Etaj 2, A", 340, 176, 39, 19);
            adaugaIncaperePoligon(bloc, "WC", "WC-B-A2", Color.WHITE, Color.BLACK, 340, 195, 379, 195, 379, 206, 364, 206, 364, 214, 340, 214);
            adaugaIncaperePoligon(bloc, null, "WC-Persoane-cu-necesități-speciale-A2", Color.WHITE, Color.BLACK, 364, 206, 379, 206, 379, 216, 364, 216);
            adaugaIncaperePoligon(bloc, "WC", "WC-F-A2", Color.WHITE, Color.BLACK, 340, 214, 364, 214, 364, 216, 379, 216, 379, 232, 340, 232);
            adaugaIncapere(bloc, "224", "Limba și Literatura Română", 340, 232, 39, 51);
            adaugaIncapere(bloc, "222", "Limba și Literatura Română", 340, 283, 39, 51);
            adaugaIncapere(bloc, "220", "Limba franceză", 340, 335, 39, 33);
            adaugaIncapere(bloc, null, "Prima scară, Etaj 2, A", 340, 368, 39, 19);
            adaugaIncapere(bloc, "226", "Limba engleză", 393, 176, 39, 38);
            adaugaIncapere(bloc, "225", "Servicii admnistrative", 393, 214, 39, 51);
            adaugaIncapere(bloc, "223", "Secția didactică 4 (Șef: Vasile Melnic)", 393, 265, 39, 17);
            adaugaIncapere(bloc, "221", "Servicii administrative", 393, 282, 39, 53);
            adaugaIncapere(bloc, "219", "Istorie", 393, 335, 39, 52);
        }

        private void asambleazaBlocA3(Pane bloc) {
            adaugaIncapere(bloc, null, "A doua scară, Etaj 3, A", 340, 176, 39, 19);
            adaugaIncapere(bloc, "308", "Catedra Științe reale", 340, 194, 39, 38);
            adaugaIncapere(bloc, "306", "Chimie", 340, 232, 39, 51);
            adaugaIncapere(bloc, "304", "Limba și Literatura Română", 340, 283, 39, 51);
            adaugaIncapere(bloc, "302", "Limba engleză", 340, 335, 39, 33);
            adaugaIncapere(bloc, null, "Prima scară, Etaj 3, A", 340, 368, 39, 19);
            adaugaIncapere(bloc, "309", "Limba engleză", 393, 176, 39, 38);
            adaugaIncapere(bloc, "307", "Laboratorul de chimie", 393, 214, 39, 16);
            adaugaIncapere(bloc, "305", "Geografie", 393, 230, 39, 52);
            adaugaIncapere(bloc, "303", "Geografie", 393, 282, 39, 53);
            adaugaIncapere(bloc, "301", "Limba și Literatura Română", 393, 335, 39, 52);
        }

        private void asambleazaBlocB1(Pane bloc) {
        adaugaIncapere(bloc, "115", "", 402, 394, 441-402, 412-394);
        adaugaIncapere(bloc, "113", "", 402, 412, 441-402, 467-412);
        adaugaIncapere(bloc, "111", "", 442, 431, 466-441, 467- 431);
        adaugaIncapere(bloc, null, null, 441, 394, 466-441, 431-394);
        adaugaIncapere(bloc, "118", "", 402, 490,  437-402, 533-490);
        adaugaIncapere(bloc, "116", "", 436, 490, 483-436, 533- 490);
        adaugaIncapere(bloc, null, null, 466, 447, 485-466, 467-447);
        adaugaIncapere(bloc, "114", "", 483, 490, 521-483, 545-490);
        adaugaIncapere(bloc, "112", "", 521, 490, 574-521, 533-490);
        adaugaIncapere(bloc, "110", "", 574, 490, 599-574, 533-490);
        adaugaIncapere(bloc, null, "Rcepție", 599, 490, 637-599, 546-490);
        adaugaIncapere(bloc, null, null, 599, 528, 611-599, 545-528);
        adaugaIncapere(bloc, null, null, 625, 528, 637-625, 546-528);
        adaugaIncapere(bloc, "109", "", 571, 423, 605-571, 467-423);
        adaugaIncapere(bloc, null, null, 581, 402, 607-581, 419-402);
        adaugaIncapere(bloc, null, null, 581, 370, 607-581, 402-370);
        adaugaIncapere(bloc, null, null, 619, 390, 656-619, 420-390);
        adaugaIncapere(bloc, null, null, 619, 370, 656-619, 390-370);
        adaugaIncapere(bloc, "Bufet", "Bufet", 575, 305, 619-575, 370-305);
        adaugaIncapere(bloc, null, "Bufet", 575, 285, 620-575, 304-285);
        adaugaIncapere(bloc, null, null, 619, 285, 662-619, 370-285);
        adaugaIncaperePoligon(bloc, null, null, Color.WHITE, Color.BLACK, 633, 285, 633, 301, 636, 304, 662, 303, 662, 285);
        adaugaIncaperePoligon(bloc, null, null, Color.WHITE, Color.BLACK, 636, 304, 636, 332, 662, 332, 662, 303);
        adaugaIncaperePoligon(bloc, null, null, Color.WHITE, Color.BLACK, 636, 332, 636, 337, 632, 337, 632, 348, 662, 348, 662, 331);
        adaugaIncaperePoligon(bloc, null, null, Color.WHITE, Color.BLACK, 629, 348, 629, 370, 662, 370, 662, 349);
        adaugaIncapere(bloc, "107", "", 632, 429, 667-632, 467-429);
        adaugaIncapere(bloc, "105", "", 680, 429, 722-680, 464-429);
        adaugaIncapere(bloc, "103", "", 722, 429, 768-722, 462-429);
        adaugaIncapere(bloc, null, null, 768, 455, 807-768, 462-455);
        adaugaIncaperePoligon(bloc, "Sala de\nLectură", "Sala de Lectură", Color.WHITE, Color.BLACK, 769, 455, 769, 307, 827, 307, 827, 440, 807, 440, 807, 455);
        adaugaIncapere(bloc, null, null, 807, 440, 827-807, 455-440);
        adaugaIncapere(bloc, "101", "", 828, 381, 869-828, 455-381);
        adaugaIncapere(bloc, "Bibli-\notecă", "Bibliotecă", 827, 328, 869-827, 382-328);
        adaugaIncapere(bloc, null, null, 827, 307, 869-827, 328-307);
        adaugaIncapere(bloc, "108", "", 636, 490, 680-636, 533-490);
        adaugaIncapere(bloc, "106", "", 680, 490, 717-680, 533-490);
        adaugaIncapere(bloc, null, null, 717, 483, 725-717, 534-483);
        adaugaIncapere(bloc, null, null, 725, 493, 740-725, 512-493);
        adaugaIncapere(bloc, null, null, 740, 493, 764-740, 512-493);
        adaugaIncapere(bloc, "104", null, 725, 512, 764-725, 545-512);
        adaugaIncapere(bloc, "102", "", 764, 493, 845-764, 534-493);
        adaugaIncapere(bloc, null, null, 575, 272, 626-575, 285-272);
        adaugaIncapere(bloc, "140", "", 572, 236, 626-572, 272-236);
        adaugaIncapere(bloc, "139", "", 572, 199, 617-572, 236-199);
        adaugaIncapere(bloc, "141", "", 626, 236, 662-626, 284-236);
        adaugaIncapere(bloc, null, null, 638, 217, 662-638, 226-217);
        adaugaIncapere(bloc, null, null, 640, 199, 662-640, 218-199);
    }
    private Pane creeazaEtaj() {
        Pane etaj = new Pane();
        etaj.setMinSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj.setMaxSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj.setBackground(new Background(new BackgroundFill(Color.rgb(242, 240, 239), CornerRadii.EMPTY, Insets.EMPTY)));
        etaj.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        return etaj;
    }

    private Pane creeazaBloc(Pane etaj, String nume, double[] coordonate) {
        Pane bloc = new Pane();
        bloc.setId(nume);
        bloc.setPickOnBounds(false);

        Polygon polygon = new Polygon(coordonate);
        polygon.setFill(Color.WHITE);
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(2.0);
        bloc.getChildren().add(polygon);

        polygon.setMouseTransparent(true);

        etaj.getChildren().add(bloc);
        return bloc;
    }

    private void adaugaIncapere(Pane bloc, String cod, String nume, double coordonataX, double coordonataY, double lungime, double latime) {
        adaugaIncapere(bloc, cod, nume, coordonataX, coordonataY, lungime, latime, Color.WHITE, Color.BLACK);
    }

    private void adaugaIncapere(Pane bloc, String cod, String nume, double coordonataX, double coordonataY, double lungime, double latime, Color culoareFundal, Color culoareBordura) {
        Rectangle rectangle = new Rectangle(coordonataX, coordonataY, lungime, latime);
        rectangle.setFill(culoareFundal);
        rectangle.setStroke(culoareBordura);
        rectangle.setStrokeWidth(1.5);

        // Numele încăperii (de ex: 112, 123, 118, 213, 250, Sala Sportivă, Bufet, Sala de Lectură)
        Text text = creeazaText(cod, 10);
        afiseazaTextCentrat(text, coordonataX + lungime / 2, coordonataY + latime / 2, lungime - 4, latime - 2);

        // Grupare figura geometrica cu text
        Group grupIncapere = new Group(rectangle, text);
        interactiv(grupIncapere, rectangle, culoareFundal, cod, nume);
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

        Text text = creeazaText(cod, 10);
        afiseazaTextCentrat(text, polygon.getLayoutBounds().getCenterX(), polygon.getLayoutBounds().getCenterY(), polygon.getLayoutBounds().getWidth() - 4, polygon.getLayoutBounds().getHeight() - 2);

        // Grup de încăpere în formă de poligon cu nume
        Group grupIncapere = new Group(polygon, text);
        interactiv(grupIncapere, polygon, culoareFundal, cod, nume);
        bloc.getChildren().add(grupIncapere);
    }

    private void adaugaUsa(Pane bloc, double startX, double startY, double endX, double endY) {
        Line usa = new Line(startX, startY, endX, endY);
        usa.setStroke(Color.WHITE);
        usa.setStrokeWidth(4);
        usa.setStrokeLineCap(StrokeLineCap.BUTT);
        usa.setMouseTransparent(true);
        bloc.getChildren().add(usa);
    }

    private Text creeazaText(String continut, double dimensiune_text) {
        Text text = new Text(continut != null ? continut : "");
        if (esteCod(text.getText())) {
            dimensiune_text += 4;
        }

        text.setFont(incarcaFontPersonalizat(dimensiune_text));
        text.setFill(Color.BLACK);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setTextOrigin(VPos.CENTER);
        return text;
    }

    private Font incarcaFontPersonalizat(double dimensiune_text) {
        Font fontPersonalizat = Font.loadFont(getClass().getResourceAsStream("/fonts/RobotoMono-Bold.ttf"), dimensiune_text);
        if (fontPersonalizat == null) {
            fontPersonalizat = Font.font("Monospaced", FontWeight.SEMI_BOLD, dimensiune_text);
        }
        return fontPersonalizat;
    }

    private static boolean esteCod(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void afiseazaTextCentrat(Text text, double centruX, double centruY, double latimeMaxima, double inaltimeMaxima) {
        double dimensiune_text = text.getFont().getSize();
        while (dimensiune_text > 6 && (text.getLayoutBounds().getWidth() > latimeMaxima || text.getLayoutBounds().getHeight() > inaltimeMaxima)) {
            dimensiune_text -= 1;
            text.setFont(incarcaFontPersonalizat(dimensiune_text));
        }
        text.setX(centruX - text.getLayoutBounds().getWidth() / 2);
        text.setY(centruY);
    }

    private void interactiv(Group grup, Shape forma, Color culoareFundal, String cod, String nume) {
        grup.setCursor(Cursor.HAND);
        grup.setOnMouseEntered(e -> forma.setFill(culoareFundal.darker()));
        grup.setOnMouseExited(e -> forma.setFill(culoareFundal));
        grup.setOnMouseClicked(e -> afiseazaSelectie(cod, nume));
    }

    private void afiseazaSelectie(String cod, String nume) {
        List<String> info = new ArrayList<>();
        if (cod != null && !cod.isBlank()) {
            info.add(cod.replace("\n", ""));
        }
        if (nume != null && !nume.isBlank()) {
            info.add(nume);
        }

        System.out.println("A fost selectat: " + (info.isEmpty() ? "-" : String.join(" - ", info)));
    }

    private void redimensioneazaHarta(double latimeDisponibila, double inaltimeDisponibila) {
        if (latimeDisponibila <= 0 || inaltimeDisponibila <= 0) {
            return;
        }

        double scale = Math.min(latimeDisponibila / VIRTUAL_WIDTH, inaltimeDisponibila / VIRTUAL_HEIGHT);
        mapScale.setX(scale);
        mapScale.setY(scale);

        toolBar.setMaxWidth(VIRTUAL_WIDTH * scale);
    }

    private void afiseazaEtaj(int etaj) {
        for (int i = 0; i < etaje.size(); i++) {
            boolean activ = (i + 1 == etaj);
            etaje.get(i).setVisible(activ);
            etaje.get(i).setManaged(activ);
        }
        lblEtaj.setText("Etajul: " + etaj);
    }
}