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
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import md.ecosort.ecosort.model.Categorie;
import md.ecosort.ecosort.model.PunctColectare;
import md.ecosort.ecosort.model.StareCos;
import md.ecosort.ecosort.service.ColectareService;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.effect.DropShadow;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

import java.math.BigDecimal;
import java.util.*;

public class InteractiveMap extends BorderPane {

    private static final double VIRTUAL_WIDTH = 1280;
    private static final double VIRTUAL_HEIGHT = 720;
    private static final int numar_etaje = 3;

    private static final double[][] Coordonate_bloc_A = {
            {110, 387, 110, 176, 138, 176, 138, 169, 149, 169, 149, 176, 201, 176, 201, 387},
            {110, 387, 110, 176, 138, 176, 138, 169, 149, 169, 149, 176, 201, 176, 201, 387},
            {110, 387, 110, 176, 138, 176, 138, 169, 149, 169, 149, 176, 201, 176, 201, 387}
    };
    private static final double[][] Coordonate_bloc_B = {
            {539, 307, 639, 307, 639, 122, 654, 122, 654, 5, 430, 5, 430, 154, 539, 154, 539, 251, 521, 251, 521, 272, 539, 272},
            {539, 380, 580, 380, 580, 251, 639, 251, 639, 122, 654, 122, 654, 5, 430, 5, 430, 154, 539, 154, 539, 251, 521, 251, 521, 272, 539, 272},
            {539, 437, 568, 437, 568, 454, 639, 454, 639, 251, 639, 122, 654, 122, 654, 5, 430, 5, 430, 154, 539, 154, 539, 251, 521, 251, 521, 272, 539, 272}
    };
    private static final double[][] Coordonate_bloc_C = {
            {146, 387, 201, 387, 201, 394, 236, 394, 236, 447, 254, 447, 254, 467, 341, 467, 341, 423, 350, 423, 350, 370, 344, 370, 344, 272, 342, 272, 342, 199, 454, 199, 454, 254, 470, 254, 470, 270, 454, 270, 454, 399, 458, 399, 458, 414, 454, 414, 454, 429, 539, 429, 539, 307, 639, 307, 639, 456, 597, 456, 597, 462, 615, 462, 615, 534, 535, 534, 535, 545, 495, 545, 495, 534, 406, 534, 406, 545, 369, 545, 369, 533, 290, 533, 290, 545, 253, 545, 253, 534, 170, 534, 170, 490, 146, 490},
            {146, 387, 201, 387, 201, 394, 236, 394, 236, 447, 254, 447, 254, 467, 341, 467, 341, 423, 350, 423, 350, 370, 344, 370, 344, 275, 341, 275, 341, 199, 454, 199, 454, 254, 470, 254, 470, 270, 454, 270, 454, 399, 458, 399, 458, 414, 454, 414, 454, 429, 539, 429, 539, 380, 580, 379, 580, 251, 639, 251, 639, 456, 597, 456, 597, 462, 615, 462, 615, 534, 535, 534, 535, 545, 495, 545, 495, 534, 406, 534, 406, 545, 369, 545, 369, 533, 290, 533, 290, 545, 253, 545, 253, 534, 170, 534, 170, 490, 146, 490},
            {146, 387, 201, 387, 201, 394, 236, 394, 236, 447, 254, 447, 254, 467, 341, 467, 341, 423, 350, 423, 350, 370, 344, 370, 344, 275, 341, 275, 341, 199, 454, 199, 454, 254, 470, 254, 470, 270, 454, 270, 454, 399, 458, 399, 458, 414, 454, 414, 454, 429, 539, 429, 539, 437, 568, 437, 568, 454, 580, 454, 580, 462, 615, 462, 615, 534, 535, 534, 535, 545, 495, 545, 495, 534, 406, 534, 406, 545, 369, 545, 369, 533, 290, 533, 290, 545, 253, 545, 253, 534, 170, 534, 170, 490, 146, 490}
    };
    private static final double[][] Coordonate_bloc_D = {
            {5, 421, 5, 240, 8, 240, 25, 127, 42, 127, 33, 209, 46, 239, 46, 421},
            {5, 421, 5, 240, 8, 240, 25, 127, 42, 127, 33, 209, 46, 239, 46, 421},
            {5, 421, 5, 240, 8, 240, 25, 127, 42, 127, 33, 209, 46, 239, 46, 421}
    };
    private static final double[][] Coordonate_bloc_E = {
            {430, 125, 430, 18, 390, 18, 390, 25, 353, 25, 353, 37, 322, 37, 322, 80, 320, 80, 320, 87, 208, 87, 208, 89, 119, 89, 119, 125, 179, 125, 179, 134, 229, 134, 229, 125, 263, 125, 263, 134, 301, 134, 301, 125, 320, 125, 320, 134, 340, 134, 340, 125, 390, 125, 390, 110, 416, 110, 416, 125},
            {430, 125, 430, 18, 390, 18, 390, 25, 353, 25, 353, 37, 322, 37, 322, 80, 320, 80, 320, 87, 208, 87, 208, 89, 119, 89, 119, 125, 179, 125, 179, 134, 229, 134, 229, 125, 263, 125, 263, 134, 301, 134, 301, 125, 320, 125, 320, 134, 340, 134, 340, 125, 390, 125, 390, 110, 416, 110, 416, 125},
            {430, 125, 430, 18, 390, 18, 390, 25, 353, 25, 353, 37, 322, 37, 322, 80, 320, 80, 320, 87, 208, 87, 208, 89, 119, 89, 119, 125, 179, 125, 179, 134, 229, 134, 229, 125, 263, 125, 263, 134, 301, 134, 301, 125, 320, 125, 320, 134, 340, 134, 340, 125, 390, 125, 390, 110, 416, 110, 416, 125}
    };


    private final List<Pane> etaje = new ArrayList<>();
    private final Label lblEtaj = new Label();
    private final BorderPane toolBar = new BorderPane();
    private final Scale mapScale = new Scale(1, 1);

    // Încăperile care pot găzdui un punct de colectare, după cheia "etaj|bloc|încăpere".
    private record Incapere(Pane bloc, double centruX, double centruY, String cod, String nume, Shape forma) { }
    private final ComboBox<String> comboIncaperi = new ComboBox<>();
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

    private final StackPane containerHarta = new StackPane();
    private final Pane viewport = new Pane();

    private static final double ZOOM_MAX = 6;
    private static final double PAS_ZOOM = 1.25;
    private static final Color CULOARE_ICON = Color.web("#065f46");
    private static final String SVG_ZOOM_IN = "M23.957 41.77a18.02 18.02 0 0 0 10.477-3.376l11.109 11.11a2.66 2.66 0 0 0 1.898.773c1.524 0 2.625-1.172 2.625-2.672c0-.703-.234-1.359-.75-1.874L38.277 34.668c2.32-3.047 3.703-6.82 3.703-10.922c0-9.914-8.109-18.023-18.023-18.023c-9.937 0-18.023 8.109-18.023 18.023S14.02 41.77 23.957 41.77m0-3.891c-7.758 0-14.133-6.398-14.133-14.133S16.2 9.613 23.957 9.613c7.734 0 14.133 6.399 14.133 14.133c0 7.735-6.399 14.133-14.133 14.133m1.898-7.195v-5.18h4.735c.96 0 1.781-.82 1.781-1.758c0-.937-.82-1.758-1.781-1.758h-4.735v-5.18c0-1.124-.914-1.804-1.898-1.804s-1.922.68-1.922 1.805v5.18h-4.734c-.961 0-1.758.82-1.758 1.757c0 .938.797 1.758 1.758 1.758h4.734v5.18c0 1.125.938 1.804 1.922 1.804s1.898-.68 1.898-1.804";
    private static final String SVG_ZOOM_OUT = "M23.957 41.77a18.02 18.02 0 0 0 10.477-3.376l11.109 11.11a2.66 2.66 0 0 0 1.898.773c1.524 0 2.625-1.172 2.625-2.672c0-.703-.234-1.359-.75-1.874L38.277 34.668c2.32-3.047 3.703-6.82 3.703-10.922c0-9.914-8.109-18.023-18.023-18.023c-9.937 0-18.023 8.109-18.023 18.023S14.02 41.77 23.957 41.77m0-3.891c-7.758 0-14.133-6.398-14.133-14.133S16.2 9.613 23.957 9.613c7.734 0 14.133 6.399 14.133 14.133c0 7.735-6.399 14.133-14.133 14.133m-6.656-12.375H30.59c.96 0 1.781-.82 1.781-1.758c0-.937-.82-1.758-1.781-1.758H17.3c-.96 0-1.757.82-1.757 1.758s.797 1.758 1.758 1.758";

    private double zoom = 1;
    private double scaraBaza = 1;
    private double tx, ty;
    private double ancoraX, ancoraY, ancoraTx, ancoraTy;
    private boolean seTrage = false;

