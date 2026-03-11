package com.retete.controller;

import com.retete.model.ElementListaCumparaturi;
import com.retete.model.Ingredient;
import com.retete.model.Reteta;
import com.retete.service.RetetaService;
import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // ---- Title Bar ----
    @FXML private HBox titleBar;
    @FXML private Button btnClose;
    @FXML private Button btnMinimize;

    // ---- Sidebar ----
    @FXML private TextField campCautare;
    @FXML private ComboBox<String> comboCategorie;
    @FXML private ListView<Reteta> listaRetete;
    @FXML private Label labelCount;

    // ---- Content ----
    @FXML private StackPane detailContainer;
    @FXML private VBox emptyState;
    @FXML private VBox retetaContent;
    @FXML private VBox ingredienteCard;
    @FXML private VBox instructiuniCard;

    // ---- Recipe detail ----
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

    // ---- Shopping list (not in new FXML – null-safe) ----
    @FXML private ListView<ElementListaCumparaturi> listaItems;
    @FXML private Label labelTotal;
    @FXML private Label labelCumparate;
    @FXML private Label labelProgres;
    @FXML private Label labelHeaderStats;
    @FXML private Region progressFill;
    @FXML private HBox progressBox;
    @FXML private TextField campNumeItem;
    @FXML private TextField campCantitateItem;
    @FXML private TextField campUnitateItem;

    private double xOffset = 0;
    private double yOffset = 0;

    private RetetaService service;
    private Reteta retetaSelectata;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = new RetetaService();
        setupTitleBar();
        initRetete();
        if (listaItems != null) {
            initListaCumparaturi();
        }
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
        if (btnClose != null) {
            btnClose.setOnAction(e -> Platform.exit());
        }
        if (btnMinimize != null) {
            btnMinimize.setOnAction(e -> ((Stage) btnMinimize.getScene().getWindow()).setIconified(true));
        }
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
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                    setStyle("");
                    return;
                }

                boolean selected = getListView().getSelectionModel().getSelectedItem() == item;

                HBox root = new HBox(0);
                root.setAlignment(Pos.CENTER_LEFT);

                Region indicator = new Region();
                indicator.setPrefWidth(4);
                indicator.setMinWidth(4);
                indicator.setStyle(selected
                        ? "-fx-background-color: white;"
                        : "-fx-background-color: transparent;");

                VBox content = new VBox(3);
                content.setPadding(new Insets(10, 14, 10, 10));
                HBox.setHgrow(content, Priority.ALWAYS);

                Label numeLabel = new Label(item.getNume());
                numeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: "
                        + (selected ? "white;" : "rgba(255,255,255,0.9);"));

                HBox meta = new HBox(8);
                meta.setAlignment(Pos.CENTER_LEFT);
                String emoji = getCategoryEmoji(item.getCategorie());
                Label catLabel = new Label(emoji + " " + item.getCategorie());
                catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.6);");
                Label timpLabel = new Label("⏱ " + item.getTimpPreparare());
                timpLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.6);");
                meta.getChildren().addAll(catLabel, timpLabel);

                content.getChildren().addAll(numeLabel, meta);
                root.getChildren().addAll(indicator, content);
                setGraphic(root);
                setText(null);
            }
        });

        listaRetete.getSelectionModel().selectedItemProperty().addListener((obs, old, nou) -> {
            if (nou != null) {
                listaRetete.refresh();
                afiseazaRetetaAnimat(nou);
            }
        });

        actualizeazaCount();

        if (!service.getRetete().isEmpty()) {
            listaRetete.getSelectionModel().selectFirst();
        }

        campCautare.textProperty().addListener((obs, old, nou) -> filtreaza());
        comboCategorie.valueProperty().addListener((obs, old, nou) -> filtreaza());
    }

    private void afiseazaRetetaAnimat(Reteta r) {
        retetaSelectata = r;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(120), retetaContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            incarcaDateReteta(r);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), retetaContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            animaCard(ingredienteCard, 80);
            animaCard(instructiuniCard, 160);
        });

        if (retetaContent.isVisible()) {
            fadeOut.play();
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            retetaContent.setVisible(true);
            retetaContent.setManaged(true);
            ingredienteCard.setVisible(true);
            ingredienteCard.setManaged(true);
            instructiuniCard.setVisible(true);
            instructiuniCard.setManaged(true);
            if (btnAdaugaInLista != null) {
                btnAdaugaInLista.setVisible(true);
                btnAdaugaInLista.setManaged(true);
            }
            incarcaDateReteta(r);
            animaCard(retetaContent, 0);
            animaCard(ingredienteCard, 80);
            animaCard(instructiuniCard, 160);
        }
    }

    private void animaCard(VBox card, int delayMs) {
        card.setTranslateY(20);
        card.setOpacity(0);

        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(300), card);
            fade.setFromValue(0); fade.setToValue(1);

            TranslateTransition slide = new TranslateTransition(Duration.millis(300), card);
            slide.setFromY(20); slide.setToY(0);
            slide.setInterpolator(Interpolator.EASE_OUT);

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
                @Override
                protected Image call() {
                    return new Image(reteta.getImagine(), true);
                }
            };
            task.setOnSucceeded(e -> {
                Image img = task.getValue();
                if (heroImage != null && !img.isError()) {
                    heroImage.setImage(img);
                    heroImage.fitWidthProperty().bind(imageContainer.widthProperty());
                    FadeTransition ft = new FadeTransition(Duration.millis(400), heroImage);
                    ft.setFromValue(0); ft.setToValue(1);
                    ft.play();
                }
                if (imagePlaceholder != null) imagePlaceholder.setVisible(img.isError());
            });
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        }
    }

    private void construiesteCartiIngrediente(List<Ingredient> ingrediente) {
        panelIngrediente.getChildren().clear();
        for (Ingredient ing : ingrediente) {
            VBox card = new VBox(6);
            card.setPrefWidth(110);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(12));
            card.getStyleClass().add("ingredient-card");

            StackPane imgPlaceholder = new StackPane();
            imgPlaceholder.setPrefSize(70, 70);
            imgPlaceholder.setStyle("-fx-background-color: #F0EDE8; -fx-background-radius: 50%;");
            Label emoji = new Label(getEmojiForIngredient(ing.getNume()));
            emoji.setStyle("-fx-font-size: 28px;");
            imgPlaceholder.getChildren().add(emoji);

            Label lblNume = new Label(ing.getNume());
            lblNume.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;"
                    + " -fx-text-alignment: center; -fx-wrap-text: true; -fx-max-width: 95px;");
            lblNume.setWrapText(true);
            lblNume.setTextAlignment(TextAlignment.CENTER);

            String cantText = (ing.getCantitate() == (long) ing.getCantitate())
                    ? ((long) ing.getCantitate()) + " " + ing.getUnitate()
                    : ing.getCantitate() + " " + ing.getUnitate();
            Label lblCant = new Label(cantText);
            lblCant.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");

            card.getChildren().addAll(imgPlaceholder, lblNume, lblCant);
            panelIngrediente.getChildren().add(card);
        }
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
        if (c.contains("salata")) return "🥗";
        if (c.contains("desert") || c.contains("prajitura")) return "🍰";
        if (c.contains("carne") || c.contains("grill")) return "🥩";
        if (c.contains("peste") || c.contains("fructe de mare")) return "🐟";
        if (c.contains("pizza")) return "🍕";
        if (c.contains("sandvi") || c.contains("burger")) return "🥪";
        return "🍽";
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

    private void actualizeazaHeaderStats() {
        if (labelHeaderStats == null) return;
        int total = service.getRetete().size();
        int lista = service.getListaCumparaturi().size();
        labelHeaderStats.setText(total + " retete  ·  " + lista + " produse in cos");
    }

    // ===================== FXML HANDLERS =====================

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
            scale.setFromX(1); scale.setFromY(1);
            scale.setToX(0.95); scale.setToY(0.95);
            scale.setAutoReverse(true); scale.setCycleCount(2);
            scale.play();
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
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white;");

        TextField tfNume = styledField("ex: Ciorba de burta");
        TextField tfCategorie = styledField("ex: Supe & Ciorbe");
        TextField tfTimp = styledField("ex: 45 min");
        TextField tfPortii = styledField("4");
        TextArea taDescriere = new TextArea();
        taDescriere.setPromptText("Descriere scurta...");
        taDescriere.setPrefHeight(70);
        taDescriere.setStyle("-fx-background-color: #F5F1EC; -fx-border-color: transparent; -fx-background-radius: 12; -fx-font-size: 13px;");
        TextArea taInstructiuni = new TextArea();
        taInstructiuni.setPromptText("Pasii de preparare...");
        taInstructiuni.setPrefHeight(110);
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
                    return new Reteta(null, tfNume.getText().trim(),
                            tfCategorie.getText().trim(), tfTimp.getText().trim(),
                            taDescriere.getText().trim(), taInstructiuni.getText().trim(), portii);
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
                if (btnAdaugaInLista != null) {
                    btnAdaugaInLista.setVisible(false); btnAdaugaInLista.setManaged(false);
                }
                emptyState.setVisible(true); emptyState.setManaged(true);
            }
        });
    }

    // ===================== LISTA CUMPARATURI =====================

    private void initListaCumparaturi() {
        if (listaItems == null) return;
        listaItems.setItems(service.getListaCumparaturi());
        listaItems.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ElementListaCumparaturi item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }

                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10, 16, 10, 16));

                CheckBox cb = new CheckBox();
                cb.setSelected(item.isCumparat());
                cb.setStyle("-fx-cursor: hand;");
                cb.selectedProperty().addListener((obs, old, nou) -> {
                    item.setCumparat(nou);
                    actualizeazaStatisticeLista();
                    listaItems.refresh();
                });

                VBox info = new VBox(2);
                HBox.setHgrow(info, Priority.ALWAYS);

                String textStyle = item.isCumparat()
                        ? "-fx-strikethrough: true; -fx-text-fill: #C0B8B0; -fx-font-size: 13px;"
                        : "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;";

                Label textLabel = new Label(item.getAfisare());
                textLabel.setStyle(textStyle);

                Label sursa = new Label("din: " + item.getDinReteta());
                sursa.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (item.isCumparat() ? "#D8D0C8;" : "#2D6A4F;"));

                info.getChildren().addAll(textLabel, sursa);

                if (item.isCumparat()) {
                    Label check = new Label("✓");
                    check.setStyle("-fx-text-fill: #2D6A4F; -fx-font-size: 16px; -fx-font-weight: bold;");
                    row.getChildren().addAll(cb, info, check);
                } else {
                    row.getChildren().addAll(cb, info);
                }

                setGraphic(row);
                setText(null);
            }
        });
        actualizeazaStatisticeLista();
    }

    private void actualizeazaStatisticeLista() {
        int total = service.getListaCumparaturi().size();
        long cumparate = service.getListaCumparaturi().stream()
                .filter(ElementListaCumparaturi::isCumparat).count();

        if (labelTotal != null) labelTotal.setText(total + " produse");
        if (labelCumparate != null) labelCumparate.setText(cumparate + " bifate");
        if (labelProgres != null)
            labelProgres.setText(total > 0 ? (int)(cumparate * 100.0 / total) + "%" : "0%");

        if (progressFill != null && progressBox != null) {
            double ratio = total > 0 ? (double) cumparate / total : 0;
            double targetWidth = progressBox.getWidth() * ratio;
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(400),
                            new KeyValue(progressFill.prefWidthProperty(), targetWidth, Interpolator.EASE_OUT))
            );
            timeline.play();
        }

        actualizeazaHeaderStats();
    }

    @FXML
    private void onAdaugaItemManual() {
        if (campNumeItem == null) return;
        String nume = campNumeItem.getText().trim();
        String cantStr = campCantitateItem != null ? campCantitateItem.getText().trim() : "";
        String unitate = campUnitateItem != null ? campUnitateItem.getText().trim() : "";

        if (nume.isEmpty()) {
            if (campNumeItem != null) shakeField(campNumeItem);
            return;
        }

        double cantitate = 1;
        try {
            if (!cantStr.isEmpty()) cantitate = Double.parseDouble(cantStr);
        } catch (NumberFormatException e) {
            if (campCantitateItem != null) shakeField(campCantitateItem);
            return;
        }

        ElementListaCumparaturi el = new ElementListaCumparaturi(
                nume, cantitate, unitate.isEmpty() ? "buc" : unitate, "manual");
        service.getListaCumparaturi().add(el);
        actualizeazaStatisticeLista();

        campNumeItem.clear();
        if (campCantitateItem != null) campCantitateItem.clear();
        if (campUnitateItem != null) campUnitateItem.clear();
        campNumeItem.requestFocus();
    }

    @FXML
    private void onStergeItem() {
        if (listaItems == null) return;
        ElementListaCumparaturi sel = listaItems.getSelectionModel().getSelectedItem();
        if (sel != null) {
            service.stergeElementLista(sel);
            actualizeazaStatisticeLista();
        }
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
        confirm.setTitle("Golire lista");
        confirm.setHeaderText("Golesti lista de cumparaturi?");
        confirm.setContentText("Toate produsele vor fi sterse.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.golesteLista();
                actualizeazaStatisticeLista();
            }
        });
    }

    // ===================== UTILS =====================

    private void shakeField(TextField field) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), field);
        shake.setFromX(0); shake.setByX(8); shake.setCycleCount(4);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> field.setTranslateX(0));
        shake.play();
    }

    private void showNotificare(String msg) {
        if (detailContainer == null) return;
        Label toast = new Label(msg);
        toast.getStyleClass().add("toast-label");
        toast.setOpacity(0);
        toast.setMouseTransparent(true);

        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 24, 0));
        detailContainer.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.millis(2500));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> detailContainer.getChildren().remove(toast));

        new SequentialTransition(fadeIn, pause, fadeOut).play();
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #F5F1EC; -fx-border-color: transparent; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 9 14 9 14; -fx-font-size: 13px;");
        return tf;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #9A8F85;");
        return l;
    }
}
