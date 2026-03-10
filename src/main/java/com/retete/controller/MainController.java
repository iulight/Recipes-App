package com.retete.controller;

import com.retete.model.ElementListaCumparaturi;
import com.retete.model.Ingredient;
import com.retete.model.Reteta;
import com.retete.service.RetetaService;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TextField campCautare;
    @FXML private ComboBox<String> comboCategorie;
    @FXML private ListView<Reteta> listaRetete;
    @FXML private Label labelCount;
    @FXML private Label labelHeaderStats;

    @FXML private VBox emptyState;
    @FXML private VBox retetaContent;
    @FXML private VBox ingredienteCard;
    @FXML private VBox instructiuniCard;
    @FXML private Button btnAdaugaInLista;

    @FXML private Label labelNume;
    @FXML private Label labelCategorie;
    @FXML private Label labelTimp;
    @FXML private Label labelPortii;
    @FXML private Label labelDescriere;
    @FXML private FlowPane panelIngrediente;
    @FXML private TextArea areaInstructiuni;

    @FXML private ListView<ElementListaCumparaturi> listaItems;
    @FXML private Label labelTotal;
    @FXML private Label labelCumparate;
    @FXML private Label labelProgres;
    @FXML private Region progressFill;
    @FXML private HBox progressBox;

    @FXML private TextField campNumeItem;
    @FXML private TextField campCantitateItem;
    @FXML private TextField campUnitateItem;

    private RetetaService service;
    private Reteta retetaSelectata;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = new RetetaService();
        initRetete();
        initListaCumparaturi();
        actualizeazaHeaderStats();
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
                    setStyle("-fx-background-color: white;");
                    return;
                }

                boolean selected = getListView().getSelectionModel().getSelectedItem() == item;

                HBox root = new HBox(0);
                root.setAlignment(Pos.CENTER_LEFT);

                // Indicator lateral rosu
                Region indicator = new Region();
                indicator.setPrefWidth(4);
                indicator.setMinWidth(4);
                indicator.setStyle(selected
                        ? "-fx-background-color: #e74c3c;"
                        : "-fx-background-color: transparent;");

                VBox content = new VBox(3);
                content.setPadding(new Insets(12, 14, 12, 14));
                HBox.setHgrow(content, Priority.ALWAYS);

                Label numeLabel = new Label(item.getNume());
                numeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + (selected ? "#c0392b;" : "#1a1a1a;"));

                HBox meta = new HBox(8);
                meta.setAlignment(Pos.CENTER_LEFT);
                Label catLabel = new Label("📂 " + item.getCategorie());
                catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
                Label timpLabel = new Label("⏱ " + item.getTimpPreparare());
                timpLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
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

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(120), retetaContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            incarcaDateReteta(r);
            // Fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), retetaContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            // Slide in cards
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
            btnAdaugaInLista.setVisible(true);
            btnAdaugaInLista.setManaged(true);
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
        labelPortii.setText("🍽 " + r.getPortii() + " porții");
        labelDescriere.setText(r.getDescriere());
        areaInstructiuni.setText(r.getInstructiuni());

        panelIngrediente.getChildren().clear();
        for (Ingredient ing : r.getIngrediente()) {
            HBox chip = new HBox(6);
            chip.setAlignment(Pos.CENTER_LEFT);
            chip.getStyleClass().add("ingredient-chip");

            Label dot = new Label("●");
            dot.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 8px;");
            Label text = new Label(ing.getAfisare());
            text.getStyleClass().add("ingredient-text");

            chip.getChildren().addAll(dot, text);
            panelIngrediente.getChildren().add(chip);
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
        labelCount.setText(n + (n == 1 ? " rețetă" : " rețete"));
    }

    private void actualizeazaHeaderStats() {
        int total = service.getRetete().size();
        int lista = service.getListaCumparaturi().size();
        labelHeaderStats.setText(total + " rețete  ·  " + lista + " produse în coș");
    }

    @FXML
    private void onAdaugaInLista() {
        if (retetaSelectata == null) return;
        service.adaugaIngredienteInLista(retetaSelectata);
        actualizeazaStatisticeLista();
        actualizeazaHeaderStats();

        // Animatie buton
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), btnAdaugaInLista);
        scale.setFromX(1); scale.setFromY(1);
        scale.setToX(0.95); scale.setToY(0.95);
        scale.setAutoReverse(true); scale.setCycleCount(2);
        scale.play();

        showNotificare("✅  Ingredientele din \"" + retetaSelectata.getNume() + "\" au fost adăugate!");
    }

    @FXML
    private void onAdaugaReteta() {
        Dialog<Reteta> dialog = new Dialog<>();
        dialog.setTitle("Rețetă Nouă");
        dialog.setHeaderText(null);

        ButtonType btnSalveaza = new ButtonType("✅  Salvează", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalveaza, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-font-family: 'Segoe UI';");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white;");

        TextField tfNume = styledField("ex: Ciorba de burtă");
        TextField tfCategorie = styledField("ex: Supe & Ciorbe");
        TextField tfTimp = styledField("ex: 45 min");
        TextField tfPortii = styledField("4");
        TextArea taDescriere = new TextArea();
        taDescriere.setPromptText("Descriere scurtă...");
        taDescriere.setPrefHeight(70);
        taDescriere.setStyle("-fx-background-color: #f8f4f0; -fx-border-color: transparent; -fx-background-radius: 10; -fx-font-size: 13px;");
        TextArea taInstructiuni = new TextArea();
        taInstructiuni.setPromptText("Pașii de preparare...");
        taInstructiuni.setPrefHeight(110);
        taInstructiuni.setStyle("-fx-background-color: #f8f4f0; -fx-border-color: transparent; -fx-background-radius: 10; -fx-font-size: 13px;");

        grid.add(boldLabel("Nume rețetă *"), 0, 0); grid.add(tfNume, 1, 0);
        grid.add(boldLabel("Categorie"), 0, 1); grid.add(tfCategorie, 1, 1);
        grid.add(boldLabel("Timp preparare"), 0, 2); grid.add(tfTimp, 1, 2);
        grid.add(boldLabel("Porții"), 0, 3); grid.add(tfPortii, 1, 3);
        grid.add(boldLabel("Descriere"), 0, 4); grid.add(taDescriere, 1, 4);
        grid.add(boldLabel("Instrucțiuni"), 0, 5); grid.add(taInstructiuni, 1, 5);

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
            if (!r.getNume().isEmpty()) {
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
        confirm.setHeaderText("Ștergi rețeta?");
        confirm.setContentText("\"" + retetaSelectata.getNume() + "\" va fi ștearsă definitiv.");
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
                btnAdaugaInLista.setVisible(false); btnAdaugaInLista.setManaged(false);
                emptyState.setVisible(true); emptyState.setManaged(true);
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
                        ? "-fx-strikethrough: true; -fx-text-fill: #ccc; -fx-font-size: 13px;"
                        : "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;";

                Label textLabel = new Label(item.getAfisare());
                textLabel.setStyle(textStyle);

                Label sursa = new Label("din: " + item.getDinReteta());
                sursa.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (item.isCumparat() ? "#ddd;" : "#e74c3c;"));

                info.getChildren().addAll(textLabel, sursa);

                // Checkmark icon dacă e cumparat
                if (item.isCumparat()) {
                    Label check = new Label("✓");
                    check.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");
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

        labelTotal.setText(total + " produse");
        labelCumparate.setText(cumparate + " bifate");
        labelProgres.setText(total > 0 ? (int)(cumparate * 100.0 / total) + "%" : "0%");

        // Animatie progress bar
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
        String nume = campNumeItem.getText().trim();
        String cantStr = campCantitateItem.getText().trim();
        String unitate = campUnitateItem.getText().trim();

        if (nume.isEmpty()) {
            shakeField(campNumeItem);
            return;
        }

        double cantitate = 1;
        try {
            if (!cantStr.isEmpty()) cantitate = Double.parseDouble(cantStr);
        } catch (NumberFormatException e) {
            shakeField(campCantitateItem);
            return;
        }

        ElementListaCumparaturi el = new ElementListaCumparaturi(
                nume, cantitate, unitate.isEmpty() ? "buc" : unitate, "manual");
        service.getListaCumparaturi().add(el);
        actualizeazaStatisticeLista();

        campNumeItem.clear();
        campCantitateItem.clear();
        campUnitateItem.clear();
        campNumeItem.requestFocus();
    }

    @FXML
    private void onStergeItem() {
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
        confirm.setTitle("Golire listă");
        confirm.setHeaderText("Golești lista de cumpărături?");
        confirm.setContentText("Toate produsele vor fi șterse.");
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
        field.setStyle(field.getStyle() + " -fx-border-color: #e74c3c; -fx-border-width: 1.5;");
        shake.play();
    }

    private void showNotificare(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(""); alert.setHeaderText(null);
        alert.getDialogPane().setStyle("-fx-background-color: white; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
        alert.showAndWait();
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #f8f4f0; -fx-border-color: transparent; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 9 14 9 14; -fx-font-size: 13px;");
        return tf;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #888;");
        return l;
    }
}