    private static final Color INEL_VERDE = Color.web("#16a34a"), INEL_PORTOCALIU = Color.web("#f59e0b"), INEL_ROSU = Color.web("#dc2626"), INEL_GRI = Color.web("#9ca3af");
    private static final Color BARA_BORDURA = Color.web("#e5e7eb");
    private static final Color CAMP_BORDURA = Color.web("#d1d5db");
    private static final Color PASTILA_FUNDAL = Color.web("#f3f4f6");
    private static final Color TEXT_INCHIS = Color.web("#1f2937");
    private static final Color VERDE = Color.web("#059669");
    private static final Color VERDE_INCHIS = Color.web("#065f46");
    private record TemaButon(Color fundal, Color hover, Color apasat, Color text, Color bordura) { }
    private static final TemaButon TEMA_VERDE = new TemaButon(VERDE, Color.web("#047857"), VERDE_INCHIS, Color.WHITE, VERDE_INCHIS);
    private static final TemaButon TEMA_ANULARE = new TemaButon(Color.web("#f59e0b"), Color.web("#d97706"), Color.web("#b45309"), Color.WHITE, Color.web("#b45309"));
    private static final TemaButon TEMA_ETAJ_ACTIV = new TemaButon(VERDE, Color.web("#047857"), VERDE_INCHIS, Color.WHITE, null);
    private static final TemaButon TEMA_IN_PASTILA = new TemaButon(Color.TRANSPARENT, Color.WHITE, Color.web("#e5e7eb"), TEXT_INCHIS, null);
    private static final String CHEIE_TEMA = "tema-buton";
    private final List<Button> butoaneEtaj = new ArrayList<>();

    private static final Color RADAR_CULOARE = Color.web("#22c55e");
    private static final int RADAR_INELE = 3;
    private static final double RADAR_RAZA = 34;
    private static final double RADAR_DURATA_INEL = 1.0;
    private static final double RADAR_INTARZIERE = 0.28;
    private static final int CLIPIRI = 4;
    private static final double CLIPIRE_DURATA = 2.0;
    private Animation efectCurent;
    private final List<Node> nodurileEfectului = new ArrayList<>();
    private final Map<String, Incapere> incaperiDinCombo = new HashMap<>();

    private final HBox containerLegenda = new HBox(20);
    private Categorie categorieSelectataPentruPulsatie = null;
    private Timeline timelinePulsatie = null;

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
            if (i == 0) asambleazaTeritoriu(etaj);

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

        containerHarta.getChildren().addAll(etaje);
        containerHarta.setMinSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        containerHarta.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        containerHarta.setMaxSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        containerHarta.getTransforms().add(mapScale);

        viewport.getChildren().add(containerHarta);
        viewport.setMinSize(0, 0);
        Rectangle decupaj = new Rectangle();
        decupaj.widthProperty().bind(viewport.widthProperty());
        decupaj.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(decupaj);
        viewport.layoutBoundsProperty().addListener((obs, veche, noua) -> redimensioneazaHarta(noua.getWidth(), noua.getHeight()));
        configureazaZoomSiDeplasare();

        stilizeazaButon(btnAdaugaPunctColectare, TEMA_VERDE);
        btnAdaugaPunctColectare.setVisible(esteAdmin);
        btnAdaugaPunctColectare.setManaged(esteAdmin);
        btnAdaugaPunctColectare.setOnAction(e -> seteazaModAdaugare(!modAdaugare));
        HBox unelteEco = new HBox(10, btnAdaugaPunctColectare);
        unelteEco.setAlignment(Pos.CENTER_LEFT);

        comboIncaperi.setPromptText("Caută în colegiu ...");
        comboIncaperi.setPrefWidth(400);
        comboIncaperi.setEditable(false);
        comboIncaperi.setFocusTraversable(false);
        stilizeazaCautare(comboIncaperi);

