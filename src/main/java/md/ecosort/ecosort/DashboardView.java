package md.ecosort.ecosort;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DashboardView extends BorderPane {

    //  STRUCTURI DE DATE
    public static class Utilizator {
        private final String nume;
        private final String rol;
        public Utilizator(String nume, String rol) { this.nume = nume; this.rol = rol; }
        public String getNume() { return nume; }
        public String getRol() { return rol; }
    }

    public static class InregistrareDeseu {
        private final String categorie;
        private final double cantitate;
        private final LocalDate data;
        private final String utilizator;

        public InregistrareDeseu(String categorie, double cantitate, LocalDate data, String utilizator) {
            this.categorie = categorie;
            this.cantitate = cantitate;
            this.data = data;
            this.utilizator = utilizator;
        }

        public String getCategorie() { return categorie; }
        public double getCantitate() { return cantitate; }
        public LocalDate getData() { return data; }
        public String getDataText() { return data != null ? data.toString() : ""; }
        public String getUtilizator() { return utilizator; }
    }

    public static class ColectareService {
        public static final String[] CATEGORII = {"Plastic", "Hârtie", "Sticlă", "Metal", "Baterii"};
        public static final String[] PERIOADE = {"Astăzi", "Luna aceasta", "Anul acesta", "Total"};

        // Listă goală
        private final ObservableList<InregistrareDeseu> istoric = FXCollections.observableArrayList();
        private final Map<String, Double> obiective = new HashMap<>();
        private final Map<String, Integer> puncteColectare = new HashMap<>();

        public ColectareService() {
            for (String cat : CATEGORII) {
                obiective.put(cat, 0.0);
                puncteColectare.put(cat, 0);
            }
        }

        public void adauga(InregistrareDeseu d) {
            istoric.add(0, d); }

        public ObservableList<InregistrareDeseu> getIstoric() {
            return istoric; }

        public int numarPuncte(String categorie) {
            return puncteColectare.getOrDefault(categorie, 0); }

        public double getObiectiv(String categorie) {
            return obiective.getOrDefault(categorie, 0.0); }

        public void seteazaObiectiv(String categorie, double val) {
            obiective.put(categorie, val); }

        public double colectatLunaAceasta(String categorie) {
            return istoric.stream()
                    .filter(d -> d.getCategorie().equals(categorie))
                    .mapToDouble(InregistrareDeseu::getCantitate)
                    .sum();
        }

        public double procentObiectiv(String categorie) {
            double ob = getObiectiv(categorie);
            return ob > 0 ? colectatLunaAceasta(categorie) / ob : 0;
        }

        public boolean obiectivAtins(String categorie) {
            double ob = getObiectiv(categorie);
            return ob > 0 && colectatLunaAceasta(categorie) >= ob;
        }

        public double totalUtilizator(String nume) {
            return istoric.stream()
                    .filter(d -> d.getUtilizator().equals(nume))
                    .mapToDouble(InregistrareDeseu::getCantitate)
                    .sum();
        }

        public java.util.List<InregistrareDeseu> istoricUtilizator(String nume) {
            return istoric.stream().filter(d -> d.getUtilizator().equals(nume)).toList();
        }

        public Map<String, Double> totalPeCategorie(String perioada) {
            Map<String, Double> map = new HashMap<>();
            for (String c : CATEGORII) map.put(c, 0.0);
            for (InregistrareDeseu d : istoric) {
                map.put(d.getCategorie(), map.get(d.getCategorie()) + d.getCantitate());
            }
            return map;
        }

        public java.util.List<InregistrareDeseu> dinPerioada(String perioada) {
            return istoric;
        }
    }

    private final ColectareService service = new ColectareService();
    private final Utilizator utilizator;

    private final StackPane chenar = new StackPane();
    private final VBox paginaHarta;
    private Button butonActiv;

    private final Color VERDE = Color.web("#10b981");
    private final Color VERDE_INCHIS = Color.web("#065f46");
    private final Color VERDE_DESCHIS = Color.web("#f0fdf4");

    public DashboardView(Object loginUtilizator) {
        if (loginUtilizator instanceof Utilizator u) {
            this.utilizator = u;
        } else {
            this.utilizator = new Utilizator("Administrator", "Administrator");
        }
        this.paginaHarta = ecranHarta();

        LinearGradient degrade = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#022c22")),
                new Stop(1, Color.web("#059669")));
        setBackground(new Background(new BackgroundFill(degrade, null, null)));

        Label titlu = new Label("♻  EcoSort - CEITI");
        titlu.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titlu.setTextFill(Color.WHITE);

        Label lblUtilizator = new Label(utilizator.getNume() + "  |  " + utilizator.getRol());
        lblUtilizator.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblUtilizator.setTextFill(Color.WHITE);

        Region spatiu = new Region();
        HBox.setHgrow(spatiu, Priority.ALWAYS);

        HBox sus = new HBox(titlu, spatiu, lblUtilizator);
        sus.setAlignment(Pos.CENTER_LEFT);
        sus.setPadding(new Insets(20, 30, 0, 30));
        setTop(sus);

        Button btnHarta = butonMeniu("Hartă și Coșuri", () -> paginaHarta);
        Button btnInregistrare = butonMeniu("Înregistrare deșeu", this::ecranInregistrare);
        Button btnStatistici = butonMeniu("Statistici", this::ecranStatistici);
        Button btnObiective = butonMeniu("Obiective lunare", this::ecranObiective);
        Button btnProfil = butonMeniu("Profil și Istoric", this::ecranProfil);

        VBox meniu = new VBox(8, btnHarta, btnInregistrare, btnStatistici, btnObiective, btnProfil);
        meniu.setPadding(new Insets(30, 0, 0, 25));
        meniu.setPrefWidth(230);
        setLeft(meniu);

        chenar.setPadding(new Insets(15));
        chenar.setBackground(fundal(Color.WHITE, 20));
        chenar.setEffect(new DropShadow(30, Color.rgb(0, 0, 0, 0.4)));
        BorderPane.setMargin(chenar, new Insets(20, 30, 30, 20));
        setCenter(chenar);

        btnHarta.fire();
    }

    private VBox ecranHarta() {
        VBox ecran = new VBox(10);
        InteractiveMap harta = new InteractiveMap();

        StackPane zonaHarta = new StackPane(harta);
        zonaHarta.setMinSize(0, 0);

        Rectangle taiere = new Rectangle();
        taiere.widthProperty().bind(zonaHarta.widthProperty());
        taiere.heightProperty().bind(zonaHarta.heightProperty());
        zonaHarta.setClip(taiere);
        VBox.setVgrow(zonaHarta, Priority.ALWAYS);

        HBox legenda = new HBox(25);
        legenda.setAlignment(Pos.CENTER);
        for (String categorie : ColectareService.CATEGORII) {
            Circle cerc = new Circle(7, culoareCategorie(categorie));
            Label text = new Label(categorie);
            HBox element = new HBox(6, cerc, text);
            element.setAlignment(Pos.CENTER_LEFT);
            legenda.getChildren().add(element);
        }

        ecran.getChildren().addAll(zonaHarta, legenda);
        return ecran;
    }

    private VBox ecranInregistrare() {
        VBox ecran = new VBox(8);
        ecran.setPadding(new Insets(30));
        ecran.setMaxWidth(420);
        ecran.setMaxHeight(Region.USE_PREF_SIZE);

        ComboBox<String> cmbCategorie = new ComboBox<>();
        cmbCategorie.getItems().addAll(ColectareService.CATEGORII);
        cmbCategorie.setValue(ColectareService.CATEGORII[0]);
        cmbCategorie.setMaxWidth(Double.MAX_VALUE);

        Spinner<Double> spnCantitate = new Spinner<>(0.5, 500.0, 1.0, 0.5);
        spnCantitate.setMaxWidth(Double.MAX_VALUE);
        spinnerEditabil(spnCantitate);

        DatePicker dataPicker = new DatePicker(LocalDate.now());
        dataPicker.setMaxWidth(Double.MAX_VALUE);

        stilCamp(cmbCategorie);
        stilCamp(spnCantitate);
        stilCamp(dataPicker);

        Button btnSalveaza = buton("Înregistrează deșeul");
        btnSalveaza.setMaxWidth(Double.MAX_VALUE);
        btnSalveaza.setOnAction(e -> {
            InregistrareDeseu deseu = new InregistrareDeseu(
                    cmbCategorie.getValue(),
                    spnCantitate.getValue(),
                    dataPicker.getValue(),
                    utilizator.getNume());
            service.adauga(deseu);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION,
                    "Ai înregistrat " + deseu.getCantitate() + " kg de " + deseu.getCategorie()
                            + " pentru data de " + deseu.getDataText() + ".");
            alerta.setHeaderText("Înregistrare salvată în interfață");
            alerta.show();

            spnCantitate.getValueFactory().setValue(1.0);
        });

        VBox.setMargin(btnSalveaza, new Insets(15, 0, 0, 0));
        ecran.getChildren().addAll(
                titlu("Înregistrare deșeu predat"),
                eticheta("CATEGORIE"), cmbCategorie,
                eticheta("CANTITATE (KG)"), spnCantitate,
                eticheta("DATA"), dataPicker,
                btnSalveaza
        );
        return ecran;
    }

    private VBox ecranStatistici() {
        VBox ecran = new VBox(10);
        ecran.setPadding(new Insets(15));

        ComboBox<String> cmbPerioada = new ComboBox<>();
        cmbPerioada.getItems().addAll(ColectareService.PERIOADE);
        cmbPerioada.setValue("Luna aceasta");
        stilCamp(cmbPerioada);

        HBox filtru = new HBox(10, eticheta("PERIOADA"), cmbPerioada);
        filtru.setAlignment(Pos.CENTER_LEFT);

        PieChart graficPie = new PieChart();
        graficPie.setTitle("Pe categorii");

        BarChart<String, Number> graficBare = new BarChart<>(new CategoryAxis(), new NumberAxis());
        graficBare.setTitle("Volum reciclat (kg)");
        graficBare.setLegendVisible(false);
        graficBare.setAnimated(false);

        HBox grafice = new HBox(10, graficPie, graficBare);
        HBox.setHgrow(graficPie, Priority.ALWAYS);
        HBox.setHgrow(graficBare, Priority.ALWAYS);
        VBox.setVgrow(grafice, Priority.ALWAYS);

        TableView<InregistrareDeseu> tabel = new TableView<>();
        tabel.setPrefHeight(200);
        tabel.setPlaceholder(new Label("Nu există înregistrări în această perioadă."));

        TableColumn<InregistrareDeseu, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataText()));

        TableColumn<InregistrareDeseu, String> colCategorie = new TableColumn<>("Categorie");
        colCategorie.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategorie()));

        TableColumn<InregistrareDeseu, String> colCantitate = new TableColumn<>("Cantitate");
        colCantitate.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.1f kg", c.getValue().getCantitate())));

        TableColumn<InregistrareDeseu, String> colUtilizator = new TableColumn<>("Utilizator");
        colUtilizator.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUtilizator()));

        colData.setPrefWidth(130);
        colCategorie.setPrefWidth(130);
        colCantitate.setPrefWidth(130);
        colUtilizator.setPrefWidth(160);
        tabel.getColumns().addAll(colData, colCategorie, colCantitate, colUtilizator);

        Runnable actualizeaza = () -> {
            String perioada = cmbPerioada.getValue();
            Map<String, Double> totaluri = service.totalPeCategorie(perioada);

            graficPie.getData().clear();
            graficBare.getData().clear();
            XYChart.Series<String, Number> serie = new XYChart.Series<>();

            for (String categorie : ColectareService.CATEGORII) {
                double kg = totaluri.get(categorie);
                if (kg > 0) {
                    graficPie.getData().add(new PieChart.Data(categorie, kg));
                }
                serie.getData().add(new XYChart.Data<>(categorie, kg));
            }

            graficBare.getData().add(serie);
            tabel.getItems().setAll(service.dinPerioada(perioada));
        };

        cmbPerioada.setOnAction(e -> actualizeaza.run());
        actualizeaza.run();

        ecran.getChildren().addAll(titlu("Statistici de colectare"), filtru, grafice, tabel);
        return ecran;
    }

    private VBox ecranObiective() {
        VBox ecran = new VBox(15);
        ecran.setPadding(new Insets(20));

        ComboBox<String> cmbCategorie = new ComboBox<>();
        cmbCategorie.getItems().addAll(ColectareService.CATEGORII);
        cmbCategorie.setValue(ColectareService.CATEGORII[0]);

        Spinner<Double> spnKg = new Spinner<>(0.0, 10000.0, 50.0, 5.0);
        spinnerEditabil(spnKg);

        stilCamp(cmbCategorie);
        stilCamp(spnKg);

        Button btnSeteaza = buton("Setează obiectivul lunar");
        HBox formular = new HBox(10, cmbCategorie, spnKg, new Label("kg / lună"), btnSeteaza);
        formular.setAlignment(Pos.CENTER_LEFT);

        VBox zonaProgres = new VBox(14);

        Runnable afiseaza = () -> {
            zonaProgres.getChildren().clear();

            for (String categorie : ColectareService.CATEGORII) {
                Label nume = new Label(categorie);
                nume.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                nume.setTextFill(culoareCategorie(categorie));
                nume.setMinWidth(80);

                String text = String.format("%.1f / %.1f kg",
                        service.colectatLunaAceasta(categorie), service.getObiectiv(categorie));
                if (service.obiectivAtins(categorie)) {
                    text += "   ✔ obiectiv atins!";
                }

                HBox rand = new HBox(15, nume, bara(service.procentObiectiv(categorie), 300), new Label(text));
                rand.setAlignment(Pos.CENTER_LEFT);
                zonaProgres.getChildren().add(rand);
            }
        };

        btnSeteaza.setOnAction(e -> {
            service.seteazaObiectiv(cmbCategorie.getValue(), spnKg.getValue());
            afiseaza.run();
        });
        afiseaza.run();

        ecran.getChildren().addAll(titlu("Obiective lunare de colectare"), formular, zonaProgres);
        return ecran;
    }

    private VBox ecranProfil() {
        VBox ecran = new VBox(15);
        ecran.setPadding(new Insets(20));

        Circle cerc = new Circle(30, VERDE);
        Label initiala = new Label(utilizator.getNume().substring(0, 1).toUpperCase());
        initiala.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        initiala.setTextFill(Color.WHITE);
        StackPane avatar = new StackPane(cerc, initiala);

        double total = service.totalUtilizator(utilizator.getNume());
        VBox date = new VBox(3,
                titlu(utilizator.getNume()),
                eticheta(utilizator.getRol().toUpperCase()),
                new Label(String.format("Total înregistrat: %.1f kg", total)));

        HBox antet = new HBox(15, avatar, date);
        antet.setAlignment(Pos.CENTER_LEFT);

        ListView<String> lista = new ListView<>();
        for (InregistrareDeseu d : service.istoricUtilizator(utilizator.getNume())) {
            lista.getItems().add(d.getDataText() + "   -   " + d.getCategorie() + "   -   " + d.getCantitate() + " kg");
        }
        lista.setPlaceholder(new Label("Nu există nicio înregistrare personală."));
        VBox.setVgrow(lista, Priority.ALWAYS);
        VBox coloanaIstoric = new VBox(8, eticheta("ISTORICUL CONTRIBUȚIILOR"), lista);

        HBox continut = new HBox(20, coloanaIstoric);
        HBox.setHgrow(coloanaIstoric, Priority.ALWAYS);
        VBox.setVgrow(continut, Priority.ALWAYS);

        ecran.getChildren().addAll(antet, continut);
        return ecran;
    }

    private Button butonMeniu(String text, Supplier<Node> ecran) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        b.setPadding(new Insets(12, 15, 12, 15));
        stilButonMeniu(b, false);

        b.setOnMouseEntered(e -> {
            if (b != butonActiv) b.setBackground(fundal(Color.rgb(255, 255, 255, 0.15), 10));
        });
        b.setOnMouseExited(e -> {
            if (b != butonActiv) stilButonMeniu(b, false);
        });

        b.setOnAction(e -> {
            chenar.getChildren().setAll(ecran.get());
            if (butonActiv != null) stilButonMeniu(butonActiv, false);
            butonActiv = b;
            stilButonMeniu(b, true);
        });
        return b;
    }

    private void stilButonMeniu(Button b, boolean activ) {
        if (activ) {
            b.setBackground(fundal(Color.WHITE, 10));
            b.setTextFill(VERDE_INCHIS);
        } else {
            b.setBackground(fundal(Color.TRANSPARENT, 10));
            b.setTextFill(Color.WHITE);
        }
    }

    private Background fundal(Color culoare, double raza) {
        return new Background(new BackgroundFill(culoare, new CornerRadii(raza), null));
    }

    private Label titlu(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        l.setTextFill(VERDE_INCHIS);
        return l;
    }

    private Label eticheta(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.GRAY);
        return l;
    }

    private void stilCamp(Control camp) {
        camp.setBackground(fundal(VERDE_DESCHIS, 10));
        camp.setBorder(new Border(new BorderStroke(VERDE, BorderStrokeStyle.SOLID,
                new CornerRadii(10), new BorderWidths(1.5))));
        camp.setPrefHeight(40);
    }

    private Button buton(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        b.setTextFill(Color.WHITE);
        b.setPadding(new Insets(12, 20, 12, 20));
        b.setBackground(fundal(VERDE, 10));
        b.setOnMouseEntered(e -> b.setBackground(fundal(VERDE_INCHIS, 10)));
        b.setOnMouseExited(e -> b.setBackground(fundal(VERDE, 10)));
        return b;
    }

    private void spinnerEditabil(Spinner<Double> spinner) {
        spinner.setEditable(true);
        spinner.focusedProperty().addListener((obs, vechi, nou) -> {
            if (!nou) spinner.increment(0);
        });
    }

    private StackPane bara(double procent, double latime) {
        Rectangle gol = new Rectangle(latime, 14);
        gol.setArcWidth(14);
        gol.setArcHeight(14);
        gol.setFill(Color.web("#d1fae5"));

        Rectangle plin = new Rectangle(latime * Math.min(procent, 1.0), 14);
        plin.setArcWidth(14);
        plin.setArcHeight(14);
        plin.setFill(VERDE);

        StackPane bara = new StackPane(gol, plin);
        bara.setAlignment(Pos.CENTER_LEFT);
        bara.setMaxSize(latime, 14);
        return bara;
    }

    private Color culoareCategorie(String categorie) {
        return switch (categorie) {
            case "Plastic" -> Color.web("#3b82f6");
            case "Hârtie" -> Color.web("#f59e0b");
            case "Sticlă" -> Color.web("#10b981");
            case "Metal" -> Color.web("#6b7280");
            default -> Color.web("#ef4444");
        };
    }
}