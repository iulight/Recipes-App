package com.retete.service;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps ingredient names to local icon files stored under
 * src/main/resources/com/retete/icons/ingredients/.
 *
 * Icons are sourced from Flaticon (see ATTRIBUTION.md at the project root).
 * A default icon is returned when no specific mapping exists.
 */
public class IngredientIconService {

    private static final String ICONS_BASE = "/com/retete/icons/ingredients/";
    private static final String DEFAULT_ICON = ICONS_BASE + "default.png";

    /** keyword → icon filename (evaluated in insertion order) */
    private static final Map<String, String> KEYWORD_MAP = new LinkedHashMap<>();

    static {
        // Vegetables
        KEYWORD_MAP.put("rosie",       "rosie.png");
        KEYWORD_MAP.put("tomat",       "tomat.png");
        KEYWORD_MAP.put("morcov",      "morcov.png");
        KEYWORD_MAP.put("ceapa",       "ceapa.png");
        KEYWORD_MAP.put("usturoi",     "usturoi.png");
        KEYWORD_MAP.put("castravete",  "castravete.png");
        KEYWORD_MAP.put("ardei",       "ardei.png");
        KEYWORD_MAP.put("telina",      "telina.png");
        KEYWORD_MAP.put("salata",      "salata.png");
        // Fruits
        KEYWORD_MAP.put("lamaie",      "lamaie.png");
        KEYWORD_MAP.put("mar",         "mar.png");
        KEYWORD_MAP.put("portocala",   "portocala.png");
        // Protein
        KEYWORD_MAP.put("ou",          "oua.png");
        KEYWORD_MAP.put("pui",         "pui.png");
        KEYWORD_MAP.put("carne",       "carne.png");
        KEYWORD_MAP.put("pancetta",    "pancetta.png");
        // Dairy
        KEYWORD_MAP.put("lapte",       "lapte.png");
        KEYWORD_MAP.put("smantana",    "smantana.png");
        KEYWORD_MAP.put("parmezan",    "parmezan.png");
        KEYWORD_MAP.put("cascaval",    "cascaval.png");
        KEYWORD_MAP.put("branza",      "branza.png");
        KEYWORD_MAP.put("feta",        "branza.png");
        KEYWORD_MAP.put("unt",         "unt.png");
        // Pantry
        KEYWORD_MAP.put("faina",       "faina.png");
        KEYWORD_MAP.put("zahar",       "zahar.png");
        KEYWORD_MAP.put("cacao",       "cacao.png");
        KEYWORD_MAP.put("ciocolata",   "ciocolata.png");
        KEYWORD_MAP.put("sare",        "sare.png");
        KEYWORD_MAP.put("piper",       "piper.png");
        KEYWORD_MAP.put("ulei",        "ulei.png");
        // Pasta / grains
        KEYWORD_MAP.put("spaghetti",   "spaghetti.png");
        KEYWORD_MAP.put("paste",       "paste.png");
        // Herbs / spices
        KEYWORD_MAP.put("tarhon",      "tarhon.png");
        KEYWORD_MAP.put("oregano",     "herbs.png");
        KEYWORD_MAP.put("busuioc",     "herbs.png");
        // Other
        KEYWORD_MAP.put("masline",     "masline.png");
    }

    private static final IngredientIconService INSTANCE = new IngredientIconService();

    public static IngredientIconService getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the icon {@link Image} for the given ingredient name.
     * Falls back to the default icon when no specific mapping exists.
     */
    public Image getIcon(String ingredientName) {
        String path = resolveIconPath(ingredientName);
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            is = getClass().getResourceAsStream(DEFAULT_ICON);
        }
        if (is == null) {
            return null;
        }
        try {
            return new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveIconPath(String ingredientName) {
        if (ingredientName == null || ingredientName.isBlank()) {
            return DEFAULT_ICON;
        }
        String lower = ingredientName.toLowerCase();
        for (Map.Entry<String, String> entry : KEYWORD_MAP.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return ICONS_BASE + entry.getValue();
            }
        }
        return DEFAULT_ICON;
    }
}