        comboIncaperi.valueProperty().addListener((obs, veche, nou) -> {
            Incapere inc = nou == null ? null : incaperiDinCombo.get(nou);
            if (inc != null) evidentiazaIncapere(inc);
        });
        comboIncaperi.setCellFactory(lista -> {
            ListCell<String> celula = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean gol) {
                    super.updateItem(item, gol);
                    setText(gol ? null : item);
                }
            };
            celula.setOnMouseReleased(e -> {
                if (e.getButton() != MouseButton.PRIMARY || celula.isEmpty()) return;
                if (celula.getItem().equals(comboIncaperi.getValue())) {
                    Incapere inc = incaperiDinCombo.get(celula.getItem());
                    if (inc != null) evidentiazaIncapere(inc);
                }
            });
            return celula;
        });

        Button btnZoomIn = butonZoom(SVG_ZOOM_IN, true, "Mărește (sau rotița mouse-ului)");
        btnZoomIn.setOnAction(e -> zoomLa(viewport.getWidth() / 2, viewport.getHeight() / 2, PAS_ZOOM));
        Button btnZoomOut = butonZoom(SVG_ZOOM_OUT, false, "Micșorează");
        btnZoomOut.setOnAction(e -> zoomLa(viewport.getWidth() / 2, viewport.getHeight() / 2, 1 / PAS_ZOOM));

        HBox unelteCautare = new HBox(12, comboIncaperi, pastila(btnZoomIn, btnZoomOut));
        unelteCautare.setAlignment(Pos.CENTER);

        for (int i = 0; i < numar_etaje; i++) {
            final int nr_etaj = i + 1;
            Button btn = new Button("Etaj " + nr_etaj);
            btn.setOnAction(e -> afiseazaEtaj(nr_etaj));
            butoaneEtaj.add(btn);
        }
        HBox navigare = pastila(butoaneEtaj.toArray(new Node[0]));

        stilizeazaBara();
        toolBar.setLeft(unelteEco);
        toolBar.setCenter(unelteCautare);
        toolBar.setRight(navigare);
        BorderPane.setAlignment(unelteEco, Pos.CENTER_LEFT);
        BorderPane.setAlignment(navigare, Pos.CENTER_RIGHT);

        StackPane map = new StackPane(viewport);
        map.setAlignment(Pos.TOP_CENTER);

        containerLegenda.setAlignment(Pos.CENTER);
        containerLegenda.setPadding(new Insets(15, 0, 0, 0));
        VBox mapController = new VBox();
        mapController.getChildren().addAll(toolBar, map, containerLegenda);
        mapController.setAlignment(Pos.TOP_CENTER);
        mapController.setSpacing(0);
        mapController.setPadding(new Insets(25));
        VBox.setMargin(toolBar, new Insets(0, 0, 12, 0));
        mapController.setAlignment(Pos.CENTER);
        VBox.setVgrow(map, Priority.ALWAYS);
        this.setCenter(mapController);

        afiseazaEtaj(1);
        Async.executa(service::categorii, this::construiesteLegenda);
    }

    private Pane asambleazaTeritoriu(Pane etaj) {
        Pane teritoriu = new Pane();
        teritoriu.setId("Teritoriu");
        teritoriu.setPickOnBounds(false);
        etaj.getChildren().add(teritoriu);

        adaugaIncaperePoligon(teritoriu, "Teritoriu\ninterior", "Teritoriu interior", Color.web("#e9ebee"), Color.web("#cdd2d8"), false, 46, 388, 110, 388, 110, 176, 202, 176, 202, 393, 236, 393, 236, 466, 342, 466, 342, 424, 351, 432, 351, 369, 454, 199, 454, 430, 539, 430, 539, 154, 430, 154, 430, 126, 416, 125, 416, 111, 416, 89, 25, 89, 25, 127, 41, 127, 32, 208, 45, 239);
        adaugaIncaperePoligon(teritoriu, "", "Trotuar fațadă", Color.web("#f3f4f6"), Color.web("#d9dde2"), false, 170, 534, 170, 580, 615, 580, 615, 534);
        adaugaIncaperePoligon(teritoriu, null, null, Color.web("#eef9f0"), Color.web("#d3ead9"), true, 171, 535, 171, 539, 173, 542, 251, 542, 251, 535);
        adaugaIncaperePoligon(teritoriu, null, null, Color.web("#eef9f0"), Color.web("#d3ead9"), true, 292, 535, 292, 542, 367, 542, 367, 535);
        adaugaIncaperePoligon(teritoriu, null, null, Color.web("#eef9f0"), Color.web("#d3ead9"), true, 408, 535, 408, 542, 493, 542, 493, 535);
        adaugaIncaperePoligon(teritoriu, null, null, Color.web("#eef9f0"), Color.web("#d3ead9"), true, 535, 535, 535, 542, 613, 542, 615, 540, 615, 535);
        adaugaIncaperePoligon(teritoriu, "", "Trotuar lateral", Color.web("#f3f4f6"), Color.web("#d9dde2"), false, 615, 580, 615, 462, 597, 462, 597, 455, 639, 455, 639, 134, 637, 134, 637, 123, 654, 123, 654, 5, 730, 5, 730, 580);
        adaugaIncaperePoligon(teritoriu, "Parcare\n1", "Parcare 1", Color.web("#e9ebee"), Color.web("#cdd2d8"), false, 615, 580, 615, 715, 730, 715, 730, 580);
        adaugaIncaperePoligon(teritoriu, "Parcare\n2", "Parcare 2", Color.web("#e9ebee"), Color.web("#cdd2d8"), false, 5, 421, 5, 715, 170, 715, 170, 489, 146, 489, 146, 387, 45, 387, 45, 421);
        adaugaIncaperePoligon(teritoriu, null, null, Color.web("#eef9f0"), Color.web("#d3ead9"), true, 5, 421, 5, 427, 7, 430, 44, 430, 46, 427, 46, 421);
        adaugaIncaperePoligon(teritoriu, "Parc", "Parc", Color.web("#eef9f0"), Color.web("#d3ead9"), false, 170, 580, 615, 580, 615, 715, 170, 715);
        adaugaIncaperePoligon(teritoriu, "", "Trotuar teren de sport", Color.web("#f3f4f6"), Color.web("#d9dde2"), false, 730, 527, 880, 527, 880, 547, 750, 547, 750, 715, 730, 715);
        adaugaIncaperePoligon(teritoriu, "Tennis Court", "Tennis Court", Color.web("#f8dcc0"), Color.web("#e8b98a"), false, 750, 547, 880, 547, 880, 715, 750, 715);
        adaugaIncaperePoligon(teritoriu, "Teren de Sport\n1", "Teren de Sport 1", Color.web("#eef9f0"), Color.web("#d3ead9"), false, 880, 527, 880, 715, 1075.5, 715, 1075.5, 527);
        adaugaIncaperePoligon(teritoriu, "Teren de Sport\n2", "Teren de Sport 2", Color.web("#eef9f0"), Color.web("#d3ead9"), false, 1075.5, 527, 1075.5, 715, 1275, 715, 1275, 527);

        return teritoriu;
    }

    private void asambleazaBlocA1(Pane bloc) {
        adaugaIncapere(bloc, null, "A doua scară, Etaj 1, A", 110, 176, 39, 19);
        adaugaIncaperePoligon(bloc, "WC", "WC-B-A1", Color.WHITE, Color.BLACK, false, 110, 195, 149, 195, 149, 206, 134, 206, 134, 214, 110, 214);
        adaugaIncaperePoligon(bloc, null, "WC-Persoane-cu-necesități-speciale-A1", Color.WHITE, Color.BLACK, false, 134, 206, 149, 206, 149, 216, 134, 216);
        adaugaIncaperePoligon(bloc, "WC", "WC-F-A1", Color.WHITE, Color.BLACK, false, 110, 214, 134, 214, 134, 216, 149, 216, 149, 232, 110, 232);
        adaugaIncapere(bloc, "124", "Matematică", 110, 232, 39, 51);
        adaugaIncapere(bloc, "122", "Matematică", 110, 283, 39, 51);
        adaugaIncapere(bloc, "120", "Metodist Olga Jumbei", 110, 335, 39, 33);
        adaugaIncapere(bloc, null, "Prima scară, Etaj 1, A", 110, 368, 39, 19);
        adaugaIncapere(bloc, "123", "Matematică", 163, 176, 39, 54);
        adaugaIncapere(bloc, "121", "Limba și Literatura Română", 163, 230, 39, 52);
        adaugaIncapere(bloc, "119", "Istorie", 163, 282, 39, 54);
        adaugaIncapere(bloc, "117", "Istorie", 163, 336, 39, 51);

        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 1, bloc A", 149, 176, 164, 176, 164, 387, 149, 387);
    }

    private void asambleazaBlocA2(Pane bloc) {
        adaugaIncapere(bloc, null, "A doua scară, Etaj 2, A", 110, 176, 39, 19);
        adaugaIncaperePoligon(bloc, "WC", "WC-B-A2", Color.WHITE, Color.BLACK, false, 110, 195, 149, 195, 149, 206, 134, 206, 134, 214, 110, 214);
        adaugaIncaperePoligon(bloc, null, "WC-Persoane-cu-necesități-speciale-A2", Color.WHITE, Color.BLACK, false, 134, 206, 149, 206, 149, 216, 134, 216);
        adaugaIncaperePoligon(bloc, "WC", "WC-F-A2", Color.WHITE, Color.BLACK, false, 110, 214, 134, 214, 134, 216, 149, 216, 149, 232, 110, 232);
        adaugaIncapere(bloc, "224", "Limba și Literatura Română", 110, 232, 39, 51);
        adaugaIncapere(bloc, "222", "Limba și Literatura Română", 110, 283, 39, 51);
        adaugaIncapere(bloc, "220", "Limba franceză", 110, 335, 39, 33);
        adaugaIncapere(bloc, null, "Prima scară, Etaj 2, A", 110, 368, 39, 19);
        adaugaIncapere(bloc, "226", "Limba engleză", 163, 176, 39, 38);
        adaugaIncapere(bloc, "225", "Servicii admnistrative", 163, 214, 39, 51);
        adaugaIncapere(bloc, "223", "Secția didactică 4 (Șef: Vasile Melnic)", 163, 265, 39, 17);
        adaugaIncapere(bloc, "221", "Servicii administrative", 163, 282, 39, 53);
        adaugaIncapere(bloc, "219", "Istorie", 163, 335, 39, 52);

        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 2, bloc A", 149, 176, 163, 176, 164, 387, 149, 387);
    }
    private void asambleazaBlocA3(Pane bloc) {
        adaugaIncapere(bloc, null, "A doua scară, Etaj 3, A", 110, 176, 39, 19);
        adaugaIncapere(bloc, "308", "Catedra Științe reale", 110, 194, 39, 38);
        adaugaIncapere(bloc, "306", "Chimie", 110, 232, 39, 51);
        adaugaIncapere(bloc, "304", "Limba și Literatura Română", 110, 283, 39, 51);
        adaugaIncapere(bloc, "302", "Limba engleză", 110, 335, 39, 33);
        adaugaIncapere(bloc, null, "Prima scară, Etaj 3, A", 110, 368, 39, 19);
        adaugaIncapere(bloc, "309", "Limba engleză", 163, 176, 39, 38);
        adaugaIncapere(bloc, "307", "Laboratorul de chimie", 163, 214, 39, 16);
        adaugaIncapere(bloc, "305", "Geografie", 163, 230, 39, 52);
        adaugaIncapere(bloc, "303", "Geografie", 163, 282, 39, 53);
        adaugaIncapere(bloc, "301", "Limba și Literatura Română", 163, 335, 39, 52);

        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 3, bloc A", 149, 176, 164, 176, 164, 387, 149, 387);
    }

    private void asambleazaBlocC1(Pane bloc) {
        adaugaIncapere(bloc, "115", "Comitetul sindical al elevilor", 170, 394, 212-170, 412-394);
        adaugaIncapere(bloc, "113", "Firma de exercițiu", 170, 412, 212-170, 467-412);
        adaugaIncapere(bloc, "111", "Catedra Istorie și științe socioumane", 212, 431, 236-212, 467-431);
        adaugaIncapere(bloc, null, null, 212, 394, 236-212, 431-394);
        adaugaIncapere(bloc, "118", "Limba Engleză", 170, 490,  207-170, 534-490);
        adaugaIncapere(bloc, "116", "Matematică", 206, 490, 253-206, 534-490);
        adaugaIncapere(bloc, null, null, 237, 447, 255-237, 467-447);
        adaugaIncapere(bloc, "114", "Matematică", 253, 490, 291-253, 545-490);
        adaugaIncapere(bloc, "112", "Biologie", 291, 490, 344-291, 533-490);
        adaugaIncapere(bloc, "110", "Catedra Matematică", 344, 490, 369-344, 533-490);
        adaugaIncapere(bloc, null, "Rcepție", 369, 490, 407-369, 546-490);
        adaugaIncapere(bloc, null, null, 369, 527, 381-369, 546-527);
        adaugaIncapere(bloc, null, null, 394, 527, 407-394, 546-527);
        adaugaIncapere(bloc, "109", "Catedra Limba și Literatura română", 341, 423, 375-341, 467-423);
        adaugaIncapere(bloc, null, null, 351, 402, 377-351, 423-402, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 351, 370, 377-351, 402-370, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 389, 390, 426-389, 423-390, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 389, 370, 426-389, 390-370, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 377, 370, 390-377, 423-370, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, "Bufet", "Bufet", 345, 305, 389-345, 370-305, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, "Bufet", 345, 285, 390-345, 305-285, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, null, null, 389, 285, 432-389, 370-285, Color.SILVER, Color.BLACK);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 403, 285, 403, 301, 406, 304, 432, 304, 432, 285);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 406, 304, 406, 332, 432, 332, 432, 304);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 406, 332, 406, 337, 402, 337, 402, 348, 432, 348, 432, 332);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 399, 348, 399, 370, 432, 370, 432, 348);
        adaugaIncapere(bloc, "107", "Secția Formare continuă", 402, 429, 437-402, 467-429);
        adaugaIncapere(bloc, "105", "Laborator de Informatică 4", 450, 429, 492-450, 467-429);
        adaugaIncapere(bloc, "103", "Laborator de Informatică 2", 492, 429, 538-492, 462-429);
        adaugaIncapere(bloc, null, null, 538, 455, 577-538, 462-455);
        adaugaIncaperePoligon(bloc, "Sala de\nLectură", "Sala de Lectură", Color.WHITE, Color.BLACK, false, 539, 455, 539, 307, 597, 307, 597, 440, 577, 440, 577, 455);
        adaugaIncapere(bloc, null, null, 577, 455, 597-577, 462-455);
        adaugaIncapere(bloc, null, null, 577, 440, 597-577, 455-440);
        adaugaIncapere(bloc, "101", "Biblioteca Sala împrumut", 597, 381, 639-597, 455-381);
        adaugaIncapere(bloc, "Bibli-\notecă", "Bibliotecă", 597, 328, 639-597, 382-328);
        adaugaIncapere(bloc, null, null, 597, 307, 639-597, 328-307);
        adaugaIncapere(bloc, "108", "Laborator de Informatică 6", 407, 490, 450-407, 533-490);
        adaugaIncapere(bloc, "106", "Laborator de Informatică 5", 450, 490, 487-450, 533-490);
        adaugaIncapere(bloc, null, null, 487, 483, 495-487, 534-483);
        adaugaIncapere(bloc, null, null, 495, 493, 510-495, 512-493);
        adaugaIncapere(bloc, null, null, 510, 493, 534-510, 512-493);
        adaugaIncapere(bloc, "104", "Laborator de Informatică 3", 495, 512, 534-495, 545-512);
        adaugaIncapere(bloc, "102", "Laborator de Informatică 1", 534, 493, 615-534, 534-493);adaugaIncapere(bloc, null, null, 345, 272, 396-345, 285-272);
        adaugaIncapere(bloc, "140", "Atelierul de lucru 1", 342, 236, 396-342, 272-236);
        adaugaIncapere(bloc, "139", "Sala de clasă", 342, 199, 387-342, 236-199);
        adaugaIncapere(bloc, "141", "Atelierul de lucru 2", 396, 236, 432-396, 285-236);
        adaugaIncapere(bloc, null, null, 408, 217, 432-408, 226-217);
        adaugaIncapere(bloc, null, null, 410, 199, 432-410, 217-199);

        adaugaIncaperePoligon(bloc, null, "Coridor central-lateral, Etaj 1", 146, 387, 202, 387, 202, 393, 170, 393, 170, 489, 146, 489);
        adaugaIncaperePoligon(bloc, null, "Coridor central, Etaj 1", 170, 467, 375, 467, 375, 423, 402, 423, 402, 467, 487, 467, 487, 489, 170, 489);
        adaugaIncaperePoligon(bloc, null, "Coridor Sala de Lectură", 487, 467, 492, 467, 492, 462, 615, 462, 615, 493, 495, 493, 495, 483, 487, 483);
        adaugaIncaperePoligon(bloc, null, "Coridor Forneti", 437, 467, 437, 429, 402, 429, 402, 423, 426, 423, 426, 371, 433, 371, 433, 199, 454, 199, 454, 254, 470, 254, 470, 270, 454, 270, 454, 398, 460, 398, 460, 414, 454, 414, 454, 429, 447, 429, 447, 430, 450, 430, 450, 467);
        adaugaIncaperePoligon(bloc, null, "Coridor Operatori, Etaj 1", 387, 199, 387, 236, 396, 236, 396, 212, 397, 212, 397, 236, 432, 236, 432, 226, 408, 226, 408, 217, 410, 217, 410, 199, 397, 199, 397, 202, 396, 202, 396, 199);

        adaugaUsa(bloc, 383, 545, 392, 545);
        adaugaUsa(bloc, 371, 527, 378, 527);
        adaugaUsa(bloc, 397, 527, 405, 527);
        adaugaUsa(bloc, 376, 490, 398, 490);
        adaugaUsa(bloc, 378, 424, 385, 424);
        adaugaUsa(bloc, 377, 410, 377, 406, Color.SILVER);
        adaugaUsa(bloc, 390, 404, 390, 396, Color.SILVER);
        adaugaUsa(bloc, 390, 388, 390, 381, Color.SILVER);
        adaugaUsa(bloc, 377, 387, 377, 381, Color.SILVER);
        adaugaUsa(bloc, 379, 370, 385, 370, Color.SILVER);
        adaugaUsa(bloc, 389, 356, 389, 348, Color.SILVER);
        adaugaUsa(bloc, 389, 320, 389, 314, Color.SILVER);
        adaugaUsa(bloc, 389, 298, 389, 290, Color.SILVER);
        adaugaUsa(bloc, 406, 319, 406, 310, Color.SILVER);
        adaugaUsa(bloc, 416, 304, 421, 304, Color.SILVER);
        adaugaUsa(bloc, 402, 341, 402, 344, Color.SILVER);
        adaugaUsa(bloc, 396, 265, 396, 257);
        adaugaUsa(bloc, 388, 236, 395, 236);
        adaugaUsa(bloc, 396, 212, 396, 203);
        adaugaUsa(bloc, 400, 199, 407, 199);
        adaugaUsa(bloc, 398, 236, 403, 236);
        adaugaUsa(bloc, 408, 223, 408, 220);
        adaugaUsa(bloc, 410, 212, 410, 206);
        adaugaUsa(bloc, 432, 208, 432, 202);
        adaugaUsa(bloc, 460, 401, 460, 412);
        adaugaUsa(bloc, 439, 466, 447, 466);
        adaugaUsa(bloc, 470, 257, 470, 267);

        adaugaFereastra(bloc, 380, 543, 380, 530, 1);
        adaugaFereastra(bloc, 395, 543, 395, 530, 1);
        adaugaFereastra(bloc, 371, 545, 377, 545, 2);
        adaugaFereastra(bloc, 398, 545, 404, 545, 2);
        adaugaFereastra(bloc, 414, 533, 424, 533, 2);
        adaugaFereastra(bloc, 346, 305, 388, 305, 1, Color.SILVER);
        adaugaFereastra(bloc, 432, 361, 432, 353, 2, Color.SILVER);
        adaugaFereastra(bloc, 432, 341, 432, 333, 2, Color.SILVER);
        adaugaFereastra(bloc, 432, 321, 432, 312, 2, Color.SILVER);
        adaugaFereastra(bloc, 432, 300, 432, 292, 2, Color.SILVER);
        adaugaFereastra(bloc, 388, 200, 394, 200, 2);
        adaugaFereastra(bloc, 343, 267, 343, 258, 2);
        adaugaFereastra(bloc, 343, 251, 343, 245, 2);
        adaugaFereastra(bloc, 350, 200, 357, 200, 2);
        adaugaFereastra(bloc, 366, 200, 373, 200, 2);
        adaugaFereastra(bloc, 418, 200, 426, 200, 2);
        adaugaFereastra(bloc, 439, 200, 448, 200, 2);
        adaugaFereastra(bloc, 453, 200, 453, 210, 2);
        adaugaFereastra(bloc, 453, 214, 453, 224, 2);
        adaugaFereastra(bloc, 453, 228, 453, 238, 2);
        adaugaFereastra(bloc, 453, 242, 453, 251, 2);
        adaugaFereastra(bloc, 458, 254, 467, 254, 2);
        adaugaFereastra(bloc, 458, 270, 467, 270, 2);
        adaugaFereastra(bloc, 453, 272, 453, 283, 2);
        adaugaFereastra(bloc, 453, 287, 453, 297, 2);
        adaugaFereastra(bloc, 453, 301, 453, 311, 2);
        adaugaFereastra(bloc, 453, 315, 453, 325, 2);
        adaugaFereastra(bloc, 453, 329, 453, 339, 2);
        adaugaFereastra(bloc, 453, 343, 453, 354, 2);
        adaugaFereastra(bloc, 453, 358, 453, 368, 2);
        adaugaFereastra(bloc, 453, 372, 453, 383, 2);
        adaugaFereastra(bloc, 453, 387, 453, 397, 2);
        adaugaFereastra(bloc, 453, 416, 453, 426, 2);
    }

    private void asambleazaBlocC2(Pane bloc) {
        adaugaIncapere(bloc, null, null, 170, 394, 211-170, 412-394);
        adaugaIncapere(bloc, "217", "Informatică", 170, 412, 211-170, 467-412);
        adaugaIncapere(bloc, null, null, 211, 394, 236-211, 431-394);
        adaugaIncapere(bloc, "215", "Catedra Informatică 2", 211, 431, 236-211, 467-431);
        adaugaIncapere(bloc, "218", "Secția didactică 2 (Șef: Irina Pasecinic)", 170, 467, 196-170, 490-467);
        adaugaIncapere(bloc, null, null, 236, 447, 255-236, 467-447);
        adaugaIncapere(bloc, "216", "Laboratorul de Informatică 14", 170, 490, 209-170, 533-490);
        adaugaIncapere(bloc, "214", "Laboratorul de Informatică 13", 209, 490, 253-209, 533-490);
        adaugaIncapere(bloc, "213", "Informatică", 253, 490, 290-253, 545-490);
        adaugaIncapere(bloc, "212", "Director adjunct instruire practică și producere", 290, 490, 316-290, 533-490);
        adaugaIncapere(bloc, "211", "Informatică", 316, 490, 369-316, 533-490);
        adaugaIncapere(bloc, "210\nOrange", "Laboratorul de Informatică 12", 369, 490, 406-369, 545-490);
        adaugaIncapere(bloc, "209", "Laboratorul de Informatică 11", 406, 490, 449-406, 533-490);
        adaugaIncapere(bloc, "208", "Catedra Informatică 1", 449, 490, 486-449, 533-490);
        adaugaIncapere(bloc, null, null, 486, 483, 494-486, 534-483);
        adaugaIncapere(bloc, null, null, 494, 493, 510-494, 512-493);
        adaugaIncapere(bloc, null, null, 510, 493, 534-510, 512-493);
        adaugaIncapere(bloc, "206", "Inginer Roman Jalbă", 494, 512, 534-494, 545-512);
        adaugaIncapere(bloc, "204", "Laboratorul de Informatică 8", 534, 493, 615-534, 533-493);
        adaugaIncapere(bloc, "", "", 341, 423, 376-341, 467-423);
        adaugaIncapere(bloc, "Conta-\nbilitate", "Contabilitate", 350, 399, 377-350, 423-399);
        adaugaIncapere(bloc, "", "", 350, 370, 377-350, 399-370);
        adaugaIncapere(bloc, "Anti\nCameră", "Anti Cameră", 343, 337, 377-343, 370-337);
        adaugaIncapere(bloc, "Director", "Director", 343, 284, 390-343, 337-284);
        adaugaIncapere(bloc, null, null, 343, 275, 396-343, 284-275);
        adaugaIncapere(bloc, "L.Obadă\nDir.Adj", "Director adjunct activitate didactică - Liuba Obadă", 390, 284, 433-390, 330-284);
        adaugaIncapere(bloc, "N.Gabură\nDir.Adj", "Director adjunct pentru educație - Nadejda Gabură", 407, 330, 433-407, 371-330);
        adaugaIncapere(bloc, "Secția\nserviciu\npersoană", "Secția serviciu persoană", 388, 371, 427-388, 396-371);
        adaugaIncapere(bloc, "Casa", "Casa", 388, 396, 427-388, 423-396);
        adaugaIncapere(bloc, null, null, 388, 396, 406-388, 405-396);
        adaugaIncapere(bloc, "207", "Laboratorul de Informatică 10", 402, 429, 450-402, 468-429);
        adaugaIncapere(bloc, "205", "Laboratorul de Informatică 9", 450, 429, 492-450, 468-429);
        adaugaIncapere(bloc, "203", "Laboratorul de Informatică 7", 492, 429, 539-492, 462-429);
        adaugaIncapere(bloc, null, null, 539, 455, 580-539, 462-455);
        adaugaIncapere(bloc, "202", "Formarea continuă și resurse educaționale", 539, 379, 580-539, 455-379);
        adaugaIncapere(bloc, null, null, 580, 455, 597-580, 462-455);
        adaugaIncapere(bloc, "Sala de\nFestivități", "Sala de Festivități", 580, 320, 639-580, 455-320);
        adaugaIncapere(bloc, null, null, 580, 306, 639-580, 320-306);
        adaugaIncapere(bloc, null, null, 580, 251, 639-580, 306-251);
        adaugaIncapere(bloc, "250", "Sala de clasă", 342, 236, 396-342, 275-236);
        adaugaIncapere(bloc, "249", "Sala de clasă", 342, 199, 387-342, 236-199);
        adaugaIncapere(bloc, "251", "Sala de clasă", 396, 236, 433-396, 284-236);
        adaugaIncapere(bloc, "248", "Secție didactică 3 (Șef: Ala Dumneanu)", 410, 199, 433-410, 217-199);

        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 146, 387, 202, 387, 202, 393, 170, 393, 170, 489, 146, 489);
        adaugaIncaperePoligon(bloc, null, "Coridor central, Etaj 2", 170, 467, 375, 467, 375, 423, 402, 423, 402, 467, 486, 467, 486, 489, 170, 489);
        adaugaIncaperePoligon(bloc, null, "Coridor Sala de Festivități", 486, 467, 492, 467, 492, 462, 615, 462, 615, 493, 494, 493, 494, 483, 486, 483);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 402, 429, 402, 423, 426, 423, 426, 371, 433, 371, 433, 199, 454, 199, 454, 254, 470, 254, 470, 270, 454, 270, 454, 398, 460, 398, 460, 414, 454, 414, 454, 429);
        adaugaIncaperePoligon(bloc, null, "Coridor Operatori, Etaj 2", 387, 199, 387, 236, 396, 236, 396, 212, 397, 212, 397, 236, 433, 236, 433, 226, 408, 226, 408, 217, 410, 217, 410, 199, 397, 199, 397, 202, 396, 202, 396, 199);
    }

    private void asambleazaBlocC3(Pane bloc) {
        adaugaIncapere(bloc, "311", "Laboratorul de Informatică 17", 450, 429, 492-450, 463-429);
        adaugaIncapere(bloc, "312", "Laboratorul de Informatică 18", 492, 429, 539-492, 461-429);
        adaugaIncapere(bloc, null, null, 539, 437, 560-539, 454-437);
        adaugaIncapere(bloc, "310", "Laboratorul de Informatică 16", 574, 462, 615-574, 493-462);
        adaugaIncaperePoligon(bloc, null, null, Color.WHITE, Color.BLACK, false, 492, 462, 495, 462);
        adaugaIncaperePoligon(bloc, null, null, Color.WHITE, Color.BLACK, false, 492, 462, 495, 462, 495, 534, 487, 534, 487, 463, 492, 463);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 146, 387, 202, 387, 202, 393, 237, 393, 237, 447, 254, 447, 254, 466, 341, 466, 341, 423, 349, 423, 349, 371, 343, 371, 343, 276, 341, 276, 341, 199, 454, 199, 454, 254, 470, 254, 470, 270, 454, 270, 454, 399, 460, 399, 460, 414, 454, 414, 454, 429, 450, 429, 450, 463, 487, 463, 487, 534, 407, 534, 407, 546, 368, 546, 368, 534, 521, 534, 521, 546, 482, 546, 482, 534, 170, 534, 170, 489, 146, 489);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 495, 493, 615, 493, 615, 534, 534, 534, 534, 546, 495, 546);

        adaugaIncaperePoligon(bloc, null, null, 495, 462, 558, 462, 558, 461, 539, 461, 539, 454, 560, 454, 560, 437, 568, 437, 568, 454, 580, 454, 580, 462, 568, 462, 568, 463, 574, 463, 574, 493, 495, 493);
    }

    private void asambleazaBlocB1(Pane bloc) {
        adaugaIncaperePoligon(bloc, "WC", null, 539, 272, 539, 307, 560, 307, 560, 284, 548, 284, 560, 284, 560, 272);
        adaugaIncaperePoligon(bloc, "WC", null, 560, 272, 560, 284, 571, 284, 560, 284, 560, 307, 580, 307, 580, 272);
        adaugaIncaperePoligon(bloc, null, null, 580, 274, 580, 307, 590, 307, 590, 286, 580, 286, 590, 286, 590, 274);
        adaugaIncaperePoligon(bloc, null, null, 590, 274, 590, 306, 599, 306, 599, 286, 590, 286, 599, 286, 599, 274);
        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 1, bloc B", 521, 251, 521, 272, 539, 272, 539, 267, 539, 272, 580, 272, 580, 268, 580, 274, 599, 274, 599, 134, 580, 134, 580, 255, 580, 251, 539, 251, 539, 256, 539, 251);
        adaugaIncapere(bloc, "134", "Fizică / Astronomie", 599, 251, 639-599, 328-251);
        adaugaIncapere(bloc, "136", "Secția Asigurarea calității (Șef: Lebediuc Adela)", 599, 226, 639-599, 247-226);
        adaugaIncapere(bloc, "138", "Fizică / Astronomie", 599, 169, 639-599, 226-169);
        adaugaIncapere(bloc, "135", "Fizică / Astronomie", 539, 169, 580-539, 231-169);
        adaugaIncaperePoligon(bloc, null, null, 539, 231, 580, 231, 580, 247, 565, 247, 565, 231, 565, 247, 539, 247);
        adaugaIncaperePoligon(bloc, "VF", "Vestiar Fete", 539, 134, 565, 134, 565, 160, 580, 160, 580, 169, 539, 169);
        adaugaIncapere(bloc, null, null, 565, 134, 580-565, 160-134);
        adaugaIncaperePoligon(bloc, "VB", "Vestiar Băieți", 611, 134, 639, 134, 639, 169, 599, 169, 599, 160, 611, 160);
        adaugaIncapere(bloc, null, null, 599, 134, 611-599, 160-134);
        adaugaIncaperePoligon(bloc, "Sala Sportivă\n137", "Sala Sportivă", 430, 5, 435, 5, 435, 8, 436, 8, 436, 5, 468, 5, 468, 8, 469, 8, 469, 5, 505, 5, 505, 8, 506, 8, 506, 5, 542, 5, 542, 8, 543, 8, 543, 5, 579, 5, 579, 8, 580, 8, 580, 5, 615, 5, 615, 8, 616, 8, 616, 5, 649, 5, 649, 8, 650, 8, 650, 5, 654, 5, 654, 47, 651, 47, 651, 48, 654, 48, 654, 66, 651, 66, 651, 67, 654, 67, 654, 69, 655, 69, 655, 65, 660, 65, 660, 85, 655, 85, 655, 81, 654, 81, 654, 84, 651, 84, 651, 85, 654, 85, 654, 102, 651, 102, 651, 103, 654, 103, 654, 123, 646, 123, 646, 120, 645, 120, 645, 123, 613, 123, 613, 120, 612, 120, 612, 126, 613, 126, 613, 123, 639, 123, 639, 134, 613, 134, 613, 131, 612, 131, 612, 134, 575, 134, 575, 121, 576, 121, 576, 120, 574, 120, 574, 121, 575, 121, 575, 134, 540, 134, 540, 120, 539, 120, 539, 123, 505, 123, 505, 120, 504, 120, 504, 123, 468, 123, 468, 120, 467, 120, 467, 123, 435, 123, 435, 120, 434, 120, 434, 123, 430, 123, 430, 103, 433, 103, 433, 102, 430, 102, 430, 83, 433, 83, 433, 82, 430, 82, 430, 65, 433, 65, 433, 64, 430, 64, 430, 45, 433, 45, 433, 44, 430, 44, 430, 25, 433, 25, 433, 24, 430, 24);
        adaugaIncaperePoligon(bloc, null, null, 501, 123, 540, 123, 540, 134, 539, 134, 539, 154, 501, 154);
        adaugaIncapere(bloc, null, null, 484, 123, 501-484, 137-123);
        adaugaIncapere(bloc, null, null, 484, 137, 501-484, 154-137);
        adaugaIncapere(bloc, "133", "Bazele legislației / Securitatea și sănătatea în muncă", 430, 123, 484-430, 154-123);
    }

    private void asambleazaBlocB2(Pane bloc) {
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 430, 154, 430, 5, 654, 5, 654, 123, 639, 123, 639, 134, 539, 134, 539, 154);
        adaugaIncapere(bloc, "243", "Științe economice", 539, 134, 580-539, 170-134);
        adaugaIncapere(bloc, "241", "Contabilitate", 539, 170, 580-539, 212-170);
        adaugaIncapere(bloc, "239", "Catedra Economie", 539, 212, 580-539, 231-212);
        adaugaIncapere(bloc, "238", "Atelier inginer", 539, 231, 565-539, 247-231);
        adaugaIncapere(bloc, "244", "Științe economice", 599, 134, 639-599, 170-134);
        adaugaIncapere(bloc, "242", "Științe economice", 599, 170, 639-599, 206-170);
        adaugaIncapere(bloc, "240", "Analiza economică-financiară", 599, 206, 639-599, 247-206);
        adaugaIncapere(bloc, null, null,521, 251, 539-521, 272-251, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, "245", "Catedra Servicii administrative", 539, 272, 571-539, 307-272);
        adaugaIncapere(bloc, "246", "Sala de clasă", 539, 307, 571-539, 345-307);
        adaugaIncapere(bloc, "247", "Sala de clasă", 539, 345, 580-539, 379-345);
        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 2, bloc B", 580, 134, 580, 231, 565, 231, 565, 247, 571, 247, 571, 251, 539, 251, 539, 272, 571, 272, 571, 345, 580, 345, 580, 272, 577, 272, 580, 272, 580, 247, 599, 247, 599, 134);
    }

    private void asambleazaBlocB3(Pane bloc) {
        adaugaIncaperePoligon(bloc, null, "Coridor Etaj 3, bloc B", 568, 454, 580, 454, 580, 272, 578, 272, 580, 272, 580, 251, 539, 251, 539, 272, 570, 272, 570, 307, 568, 307);
        adaugaIncapere(bloc, null, null,521, 251, 539-521, 272-251, Color.SILVER, Color.BLACK);
        adaugaIncapere(bloc, "316", "Catedra Limbi străine", 539, 272, 570-539, 307-272);
        adaugaIncapere(bloc, "315", "Limba engleză", 539, 307, 568-539, 363-307);
        adaugaIncapere(bloc, "314", "Secția didactică 1 (Șef: Nicșan Adrian)", 539, 363, 568-539, 380-363);
        adaugaIncapere(bloc, "313", "Limba franceză", 539, 380, 568-539, 437-380);
        adaugaIncapere(bloc, null, null, 580, 251, 639-580, 454-251, Color.SILVER, Color.BLACK);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 430, 154, 430, 5, 654, 5, 654, 123, 639, 123, 639, 247, 539, 247, 539, 149, 539, 154);
    }

    private void asambleazaBlocE1(Pane bloc) {
        adaugaIncapere(bloc, "128", "Sala de clasă", 118, 88, 157-118, 126-88);
        adaugaIncapere(bloc, "127", "Limba franceză", 157, 98, 194-157, 126-98);
        adaugaIncapere(bloc, "129", "Sala de clasă", 208, 87, 243-208, 126-87);
        adaugaIncapere(bloc, "130", "Sala de clasă", 243, 87, 279-243, 126-87);
        adaugaIncapere(bloc, "131", "Sala de clasă", 279, 87, 320-279, 125-87);
        adaugaIncapere(bloc, "132", "Sala de clasă", 320, 79, 358-320, 126-79);
        adaugaIncapere(bloc, null, null, 179, 126, 212-179, 134-126);
        adaugaIncapere(bloc, null, null, 212, 126, 229-212, 134-126);
        adaugaIncapere(bloc, null, null, 263, 126, 301-263, 134-126);
        adaugaIncapere(bloc, null, null, 320, 126, 340-320, 134-126);
        adaugaIncaperePoligon(bloc, null, null, 430, 125, 430, 17, 388, 17, 388, 26, 353, 26, 353, 37, 322, 37, 322, 79, 358, 79, 358, 125, 391, 125, 391, 111, 416, 111, 416, 125);
        adaugaIncaperePoligon(bloc, null, null, 157, 88, 157, 98, 195, 98, 195, 125, 208, 125, 208, 88, 195, 88, 195, 91, 194, 91, 194, 88);
    }

    private void asambleazaBlocE2(Pane bloc) {
        adaugaIncapere(bloc, "233", "Atelier de lucru 3", 118, 88, 157-118, 126-88);
        adaugaIncapere(bloc, "232", "Limba franceză", 157, 98, 195-157, 126-98);
        adaugaIncapere(bloc, "235", "Limba engleză", 206, 99, 240-206, 126-99);
        adaugaIncapere(bloc, "234", "Sala de clasă", 240, 87, 279-240, 126-87);
        adaugaIncapere(bloc, "236", "Sala de clasă", 279, 87, 320-279, 126-87);
        adaugaIncapere(bloc, "237", "Limba franceză", 320, 79, 358-320, 126-79);
        adaugaIncapere(bloc, null, null, 179, 126, 212-179, 134-126);
        adaugaIncapere(bloc, null, null, 212, 126, 229-212, 134-126);
        adaugaIncapere(bloc, null, null, 263, 126, 301-263, 134-126);
        adaugaIncapere(bloc, null, null, 320, 126, 340-320, 134-126);
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 430, 125, 430, 17, 388, 17, 388, 26, 353, 26, 353, 37, 322, 37, 322, 79, 358, 79, 358, 125, 391, 125, 391, 111, 416, 111, 416, 125);
        adaugaIncaperePoligon(bloc, null, null, 157, 88, 157, 98, 195, 98, 195, 126, 207, 126, 207, 98, 208, 98, 208, 99, 240, 99, 240, 88, 209, 88, 209, 89, 207, 89, 207, 90, 206, 90, 206, 88, 195, 88, 195, 91, 194, 91, 194, 88);
    }

    private void asambleazaBlocE3(Pane bloc) {
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 430, 125, 430, 18, 390, 18, 390, 25, 353, 25, 353, 37, 322, 37, 322, 80, 320, 80, 320, 87, 208, 87, 208, 89, 119, 89, 119, 125, 179, 125, 179, 134, 229, 134, 229, 125, 263, 125, 263, 134, 301, 134, 301, 125, 320, 125, 320, 134, 340, 134, 340, 125, 390, 125, 390, 110, 416, 110, 416, 125);
    }

    private void asambleazaBlocD1(Pane bloc) {
        adaugaIncapere(bloc, null, null, 5, 388, 45-5, 421-388);
        adaugaIncapere(bloc, null, null, 5, 362, 45-5, 388-362);
        adaugaIncapere(bloc, null, null, 5, 336, 45-5, 362-336);
        adaugaIncapere(bloc, "126", "Sala de clasă", 5, 292, 45-5, 336-292);
        adaugaIncapere(bloc, "125", "Sala de clasă", 15, 251, 45-15, 292-251);
        adaugaIncaperePoligon(bloc, null, null, 5, 239, 5, 292, 15, 292, 15, 251, 45, 251, 45, 239);
        adaugaIncaperePoligon(bloc, null, null, 7, 239, 13, 206, 33, 209, 45, 239);
        adaugaIncaperePoligon(bloc, null, null, 13, 206, 19, 169, 37, 172, 33, 209);
        adaugaIncaperePoligon(bloc, null, null, 19, 169, 23, 143, 40, 145, 37, 172);
        adaugaIncaperePoligon(bloc, null, null, 23, 143, 25, 127, 41, 127, 40, 145);
    }

    private void asambleazaBlocD2(Pane bloc) {
        adaugaIncapere(bloc, null, null, 5, 388, 46-5, 422-388);
        adaugaIncapere(bloc, "231", "Biologie", 5, 336, 46-5, 388-336);
        adaugaIncapere(bloc, "230", "Sala de clasă", 16, 292, 46-16, 336-292);
        adaugaIncapere(bloc, "229", "Consiliul elevilor", 16, 261, 46-16, 292-261);
        adaugaIncaperePoligon(bloc, "228", "Atelier inginer", 16, 249, 16, 261, 46, 261, 46, 239, 22, 239, 22, 249);
        adaugaIncaperePoligon(bloc, null, null, 5, 239, 22, 239, 22, 249, 16, 249, 16, 260, 15, 260, 15, 261, 16, 261, 16, 292, 13, 292, 13, 293, 16, 293, 16, 336, 5, 336, 5, 293, 7, 293, 7, 292, 5, 292);
        adaugaIncaperePoligon(bloc, null, null, 7, 239, 13, 206, 33, 209, 46, 239);
        adaugaIncaperePoligon(bloc, "227", "Laborator echipamente de birotică", 13, 206, 19, 164, 37, 167, 33, 209);
        adaugaIncaperePoligon(bloc, null, null, 19, 164, 23, 143, 40, 145, 37, 167);
        adaugaIncaperePoligon(bloc, null, null, 23, 143, 25, 127, 42, 127, 40, 145);
    }

    private void asambleazaBlocD3(Pane bloc) {
        adaugaIncaperePoligon(bloc, null, null, Color.SILVER, Color.BLACK, false, 5, 421, 5, 240, 8, 240, 25, 127, 42, 127, 33, 209, 46, 239, 46, 421);
    }

    private Pane creeazaEtaj() {
        Pane etaj = new Pane();
        etaj.setMinSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj.setPrefSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj.setMaxSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        etaj.setBackground(fundal(Color.WHITE, 8));
        etaj.setBorder(bordura(BARA_BORDURA, 8));
        return etaj;
    }

    private Pane creeazaBloc(Pane etaj, String nume, double[] coordonate) {
        Pane bloc = new Pane();
        bloc.setId(nume);
        bloc.setPickOnBounds(false);

        Polygon polygon = new Polygon(coordonate);
        polygon.setFill(Color.WHITE);
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(1.5);
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
        interactiv(bloc, grupIncapere, rectangle, culoareFundal, cod, nume);
        bloc.getChildren().add(grupIncapere);
    }

    private void adaugaIncaperePoligon(Pane bloc, String cod, String nume, double... puncte) {
        adaugaIncaperePoligon(bloc, cod, nume, Color.WHITE, Color.BLACK, false, puncte);
    }

    private void adaugaIncaperePoligon(Pane bloc, String cod, String nume, Color culoareFundal, Color culoareBordura, boolean accesibil, double... puncte) {
        Polygon polygon = new Polygon(puncte);
        polygon.setFill(culoareFundal);
        polygon.setStroke(culoareBordura);
        polygon.setStrokeWidth(1.5);
        polygon.setMouseTransparent(accesibil);

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
                    forma.getLayoutBounds().getCenterX(), forma.getLayoutBounds().getCenterY(), cod, nume, forma));
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
        stilizeazaButon(btnAdaugaPunctColectare, activ ? TEMA_ANULARE : TEMA_VERDE);
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
            if (marcaj.getParent() != null) {
                ((Pane) marcaj.getParent()).getChildren().remove(marcaj);
            }
        }
        marcaje.clear();
        marcajPeId.clear(); haloPeId.clear(); tooltipPeId.clear(); punctPeId.clear();

        Map<String, Integer> numarInIncapere = new HashMap<>();
        for (PunctColectare p : puncte) {
            String cheie = p.etaj() + "|" + p.bloc() + "|" + p.incapere();
            Incapere incapere = incaperi.get(cheie);
            if (incapere == null) continue;

            int index = numarInIncapere.merge(cheie, 1, Integer::sum) - 1;
            Circle marcaj = new Circle(incapere.centruX() - 6 + index * 16, incapere.centruY() + 12, 4.5,
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
        reaplicaPulsatie();
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

    private void redimensioneazaHarta(double latime, double inaltime) {
        if (latime <= 0 || inaltime <= 0) return;
        scaraBaza = Math.min(latime / VIRTUAL_WIDTH, inaltime / VIRTUAL_HEIGHT);
        toolBar.setMaxWidth(VIRTUAL_WIDTH * scaraBaza);
        aplicaCamera();
    }

    private void aplicaCamera() {
        double latime = viewport.getWidth(), inaltime = viewport.getHeight();
        double s = scaraBaza * zoom;
        double lh = VIRTUAL_WIDTH * s, ih = VIRTUAL_HEIGHT * s;

        tx = lh <= latime ? (latime - lh) / 2 : Math.max(latime - lh, Math.min(0, tx));
        ty = ih <= inaltime ? 0 : Math.max(inaltime - ih, Math.min(0, ty));
        mapScale.setX(s);
        mapScale.setY(s);
        containerHarta.setTranslateX(tx);
        containerHarta.setTranslateY(ty);
    }

    private void zoomLa(double px, double py, double factor) {
        double nou = Math.max(1, Math.min(ZOOM_MAX, zoom * factor));
        double raport = nou / zoom;
        tx = px - (px - tx) * raport;
        ty = py - (py - ty) * raport;
        zoom = nou;
        aplicaCamera();
    }

    private void configureazaZoomSiDeplasare() {
        viewport.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() == 0) return;
            zoomLa(e.getX(), e.getY(), Math.exp(e.getDeltaY() / 400.0));   // ~1,1× pe "clic" de rotiță
            e.consume();
        });
        viewport.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (!e.isPrimaryButtonDown()) return;
            ancoraX = e.getSceneX();
            ancoraY = e.getSceneY();
            ancoraTx = tx;
            ancoraTy = ty;
            seTrage = false;
        });
        viewport.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (!e.isPrimaryButtonDown()) return;
            double dx = e.getSceneX() - ancoraX, dy = e.getSceneY() - ancoraY;
            if (!seTrage && Math.hypot(dx, dy) < 4) return;                // sub 4 px e un click obișnuit
            seTrage = true;
            tx = ancoraTx + dx;
            ty = ancoraTy + dy;
            aplicaCamera();
            e.consume();
        });
        viewport.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (seTrage) {
                e.consume();
                seTrage = false;
            }
        });
    }

    private Button butonZoom(String svg, boolean plus, String sfat) {
        Button b = new Button();
        stilizeazaButon(b, TEMA_IN_PASTILA);
        b.setPadding(new Insets(7));
        b.setGraphic(svg.isBlank() ? lupa(plus) : iconSvg(svg));
        b.setTooltip(new Tooltip(sfat));
        return b;
    }

    private Node iconSvg(String d) {
        SVGPath forma = new SVGPath();
        forma.setContent(d);
        Region icon = new Region();
        icon.setShape(forma);
        icon.setMinSize(18, 18);
        icon.setPrefSize(18, 18);
        icon.setMaxSize(18, 18);
        icon.setBackground(fundal(CULOARE_ICON, 0));
        return icon;
    }

    private Node lupa(boolean plus) {
        Circle cerc = new Circle(8, 8, 6);
        cerc.setFill(Color.TRANSPARENT);
        cerc.setStroke(CULOARE_ICON);
        cerc.setStrokeWidth(2);
        Line maner = new Line(12.5, 12.5, 18, 18);
        maner.setStroke(CULOARE_ICON);
        maner.setStrokeWidth(2.6);
        maner.setStrokeLineCap(StrokeLineCap.ROUND);
        Line orizontal = new Line(5, 8, 11, 8);
        orizontal.setStroke(CULOARE_ICON);
        orizontal.setStrokeWidth(1.8);
        Group g = new Group(cerc, maner, orizontal);
        if (plus) {
            Line vertical = new Line(8, 5, 8, 11);
            vertical.setStroke(CULOARE_ICON);
            vertical.setStrokeWidth(1.8);
            g.getChildren().add(vertical);
        }
        return g;
    }

    private static Background fundal(Color culoare, double raza) {
        return new Background(new BackgroundFill(culoare, new CornerRadii(raza), Insets.EMPTY));
    }

    private static Border bordura(Color culoare, double raza) {
        return new Border(new BorderStroke(culoare, BorderStrokeStyle.SOLID, new CornerRadii(raza), new BorderWidths(1)));
    }

    private void stilizeazaBara() {
        toolBar.setPadding(new Insets(8, 12, 8, 12));
        toolBar.setBackground(fundal(Color.WHITE, 14));
        toolBar.setBorder(bordura(BARA_BORDURA, 14));
        toolBar.setEffect(new DropShadow(12, 0, 3, Color.rgb(0, 0, 0, 0.10)));
    }

    private HBox pastila(Node... continut) {
        HBox pastila = new HBox(3, continut);
        pastila.setAlignment(Pos.CENTER);
        pastila.setPadding(new Insets(3));
        pastila.setBackground(fundal(PASTILA_FUNDAL, 12));
        return pastila;
    }

    private void stilizeazaButon(Button b, TemaButon tema) {
        if (!b.getProperties().containsKey(CHEIE_TEMA)) {
            b.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
            b.setPadding(new Insets(7, 14, 7, 14));
            b.setCursor(Cursor.HAND);
            b.setFocusTraversable(false);
            b.hoverProperty().addListener((o, v, n) -> deseneazaButon(b));
            b.pressedProperty().addListener((o, v, n) -> deseneazaButon(b));
        }
        b.getProperties().put(CHEIE_TEMA, tema);
        deseneazaButon(b);
    }

    private void deseneazaButon(Button b) {
        if (!(b.getProperties().get(CHEIE_TEMA) instanceof TemaButon tema)) return;
        Color culoare = b.isPressed() ? tema.apasat() : b.isHover() ? tema.hover() : tema.fundal();
        b.setBackground(fundal(culoare, 10));
        b.setBorder(tema.bordura() == null ? Border.EMPTY : bordura(tema.bordura(), 10));
        b.setTextFill(tema.text());
    }

    private void stilizeazaCautare(ComboBox<String> combo) {
        Border normal = bordura(CAMP_BORDURA, 10), focus = bordura(VERDE, 10);
        combo.setPrefSize(280, 34);
        combo.setBackground(fundal(Color.WHITE, 10));
        combo.setBorder(normal);

        TextField editor = combo.getEditor();
        editor.setBackground(Background.EMPTY);
        editor.setBorder(Border.EMPTY);
        editor.setFont(Font.font("System", 13));
        editor.setPadding(new Insets(0, 4, 0, 12));
        editor.focusedProperty().addListener((o, v, n) -> combo.setBorder(n ? focus : normal));

        combo.skinProperty().addListener((o, v, n) -> {
            if (combo.lookup(".arrow-button") instanceof Region buton) {
                buton.setBackground(Background.EMPTY);
                buton.setBorder(Border.EMPTY);
            }
            if (combo.lookup(".arrow") instanceof Region sageata) {
                sageata.setBackground(fundal(Color.web("#6b7280"), 0));
            }
        });
    }

    private void evidentiazaIncapere(Incapere inc) {
        curataEfect();
        centreazaPe(inc);

        Pane bloc = inc.bloc();
        Bounds b = inc.forma().getLayoutBounds();

        Shape copie = copieForma(inc.forma());
        copie.setFill(Color.web("#22c55e", 0.55));
        copie.setStroke(RADAR_CULOARE);
        copie.setStrokeWidth(3);
        copie.setMouseTransparent(true);
        copie.setOpacity(0);
        bloc.getChildren().add(copie);
        nodurileEfectului.add(copie);

        FadeTransition clipire = new FadeTransition(Duration.seconds(CLIPIRE_DURATA / (2 * CLIPIRI)), copie);
        clipire.setFromValue(0);
        clipire.setToValue(1);
        clipire.setCycleCount(2 * CLIPIRI);
        clipire.setAutoReverse(true);

        List<Animation> animatii = new ArrayList<>();
        animatii.add(clipire);

        Pane etaj = (Pane) bloc.getParent();
        Bounds p = bloc.localToParent(b);
        for (int i = 0; i < RADAR_INELE; i++) {
            Rectangle inel = new Rectangle(p.getMinX(), p.getMinY(), p.getWidth(), p.getHeight());
            inel.setFill(Color.TRANSPARENT);
            inel.setStroke(RADAR_CULOARE);
            inel.setStrokeWidth(2.5);
            inel.setMouseTransparent(true);
            inel.setOpacity(0);
            etaj.getChildren().add(inel);
            nodurileEfectului.add(inel);

            DoubleProperty extindere = new SimpleDoubleProperty(0);
            extindere.addListener((o, v, n) -> {
                double g = n.doubleValue();
                inel.setX(p.getMinX() - g);
                inel.setY(p.getMinY() - g);
                inel.setWidth(p.getWidth() + 2 * g);
                inel.setHeight(p.getHeight() + 2 * g);
                inel.setArcWidth(g);
                inel.setArcHeight(g);
            });
            Timeline val = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(extindere, 0),
                            new KeyValue(inel.opacityProperty(), 0.9)),
                    new KeyFrame(Duration.seconds(RADAR_DURATA_INEL),
                            new KeyValue(extindere, RADAR_RAZA, Interpolator.EASE_OUT),
                            new KeyValue(inel.opacityProperty(), 0, Interpolator.EASE_IN)));
            val.setDelay(Duration.seconds(i * RADAR_INTARZIERE));
            animatii.add(val);
        }

        ParallelTransition efect = new ParallelTransition(animatii.toArray(new Animation[0]));
        efect.setOnFinished(e -> curataEfect());
        efectCurent = efect;
        efect.play();
    }

    private void curataEfect() {
        if (efectCurent != null) {
            efectCurent.stop();
            efectCurent = null;
        }
        for (Node n : nodurileEfectului) {
            if (n.getParent() instanceof Pane parinte) parinte.getChildren().remove(n);
        }
        nodurileEfectului.clear();
    }

    private Shape copieForma(Shape f) {
        if (f instanceof Polygon poligon) {
            return new Polygon(poligon.getPoints().stream().mapToDouble(Double::doubleValue).toArray());
        }
        if (f instanceof Rectangle r) {
            return new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
        Bounds b = f.getLayoutBounds();
        return new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    // Dacă harta e mărită și cabinetul e în afara ferestrei, o deplasează ca să-l aducă la mijloc.
    private void centreazaPe(Incapere inc) {
        Bounds b = inc.forma().getLayoutBounds();
        Point2D scena = inc.forma().localToScene(b.getCenterX(), b.getCenterY());
        Point2D p = viewport.sceneToLocal(scena);
        double margine = 40;
        boolean inAfara = p.getX() < margine || p.getY() < margine
                || p.getX() > viewport.getWidth() - margine || p.getY() > viewport.getHeight() - margine;
        if (!inAfara) return;
        tx += viewport.getWidth() / 2 - p.getX();
        ty += viewport.getHeight() / 2 - p.getY();
        aplicaCamera();
    }

    private void construiesteLegenda(List<Categorie> categorii) {
        containerLegenda.getChildren().clear();

        for (Categorie c : categorii) {
            Circle punct = new Circle(6.5, Color.web(c.culoare()));
            Label nume = new Label(c.denumire());
            nume.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
            nume.setTextFill(Color.web("#374151"));

            HBox item = new HBox(7, punct, nume);
            item.setAlignment(Pos.CENTER);
            item.setCursor(Cursor.HAND);
            Tooltip.install(item, new Tooltip("Click pentru a evidenția coșurile de tip " + c.denumire().toLowerCase()));
            item.setPadding(new Insets(5, 12, 5, 12));
            item.getProperties().put("categorie", c.denumire());

            actualizeazaStilItemLegenda(item, c.denumire(), false);

            // Efecte vizuale la interacțiunea cu mouse-ul
            item.setOnMouseEntered(e -> actualizeazaStilItemLegenda(item, c.denumire(), true));
            item.setOnMouseExited(e -> actualizeazaStilItemLegenda(item, c.denumire(), false));

            item.setOnMouseClicked(e -> {
                togglePulsatie(c);
                // Actualizăm fundalul tuturor item-urilor pentru a reflecta starea nouă de selecție
                for (Node node : containerLegenda.getChildren()) {
                    if (node instanceof HBox hb) {
                        String catNume = (String) hb.getProperties().get("categorie");
                        actualizeazaStilItemLegenda(hb, catNume, hb == item);
                    }
                }
            });

            containerLegenda.getChildren().add(item);
        }
    }

    private void actualizeazaStilItemLegenda(HBox item, String denumire, boolean isHover) {
        boolean isSelectat = (categorieSelectataPentruPulsatie != null &&
                categorieSelectataPentruPulsatie.denumire().equals(denumire));

        if (isSelectat) {
            item.setBackground(fundal(Color.web("#e5e7eb"), 10)); // Gri pentru elementul selectat
        } else if (isHover) {
            item.setBackground(fundal(Color.web("#f3f4f6"), 10)); // Gri foarte deschis la hover
        } else {
            item.setBackground(fundal(Color.TRANSPARENT, 10));
        }
    }

    private void togglePulsatie(Categorie categorie) {
        if (categorieSelectataPentruPulsatie != null && categorieSelectataPentruPulsatie.denumire().equals(categorie.denumire())) {
            categorieSelectataPentruPulsatie = null;
        } else {
            categorieSelectataPentruPulsatie = categorie;
        }
        reaplicaPulsatie();
    }

    private void reaplicaPulsatie() {
        if (timelinePulsatie != null) {
            timelinePulsatie.stop();
            timelinePulsatie = null;
        }

        boolean areSelectie = (categorieSelectataPentruPulsatie != null);
        List<DropShadow> umbre = new ArrayList<>();
        Color culoare = areSelectie ? Color.web(categorieSelectataPentruPulsatie.culoare()) : null;

        for (Map.Entry<Integer, PunctColectare> entry : punctPeId.entrySet()) {
            int id = entry.getKey();
            PunctColectare p = entry.getValue();
            Circle marcaj = marcajPeId.get(id);
            Circle halo = haloPeId.get(id);

            marcaj.setEffect(null);

            if (areSelectie) {
                if (p.categorie().denumire().equals(categorieSelectataPentruPulsatie.denumire())) {
                    marcaj.setVisible(true);
                    halo.setVisible(true);

                    DropShadow umbra = new DropShadow(0, culoare);
                    umbra.setSpread(0.65);
                    marcaj.setEffect(umbra);
                    umbre.add(umbra);
                } else {
                    marcaj.setVisible(false);
                    halo.setVisible(false);
                }
            } else {
                marcaj.setVisible(true);
                halo.setVisible(true);
            }
        }

        if (umbre.isEmpty()) return;

        timelinePulsatie = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    for (DropShadow u : umbre) u.setRadius(0);
                }),
                new KeyFrame(Duration.seconds(0.7), e -> {
                    for (DropShadow u : umbre) u.setRadius(12);
                })
        );
        timelinePulsatie.setCycleCount(Animation.INDEFINITE);
        timelinePulsatie.setAutoReverse(true);
        timelinePulsatie.play();
    }

    private void afiseazaEtaj(int etaj) {
        this.etajCurent = etaj; // Salvăm etajul local
        curataEfect();

        for (int i = 0; i < etaje.size(); i++) {
            boolean activ = (i + 1 == etaj);
            etaje.get(i).setVisible(activ);
            etaje.get(i).setManaged(activ);
        }
        for (int i = 0; i < butoaneEtaj.size(); i++) {
            stilizeazaButon(butoaneEtaj.get(i), i + 1 == etaj ? TEMA_ETAJ_ACTIV : TEMA_IN_PASTILA);
        }

        comboIncaperi.setValue(null);
        incaperiDinCombo.clear();

        List<String> optiuni = new ArrayList<>();
        for (Map.Entry<String, Incapere> entry : incaperi.entrySet()) {
            if (entry.getKey().startsWith(etaj + "|")) {
                Incapere inc = entry.getValue();

                String codInc = (inc.cod() != null) ? inc.cod().replace("\n", "").strip() : "";
                String numeInc = (inc.nume() != null) ? inc.nume().replace("\n", " ").strip() : "";

                String afisaj = codInc;
                if (!numeInc.isEmpty()) {
                    afisaj = afisaj.isEmpty() ? numeInc : afisaj + " - " + numeInc;
                }

                if (!afisaj.isEmpty() && !optiuni.contains(afisaj)) {
                    optiuni.add(afisaj);
                    incaperiDinCombo.put(afisaj, inc);
                }
            }
        }

        Collections.sort(optiuni);
        comboIncaperi.getItems().setAll(optiuni);
    }
}