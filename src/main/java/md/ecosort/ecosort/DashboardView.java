package md.ecosort.ecosort;

import javafx.beans.property.SimpleStringProperty;
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
import md.ecosort.ecosort.model.Categorie;
import md.ecosort.ecosort.model.Inregistrare;
import md.ecosort.ecosort.model.ProgresLunar;
import md.ecosort.ecosort.model.PunctColectare;
import md.ecosort.ecosort.model.Rol;
import md.ecosort.ecosort.model.Utilizator;
import md.ecosort.ecosort.service.ColectareService;
import md.ecosort.ecosort.service.ColectareService.Perioada;
import md.ecosort.ecosort.service.UtilizatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DashboardView extends BorderPane {

    // Categoriile și punctele de colectare, încărcate împreună pentru formulare.
    private record Nomenclatoare(List<Categorie> categorii, List<PunctColectare> puncte) { }

    // Element "toate categoriile" pentru filtrul din Statistici.
    private static final Categorie TOATE = new Categorie(0, "Toate categoriile", "#9CA3AF");

    private final ColectareService service = new ColectareService();
    private final UtilizatorService utilizatorService = new UtilizatorService();
    private final Utilizator utilizator;

    private final StackPane chenar = new StackPane();
    private final InteractiveMap harta;
    private final VBox paginaHarta;
    private Button butonActiv;

    private final Color VERDE = Color.web("#10b981");
    private final Color VERDE_INCHIS = Color.web("#065f46");
    private final Color VERDE_DESCHIS = Color.web("#f0fdf4");

    public DashboardView(Utilizator utilizator, Runnable laDeconectare) {
        this.utilizator = utilizator;
        this.harta = new InteractiveMap(service, utilizator.esteAdmin());
        this.paginaHarta = ecranHarta();

        LinearGradient degrade = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#022c22")),
                new Stop(1, Color.web("#059669")));
        setBackground(new Background(new BackgroundFill(degrade, null, null)));

        Label titlu = new Label("♻  EcoSort - CEITI");
        titlu.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titlu.setTextFill(Color.WHITE);

        Label lblUtilizator = new Label(utilizator.numeComplet() + "  |  " + utilizator.rol().getEticheta());
        lblUtilizator.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblUtilizator.setTextFill(Color.WHITE);

        Button btnDeconectare = new Button("Deconectare");
        btnDeconectare.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        btnDeconectare.setTextFill(Color.WHITE);
        btnDeconectare.setBackground(fundal(Color.rgb(255, 255, 255, 0.15), 10));
        btnDeconectare.setOnMouseEntered(e -> btnDeconectare.setBackground(fundal(Color.rgb(255, 255, 255, 0.30), 10)));
        btnDeconectare.setOnMouseExited(e -> btnDeconectare.setBackground(fundal(Color.rgb(255, 255, 255, 0.15), 10)));
        btnDeconectare.setOnAction(e -> laDeconectare.run());

        Region spatiu = new Region();
        HBox.setHgrow(spatiu, Priority.ALWAYS);

        HBox sus = new HBox(15, titlu, spatiu, lblUtilizator, btnDeconectare);
        sus.setAlignment(Pos.CENTER_LEFT);
        sus.setPadding(new Insets(20, 30, 0, 30));
        setTop(sus);

        Button btnHarta = butonMeniu("Hartă și Coșuri", () -> {
            harta.reincarcaPuncte();
            return paginaHarta;
        });
        VBox meniu = new VBox(8, btnHarta,
                butonMeniu("Înregistrare deșeu", this::ecranInregistrare),
                butonMeniu("Statistici", this::ecranStatistici),
                butonMeniu("Obiective lunare", this::ecranObiective),
                butonMeniu("Profil și Istoric", this::ecranProfil));
        if (utilizator.esteAdmin()) {
            meniu.getChildren().addAll(
                    butonMeniu("Categorii", this::ecranCategorii),
                    butonMeniu("Utilizatori", this::ecranUtilizatori));
        }
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

    // ===================== HARTĂ =====================

    private VBox ecranHarta() {
        VBox ecran = new VBox(10);

        StackPane zonaHarta = new StackPane(harta);
        zonaHarta.setMinSize(0, 0);

        Rectangle taiere = new Rectangle();
        taiere.widthProperty().bind(zonaHarta.widthProperty());
        taiere.heightProperty().bind(zonaHarta.heightProperty());
        zonaHarta.setClip(taiere);
        VBox.setVgrow(zonaHarta, Priority.ALWAYS);

        HBox legenda = new HBox(25);
        legenda.setAlignment(Pos.CENTER);
        incarca(service::categorii, categorii -> {
            for (Categorie categorie : categorii) {
                Circle cerc = new Circle(7, culoare(categorie));
                HBox element = new HBox(6, cerc, new Label(categorie.denumire()));
                element.setAlignment(Pos.CENTER_LEFT);
                legenda.getChildren().add(element);
            }
        });

        ecran.getChildren().addAll(zonaHarta, legenda);
        return ecran;
    }

    // ===================== ÎNREGISTRARE =====================

    private VBox ecranInregistrare() {
        VBox ecran = new VBox(8);
        ecran.setPadding(new Insets(30));
        ecran.setMaxWidth(420);
        ecran.setMaxHeight(Region.USE_PREF_SIZE);

        ComboBox<Categorie> cmbCategorie = new ComboBox<>();
        cmbCategorie.setMaxWidth(Double.MAX_VALUE);
        ComboBox<PunctColectare> cmbPunct = comboPunct();

        Spinner<Double> spnCantitate = new Spinner<>(0.5, 500.0, 1.0, 0.5);
        spnCantitate.setMaxWidth(Double.MAX_VALUE);
        spinnerEditabil(spnCantitate);

        DatePicker dataPicker = new DatePicker(LocalDate.now());
        dataPicker.setMaxWidth(Double.MAX_VALUE);

        stilCamp(cmbCategorie);
        stilCamp(cmbPunct);
        stilCamp(spnCantitate);
        stilCamp(dataPicker);

        Button btnSalveaza = buton("Înregistrează deșeul");
        btnSalveaza.setMaxWidth(Double.MAX_VALUE);
        btnSalveaza.setDisable(true);

        incarcaNomenclatoare(n -> {
            cmbCategorie.getItems().setAll(n.categorii());
            legaPuncte(cmbCategorie, cmbPunct, n.puncte());
            if (!n.categorii().isEmpty()) cmbCategorie.setValue(n.categorii().get(0));
            btnSalveaza.setDisable(n.categorii().isEmpty());
        });

        btnSalveaza.setOnAction(e -> {
            Categorie categorie = cmbCategorie.getValue();
            PunctColectare punct = cmbPunct.getValue();
            BigDecimal cantitate = BigDecimal.valueOf(spnCantitate.getValue());
            LocalDate data = dataPicker.getValue();
            btnSalveaza.setDisable(true);
            Async.executa(() -> {
                service.inregistreaza(categorie, punct, cantitate, data);
                return null;
            }, v -> {
                btnSalveaza.setDisable(false);
                Alert alerta = new Alert(Alert.AlertType.INFORMATION,
                        "Ai înregistrat " + cantitate.stripTrailingZeros().toPlainString() + " kg de "
                                + categorie.denumire() + " pentru data de " + data + ".");
                alerta.setHeaderText("Înregistrare salvată");
                alerta.show();
                spnCantitate.getValueFactory().setValue(1.0);
            }, mesaj -> {
                btnSalveaza.setDisable(false);
                Async.arataEroare(mesaj);
            });
        });

        VBox.setMargin(btnSalveaza, new Insets(15, 0, 0, 0));
        ecran.getChildren().addAll(
                titlu("Înregistrare deșeu predat"),
                eticheta("CATEGORIE"), cmbCategorie,
                eticheta("PUNCT DE COLECTARE (OPȚIONAL)"), cmbPunct,
                eticheta("CANTITATE (KG)"), spnCantitate,
                eticheta("DATA"), dataPicker,
                btnSalveaza
        );
        return ecran;
    }

    // ===================== STATISTICI =====================

    private VBox ecranStatistici() {
        VBox ecran = new VBox(10);
        ecran.setPadding(new Insets(15));

        ComboBox<Perioada> cmbPerioada = new ComboBox<>();
        cmbPerioada.getItems().addAll(Perioada.values());
        cmbPerioada.setValue(Perioada.LUNA);
        stilCamp(cmbPerioada);

        ComboBox<Categorie> cmbCategorie = new ComboBox<>();
        cmbCategorie.getItems().add(TOATE);
        cmbCategorie.setValue(TOATE);
        stilCamp(cmbCategorie);

        HBox filtru = new HBox(10, eticheta("PERIOADA"), cmbPerioada, eticheta("CATEGORIA"), cmbCategorie);
        filtru.setAlignment(Pos.CENTER_LEFT);

        PieChart graficPie = new PieChart();
        graficPie.setTitle("Pe categorii (tot colegiul)");

        BarChart<String, Number> graficBare = new BarChart<>(new CategoryAxis(), new NumberAxis());
        graficBare.setTitle("Volum reciclat (kg)");
        graficBare.setLegendVisible(false);
        graficBare.setAnimated(false);

        HBox grafice = new HBox(10, graficPie, graficBare);
        HBox.setHgrow(graficPie, Priority.ALWAYS);
        HBox.setHgrow(graficBare, Priority.ALWAYS);
        VBox.setVgrow(grafice, Priority.ALWAYS);

        TableView<Inregistrare> tabel = new TableView<>();
        tabel.setPrefHeight(220);
        tabel.setPlaceholder(new Label("Nu există înregistrări în această perioadă."));

        TableColumn<Inregistrare, String> colData = coloana("Data", 110, r -> r.data().toString());
        TableColumn<Inregistrare, String> colCategorie = coloana("Categorie", 120, r -> r.categorie().denumire());
        TableColumn<Inregistrare, String> colCantitate = coloana("Cantitate", 100, r -> kg(r.cantitate()));
        TableColumn<Inregistrare, String> colPunct = coloana("Punct de colectare", 200,
                r -> r.punct() == null ? "—" : r.punct());
        TableColumn<Inregistrare, String> colUtilizator = coloana("Utilizator", 130, Inregistrare::username);
        tabel.getColumns().addAll(List.of(colData, colCategorie, colCantitate, colPunct, colUtilizator));

        Label lblTotal = new Label();
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Runnable[] actualizeaza = new Runnable[1];
        actualizeaza[0] = () -> {
            Perioada perioada = cmbPerioada.getValue();
            Categorie filtruCategorie = cmbCategorie.getValue() == TOATE ? null : cmbCategorie.getValue();
            incarca(() -> service.totaluriPeCategorii(perioada), totaluri -> {
                graficPie.getData().clear();
                graficBare.getData().clear();
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                for (Map.Entry<Categorie, BigDecimal> t : totaluri.entrySet()) {
                    double valoare = t.getValue().doubleValue();
                    if (valoare > 0) graficPie.getData().add(new PieChart.Data(t.getKey().denumire(), valoare));
                    XYChart.Data<String, Number> bara = new XYChart.Data<>(t.getKey().denumire(), valoare);
                    String culoare = t.getKey().culoare();
                    bara.nodeProperty().addListener((obs, vechi, nod) -> {
                        if (nod != null) nod.setStyle("-fx-bar-fill: " + culoare + ";");
                    });
                    serie.getData().add(bara);
                }
                graficBare.getData().add(serie);
            });
            incarca(() -> service.inregistrari(perioada, filtruCategorie), lista -> {
                tabel.getItems().setAll(lista);
                BigDecimal total = lista.stream().map(Inregistrare::cantitate).reduce(BigDecimal.ZERO, BigDecimal::add);
                lblTotal.setText((utilizator.esteAdmin() ? "Total înregistrări afișate: " : "Totalul tău în perioadă: ")
                        + kg(total));
            });
        };

        // Editare / ștergere pentru înregistrarea selectată (proprie sau, pentru admin, oricare)
        Button btnEditeaza = buton("Editează");
        Button btnSterge = buton("Șterge");
        btnEditeaza.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());
        btnSterge.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());
        btnEditeaza.setOnAction(e -> editeazaInregistrare(tabel.getSelectionModel().getSelectedItem(), actualizeaza[0]));
        btnSterge.setOnAction(e -> {
            Inregistrare r = tabel.getSelectionModel().getSelectedItem();
            if (confirma("Ștergi înregistrarea din " + r.data() + " (" + kg(r.cantitate()) + " "
                    + r.categorie().denumire() + ")?")) {
                executaSiReincarca(() -> service.stergeInregistrare(r), actualizeaza[0]);
            }
        });
        tabel.setRowFactory(tv -> {
            TableRow<Inregistrare> rand = new TableRow<>();
            rand.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !rand.isEmpty()) editeazaInregistrare(rand.getItem(), actualizeaza[0]);
            });
            return rand;
        });

        Region spatiu = new Region();
        HBox.setHgrow(spatiu, Priority.ALWAYS);
        HBox subTabel = new HBox(10, lblTotal, spatiu, btnEditeaza, btnSterge);
        subTabel.setAlignment(Pos.CENTER_LEFT);

        incarca(service::categorii, categorii -> cmbCategorie.getItems().addAll(categorii));
        cmbPerioada.setOnAction(e -> actualizeaza[0].run());
        cmbCategorie.setOnAction(e -> actualizeaza[0].run());
        actualizeaza[0].run();

        String titluTabel = utilizator.esteAdmin() ? "TOATE ÎNREGISTRĂRILE" : "ÎNREGISTRĂRILE MELE";
        ecran.getChildren().addAll(titlu("Statistici de colectare"), filtru, grafice,
                eticheta(titluTabel), tabel, subTabel);
        return ecran;
    }

    private void editeazaInregistrare(Inregistrare r, Runnable dupa) {
        if (!service.poateModifica(r)) {
            Async.arataEroare("Puteți modifica doar propriile înregistrări.");
            return;
        }
        incarcaNomenclatoare(n -> {
            ComboBox<Categorie> cmbCategorie = new ComboBox<>();
            cmbCategorie.getItems().setAll(n.categorii());
            ComboBox<PunctColectare> cmbPunct = comboPunct();
            legaPuncte(cmbCategorie, cmbPunct, n.puncte());
            cmbCategorie.setValue(r.categorie());
            n.puncte().stream().filter(p -> r.punctId() != null && p.id() == r.punctId())
                    .findFirst().ifPresent(cmbPunct::setValue);

            Spinner<Double> spnCantitate = new Spinner<>(0.01, 99999.0, r.cantitate().doubleValue(), 0.5);
            spinnerEditabil(spnCantitate);
            DatePicker dataPicker = new DatePicker(r.data());

            VBox continut = new VBox(6,
                    eticheta("CATEGORIE"), cmbCategorie,
                    eticheta("PUNCT DE COLECTARE (OPȚIONAL)"), cmbPunct,
                    eticheta("CANTITATE (KG)"), spnCantitate,
                    eticheta("DATA"), dataPicker);
            for (Node nod : continut.getChildren()) if (nod instanceof Control c) c.setMaxWidth(Double.MAX_VALUE);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Editare înregistrare");
            dialog.setHeaderText("Înregistrarea #" + r.id() + " — " + r.username());
            dialog.getDialogPane().setContent(continut);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> executaSiReincarca(
                    () -> service.modificaInregistrare(r, cmbCategorie.getValue(), cmbPunct.getValue(),
                            BigDecimal.valueOf(spnCantitate.getValue()), dataPicker.getValue()),
                    dupa));
        });
    }

    // ===================== OBIECTIVE =====================

    private VBox ecranObiective() {
        VBox ecran = new VBox(15);
        ecran.setPadding(new Insets(20));

        YearMonth luna = YearMonth.now();
        String numeLuna = luna.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ro"));

        VBox zonaProgres = new VBox(14);

        Runnable afiseaza = () -> incarca(() -> service.progres(luna), progres -> {
            zonaProgres.getChildren().clear();
            for (ProgresLunar p : progres) {
                Label nume = new Label(p.categorie().denumire());
                nume.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                nume.setTextFill(culoare(p.categorie()));
                nume.setMinWidth(90);

                String text = p.tinta().signum() > 0
                        ? String.format("%.2f / %.2f kg", p.colectat(), p.tinta())
                        : String.format("%.2f kg (fără obiectiv)", p.colectat());
                if (p.atins()) {
                    text += "   ✔ obiectiv atins!";
                }

                HBox rand = new HBox(15, nume, bara(p.procent(), 300), new Label(text));
                rand.setAlignment(Pos.CENTER_LEFT);
                zonaProgres.getChildren().add(rand);
            }
        });

        ecran.getChildren().add(titlu("Obiective lunare de colectare — " + numeLuna + " " + luna.getYear()));

        if (utilizator.esteAdmin()) {
            ComboBox<Categorie> cmbCategorie = new ComboBox<>();
            Spinner<Double> spnKg = new Spinner<>(0.0, 100000.0, 50.0, 5.0);
            spinnerEditabil(spnKg);

            stilCamp(cmbCategorie);
            stilCamp(spnKg);
            incarca(service::categorii, categorii -> {
                cmbCategorie.getItems().setAll(categorii);
                if (!categorii.isEmpty()) cmbCategorie.setValue(categorii.get(0));
            });

            Button btnSeteaza = buton("Setează obiectivul lunar");
            btnSeteaza.setOnAction(e -> executaSiReincarca(() -> service.seteazaObiectiv(
                    cmbCategorie.getValue(), luna, BigDecimal.valueOf(spnKg.getValue())), afiseaza));

            HBox formular = new HBox(10, cmbCategorie, spnKg, new Label("kg / lună"), btnSeteaza);
            formular.setAlignment(Pos.CENTER_LEFT);
            ecran.getChildren().addAll(formular, eticheta("O ȚINTĂ DE 0 KG ELIMINĂ OBIECTIVUL CATEGORIEI."));
        }

        afiseaza.run();
        ecran.getChildren().add(zonaProgres);
        return ecran;
    }

    // ===================== PROFIL =====================

    private VBox ecranProfil() {
        VBox ecran = new VBox(15);
        ecran.setPadding(new Insets(20));

        Circle cerc = new Circle(30, VERDE);
        Label initiala = new Label(utilizator.numeComplet().substring(0, 1).toUpperCase());
        initiala.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        initiala.setTextFill(Color.WHITE);
        StackPane avatar = new StackPane(cerc, initiala);

        Label lblTotal = new Label();
        VBox date = new VBox(3,
                titlu(utilizator.numeComplet()),
                eticheta(utilizator.rol().getEticheta().toUpperCase() + "  ·  @" + utilizator.username()),
                lblTotal);

        HBox antet = new HBox(15, avatar, date);
        antet.setAlignment(Pos.CENTER_LEFT);

        incarca(utilizatorService::profilulMeu, profil -> lblTotal.setText(String.format(
                "Total înregistrat: %s  ·  %d înregistrări", kg(profil.totalKg()), profil.numarInregistrari())));

        ListView<String> lista = new ListView<>();
        lista.setPlaceholder(new Label("Nu există nicio înregistrare personală."));
        incarca(service::istoriculMeu, istoric -> {
            for (Inregistrare d : istoric) {
                lista.getItems().add(d.data() + "   -   " + d.categorie().denumire() + "   -   " + kg(d.cantitate())
                        + (d.punct() == null ? "" : "   -   " + d.punct()));
            }
        });
        VBox.setVgrow(lista, Priority.ALWAYS);
        VBox coloanaIstoric = new VBox(8, eticheta("ISTORICUL CONTRIBUȚIILOR"), lista);

        HBox continut = new HBox(20, coloanaIstoric);
        HBox.setHgrow(coloanaIstoric, Priority.ALWAYS);
        VBox.setVgrow(continut, Priority.ALWAYS);

        ecran.getChildren().addAll(antet, continut);
        return ecran;
    }

    // ===================== CATEGORII (ADMIN) =====================

    private VBox ecranCategorii() {
        VBox ecran = new VBox(12);
        ecran.setPadding(new Insets(20));

        TableView<Categorie> tabel = new TableView<>();
        tabel.setPlaceholder(new Label("Nu există categorii."));
        TableColumn<Categorie, String> colCuloare = new TableColumn<>("Culoare");
        colCuloare.setPrefWidth(80);
        colCuloare.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String hex, boolean gol) {
                super.updateItem(hex, gol);
                setGraphic(gol || hex == null ? null : new Circle(8, Color.web(hex)));
            }
        });
        colCuloare.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().culoare()));
        tabel.getColumns().addAll(List.of(colCuloare, coloana("Denumire", 250, Categorie::denumire)));
        VBox.setVgrow(tabel, Priority.ALWAYS);

        TextField txtDenumire = new TextField();
        txtDenumire.setPromptText("Denumirea categoriei");
        ColorPicker culoare = new ColorPicker(VERDE);
        stilCamp(txtDenumire);

        Runnable reincarca = () -> incarca(service::categorii, tabel.getItems()::setAll);
        tabel.getSelectionModel().selectedItemProperty().addListener((obs, vechi, c) -> {
            if (c != null) {
                txtDenumire.setText(c.denumire());
                culoare.setValue(Color.web(c.culoare()));
            }
        });

        Button btnAdauga = buton("Adaugă");
        Button btnSalveaza = buton("Salvează modificările");
        Button btnSterge = buton("Șterge");
        btnSalveaza.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());
        btnSterge.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());

        btnAdauga.setOnAction(e -> executaSiReincarca(
                () -> service.adaugaCategorie(txtDenumire.getText(), hex(culoare.getValue())), reincarca));
        btnSalveaza.setOnAction(e -> {
            Categorie c = tabel.getSelectionModel().getSelectedItem();
            executaSiReincarca(() -> service.modificaCategorie(c.id(), txtDenumire.getText(), hex(culoare.getValue())),
                    reincarca);
        });
        btnSterge.setOnAction(e -> {
            Categorie c = tabel.getSelectionModel().getSelectedItem();
            if (confirma("Ștergi categoria „" + c.denumire() + "”? Obiectivele ei lunare vor fi șterse.")) {
                executaSiReincarca(() -> service.stergeCategorie(c.id()), reincarca);
            }
        });

        HBox formular = new HBox(10, txtDenumire, culoare, btnAdauga, btnSalveaza, btnSterge);
        formular.setAlignment(Pos.CENTER_LEFT);
        reincarca.run();

        ecran.getChildren().addAll(titlu("Categorii de deșeuri"), formular, tabel);
        return ecran;
    }

    // ===================== UTILIZATORI (ADMIN) =====================

    private VBox ecranUtilizatori() {
        VBox ecran = new VBox(12);
        ecran.setPadding(new Insets(20));

        TableView<Utilizator> tabel = new TableView<>();
        tabel.getColumns().addAll(List.of(
                coloana("Utilizator", 150, Utilizator::username),
                coloana("Nume complet", 220, Utilizator::numeComplet),
                coloana("Rol", 130, u -> u.rol().getEticheta()),
                coloana("Stare", 100, u -> u.activ() ? "Activ" : "Dezactivat")));
        VBox.setVgrow(tabel, Priority.ALWAYS);

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Nume de utilizator");
        TextField txtNume = new TextField();
        txtNume.setPromptText("Nume complet");
        ComboBox<Rol> cmbRol = new ComboBox<>();
        cmbRol.getItems().addAll(Rol.values());
        cmbRol.setValue(Rol.UTILIZATOR);
        CheckBox chkActiv = new CheckBox("Activ");
        chkActiv.setSelected(true);
        PasswordField txtParola = new PasswordField();
        txtParola.setPromptText("Parolă (min. 8 caractere)");
        PasswordField txtConfirmare = new PasswordField();
        txtConfirmare.setPromptText("Repetă parola");
        for (Control c : List.of(txtUsername, txtNume, cmbRol, txtParola, txtConfirmare)) stilCamp(c);

        Runnable reincarca = () -> incarca(utilizatorService::lista, tabel.getItems()::setAll);
        tabel.getSelectionModel().selectedItemProperty().addListener((obs, vechi, u) -> {
            if (u != null) {
                txtUsername.setText(u.username());
                txtNume.setText(u.numeComplet());
                cmbRol.setValue(u.rol());
                chkActiv.setSelected(u.activ());
            }
        });

        Button btnAdauga = buton("Adaugă cont");
        Button btnSalveaza = buton("Salvează modificările");
        Button btnParola = buton("Resetează parola");
        btnSalveaza.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());
        btnParola.disableProperty().bind(tabel.getSelectionModel().selectedItemProperty().isNull());

        btnAdauga.setOnAction(e -> {
            char[] parola = txtParola.getText().toCharArray();
            char[] confirmare = txtConfirmare.getText().toCharArray();
            String username = txtUsername.getText();
            String nume = txtNume.getText();
            Rol rol = cmbRol.getValue();
            executaSiReincarca(() -> {
                try {
                    utilizatorService.adauga(username, nume, parola, confirmare, rol);
                } finally {
                    Arrays.fill(parola, '\0');
                    Arrays.fill(confirmare, '\0');
                }
            }, () -> {
                txtParola.clear();
                txtConfirmare.clear();
                reincarca.run();
            });
        });
        btnSalveaza.setOnAction(e -> {
            Utilizator u = tabel.getSelectionModel().getSelectedItem();
            executaSiReincarca(() -> utilizatorService.modifica(u.id(), txtNume.getText(), cmbRol.getValue(),
                    chkActiv.isSelected()), reincarca);
        });
        btnParola.setOnAction(e -> {
            Utilizator u = tabel.getSelectionModel().getSelectedItem();
            char[] parola = txtParola.getText().toCharArray();
            char[] confirmare = txtConfirmare.getText().toCharArray();
            executaSiReincarca(() -> {
                try {
                    utilizatorService.reseteazaParola(u.id(), parola, confirmare);
                } finally {
                    Arrays.fill(parola, '\0');
                    Arrays.fill(confirmare, '\0');
                }
            }, () -> {
                txtParola.clear();
                txtConfirmare.clear();
                new Alert(Alert.AlertType.INFORMATION, "Parola utilizatorului " + u.username() + " a fost schimbată.").show();
            });
        });

        GridPane formular = new GridPane();
        formular.setHgap(10);
        formular.setVgap(8);
        formular.addRow(0, eticheta("UTILIZATOR"), txtUsername, eticheta("NUME"), txtNume, eticheta("ROL"), cmbRol, chkActiv);
        formular.addRow(1, eticheta("PAROLĂ"), txtParola, eticheta("CONFIRMARE"), txtConfirmare);
        HBox butoane = new HBox(10, btnAdauga, btnSalveaza, btnParola);
        reincarca.run();

        ecran.getChildren().addAll(titlu("Utilizatori"), formular, butoane,
                eticheta("SELECTAȚI UN CONT PENTRU A-L MODIFICA. NUMELE DE UTILIZATOR NU SE POATE SCHIMBA."), tabel);
        return ecran;
    }

    // ===================== ÎNCĂRCARE DATE =====================

    // Încarcă date din baza de date pe firul de fundal, apoi le afișează pe firul JavaFX.
    private <T> void incarca(Callable<T> sursa, Consumer<T> afisare) {
        Async.executa(sursa, afisare);
    }

    private void incarcaNomenclatoare(Consumer<Nomenclatoare> afisare) {
        incarca(() -> new Nomenclatoare(service.categorii(), service.puncte()), afisare);
    }

    // Execută o operație de scriere; după succes, reîncarcă datele afișate.
    private void executaSiReincarca(Runnable operatie, Runnable dupa) {
        Async.executa(() -> {
            operatie.run();
            return null;
        }, v -> dupa.run());
    }

    private boolean confirma(String mesaj) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mesaj, ButtonType.YES, ButtonType.NO);
        alerta.setHeaderText("Confirmare");
        return alerta.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    // Lista de puncte se filtrează după categoria aleasă (un punct acceptă o singură categorie).
    private void legaPuncte(ComboBox<Categorie> cmbCategorie, ComboBox<PunctColectare> cmbPunct,
                            List<PunctColectare> puncte) {
        cmbCategorie.valueProperty().addListener((obs, vechi, categorie) -> {
            cmbPunct.setValue(null);
            cmbPunct.getItems().setAll(puncte.stream()
                    .filter(p -> categorie != null && p.categorie().id() == categorie.id()).toList());
        });
    }

    private ComboBox<PunctColectare> comboPunct() {
        ComboBox<PunctColectare> cmb = new ComboBox<>();
        cmb.setMaxWidth(Double.MAX_VALUE);
        cmb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PunctColectare p, boolean gol) {
                super.updateItem(p, gol);
                setText(gol || p == null ? "Fără punct anume" : p.denumire());
            }
        });
        cmb.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.DELETE || e.getCode() == javafx.scene.input.KeyCode.BACK_SPACE) {
                cmb.setValue(null);
            }
        });
        cmb.setTooltip(new Tooltip("Delete / Backspace: fără punct anume"));
        return cmb;
    }

    // ===================== STIL (neschimbat) =====================

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

    private static <S> TableColumn<S, String> coloana(String titlu, double latime, java.util.function.Function<S, String> valoare) {
        TableColumn<S, String> col = new TableColumn<>(titlu);
        col.setPrefWidth(latime);
        col.setCellValueFactory(c -> new SimpleStringProperty(valoare.apply(c.getValue())));
        return col;
    }

    private static Color culoare(Categorie categorie) {
        return Color.web(categorie.culoare());
    }

    private static String hex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed() * 255), (int) Math.round(c.getGreen() * 255), (int) Math.round(c.getBlue() * 255));
    }

    private static String kg(BigDecimal valoare) {
        return String.format("%.2f kg", valoare);
    }
}
