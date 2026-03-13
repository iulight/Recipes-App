package com.retete.controller;

import com.retete.model.ElementListaCumparaturi;
import com.retete.model.Ingredient;
import com.retete.model.PurchaseEvent;
import com.retete.model.Reteta;
import com.retete.service.RetetaService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // ---- Title Bar ----
    @FXML private HBox titleBar;
    @FXML private Button btnClose;
    @FXML private Button btnMinimize;

    // ---- Nav Sidebar ----
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavRecipes;
    @FXML private Button btnNavShopping;
    @FXML private Label labelHeaderStats;

    // ---- Views ----
    @FXML private VBox dashboardView;
    @FXML private HBox recipesView;
    @FXML private VBox shoppingView;
    @FXML private StackPane contentArea;

    // ---- Dashboard ----
    @FXML private FlowPane dashGrid;
    @FXML private TextField dashSearch;

    // ---- Recipes sidebar ----
    @FXML private TextField campCautare;
    @FXML private ComboBox<String> comboCategorie;
    @FXML private ListView<Reteta> listaRetete;
    @FXML private Label labelCount;

    // ---- Recipe detail ----
    @FXML private StackPane detailContainer;
    @FXML private VBox emptyState;
    @FXML private VBox retetaContent;
    @FXML private VBox ingredienteCard;
    @FXML private VBox instructiuniCard;
    @FXML private StackPane imageContainer;
    @FXML private ImageView heroImage;
    @FXML private Label imagePlaceholder;
    @FXML private Label labelNume;
    @FXML private Label labelCategorie;
    @FXML private Label labelTimp;
    @FXML private Label labelPortii;
    @FXML private Label labelDescriere;
    @FXML private FlowPane panelIngrediente;
    @FXML private TextArea areaInstructiuni;
    @FXML private Button btnAdaugaInLista;

    // ---- Shopping List ----
    @FXML private HBox dateBar;
    @FXML private DatePicker dateStart;
    @FXML private DatePicker dateEnd;
    @FXML private ListView<ElementListaCumparaturi> listaItems;
    @FXML private Label labelTotal;
    @FXML private Label labelCumparate;
    @FXML private Label labelProgres;
    @FXML private ProgressBar progressBar;
    @FXML private TextField campNumeItem;
    @FXML private TextField campCantitateItem;
    @FXML private TextField campUnitateItem;
    @FXML private TextField campCategorie;
    @FXML private TextField campPret;

    // ---- Purchase History ----
    @FXML private TableView<PurchaseEvent> tableIstoric;
    @FXML private TableColumn<PurchaseEvent, String> colProdus;
    @FXML private TableColumn<PurchaseEvent, String> colCantitate;
    @FXML private TableColumn<PurchaseEvent, String> colCategorie;
    @FXML private TableColumn<PurchaseEvent, String> colPret;
    @FXML private TableColumn<PurchaseEvent, String> colData;
    @FXML private Label labelHistoryCount;

    private double xOffset = 0;
    private double yOffset = 0;
    private RetetaService service;
    private Reteta retetaSelectata;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = new RetetaService();
        setupTitleBar();
        initDashboard();
        initRetete();
        initListaCumparaturi();
        initPurchaseHistoryTable();
        actualizeazaHeaderStats();
    }

    // ===================== TITLE BAR =====================

    private void setupTitleBar() {
        if (titleBar == null) return;
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
        if (btnClose != null) btnClose.setOnAction(e -> Platform.exit());
        if (btnMinimize != null)
            btnMinimize.setOnAction(e -> ((Stage) btnMinimize.getScene().getWindow()).setIconified(true));
    }

    // ===================== NAVIGATION =====================

    @FXML
    private void onNavDashboard() {
        showView(dashboardView, recipesView, shoppingView);
        setActiveNav(btnNavDashboard);
    }

    @FXML
    private void onNavRecipes() {
        showView(recipesView, dashboardView, shoppingView);
        setActiveNav(btnNavRecipes);
    }

    @FXML
    private void onNavShopping() {
        showView(shoppingView, dashboardView, recipesView);
        setActiveNav(btnNavShopping);
        refreshPurchaseHistory();
    }

    private void showView(Region visible, Region... hidden) {
        visible.setVisible(true);
        visible.setManaged(true);
        for (Region h : hidden) {
            h.setVisible(false);
            h.setManaged(false);
        }
    }

    private void setActiveNav(Button active) {
        for (Button b : new Button[]{btnNavDashboard, btnNavRecipes, btnNavShopping}) {
            if (b == null) continue;
            b.getStyleClass().remove("nav-btn-active");
        }
        if (active != null) {
            if (!active.getStyleClass().contains("nav-btn-active"))
                active.getStyleClass().add("nav-btn-active");
        }
    }

    // ===================== DASHBOARD =====================

    private void initDashboard() {
        buildDashboardCards(service.getRetete());
    }

    private void buildDashboardCards(List<Reteta> retete) {
        if (dashGrid == null) return;
        dashGrid.getChildren().clear();
        for (Reteta r : retete) {
            dashGrid.getChildren().add(buildRecipeCard(r));
        }
    }

    private VBox buildRecipeCard(Reteta r) {
        VBox card = new VBox(10);
        card.setPrefWidth(200);
        card.setMaxWidth(200);
        card.setPadding(new Insets(0, 0, 14, 0));
        card.getStyleClass().add("dash-card");
        card.setOnMouseClicked(e -> {
            onNavRecipes();
            listaRetete.getSelectionModel().select(r);
        });

        // Image area
        StackPane imgArea = new StackPane();
        imgArea.setPrefHeight(130);
        imgArea.setStyle("-fx-background-color: #E8E4DC; -fx-background-radius: 14 14 0 0;");
        Label emojiPh = new Label(getCategoryEmoji(r.getCategorie()));
        emojiPh.setStyle("-fx-font-size: 42px; -fx-opacity: 0.5;");
        imgArea.getChildren().add(emojiPh);

        if (r.getImagine() != null && !r.getImagine().isEmpty()) {
            ImageView iv = new ImageView();
            iv.setFitWidth(200);
            iv.setFitHeight(130);
            iv.setPreserveRatio(false);
            iv.setSmooth(true);
            iv.setStyle("-fx-background-radius: 14 14 0 0;");
            imgArea.getChildren().add(0, iv);
            Task<Image> task = new Task<>() {
                @Override protected Image call() { return new Image(r.getImagine(), true); }
            };
            task.setOnSucceeded(ev -> {
                Image img = task.getValue();
                if (!img.isError()) {
                    iv.setImage(img);
                    emojiPh.setVisible(false);
                }
            });
            Thread t = new Thread(task); t.setDaemon(true); t.start();
        }

        // Text area
        VBox info = new VBox(4);
        info.setPadding(new Insets(0, 12, 0, 12));

        Label name = new Label(r.getNume());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1A1A1A; -fx-wrap-text: true;");
        name.setWrapText(true);

        HBox meta = new HBox(6);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label cat = new Label(getCategoryEmoji(r.getCategorie()) + " " + r.getCategorie());
        cat.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
        Label timp = new Label("⏱ " + r.getTimpPreparare());
        timp.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
        meta.getChildren().addAll(cat, timp);

        info.getChildren().addAll(name, meta);
        card.getChildren().addAll(imgArea, info);
        return card;
    }

    @FXML
    private void onDashSearch() {
        String text = dashSearch != null ? dashSearch.getText().trim() : "";
        List<Reteta> results = service.cauta(text, "Toate categoriile");
        buildDashboardCards(results);
    }

    // ===================== RETETE =====================

    private void initRetete() {
        comboCategorie.setItems(FXCollections.observableList(service.getCategorii()));
        comboCategorie.setValue("Toate categoriile");

        listaRetete.setItems(service.getRetete());
        listaRetete.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Reteta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); setStyle(""); return; }
                boolean selected = getListView().getSelectionModel().getSelectedItem() == item;

                HBox root = new HBox(0);
                root.setAlignment(Pos.CENTER_LEFT);

                Region indicator = new Region();
                indicator.setPrefWidth(4); indicator.setMinWidth(4);
                indicator.setStyle(selected ? "-fx-background-color: white;" : "-fx-background-color: transparent;");

                VBox content = new VBox(3);
                content.setPadding(new Insets(10, 14, 10, 10));
                HBox.setHgrow(content, Priority.ALWAYS);

                Label numeLabel = new Label(item.getNume());
                numeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: "
                        + (selected ? "white;" : "rgba(255,255,255,0.9);"));

                HBox meta = new HBox(8); meta.setAlignment(Pos.CENTER_LEFT);
                Label catLabel = new Label(getCategoryEmoji(item.getCategorie()) + " " + item.getCategorie());
                catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.6);");
                Label timpLabel = new Label("⏱ " + item.getTimpPreparare());
                timpLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.6);");
                meta.getChildren().addAll(catLabel, timpLabel);

                content.getChildren().addAll(numeLabel, meta);
                root.getChildren().addAll(indicator, content);
                setGraphic(root); setText(null);
            }
        });

        listaRetete.getSelectionModel().selectedItemProperty().addListener((obs, old, nou) -> {
            if (nou != null) { listaRetete.refresh(); afiseazaRetetaAnimat(nou); }
        });

        actualizeazaCount();
        if (!service.getRetete().isEmpty()) listaRetete.getSelectionModel().selectFirst();

        campCautare.textProperty().addListener((obs, old, nou) -> filtreaza());
        comboCategorie.valueProperty().addListener((obs, old, nou) -> filtreaza());
    }

    private void afiseazaRetetaAnimat(Reteta r) {
        retetaSelectata = r;
        FadeTransition fadeOut = new FadeTransition(Duration.millis(120), retetaContent);
        fadeOut.setFromValue(1.0); fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            incarcaDateReteta(r);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), retetaContent);
            fadeIn.setFromValue(0.0); fadeIn.setToValue(1.0);
            fadeIn.play();
            animaCard(ingredienteCard, 80);
            animaCard(instructiuniCard, 160);
        });
        if (retetaContent.isVisible()) {
            fadeOut.play();
        } else {
            emptyState.setVisible(false); emptyState.setManaged(false);
            retetaContent.setVisible(true); retetaContent.setManaged(true);
            ingredienteCard.setVisible(true); ingredienteCard.setManaged(true);
            instructiuniCard.setVisible(true); instructiuniCard.setManaged(true);
            if (btnAdaugaInLista != null) { btnAdaugaInLista.setVisible(true); btnAdaugaInLista.setManaged(true); }
            incarcaDateReteta(r);
            animaCard(retetaContent, 0);
            animaCard(ingredienteCard, 80);
            animaCard(instructiuniCard, 160);
        }
    }

    private void animaCard(VBox card, int delayMs) {
        card.setTranslateY(20); card.setOpacity(0);
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(300), card);
            fade.setFromValue(0); fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(300), card);
            slide.setFromY(20); slide.setToY(0); slide.setInterpolator(Interpolator.EASE_OUT);
            new ParallelTransition(fade, slide).play();
        });
        pause.play();
    }

    private void incarcaDateReteta(Reteta r) {
        labelNume.setText(r.getNume());
        labelCategorie.setText("📂 " + r.getCategorie());
        labelTimp.setText("⏱ " + r.getTimpPreparare());
        labelPortii.setText("🍽 " + r.getPortii() + " portii");
        labelDescriere.setText(r.getDescriere());
        areaInstructiuni.setText(r.getInstructiuni());
        afiseazaImagineReteta(r);
        construiesteCartiIngrediente(r.getIngrediente());
    }

    private void afiseazaImagineReteta(Reteta reteta) {
        if (heroImage != null) heroImage.setImage(null);
        if (imagePlaceholder != null) imagePlaceholder.setVisible(true);
        if (reteta.getImagine() != null && !reteta.getImagine().isEmpty()) {
            Task<Image> task = new Task<>() {
                @Override protected Image call() { return new Image(reteta.getImagine(), true); }
            };
            task.setOnSucceeded(e -> {
                Image img = task.getValue();
                if (heroImage != null && !img.isError()) {
                    heroImage.setImage(img);
                    heroImage.fitWidthProperty().bind(imageContainer.widthProperty());
                    FadeTransition ft = new FadeTransition(Duration.millis(400), heroImage);
                    ft.setFromValue(0); ft.setToValue(1); ft.play();
                }
                if (imagePlaceholder != null) imagePlaceholder.setVisible(img.isError());
            });
            Thread t = new Thread(task); t.setDaemon(true); t.start();
        }
    }

    private void construiesteCartiIngrediente(List<Ingredient> ingrediente) {
        panelIngrediente.getChildren().clear();
        for (Ingredient ing : ingrediente) {
            VBox card = new VBox(6);
            card.setPrefWidth(110); card.setAlignment(Pos.CENTER); card.setPadding(new Insets(12));
            card.getStyleClass().add("ingredient-card");

            StackPane imgPh = new StackPane(); imgPh.setPrefSize(70, 70);
            imgPh.setStyle("-fx-background-color: #F0EDE8; -fx-background-radius: 50%;");
            Label emoji = new Label(getEmojiForIngredient(ing.getNume()));
            emoji.setStyle("-fx-font-size: 28px;");
            imgPh.getChildren().add(emoji);

            Label lblNume = new Label(ing.getNume());
            lblNume.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A; -fx-text-alignment: center; -fx-wrap-text: true; -fx-max-width: 95px;");
            lblNume.setWrapText(true); lblNume.setTextAlignment(TextAlignment.CENTER);

            String cantText = (ing.getCantitate() == (long) ing.getCantitate())
                    ? ((long) ing.getCantitate()) + " " + ing.getUnitate()
                    : ing.getCantitate() + " " + ing.getUnitate();
            Label lblCant = new Label(cantText);
            lblCant.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");

            card.getChildren().addAll(imgPh, lblNume, lblCant);
            panelIngrediente.getChildren().add(card);
        }
    }

    private void filtreaza() {
        String text = campCautare.getText();
        String categorie = comboCategorie.getValue();
        List<Reteta> rezultate = service.cauta(text, categorie);
        listaRetete.setItems(FXCollections.observableList(rezultate));
        actualizeazaCount();
        if (!rezultate.isEmpty()) listaRetete.getSelectionModel().selectFirst();
    }

    private void actualizeazaCount() {
        int n = listaRetete.getItems().size();
        labelCount.setText(n + (n == 1 ? " reteta" : " retete"));
    }

    @FXML
    private void adaugaInListaCumparaturi() {
        onAdaugaInLista();
    }

    @FXML
    private void onAdaugaInLista() {
        if (retetaSelectata == null) return;
        service.adaugaIngredienteInLista(retetaSelectata);
        actualizeazaStatisticeLista();
        actualizeazaHeaderStats();

        if (btnAdaugaInLista != null) {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), btnAdaugaInLista);
            scale.setFromX(1); scale.setFromY(1); scale.setToX(0.95); scale.setToY(0.95);
            scale.setAutoReverse(true); scale.setCycleCount(2); scale.play();
        }
        showNotificare("✅  Ingredientele din \"" + retetaSelectata.getNume() + "\" au fost adaugate!");
    }

    @FXML
    private void onAdaugaReteta() {
        Dialog<Reteta> dialog = new Dialog<>();
        dialog.setTitle("Reteta Noua");
        dialog.setHeaderText(null);
        ButtonType btnSalveaza = new ButtonType("✅  Salveaza", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalveaza, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-font-family: 'Segoe UI';");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white;");

        TextField tfNume = styledField("ex: Ciorba de burta");
        TextField tfCategorie = styledField("ex: Supe & Ciorbe");
        TextField tfTimp = styledField("ex: 45 min");
        TextField tfPortii = styledField("4");
        TextArea taDescriere = new TextArea(); taDescriere.setPromptText("Descriere scurta..."); taDescriere.setPrefHeight(70);
        taDescriere.setStyle("-fx-background-color: #F5F1EC; -fx-border-color: transparent; -fx-background-radius: 12; -fx-font-size: 13px;");
        TextArea taInstructiuni = new TextArea(); taInstructiuni.setPromptText("Pasii de preparare..."); taInstructiuni.setPrefHeight(110);
        taInstructiuni.setStyle("-fx-background-color: #F5F1EC; -fx-border-color: transparent; -fx-background-radius: 12; -fx-font-size: 13px;");

        grid.add(boldLabel("Nume reteta *"), 0, 0); grid.add(tfNume, 1, 0);
        grid.add(boldLabel("Categorie"), 0, 1); grid.add(tfCategorie, 1, 1);
        grid.add(boldLabel("Timp preparare"), 0, 2); grid.add(tfTimp, 1, 2);
        grid.add(boldLabel("Portii"), 0, 3); grid.add(tfPortii, 1, 3);
        grid.add(boldLabel("Descriere"), 0, 4); grid.add(taDescriere, 1, 4);
        grid.add(boldLabel("Instructiuni"), 0, 5); grid.add(taInstructiuni, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(520);
        dialog.setResultConverter(btn -> {
            if (btn == btnSalveaza) {
                try {
                    int portii = Integer.parseInt(tfPortii.getText().trim());
                    return new Reteta(null, tfNume.getText().trim(), tfCategorie.getText().trim(),
                            tfTimp.getText().trim(), taDescriere.getText().trim(),
                            taInstructiuni.getText().trim(), portii);
                } catch (NumberFormatException e) { return null; }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            if (r != null && !r.getNume().isEmpty()) {
                service.adaugaReteta(r);
                comboCategorie.setItems(FXCollections.observableList(service.getCategorii()));
                listaRetete.setItems(service.getRetete());
                actualizeazaCount();
                actualizeazaHeaderStats();
                listaRetete.getSelectionModel().select(r);
                buildDashboardCards(service.getRetete());
            }
        });
    }

    @FXML
    private void onStergeReteta() {
        if (retetaSelectata == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmare");
        confirm.setHeaderText("Stergi reteta?");
        confirm.setContentText("\"" + retetaSelectata.getNume() + "\" va fi stearsa definitiv.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.stergeReteta(retetaSelectata);
                listaRetete.setItems(service.getRetete());
                actualizeazaCount();
                actualizeazaHeaderStats();
                retetaSelectata = null;
                retetaContent.setVisible(false); retetaContent.setManaged(false);
                ingredienteCard.setVisible(false); ingredienteCard.setManaged(false);
                instructiuniCard.setVisible(false); instructiuniCard.setManaged(false);
                if (btnAdaugaInLista != null) { btnAdaugaInLista.setVisible(false); btnAdaugaInLista.setManaged(false); }
                emptyState.setVisible(true); emptyState.setManaged(true);
                buildDashboardCards(service.getRetete());
            }
        });
    }

    // ===================== LISTA CUMPARATURI =====================

    private void initListaCumparaturi() {
        listaItems.setItems(service.getListaCumparaturi());
        listaItems.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ElementListaCumparaturi item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }

                HBox row = new HBox(12); row.setAlignment(Pos.CENTER_LEFT); row.setPadding(new Insets(10, 16, 10, 16));

                CheckBox cb = new CheckBox();
                cb.setSelected(item.isCumparat());
                cb.setStyle("-fx-cursor: hand;");
                cb.selectedProperty().addListener((obs, old, nou) -> {
                    service.marcheazaCumparat(item, nou);
                    actualizeazaStatisticeLista();
                    listaItems.refresh();
                    refreshPurchaseHistory();
                });

                VBox info = new VBox(2); HBox.setHgrow(info, Priority.ALWAYS);
                String textStyle = item.isCumparat()
                        ? "-fx-strikethrough: true; -fx-text-fill: #C0B8B0; -fx-font-size: 13px;"
                        : "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;";

                Label textLabel = new Label(item.getAfisare()); textLabel.setStyle(textStyle);
                HBox metaRow = new HBox(8); metaRow.setAlignment(Pos.CENTER_LEFT);
                Label sursa = new Label("din: " + item.getDinReteta());
                sursa.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (item.isCumparat() ? "#D8D0C8;" : "#2D6A4F;"));
                metaRow.getChildren().add(sursa);
                if (!item.getCategorie().isEmpty()) {
                    Label catTag = new Label(item.getCategorie());
                    catTag.setStyle("-fx-font-size: 10px; -fx-text-fill: white; -fx-background-color: rgba(45,106,79,0.7); -fx-background-radius: 8; -fx-padding: 1 6 1 6;");
                    metaRow.getChildren().add(catTag);
                }
                if (item.getPret() > 0) {
                    Label pretLabel = new Label(String.format("%.2f lei", item.getPret()));
                    pretLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
                    metaRow.getChildren().add(pretLabel);
                }
                info.getChildren().addAll(textLabel, metaRow);

                if (item.isCumparat()) {
                    Label check = new Label("✓"); check.setStyle("-fx-text-fill: #2D6A4F; -fx-font-size: 16px; -fx-font-weight: bold;");
                    row.getChildren().addAll(cb, info, check);
                } else {
                    row.getChildren().addAll(cb, info);
                }
                setGraphic(row); setText(null);
            }
        });
        actualizeazaStatisticeLista();
    }

    private void actualizeazaStatisticeLista() {
        int total = service.getListaCumparaturi().size();
        long cumparate = service.getListaCumparaturi().stream().filter(ElementListaCumparaturi::isCumparat).count();
        if (labelTotal != null) labelTotal.setText(total + " produse");
        if (labelCumparate != null) labelCumparate.setText(cumparate + " bifate");
        if (labelProgres != null) labelProgres.setText(total > 0 ? (int)(cumparate * 100.0 / total) + "%" : "0%");
        if (progressBar != null) progressBar.setProgress(total > 0 ? (double) cumparate / total : 0);
        actualizeazaHeaderStats();
    }

    private void actualizeazaHeaderStats() {
        if (labelHeaderStats == null) return;
        int totalRetete = service.getRetete().size();
        int lista = service.getListaCumparaturi().size();
        labelHeaderStats.setText(totalRetete + " retete  ·  " + lista + " produse in cos");
    }

    @FXML
    private void onAdaugaItemManual() {
        if (campNumeItem == null) return;
        String nume = campNumeItem.getText().trim();
        if (nume.isEmpty()) { shakeField(campNumeItem); return; }

        String cantStr = campCantitateItem != null ? campCantitateItem.getText().trim() : "";
        String unitate = campUnitateItem != null ? campUnitateItem.getText().trim() : "";
        String categ = campCategorie != null ? campCategorie.getText().trim() : "";
        String pretStr = campPret != null ? campPret.getText().trim() : "";

        double cantitate = 1;
        try {
            if (!cantStr.isEmpty()) cantitate = Double.parseDouble(cantStr);
        } catch (NumberFormatException e) {
            if (campCantitateItem != null) shakeField(campCantitateItem);
            return;
        }

        double pret = 0;
        try {
            if (!pretStr.isEmpty()) pret = Double.parseDouble(pretStr.replace(",", "."));
        } catch (NumberFormatException ignored) {}

        ElementListaCumparaturi el = new ElementListaCumparaturi(
                nume, cantitate, unitate.isEmpty() ? "buc" : unitate, "manual");
        el.setCategorie(categ);
        el.setPret(pret);
        service.adaugaItemManual(el);
        actualizeazaStatisticeLista();

        campNumeItem.clear();
        if (campCantitateItem != null) campCantitateItem.clear();
        if (campUnitateItem != null) campUnitateItem.clear();
        if (campCategorie != null) campCategorie.clear();
        if (campPret != null) campPret.clear();
        campNumeItem.requestFocus();
    }

    @FXML
    private void onStergeItem() {
        ElementListaCumparaturi sel = listaItems.getSelectionModel().getSelectedItem();
        if (sel != null) { service.stergeElementLista(sel); actualizeazaStatisticeLista(); }
    }

    @FXML
    private void onStergeCumparate() {
        service.stergeElementeCumparate();
        actualizeazaStatisticeLista();
    }

    @FXML
    private void onGolesteLista() {
        if (service.getListaCumparaturi().isEmpty()) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Golire lista"); confirm.setHeaderText("Golesti lista de cumparaturi?");
        confirm.setContentText("Toate produsele vor fi sterse.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) { service.golesteLista(); actualizeazaStatisticeLista(); }
        });
    }

    // ===================== DATE BAR / PURCHASE HISTORY =====================

    private void initPurchaseHistoryTable() {
        if (tableIstoric == null) return;
        colProdus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getItemName()));
        colCantitate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCantitateAfisare()));
        colCategorie.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCategorie() != null ? cell.getValue().getCategorie() : ""));
        colPret.setCellValueFactory(cell -> {
            double p = cell.getValue().getPretTotal();
            return new SimpleStringProperty(p > 0 ? String.format("%.2f", p) : "-");
        });
        colData.setCellValueFactory(cell -> {
            LocalDateTime dt = cell.getValue().getBoughtAt();
            return new SimpleStringProperty(dt != null ? dt.format(DT_FMT) : "");
        });
        refreshPurchaseHistory();
    }

    private void refreshPurchaseHistory() {
        if (tableIstoric == null) return;
        LocalDate from = dateStart != null && dateStart.getValue() != null
                ? dateStart.getValue() : LocalDate.now().minusMonths(1);
        LocalDate to = dateEnd != null && dateEnd.getValue() != null
                ? dateEnd.getValue() : LocalDate.now();
        List<PurchaseEvent> events = service.getPersistenceService()
                .loadPurchaseHistory(from.atStartOfDay(), to.atTime(LocalTime.MAX));
        tableIstoric.setItems(FXCollections.observableArrayList(events));
        if (labelHistoryCount != null) labelHistoryCount.setText(events.size() + " inregistrari");
    }

    @FXML
    private void onFilterByDate() {
        refreshPurchaseHistory();
    }

    @FXML
    private void onFilterToday() {
        if (dateStart != null) dateStart.setValue(LocalDate.now());
        if (dateEnd != null) dateEnd.setValue(LocalDate.now());
        refreshPurchaseHistory();
    }

    @FXML
    private void onFilterWeek() {
        if (dateStart != null) dateStart.setValue(LocalDate.now().minusDays(6));
        if (dateEnd != null) dateEnd.setValue(LocalDate.now());
        refreshPurchaseHistory();
    }

    @FXML
    private void onFilterMonth() {
        if (dateStart != null) dateStart.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateEnd != null) dateEnd.setValue(LocalDate.now());
        refreshPurchaseHistory();
    }

    // ===================== UTILS =====================

    private void showNotificare(String msg) {
        if (detailContainer == null) return;
        Label toast = new Label(msg);
        toast.getStyleClass().add("toast-label");
        toast.setOpacity(0); toast.setMouseTransparent(true);
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 24, 0));
        detailContainer.getChildren().add(toast);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast); fadeIn.setFromValue(0); fadeIn.setToValue(1);
        PauseTransition pause = new PauseTransition(Duration.millis(2500));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast); fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> detailContainer.getChildren().remove(toast));
        new SequentialTransition(fadeIn, pause, fadeOut).play();
    }

    private void shakeField(TextField field) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), field);
        shake.setFromX(0); shake.setByX(8); shake.setCycleCount(4); shake.setAutoReverse(true);
        shake.setOnFinished(e -> field.setTranslateX(0));
        shake.play();
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField(); tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #F5F1EC; -fx-border-color: transparent; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 9 14 9 14; -fx-font-size: 13px;");
        return tf;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text); l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #9A8F85;"); return l;
    }

    private String getEmojiForIngredient(String name) {
        if (name == null) return "🌿";
        String n = name.toLowerCase();
        if (n.contains("rosie") || n.contains("tomat")) return "🍅";
        if (n.contains("morcov")) return "🥕";
        if (n.contains("ceapa")) return "🧅";
        if (n.contains("usturoi")) return "🧄";
        if (n.contains("lamaie")) return "🍋";
        if (n.contains("ou")) return "🥚";
        if (n.contains("pui") || n.contains("carne")) return "🍗";
        if (n.contains("paste") || n.contains("spaghetti")) return "🍝";
        if (n.contains("branza") || n.contains("parmezan")) return "🧀";
        if (n.contains("lapte") || n.contains("smantana")) return "🥛";
        if (n.contains("ulei")) return "🫙";
        if (n.contains("sare") || n.contains("piper")) return "🧂";
        if (n.contains("faina")) return "🌾";
        if (n.contains("mar")) return "🍎";
        return "🌿";
    }

    private String getCategoryEmoji(String categorie) {
        if (categorie == null) return "🍽";
        String c = categorie.toLowerCase();
        if (c.contains("paste") || c.contains("spaghett")) return "🍝";
        if (c.contains("supa") || c.contains("ciorba")) return "🍲";
        if (c.contains("salata")) return "\uD83E\uDD57"; // 🥗
        if (c.contains("desert") || c.contains("prajitura")) return "🍰";
        if (c.contains("carne") || c.contains("grill")) return "🥩";
        if (c.contains("peste") || c.contains("fructe de mare")) return "🐟";
        if (c.contains("pizza")) return "🍕";
        if (c.contains("sandvi") || c.contains("burger")) return "🥪";
        if (c.contains("mic dejun") || c.contains("omleta")) return "🍳";
        return "🍽";
    }
}
