package md.ecosort.ecosort;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import md.ecosort.ecosort.model.Categorie;
import md.ecosort.ecosort.model.PunctColectare;
import md.ecosort.ecosort.model.StareCos;
import md.ecosort.ecosort.service.ColectareService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            {769, 307, 869, 307, 869, 122, 884, 122, 884, 5, 660, 5, 660, 154, 769, 154, 769, 251, 751, 251, 751, 272, 769, 272},
            {769, 380, 810, 380, 810, 251, 869, 251, 869, 122, 884, 122, 884, 5, 660, 5, 660, 154, 769, 154, 769, 251, 751, 251, 751, 272, 769, 272},
            {769, 437, 798, 437, 798, 454, 869, 454, 869, 251, 869, 122, 884, 122, 884, 5, 660, 5, 660, 154, 769, 154, 769, 251, 751, 251, 751, 272, 769, 272}
    };
    private static final double[][] Coordonate_bloc_C = {
            {376, 387, 431, 387, 431, 394, 466, 394, 466, 447, 484, 447, 484, 467, 571, 467, 571, 423, 580, 423, 580, 370, 574, 370, 574, 272, 572, 272, 572, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 399, 688, 399, 688, 414, 684, 414, 684, 429, 769, 429, 769, 307, 869, 307, 869, 456, 827, 456, 827, 462, 845, 462, 845, 534, 765, 534, 765, 545, 725, 545, 725, 534, 636, 534, 636, 545, 599, 545, 599, 533, 520, 533, 520, 545, 483, 545, 483, 534, 400, 534, 400, 490, 376, 490},
            {376, 387, 431, 387, 431, 394, 466, 394, 466, 447, 484, 447, 484, 467, 571, 467, 571, 423, 580, 423, 580, 370, 574, 370, 574, 275, 571, 275, 571, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 399, 688, 399, 688, 414, 684, 414, 684, 429, 769, 429, 769, 380, 810, 379, 810, 251, 869, 251, 869, 456, 827, 456, 827, 462, 845, 462, 845, 534, 765, 534, 765, 545, 725, 545, 725, 534, 636, 534, 636, 545, 599, 545, 599, 533, 520, 533, 520, 545, 483, 545, 483, 534, 400, 534, 400, 490, 376, 490},
            {376, 387, 431, 387, 431, 394, 466, 394, 466, 447, 484, 447, 484, 467, 571, 467, 571, 423, 580, 423, 580, 370, 574, 370, 574, 275, 571, 275, 571, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 399, 688, 399, 688, 414, 684, 414, 684, 429, 769, 429, 769, 437, 798, 437, 798, 454, 810, 454, 810, 462, 845, 462, 845, 534, 765, 534, 765, 545, 725, 545, 725, 534, 636, 534, 636, 545, 599, 545, 599, 533, 520, 533, 520, 545, 483, 545, 483, 534, 400, 534, 400, 490, 376, 490}
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

    // Încăperile care pot găzdui un punct de colectare, după cheia "etaj|bloc|încăpere".
    private record Incapere(Pane bloc, double centruX, double centruY) { }

    private final ColectareService service;
    private final boolean esteAdmin;
    private final Map<String, Incapere> incaperi = new HashMap<>();
    private final List<Node> marcaje = new ArrayList<>();
    private final Button btnAdaugaPunctColectare = new Button("+ Punct colectare");
    private int etajCurent;
    private boolean modAdaugare = false;

    private final Map<Integer, Circle> marcajPeId = new HashMap<>();
    private final Map<Integer, Circle> haloPeId = new HashMap<>();
    private final Map<Integer, Tooltip> tooltipPeId = new HashMap<>();
    private final Map<Integer, PunctColectare> punctPeId = new HashMap<>();
    private Map<Integer, StareCos> stare = Map.of();
    private Timeline sondaj;

    private static final Color INEL_VERDE = Color.web("#16a34a"), INEL_PORTOCALIU = Color.web("#f59e0b"),
            INEL_ROSU = Color.web("#dc2626"), INEL_GRI = Color.web("#9ca3af");

    public InteractiveMap(ColectareService service, boolean esteAdmin) {
        this.service = service;
        this.esteAdmin = esteAdmin;

        sondaj = new Timeline(new KeyFrame(Duration.seconds(5), e -> reincarcaStarea()));
        sondaj.setCycleCount(Animation.INDEFINITE);
        sceneProperty().addListener((obs, veche, noua) -> {
            if (noua == null) sondaj.stop(); else sondaj.play();
        });

        Pane[] bloc_A = new Pane[numar_etaje];
        Pane[] bloc_B = new Pane[numar_etaje];
        Pane[] bloc_C = new Pane[numar_etaje];
        Pane[] bloc_D = new Pane[numar_etaje];
        Pane[] bloc_E = new Pane[numar_etaje];

        for (int i = 0; i < numar_etaje; i++) {
            Pane etaj = creeazaEtaj();
            etaje.add(etaj);
            etajCurent = i + 1;

            bloc_A[i] = creeazaBloc(etaj, "Blocul A", Coordonate_bloc_A[i]);
            bloc_B[i] = creeazaBloc(etaj, "Blocul B", Coordonate_bloc_B[i]);
            bloc_C[i] = creeazaBloc(etaj, "Blocul C", Coordonate_bloc_C[i]);
            bloc_D[i] = creeazaBloc(etaj, "Blocul D", Coordonate_bloc_D[i]);
            bloc_E[i] = creeazaBloc(etaj, "Blocul E", Coordonate_bloc_E[i]);

            if (i == 0) {
                asambleazaBlocA1(bloc_A[i]);
                asambleazaBlocB1(bloc_B[i]);
                asambleazaBlocC1(bloc_C[i]);
                asambleazaBlocD1(bloc_D[i]);
                asambleazaBlocE1(bloc_E[i]);
            } else if (i == 1) {
                asambleazaBlocA2(bloc_A[i]);
                asambleazaBlocB2(bloc_B[i]);
                asambleazaBlocC2(bloc_C[i]);
                asambleazaBlocD2(bloc_D[i]);
                asambleazaBlocE2(bloc_E[i]);
            } else {
                asambleazaBlocA3(bloc_A[i]);
                asambleazaBlocB3(bloc_B[i]);
                asambleazaBlocC3(bloc_C[i]);
                asambleazaBlocD3(bloc_D[i]);
                asambleazaBlocE3(bloc_E[i]);
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

        // Doar administratorul amplasează coșuri: butonul activează alegerea unei încăperi pe hartă.
        btnAdaugaPunctColectare.setVisible(esteAdmin);
        btnAdaugaPunctColectare.setManaged(esteAdmin);
        btnAdaugaPunctColectare.setOnAction(e -> seteazaModAdaugare(!modAdaugare));
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

        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 1, bloc A", 379, 176, 394, 176, 394, 387, 379, 387);
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

        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 2, bloc A", 379, 176, 393, 176, 394, 387, 379, 387);
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

        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 3, bloc A", 379, 176, 394, 176, 394, 387, 379, 387);
    }

    private void asambleazaBlocC1(Pane bloc) {
        adaugaIncapere(bloc, "115", "", 400, 394, 442-400, 412-394);
        adaugaIncapere(bloc, "113", "", 400, 412, 442-400, 467-412);
        adaugaIncapere(bloc, "111", "", 442, 431, 466-442, 467- 431);
        adaugaIncapere(bloc, null, null, 442, 394, 466-442, 431-394);
        adaugaIncapere(bloc, "118", "", 400, 490,  437-400, 533-490);
        adaugaIncapere(bloc, "116", "", 436, 490, 483-436, 533- 490);
        adaugaIncapere(bloc, null, null, 467, 447, 485-467, 467-447);
        adaugaIncapere(bloc, "114", "", 483, 490, 521-483, 545-490);
        adaugaIncapere(bloc, "112", "", 521, 490, 574-521, 533-490);
        adaugaIncapere(bloc, "110", "", 574, 490, 599-574, 533-490);
        adaugaIncapere(bloc, null, "Rcepție", 599, 490, 637-599, 546-490);
        adaugaIncapere(bloc, null, null, 599, 527, 611-599, 546-527);
        adaugaIncapere(bloc, null, null, 624, 527, 637-624, 546-527);
        adaugaIncapere(bloc, "109", "", 571, 423, 605-571, 467-423);
        adaugaIncapere(bloc, null, null, 581, 402, 607-581, 423-402, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 581, 370, 607-581, 402-370, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 619, 390, 656-619, 423-390, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 619, 370, 656-619, 390-370, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 607, 370, 620-607, 423-370, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, "Bufet", "Bufet", 575, 305, 619-575, 370-305, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, "Bufet", 575, 285, 620-575, 305-285, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 619, 285, 662-619, 370-285, Color.SILVER, Color.BLACK);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 633, 285, 633, 301, 636, 304, 662, 304, 662, 285);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 636, 304, 636, 332, 662, 332, 662, 304);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 636, 332, 636, 337, 632, 337, 632, 348, 662, 348, 662, 332);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 629, 348, 629, 370, 662, 370, 662, 348);
        adaugaIncapere(bloc, "107", "", 632, 429, 667-632, 467-429);
        adaugaIncapere(bloc, "105", "", 680, 429, 722-680, 467-429);
        adaugaIncapere(bloc, "103", "", 722, 429, 768-722, 462-429);
        adaugaIncapere(bloc, null, null, 768, 455, 807-768, 462-455);
        adaugaIncaperePoligon(bloc, "Sala de\nLectură", "Sala de Lectură", Color.WHITE, Color.BLACK, 769, 455, 769, 307, 827, 307, 827, 440, 807, 440, 807, 455);
        adaugaIncapere(bloc, null, null, 807, 455, 827-807, 462-455);
        adaugaIncapere(bloc, null, null, 807, 440, 827-807, 455-440);
        adaugaIncapere(bloc, "101", "", 827, 381, 869-827, 455-381);
        adaugaIncapere(bloc, "Bibli-\notecă", "Bibliotecă", 827, 328, 869-827, 382-328);
        adaugaIncapere(bloc, null, null, 827, 307, 869-827, 328-307);
        adaugaIncapere(bloc, "108", "", 637, 490, 680-637, 533-490);
        adaugaIncapere(bloc, "106", "", 680, 490, 717-680, 533-490);
        adaugaIncapere(bloc, null, null, 717, 483, 725-717, 534-483);
        adaugaIncapere(bloc, null, null, 725, 493, 740-725, 512-493);
        adaugaIncapere(bloc, null, null, 740, 493, 764-740, 512-493);
        adaugaIncapere(bloc, "104", null, 725, 512, 764-725, 545-512);
        adaugaIncapere(bloc, "102", "", 764, 493, 845-764, 534-493);adaugaIncapere(bloc, null, null, 575, 272, 626-575, 285-272);
        adaugaIncapere(bloc, "140", "", 572, 236, 626-572, 272-236);
        adaugaIncapere(bloc, "139", "", 572, 199, 617-572, 236-199);
        adaugaIncapere(bloc, "141", "", 626, 236, 662-626, 285-236);
        adaugaIncapere(bloc, null, null, 638, 217, 662-638, 226-217);
        adaugaIncapere(bloc, null, null, 640, 199, 662-640, 217-199);

        adaugaIncaperePoligon(bloc, null, "Coridor central-lateral, Etaj 1", 376, 387, 432, 387, 432, 393, 400, 393, 400, 489, 376, 489);
        adaugaIncaperePoligon(bloc, null, "Coridor central, Etaj 1", 400, 467, 605, 467, 605, 423, 632, 423, 632, 467, 717, 467, 717, 489, 400, 489);
        adaugaIncaperePoligon(bloc, null, "Coridor Sala de Lectură", 717, 467, 722, 467, 722, 462, 845, 462, 845, 493, 725, 493, 725, 483, 717, 483);
        adaugaIncaperePoligon(bloc, null, "Coridor Forneti", 667, 467, 667, 429, 632, 429, 632, 423, 656, 423, 656, 371, 663, 371, 663, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 398, 690, 398, 690, 414, 684, 414, 684, 429, 677, 429, 677, 430, 680, 430, 680, 467);
        adaugaIncaperePoligon(bloc, null, "Coridor Operatori, Etaj 1", 617, 199, 617, 236, 626, 236, 626, 212, 627, 212, 627, 236, 662, 236, 662, 226, 638, 226, 638, 217, 640, 217, 640, 199, 627, 199, 627, 202, 626, 202, 626, 199);

        adaugaUsa(bloc, 613, 545, 622, 545);
        adaugaUsa(bloc, 601, 527, 608, 527);
        adaugaUsa(bloc, 627, 527, 635, 527);
        adaugaUsa(bloc, 606, 490, 628, 490);
        adaugaUsa(bloc, 608, 424, 615, 424);
        adaugaUsa(bloc, 607, 410, 607, 406, Color.SILVER);
        adaugaUsa(bloc, 620, 404, 620, 396, Color.SILVER);
        adaugaUsa(bloc, 620, 388, 620, 381, Color.SILVER);
        adaugaUsa(bloc, 607, 387, 607, 381, Color.SILVER);
        adaugaUsa(bloc, 609, 370, 615, 370, Color.SILVER);
        adaugaUsa(bloc, 619, 356, 619, 348, Color.SILVER);
        adaugaUsa(bloc, 619, 320, 619, 314, Color.SILVER);
        adaugaUsa(bloc, 619, 298, 619, 290, Color.SILVER);
        adaugaUsa(bloc, 636, 319, 636, 310, Color.SILVER);
        adaugaUsa(bloc, 646, 304, 651, 304, Color.SILVER);
        adaugaUsa(bloc, 632, 341, 632, 344, Color.SILVER);
        adaugaUsa(bloc, 626, 265, 626, 257);
        adaugaUsa(bloc, 618, 236, 625, 236);
        adaugaUsa(bloc, 626, 212, 626, 203);
        adaugaUsa(bloc, 630, 199, 637, 199);
        adaugaUsa(bloc, 628, 236, 633, 236);
        adaugaUsa(bloc, 638, 223, 638, 220);
        adaugaUsa(bloc, 640, 212, 640, 206);
        adaugaUsa(bloc, 662, 208, 662, 202);
        adaugaUsa(bloc, 690, 401, 690, 412);
        adaugaUsa(bloc, 669, 466, 677, 466);
        adaugaUsa(bloc, 700, 257, 700, 267);

        adaugaFereastra(bloc, 610, 543, 610, 530, 1);
        adaugaFereastra(bloc, 625, 543, 625, 530, 1);
        adaugaFereastra(bloc, 601, 545, 607, 545, 2);
        adaugaFereastra(bloc, 628, 545, 634, 545, 2);
        adaugaFereastra(bloc, 644, 533, 654, 533, 2);
        adaugaFereastra(bloc, 576, 305, 618, 305, 1, Color.SILVER);
        adaugaFereastra(bloc, 662, 361, 662, 353, 2, Color.SILVER);
        adaugaFereastra(bloc, 662, 341, 662, 333, 2, Color.SILVER);
        adaugaFereastra(bloc, 662, 321, 662, 312, 2, Color.SILVER);
        adaugaFereastra(bloc, 662, 300, 662, 292, 2, Color.SILVER);
        adaugaFereastra(bloc, 618, 200, 624, 200, 2);
        adaugaFereastra(bloc, 573, 267, 573, 258, 2);
        adaugaFereastra(bloc, 573, 251, 573, 245, 2);
        adaugaFereastra(bloc, 580, 200, 587, 200, 2);
        adaugaFereastra(bloc, 596, 200, 603, 200, 2);
        adaugaFereastra(bloc, 648, 200, 656, 200, 2);
        adaugaFereastra(bloc, 669, 200, 678, 200, 2);
        adaugaFereastra(bloc, 683, 200, 683, 210, 2);
        adaugaFereastra(bloc, 683, 214, 683, 224, 2);
        adaugaFereastra(bloc, 683, 228, 683, 238, 2);
        adaugaFereastra(bloc, 683, 242, 683, 251, 2);
        adaugaFereastra(bloc, 688, 254, 697, 254, 2);
        adaugaFereastra(bloc, 688, 270, 697, 270, 2);
        adaugaFereastra(bloc, 683, 272, 683, 283, 2);
        adaugaFereastra(bloc, 683, 287, 683, 297, 2);
        adaugaFereastra(bloc, 683, 301, 683, 311, 2);
        adaugaFereastra(bloc, 683, 315, 683, 325, 2);
        adaugaFereastra(bloc, 683, 329, 683, 339, 2);
        adaugaFereastra(bloc, 683, 343, 683, 354, 2);
        adaugaFereastra(bloc, 683, 358, 683, 368, 2);
        adaugaFereastra(bloc, 683, 372, 683, 383, 2);
        adaugaFereastra(bloc, 683, 387, 683, 397, 2);
        adaugaFereastra(bloc, 683, 416, 683, 426, 2);
    }

    private void asambleazaBlocC2(Pane bloc) {
        adaugaIncapere(bloc, null, null, 400, 394, 441-400, 412-394);
        adaugaIncapere(bloc, "217", "", 400, 412, 441-400, 467-412);
        adaugaIncapere(bloc, null, null, 441, 394, 466-441, 431-394);
        adaugaIncapere(bloc, "215", "", 441, 431, 466-441, 467-431);
        adaugaIncapere(bloc, "218", "", 400, 467, 426-400, 490-467);
        adaugaIncapere(bloc, null, null, 466, 447, 485-466, 467-447);
        adaugaIncapere(bloc, "216", "", 400, 490, 439-400, 533-490);
        adaugaIncapere(bloc, "214", "", 439, 490, 483-439, 533-490);
        adaugaIncapere(bloc, "213", "", 483, 490, 520-483, 545-490);
        adaugaIncapere(bloc, "212", "", 520, 490, 546-520, 533-490);
        adaugaIncapere(bloc, "211", "", 546, 490, 599-546, 533-490);
        adaugaIncapere(bloc, "210", "Orange", 599, 490, 636-599, 545-490);
        adaugaIncapere(bloc, "209", "", 636, 490, 679-636, 533-490);
        adaugaIncapere(bloc, "208", "", 679, 490, 716-679, 533-490);
        adaugaIncapere(bloc, null, null, 716, 483, 724-716, 534-483);
        adaugaIncapere(bloc, null, null, 724, 493, 740-724, 512-493);
        adaugaIncapere(bloc, null, null, 740, 493, 764-740, 512-493);
        adaugaIncapere(bloc, "206", "", 724, 512, 764-724, 545-512);
        adaugaIncapere(bloc, "204", "", 764, 493, 845-764, 533-493);
        adaugaIncapere(bloc, "", "", 571, 423, 606-571, 467-423);
        adaugaIncapere(bloc, "", "", 580, 399, 607-580, 423-399);
        adaugaIncapere(bloc, "", "", 580, 370, 607-580, 399-370);
        adaugaIncapere(bloc, "", "", 573, 337, 607-573, 370-337);
        adaugaIncapere(bloc, "", "", 573, 284, 620-573, 337-284);
        adaugaIncapere(bloc, null, null, 573, 275, 626-573, 284-275);
        adaugaIncapere(bloc, "", "", 620, 284, 663-620, 330-284);
        adaugaIncapere(bloc, "", "", 637, 330, 663-637, 371-330);
        adaugaIncapere(bloc, "", "", 618, 371, 657-618, 396-371);
        adaugaIncapere(bloc, "", "", 618, 396, 657-618, 423-396);
        adaugaIncapere(bloc, null, null, 618, 396, 636-618, 405-396);
        adaugaIncapere(bloc, "207", "", 632, 429, 680-632, 468-429);
        adaugaIncapere(bloc, "205", "", 680, 429, 722-680, 468-429);
        adaugaIncapere(bloc, "203", "", 722, 429, 769-722, 462-429);
        adaugaIncapere(bloc, null, null, 769, 455, 810-769, 462-455);
        adaugaIncapere(bloc, "202", "", 769, 379, 810-769, 455-379);
        adaugaIncapere(bloc, null, null, 810, 455, 827-810, 462-455);
        adaugaIncapere(bloc, "Sala de\nFestivități", "", 810, 320, 869-810, 455-320);
        adaugaIncapere(bloc, null, null, 810, 306, 869-810, 320-306);
        adaugaIncapere(bloc, null, null, 810, 251, 869-810, 306-251);
        adaugaIncapere(bloc, "250", "", 572, 236, 626-572, 275-236);
        adaugaIncapere(bloc, "249", "", 572, 199, 617-572, 236-199);
        adaugaIncapere(bloc, "251", "", 626, 236, 663-626, 284-236);
        adaugaIncapere(bloc, "248", "", 640, 199, 663-640, 217-199);

        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 376, 387, 432, 387, 432, 393, 400, 393, 400, 489, 376, 489);
        adaugaIncaperePoligon(bloc, null, "Coridor central, Etaj 2", 400, 467, 605, 467, 605, 423, 632, 423, 632, 467, 716, 467, 716, 489, 400, 489);
        adaugaIncaperePoligon(bloc, null, "Coridor Sala de Festivități", 716, 467, 722, 467, 722, 462, 845, 462, 845, 493, 724, 493, 724, 483, 716, 483);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 632, 429, 632, 423, 656, 423, 656, 371, 663, 371, 663, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 398, 690, 398, 690, 414, 684, 414, 684, 429);
        adaugaIncaperePoligon(bloc, null, "Coridor Operatori, Etaj 2", 617, 199, 617, 236, 626, 236, 626, 212, 627, 212, 627, 236, 663, 236, 663, 226, 638, 226, 638, 217, 640, 217, 640, 199, 627, 199, 627, 202, 626, 202, 626, 199);
    }

    private void asambleazaBlocC3(Pane bloc) {
        adaugaIncapere(bloc, "311", "", 680, 429, 722-680, 463-429);
        adaugaIncapere(bloc, "312", "", 722, 429, 769-722, 461-429);
        adaugaIncapere(bloc, null, null, 769, 437, 790-769, 454-437);
        adaugaIncapere(bloc, "310", "", 804, 462, 845-804, 493-462);
        adaugaIncaperePoligon(bloc, null, null, Color.WHITE, Color.BLACK, 722, 462, 725, 462);
        adaugaIncaperePoligon(bloc, null, null, Color.WHITE, Color.BLACK, 722, 462, 725, 462, 725, 534, 717, 534, 717, 463, 722, 463);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 376, 387, 432, 387, 432, 393, 467, 393, 467, 447, 484, 447, 484, 466, 571, 466, 571, 423, 579, 423, 579, 371, 573, 371, 573, 276, 571, 276, 571, 199, 684, 199, 684, 254, 700, 254, 700, 270, 684, 270, 684, 399, 690, 399, 690, 414, 684, 414, 684, 429, 680, 429, 680, 463, 717, 463, 717, 534, 637, 534, 637, 546, 598, 546, 598, 534, 521, 534, 521, 546, 482, 546, 482, 534, 400, 534, 400, 489, 376, 489);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 725, 493, 845, 493, 845, 534, 764, 534, 764, 546, 725, 546);

        adaugaIncaperePoligon(bloc, null, null, 725, 462, 788, 462, 788, 461, 769, 461, 769, 454, 790, 454, 790, 437, 798, 437, 798, 454, 810, 454, 810, 462, 798, 462, 798, 463, 804, 463, 804, 493, 725, 493);
    }

    private void asambleazaBlocB1(Pane bloc) {
        adaugaIncaperePoligon(bloc, "WC", null, 769, 272, 769, 307, 790, 307, 790, 284, 778, 284, 790, 284, 790, 272);
        adaugaIncaperePoligon(bloc, "WC", null, 790, 272, 790, 284, 801, 284, 790, 284, 790, 307, 810, 307, 810, 272);
        adaugaIncaperePoligon(bloc, null, null, 810, 274, 810, 307, 820, 307, 820, 286, 810, 286, 820, 286, 820, 274);
        adaugaIncaperePoligon(bloc, null, null, 820, 274, 820, 306, 829, 306, 829, 286, 820, 286, 829, 286, 829, 274);
        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 1, bloc B", 751, 251, 751, 272, 769, 272, 769, 267, 769, 272, 810, 272, 810, 268, 810, 274, 829, 274, 829, 134, 810, 134, 810, 255, 810, 251, 769, 251, 769, 256, 769, 251);
        adaugaIncapere(bloc, "134", "", 829, 251, 869-829, 328-251);
        adaugaIncapere(bloc, "136", "", 829, 226, 869-829, 247-226);
        adaugaIncapere(bloc, "138", "", 829, 169, 869-829, 226-169);
        adaugaIncapere(bloc, "135", "", 769, 169, 810-769, 231-169);
        adaugaIncaperePoligon(bloc, null, null, 769, 231, 810, 231, 810, 247, 795, 247, 795, 231, 795, 247, 769, 247);
        adaugaIncaperePoligon(bloc, "VF", "", 769, 134, 795, 134, 795, 160, 810, 160, 810, 169, 769, 169);
        adaugaIncapere(bloc, null, null, 795, 134, 810-795, 160-134);
        adaugaIncaperePoligon(bloc, "VB", "", 841, 134, 869, 134, 869, 169, 829, 169, 829, 160, 841, 160);
        adaugaIncapere(bloc, null, null, 829, 134, 841-829, 160-134);
        adaugaIncaperePoligon(bloc, "Sala Sportivă\n137", "Sala Sportivă", 660, 5, 665, 5, 665, 8, 666, 8, 666, 5, 698, 5, 698, 8, 699, 8, 699, 5, 735, 5, 735, 8, 736, 8, 736, 5, 772, 5, 772, 8, 773, 8, 773, 5, 809, 5, 809, 8, 810, 8, 810, 5, 845, 5, 845, 8, 846, 8, 846, 5, 879, 5, 879, 8, 880, 8, 880, 5, 884, 5, 884, 47, 881, 47, 881, 48, 884, 48, 884, 66, 881, 66, 881, 67, 884, 67, 884, 69, 885, 69, 885, 65, 890, 65, 890, 85, 885, 85, 885, 81, 884, 81, 884, 84, 881, 84, 881, 85, 884, 85, 884, 102, 881, 102, 881, 103, 884, 103, 884, 123, 876, 123, 876, 120, 875, 120, 875, 123, 843, 123, 843, 120, 842, 120, 842, 126, 843, 126, 843, 123, 869, 123, 869, 134, 843, 134, 843, 131, 842, 131, 842, 134, 805, 134, 805, 121, 806, 121, 806, 120, 804, 120, 804, 121, 805, 121, 805, 134, 770, 134, 770, 120, 769, 120, 769, 123, 735, 123, 735, 120, 734, 120, 734, 123, 698, 123, 698, 120, 697, 120, 697, 123, 665, 123, 665, 120, 664, 120, 664, 123, 660, 123, 660, 103, 663, 103, 663, 102, 660, 102, 660, 83, 663, 83, 663, 82, 660, 82, 660, 65, 663, 65, 663, 64, 660, 64, 660, 45, 663, 45, 663, 44, 660, 44, 660, 25, 663, 25, 663, 24, 660, 24);
        adaugaIncaperePoligon(bloc, null, null, 731, 123, 770, 123, 770, 134, 769, 134, 769, 154, 731, 154);
        adaugaIncapere(bloc, null, null, 714, 123, 731-714, 137-123);
        adaugaIncapere(bloc, null, null, 714, 137, 731-714, 154-137);
        adaugaIncapere(bloc, "133", "", 660, 123, 714-660, 154-123);
    }

    private void asambleazaBlocB2(Pane bloc) {
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 660, 154, 660, 5, 884, 5, 884, 123, 869, 123, 869, 134, 769, 134, 769, 154);
        adaugaIncapere(bloc, "243", "", 769, 134, 810-769, 170-134);
        adaugaIncapere(bloc, "241", "", 769, 170, 810-769, 212-170);
        adaugaIncapere(bloc, "239", "", 769, 212, 810-769, 231-212);
        adaugaIncapere(bloc, "238", "", 769, 231, 795-769, 247-231);
        adaugaIncapere(bloc, "244", "", 829, 134, 869-829, 170-134);
        adaugaIncapere(bloc, "242", "", 829, 170, 869-829, 206-170);
        adaugaIncapere(bloc, "240", "", 829, 206, 869-829, 247-206);
        adaugaIncapere(bloc, null, null,751, 251, 769-751, 272-251, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, "245", "", 769, 272, 801-769, 307-272);
        adaugaIncapere(bloc, "246", "", 769, 307, 801-769, 345-307);
        adaugaIncapere(bloc, "247", "", 769, 345, 810-769, 379-345);
        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 2, bloc B", 810, 134, 810, 231, 795, 231, 795, 247, 801, 247, 801, 251, 769, 251, 769, 272, 801, 272, 801, 345, 810, 345, 810, 272, 807, 272, 810, 272, 810, 247, 829, 247, 829, 134);
    }

    private void asambleazaBlocB3(Pane bloc) {
        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 3, bloc B", 798, 454, 810, 454, 810, 272, 808, 272, 810, 272, 810, 251, 769, 251, 769, 272, 800, 272, 800, 307, 798, 307);
        adaugaIncapere(bloc, null, null,751, 251, 769-751, 272-251, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, "316", "", 769, 272, 800-769, 307-272);
        adaugaIncapere(bloc, "315", "", 769, 307, 798-769, 363-307);
        adaugaIncapere(bloc, "314", "", 769, 363, 798-769, 380-363);
        adaugaIncapere(bloc, "313", "", 769, 380, 798-769, 437-380);
        adaugaIncapere(bloc, null, null, 810, 251, 869-810, 454-251, Color.SILVER, Color.BLACK);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 660, 154, 660, 5, 884, 5, 884, 123, 869, 123, 869, 247, 769, 247, 769, 149, 769, 154);
    }

    private void asambleazaBlocE1(Pane bloc) {
        adaugaIncapere(bloc, "128", "", 348, 88, 387-348, 126-88);
        adaugaIncapere(bloc, "127", "", 387, 98, 424-387, 126-98);
        adaugaIncapere(bloc, "129", "", 438, 87, 473-438, 126-87);
        adaugaIncapere(bloc, "130", "", 473, 87, 509-473, 126-87);
        adaugaIncapere(bloc, "131", "", 509, 87, 550-509, 125-87);
        adaugaIncapere(bloc, "132", "", 550, 79, 588-550, 126-79);
        adaugaIncapere(bloc, null, null, 409, 126, 442-409, 134-126);
        adaugaIncapere(bloc, null, null, 442, 126, 459-442, 134-126);
        adaugaIncapere(bloc, null, null, 493, 126, 531-493, 134-126);
        adaugaIncapere(bloc, null, null, 550, 126, 570-550, 134-126);
        adaugaIncaperePoligon(bloc, null, null, 660, 125, 660, 17, 618, 17, 618, 26, 583, 26, 583, 37, 552, 37, 552, 79, 588, 79, 588, 125, 621, 125, 621, 111, 646, 111, 646, 125);
        adaugaIncaperePoligon(bloc, null, null, 387, 88, 387, 98, 425, 98, 425, 125, 438, 125, 438, 88, 425, 88, 425, 91, 424, 91, 424, 88);
    }

    private void asambleazaBlocE2(Pane bloc) {
        adaugaIncapere(bloc, "233", "", 348, 88, 387-348, 126-88);
        adaugaIncapere(bloc, "232", "", 387, 98, 425-387, 126-98);
        adaugaIncapere(bloc, "235", "", 436, 99, 470-436, 126-99);
        adaugaIncapere(bloc, "234", "", 470, 87, 509-470, 126-87);
        adaugaIncapere(bloc, "236", "", 509, 87, 550-509, 126-87);
        adaugaIncapere(bloc, "237", "", 550, 79, 588-550, 126-79);
        adaugaIncapere(bloc, null, null, 409, 126, 442-409, 134-126);
        adaugaIncapere(bloc, null, null, 442, 126, 459-442, 134-126);
        adaugaIncapere(bloc, null, null, 493, 126, 531-493, 134-126);
        adaugaIncapere(bloc, null, null, 550, 126, 570-550, 134-126);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 660, 125, 660, 17, 618, 17, 618, 26, 583, 26, 583, 37, 552, 37, 552, 79, 588, 79, 588, 125, 621, 125, 621, 111, 646, 111, 646, 125);
        adaugaIncaperePoligon(bloc, null, null, 387, 88, 387, 98, 425, 98, 425, 126, 437, 126, 437, 98, 438, 98, 438, 99, 470, 99, 470, 88, 439, 88, 439, 89, 437, 89, 437, 90, 436, 90, 436, 88, 425, 88, 425, 91, 424, 91, 424, 88);
    }

    private void asambleazaBlocE3(Pane bloc) {
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 660, 125, 660, 18, 620, 18, 620, 25, 583, 25, 583, 37, 552, 37, 552, 80, 550, 80, 550, 87, 438, 87, 438, 89, 349, 89, 349, 125, 409, 125, 409, 134, 459, 134, 459, 125, 493, 125, 493, 134, 531, 134, 531, 125, 550, 125, 550, 134, 570, 134, 570, 125, 620, 125, 620, 110, 646, 110, 646, 125);
    }

    private void asambleazaBlocD1(Pane bloc) {
        adaugaIncapere(bloc, null, null, 235, 388, 275-235, 421-388);
        adaugaIncapere(bloc, null, null, 235, 362, 275-235, 388-362);
        adaugaIncapere(bloc, null, null, 235, 336, 275-235, 362-336);
        adaugaIncapere(bloc, "126", "", 235, 292, 275-235, 336-292);
        adaugaIncapere(bloc, "125", "", 245, 251, 275-245, 292-251);
        adaugaIncaperePoligon(bloc, null, null, 235, 239, 235, 292, 245, 292, 245, 251, 275, 251, 275, 239);
        adaugaIncaperePoligon(bloc, null, null, 237, 239, 243, 206, 263, 209, 275, 239);
        adaugaIncaperePoligon(bloc, null, null, 243, 206, 249, 169, 267, 172, 263, 209);
        adaugaIncaperePoligon(bloc, null, null, 249, 169, 253, 143, 270, 145, 267, 172);
        adaugaIncaperePoligon(bloc, null, null, 253, 143, 255, 127, 271, 127, 270, 145);
    }

    private void asambleazaBlocD2(Pane bloc) {
        adaugaIncapere(bloc, null, null, 235, 388, 276-235, 422-388);
        adaugaIncapere(bloc, "231", "", 235, 336, 276-235, 388-336);
        adaugaIncapere(bloc, "230", "", 246, 292, 276-246, 336-292);
        adaugaIncapere(bloc, "229", "", 246, 261, 276-246, 292-261);
        adaugaIncaperePoligon(bloc, "228", "", 246, 249, 246, 261, 276, 261, 276, 239, 252, 239, 252, 249);
        adaugaIncaperePoligon(bloc, null, null, 235, 239, 252, 239, 252, 249, 246, 249, 246, 260, 245, 260, 245, 261, 246, 261, 246, 292, 243, 292, 243, 293, 246, 293, 246, 336, 235, 336, 235, 293, 237, 293, 237, 292, 235, 292);
        adaugaIncaperePoligon(bloc, null, null, 237, 239, 243, 206, 263, 209, 276, 239);
        adaugaIncaperePoligon(bloc, "227", "", 243, 206, 249, 164, 267, 167, 263, 209);
        adaugaIncaperePoligon(bloc, null, null, 249, 164, 253, 143, 270, 145, 267, 167);
        adaugaIncaperePoligon(bloc, null, null, 253, 143, 255, 127, 272, 127, 270, 145);
    }

    private void asambleazaBlocD3(Pane bloc) {
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, 235, 421, 235, 240, 238, 240, 255, 127, 272, 127, 263, 209, 276, 239, 276, 421);
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
        rectangle.setStrokeWidth(2);

        // Numele încăperii (de ex: 112, 123, 118, 213, 250, Sala Sportivă, Bufet, Sala de Lectură)
        Text text = creeazaText(cod, 10);
        afiseazaTextCentrat(text, coordonataX + lungime / 2, coordonataY + latime / 2, lungime - 4, latime - 2);

        // Grupare figura geometrica cu text
        Group grupIncapere = new Group(rectangle, text);
        interactiv(bloc, grupIncapere, rectangle, culoareFundal, cod, nume);
        bloc.getChildren().add(grupIncapere);
    }

    private void adaugaIncaperePoligon(Pane bloc, String cod, String nume, double... puncte) {
        adaugaIncaperePoligon(bloc, cod, nume, Color.WHITE, Color.BLACK, puncte);
    }

    private void adaugaIncaperePoligon(Pane bloc, String cod, String nume, Color culoareFundal, Color culoareBordura, double... puncte) {
        Polygon polygon = new Polygon(puncte);
        polygon.setFill(culoareFundal);
        polygon.setStroke(culoareBordura);
        polygon.setStrokeWidth(2);

        Text text = creeazaText(cod, 10);
        afiseazaTextCentrat(text, polygon.getLayoutBounds().getCenterX(), polygon.getLayoutBounds().getCenterY(), polygon.getLayoutBounds().getWidth() - 4, polygon.getLayoutBounds().getHeight() - 2);

        // Grup de încăpere în formă de poligon cu nume
        Group grupIncapere = new Group(polygon, text);
        interactiv(bloc, grupIncapere, polygon, culoareFundal, cod, nume);
        bloc.getChildren().add(grupIncapere);
    }

    private void adaugaUsa(Pane bloc, double startX, double startY, double endX, double endY) {
        adaugaUsa(bloc, startX, startY, endX, endY, Color.WHITE);
    }

    private void adaugaUsa(Pane bloc, double startX, double startY, double endX, double endY, Color culoare) {
        Line usa = new Line(startX, startY, endX, endY);
        usa.setStroke(culoare);
        usa.setStrokeWidth(4);
        usa.setStrokeLineCap(StrokeLineCap.BUTT);
        usa.setMouseTransparent(true);
        bloc.getChildren().add(usa);
    }

    private void adaugaFereastra(Pane bloc, double startX, double startY, double endX, double endY, int nr_de_straturi){
        adaugaFereastra(bloc, startX, startY, endX, endY, nr_de_straturi, Color.WHITE);
    }

    private void adaugaFereastra(Pane bloc, double startX, double startY, double endX, double endY, int nr_de_straturi, Color culoare) {
        Color culoareFereastra = culoare;
        double grosimeLinie = 1.0;

        double spatiere = 1.5;

        double dx = endX - startX;
        double dy = endY - startY;
        double lungime = Math.sqrt(dx * dx + dy * dy);

        if (lungime == 0) return;

        double normalX = -dy / lungime;
        double normalY = dx / lungime;

        double latimeTotala = (nr_de_straturi - 1) * spatiere;
        double offsetStart = -latimeTotala / 2.0;

        for (int i = 0; i < nr_de_straturi; i++) {
            double offsetCurent = offsetStart + (i * spatiere);

            double x1 = startX + normalX * offsetCurent;
            double y1 = startY + normalY * offsetCurent;
            double x2 = endX + normalX * offsetCurent;
            double y2 = endY + normalY * offsetCurent;

            Line strat = new Line(x1, y1, x2, y2);
            strat.setStroke(culoareFereastra);
            strat.setStrokeWidth(grosimeLinie);
            strat.setStrokeLineCap(StrokeLineCap.BUTT);
            strat.setMouseTransparent(true);

            bloc.getChildren().add(strat);
        }
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

    private void interactiv(Pane bloc, Group grup, Shape forma, Color culoareFundal, String cod, String nume) {
        String cheie = cheieIncapere(cod, nume);
        String literaBloc = bloc.getId().replace("Blocul ", "");
        int etaj = etajCurent;
        if (cheie != null) {
            // Prima încăpere cu o anumită cheie primește marcajele (unele încăperi sunt desenate din mai multe forme).
            incaperi.putIfAbsent(etaj + "|" + literaBloc + "|" + cheie, new Incapere(bloc,
                    forma.getLayoutBounds().getCenterX(), forma.getLayoutBounds().getCenterY()));
        }

        grup.setCursor(Cursor.HAND);
        grup.setOnMouseEntered(e -> forma.setFill(culoareFundal.darker()));
        grup.setOnMouseExited(e -> forma.setFill(culoareFundal));
        grup.setOnMouseClicked(e -> {
            if (modAdaugare) {
                seteazaModAdaugare(false);
                if (cheie == null) {
                    Async.arataEroare("Încăperea selectată nu are cod sau denumire pe hartă.");
                } else {
                    dialogPunctNou(literaBloc, etaj, cheie);
                }
            } else {
                afiseazaSelectie(cod, nume);
            }
        });
    }

    // Identificatorul încăperii salvat în puncte_colectare.incapere: codul (fără ruperile de rând) sau denumirea.
    static String cheieIncapere(String cod, String nume) {
        if (cod != null && !cod.isBlank()) return cod.replace("-\n", "").replace("\n", " ").strip();
        if (nume != null && !nume.isBlank()) return nume.strip();
        return null;
    }

    private void seteazaModAdaugare(boolean activ) {
        modAdaugare = activ;
        btnAdaugaPunctColectare.setText(activ ? "Alege încăperea… (click pentru anulare)" : "+ Punct colectare");
    }

    private void dialogPunctNou(String bloc, int etaj, String incapere) {
        Async.executa(service::categorii, categorii -> {
            if (categorii.isEmpty()) {
                Async.arataEroare("Adăugați mai întâi o categorie de deșeuri.");
                return;
            }
            ComboBox<Categorie> cmbCategorie = new ComboBox<>();
            cmbCategorie.getItems().setAll(categorii);
            cmbCategorie.setValue(categorii.get(0));
            TextField txtDenumire = new TextField();
            txtDenumire.setPromptText("Denumirea coșului");
            cmbCategorie.valueProperty().addListener((obs, vechi, c) ->
                    txtDenumire.setText("Coș " + c.denumire().toLowerCase() + " — " + incapere));
            txtDenumire.setText("Coș " + categorii.get(0).denumire().toLowerCase() + " — " + incapere);

            VBox continut = new VBox(6, new Label("Categoria"), cmbCategorie, new Label("Denumire"), txtDenumire);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Punct de colectare nou");
            dialog.setHeaderText("Bloc " + bloc + ", etajul " + etaj + ", încăperea " + incapere);
            dialog.getDialogPane().setContent(continut);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
                Categorie categorie = cmbCategorie.getValue();
                String denumire = txtDenumire.getText();
                Async.executa(() -> {
                    service.adaugaPunct(denumire, bloc, etaj, incapere, categorie);
                    return null;
                }, v -> reincarcaPuncte());
            });
        });
    }

    // Reîncarcă din baza de date coșurile și le desenează ca cercuri colorate după categorie.
    public void reincarcaPuncte() {
        Async.executa(service::puncte, puncte -> {
            deseneazaPuncte(puncte);
            reincarcaStarea();
        });
    }

    public void reincarcaStarea() {
        // Varianta cu 3 argumente: o pană de rețea nu trebuie să deschidă o alertă la fiecare 5 secunde.
        Async.executa(service::stareCosuri, lista -> {
            Map<Integer, StareCos> nou = new HashMap<>();
            for (StareCos s : lista) nou.put(s.punctId(), s);
            stare = nou;
            for (Integer id : marcajPeId.keySet()) actualizeazaMarcaj(id);
        }, mesaj -> System.err.println("Starea coșurilor nu a putut fi citită: " + mesaj));
    }

    private void deseneazaPuncte(List<PunctColectare> puncte) {
        for (Node marcaj : marcaje) {
            ((Pane) marcaj.getParent()).getChildren().remove(marcaj);
        }
        marcaje.clear();
        marcajPeId.clear(); haloPeId.clear(); tooltipPeId.clear(); punctPeId.clear();

        Map<String, Integer> numarInIncapere = new HashMap<>();
        for (PunctColectare p : puncte) {
            String cheie = p.etaj() + "|" + p.bloc() + "|" + p.incapere();
            Incapere incapere = incaperi.get(cheie);
            if (incapere == null) continue;

            int index = numarInIncapere.merge(cheie, 1, Integer::sum) - 1;
            Circle marcaj = new Circle(incapere.centruX() - 6 + index * 16, incapere.centruY() + 9, 4.5,
                    Color.web(p.categorie().culoare()));
            marcaj.setStroke(Color.WHITE);
            marcaj.setStrokeWidth(1.2);

            Circle halo = new Circle(marcaj.getCenterX(), marcaj.getCenterY(), 7.5, Color.TRANSPARENT);
            halo.setMouseTransparent(true);
            Tooltip tooltip = new Tooltip();
            Tooltip.install(marcaj, tooltip);

            if (esteAdmin) {
                marcaj.setCursor(Cursor.HAND);
                marcaj.setOnMouseClicked(e -> {
                    e.consume();
                    meniuAdmin(p).show(marcaj, e.getScreenX(), e.getScreenY());
                });
            } else {
                marcaj.setMouseTransparent(true);
            }

            incapere.bloc().getChildren().addAll(halo, marcaj);
            marcaje.add(halo);
            marcaje.add(marcaj);
            marcajPeId.put(p.id(), marcaj);
            haloPeId.put(p.id(), halo);
            tooltipPeId.put(p.id(), tooltip);
            punctPeId.put(p.id(), p);
            actualizeazaMarcaj(p.id());
        }
    }

    private ContextMenu meniuAdmin(PunctColectare p) {
        MenuItem leaga = new MenuItem("Leagă senzor…");
        leaga.setOnAction(e -> dialogSenzor(p));

        MenuItem dezleaga = new MenuItem("Dezleagă senzorul");
        dezleaga.setDisable(!stare.containsKey(p.id()));
        dezleaga.setOnAction(e -> Async.executa(() -> {
            service.dezleagaSenzor(p);
            return null;
        }, v -> reincarcaStarea()));

        MenuItem sterge = new MenuItem("Șterge punctul…");
        sterge.setOnAction(e -> {
            Alert confirmare = new Alert(Alert.AlertType.CONFIRMATION,
                    "Ștergi punctul „" + p.denumire() + "”? Înregistrările vechi rămân în istoric.",
                    ButtonType.YES, ButtonType.NO);
            confirmare.setHeaderText("Ștergere punct de colectare");
            confirmare.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> Async.executa(() -> {
                service.stergePunct(p.id());
                return null;
            }, v -> reincarcaPuncte()));
        });
        return new ContextMenu(leaga, dezleaga, sterge);
    }

    private void dialogSenzor(PunctColectare p) {
        StareCos curent = stare.get(p.id());
        TextField txtCod = new TextField(curent == null ? "" : curent.dispozitiv());
        txtCod.setPromptText("ex. BIN-C1-01 (ca în firmware)");
        ComboBox<Integer> cmbCanal = new ComboBox<>();
        cmbCanal.getItems().setAll(1, 2, 3);
        cmbCanal.setValue(curent == null ? 1 : curent.canal());
        TextField txtCapacitate = new TextField(curent == null ? "10" : String.valueOf(curent.capacitateKg()));
        TextField txtGol = new TextField("350");   // distanța afișată pe OLED cu coșul gol
        TextField txtPlin = new TextField("50");   // distanța la nivelul la care îl considerați plin

        VBox continut = new VBox(6, new Label("Cod dispozitiv"), txtCod, new Label("Canal (compartiment)"), cmbCanal,
                new Label("Capacitate (kg)"), txtCapacitate, new Label("Distanță coș gol (mm)"), txtGol,
                new Label("Distanță coș plin (mm)"), txtPlin);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Senzor pentru coș");
        dialog.setHeaderText(p.denumire());
        dialog.getDialogPane().setContent(continut);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> {
            String cod = txtCod.getText();
            Integer canal = cmbCanal.getValue();
            try {
                BigDecimal capacitate = new BigDecimal(txtCapacitate.getText().trim().replace(',', '.'));
                int gol = Integer.parseInt(txtGol.getText().trim());
                int plin = Integer.parseInt(txtPlin.getText().trim());
                Async.executa(() -> {
                    service.leagaSenzor(p, cod, canal, capacitate, gol, plin);
                    return null;
                }, v -> reincarcaStarea());
            } catch (NumberFormatException ex) {
                Async.arataEroare("Capacitatea și distanțele trebuie să fie numere.");
            }
        });
    }

    private void actualizeazaMarcaj(int punctId) {
        Circle halo = haloPeId.get(punctId);
        Tooltip tooltip = tooltipPeId.get(punctId);
        PunctColectare p = punctPeId.get(punctId);
        if (halo == null || tooltip == null || p == null) return;

        StringBuilder text = new StringBuilder(p.denumire()).append('\n').append(p.categorie().denumire());
        StareCos s = stare.get(punctId);
        halo.getStrokeDashArray().clear();
        halo.setStrokeWidth(2.4);
        if (s == null) {
            halo.setStroke(Color.TRANSPARENT);
        } else if (!s.arePrimitDate()) {
            halo.setStroke(INEL_GRI);
            halo.getStrokeDashArray().setAll(3.0, 2.5);
            text.append("\nSenzor legat, încă fără date");
        } else if (!s.esteOnline()) {
            halo.setStroke(INEL_GRI);
            halo.getStrokeDashArray().setAll(3.0, 2.5);
            text.append("\nFără semnal de ").append(s.vechimeSecunde()).append(" s");
        } else {
            Integer pct = s.umplerePct();
            halo.setStroke(pct == null ? INEL_GRI : pct >= 85 ? INEL_ROSU : pct >= 60 ? INEL_PORTOCALIU : INEL_VERDE);
            text.append("\nUmplere: ").append(pct == null ? "necunoscută" : pct + "%");
            if (s.greutateG() != null) {
                text.append(String.format(java.util.Locale.ROOT, "\nGreutate: %.2f kg", s.greutateG() / 1000.0));
            }
            text.append("\nActualizat acum ").append(s.vechimeSecunde()).append(" s");
        }
        if (esteAdmin) text.append("\n(click pentru meniu)");
        tooltip.setText(text.toString());
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