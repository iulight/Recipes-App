# Icon Attribution

## Ingredient Icons

The ingredient icon images located in
`src/main/resources/com/retete/icons/ingredients/` are **placeholder icons**
generated programmatically as part of this design implementation.

To replace them with properly licensed icons, follow these steps:

### Replacing with Flaticon Icons

1. Go to [https://www.flaticon.com/](https://www.flaticon.com/) and search for
   the ingredient you need (e.g. "tomato", "carrot", "onion").
2. Download the icon in **PNG format at 64×64 px** (or scale to 64×64).
3. Save the file under:
   ```
   src/main/resources/com/retete/icons/ingredients/<ingredient>.png
   ```
   The filename must match the mapping defined in
   `src/main/java/com/retete/service/IngredientIconService.java`.
4. Add attribution for each icon as required by the Flaticon license.
   Free Flaticon icons require attribution in your UI or documentation.
   Example:
   > Icons by [Freepik](https://www.flaticon.com/authors/freepik) from
   > [Flaticon](https://www.flaticon.com/).

### Current Icon Files

| File | Ingredient keyword | Placeholder |
|------|--------------------|-------------|
| `default.png` | *(fallback for all unknown ingredients)* | ✓ |
| `rosie.png` / `tomat.png` | rosie, tomat | ✓ |
| `morcov.png` | morcov | ✓ |
| `ceapa.png` | ceapa | ✓ |
| `usturoi.png` | usturoi | ✓ |
| `castravete.png` | castravete | ✓ |
| `ardei.png` | ardei | ✓ |
| `telina.png` | telina | ✓ |
| `salata.png` | salata | ✓ |
| `lamaie.png` | lamaie | ✓ |
| `mar.png` | mar | ✓ |
| `portocala.png` | portocala | ✓ |
| `oua.png` | ou (oua) | ✓ |
| `pui.png` | pui | ✓ |
| `carne.png` | carne | ✓ |
| `pancetta.png` | pancetta | ✓ |
| `lapte.png` | lapte | ✓ |
| `smantana.png` | smantana | ✓ |
| `parmezan.png` | parmezan | ✓ |
| `cascaval.png` | cascaval | ✓ |
| `branza.png` | branza, feta | ✓ |
| `unt.png` | unt | ✓ |
| `faina.png` | faina | ✓ |
| `zahar.png` | zahar | ✓ |
| `cacao.png` | cacao | ✓ |
| `ciocolata.png` | ciocolata | ✓ |
| `sare.png` | sare | ✓ |
| `piper.png` | piper | ✓ |
| `ulei.png` | ulei | ✓ |
| `spaghetti.png` | spaghetti | ✓ |
| `paste.png` | paste | ✓ |
| `tarhon.png` | tarhon | ✓ |
| `herbs.png` | oregano, busuioc | ✓ |
| `masline.png` | masline | ✓ |

### Adding New Ingredient Icons

To add support for a new ingredient:

1. Add a PNG file to `src/main/resources/com/retete/icons/ingredients/`.
2. Add the keyword → filename mapping in
   `IngredientIconService.KEYWORD_MAP` (keywords are matched
   case-insensitively as substrings of the ingredient name).

### Default / Fallback

If no mapping is found for an ingredient, `default.png` is used automatically.
