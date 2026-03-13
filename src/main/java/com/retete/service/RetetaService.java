package com.retete.service;

import com.retete.model.ElementListaCumparaturi;
import com.retete.model.Ingredient;
import com.retete.model.PurchaseEvent;
import com.retete.model.Reteta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RetetaService {

    private ObservableList<Reteta> retete;
    private ObservableList<ElementListaCumparaturi> listaCumparaturi;
    private final PersistenceService persistenceService;

    public RetetaService() {
        retete = FXCollections.observableArrayList();
        persistenceService = new PersistenceService();
        List<ElementListaCumparaturi> persisted = persistenceService.loadShoppingItems();
        listaCumparaturi = FXCollections.observableArrayList(persisted);
        incarcaDateInitiale();
    }

    private void incarcaDateInitiale() {
        Reteta carbonara = new Reteta(uuid(), "Paste Carbonara", "Paste",
                "30 min", "Clasica reteta italiana cu oua, pancetta si parmezan.",
                "1. Fierbe pastele in apa sarata.\n2. Prajeste pancetta pana devine crocanta.\n3. Bate ouale cu parmezanul ras.\n4. Amesteca pastele scurse cu pancetta, apoi adauga amestecul de oua departe de foc.\n5. Adauga piper negru si serveste imediat.",
                4);
        carbonara.adaugaIngredient(new Ingredient("Spaghetti", 400, "g"));
        carbonara.adaugaIngredient(new Ingredient("Pancetta", 150, "g"));
        carbonara.adaugaIngredient(new Ingredient("Oua", 4, "buc"));
        carbonara.adaugaIngredient(new Ingredient("Parmezan ras", 100, "g"));
        carbonara.adaugaIngredient(new Ingredient("Piper negru", 1, "lingurita"));
        carbonara.adaugaIngredient(new Ingredient("Sare", 1, "lingurita"));
        carbonara.setImagine("https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600&q=80");

        Reteta ciorba = new Reteta(uuid(), "Ciorba de Pui", "Supe & Ciorbe",
                "60 min", "Ciorba traditionala romaneasca cu legume si tarhon.",
                "1. Fierbe puiul in 2L de apa cu sare.\n2. Adauga morcovul, telina si ceapa taiata.\n3. Dupa 40 min, scoate puiul si taie-l bucatele.\n4. Adauga zeama de lamaie si tarhon.\n5. Potriveste de sare si serveste cu smantana.",
                6);
        ciorba.adaugaIngredient(new Ingredient("Pui intreg", 1, "buc"));
        ciorba.adaugaIngredient(new Ingredient("Morcovi", 3, "buc"));
        ciorba.adaugaIngredient(new Ingredient("Telina radacina", 0.5, "buc"));
        ciorba.adaugaIngredient(new Ingredient("Ceapa", 2, "buc"));
        ciorba.adaugaIngredient(new Ingredient("Tarhon uscat", 2, "linguri"));
        ciorba.adaugaIngredient(new Ingredient("Lamaie", 1, "buc"));
        ciorba.adaugaIngredient(new Ingredient("Smantana", 200, "ml"));
        ciorba.setImagine("https://images.unsplash.com/photo-1547592180-85f173990554?w=600&q=80");

        Reteta tort = new Reteta(uuid(), "Tort de Ciocolata", "Deserturi",
                "90 min", "Tort umed si bogat cu glazura de ciocolata.",
                "1. Incalzeste cuptorul la 180\u00b0C.\n2. Amesteca faina, cacao, zaharul, soda.\n3. Adauga ouale, laptele si uleiul.\n4. Coace 35 min in tava unsa.\n5. Prepara glazura din ciocolata si smantana.\n6. Acopera tortul racit cu glazura.",
                8);
        tort.adaugaIngredient(new Ingredient("Faina", 250, "g"));
        tort.adaugaIngredient(new Ingredient("Cacao", 80, "g"));
        tort.adaugaIngredient(new Ingredient("Zahar", 300, "g"));
        tort.adaugaIngredient(new Ingredient("Oua", 3, "buc"));
        tort.adaugaIngredient(new Ingredient("Lapte", 240, "ml"));
        tort.adaugaIngredient(new Ingredient("Ulei", 120, "ml"));
        tort.adaugaIngredient(new Ingredient("Ciocolata neagra", 200, "g"));
        tort.adaugaIngredient(new Ingredient("Smantana lichida", 150, "ml"));
        tort.setImagine("https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600&q=80");

        Reteta salata = new Reteta(uuid(), "Salata Greceasca", "Salate",
                "15 min", "Salata proaspata cu rosii, castraveti, masline si feta.",
                "1. Taie rosiile si castravetii cubulete.\n2. Taie ceapa rosie subtire.\n3. Adauga maslinele si branza feta.\n4. Condimenteaza cu oregano, sare si piper.\n5. Toarna ulei de masline si amesteca usor.",
                4);
        salata.adaugaIngredient(new Ingredient("Rosii", 4, "buc"));
        salata.adaugaIngredient(new Ingredient("Castravete", 2, "buc"));
        salata.adaugaIngredient(new Ingredient("Branza Feta", 200, "g"));
        salata.adaugaIngredient(new Ingredient("Masline negre", 100, "g"));
        salata.adaugaIngredient(new Ingredient("Ceapa rosie", 1, "buc"));
        salata.adaugaIngredient(new Ingredient("Ulei de masline", 3, "linguri"));
        salata.adaugaIngredient(new Ingredient("Oregano", 1, "lingurita"));
        salata.setImagine("https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=600&q=80");

        Reteta omleta = new Reteta(uuid(), "Omleta cu Legume", "Mic Dejun",
                "15 min", "Omleta pufoasa cu ardei, rosii si branza.",
                "1. Bate ouale cu sare si piper.\n2. Incinge untul in tigaie.\n3. Adauga legumele taiate si caleste 2 min.\n4. Toarna ouale si gateste la foc mic.\n5. Adauga branza rasa si pliaza omleta.",
                2);
        omleta.adaugaIngredient(new Ingredient("Oua", 4, "buc"));
        omleta.adaugaIngredient(new Ingredient("Ardei rosu", 1, "buc"));
        omleta.adaugaIngredient(new Ingredient("Rosii cherry", 8, "buc"));
        omleta.adaugaIngredient(new Ingredient("Cascaval ras", 50, "g"));
        omleta.adaugaIngredient(new Ingredient("Unt", 20, "g"));
        omleta.setImagine("https://images.unsplash.com/photo-1510693206972-df098062cb71?w=600&q=80");

        retete.addAll(carbonara, ciorba, tort, salata, omleta);
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }

    public ObservableList<Reteta> getRetete() { return retete; }

    public ObservableList<ElementListaCumparaturi> getListaCumparaturi() { return listaCumparaturi; }

    public PersistenceService getPersistenceService() { return persistenceService; }

    public List<String> getCategorii() {
        List<String> categorii = retete.stream()
                .map(Reteta::getCategorie)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        result.add("Toate categoriile");
        result.addAll(categorii);
        return result;
    }

    public void adaugaReteta(Reteta reteta) {
        reteta.setId(uuid());
        retete.add(reteta);
    }

    public void stergeReteta(Reteta reteta) {
        retete.remove(reteta);
    }

    public void adaugaIngredienteInLista(Reteta reteta) {
        for (Ingredient ing : reteta.getIngrediente()) {
            boolean exista = listaCumparaturi.stream()
                    .anyMatch(e -> e.getNume().equalsIgnoreCase(ing.getNume())
                            && e.getDinReteta().equals(reteta.getNume()));
            if (!exista) {
                ElementListaCumparaturi el = new ElementListaCumparaturi(
                        ing.getNume(), ing.getCantitate(),
                        ing.getUnitate(), reteta.getNume());
                el.setCategorie(reteta.getCategorie());
                listaCumparaturi.add(el);
                persistenceService.saveShoppingItem(el);
            }
        }
    }

    public void adaugaItemManual(ElementListaCumparaturi item) {
        listaCumparaturi.add(item);
        persistenceService.saveShoppingItem(item);
    }

    public void marcheazaCumparat(ElementListaCumparaturi item, boolean cumparat) {
        item.setCumparat(cumparat);
        persistenceService.updateShoppingItemBought(item.getId(), cumparat);
        if (cumparat) {
            PurchaseEvent event = new PurchaseEvent(
                    item.getId(), item.getNume(), item.getCantitate(),
                    item.getUnitate(), item.getCategorie(),
                    item.getPret(), LocalDateTime.now());
            persistenceService.savePurchaseEvent(event);
        }
    }

    public void stergeElementLista(ElementListaCumparaturi element) {
        listaCumparaturi.remove(element);
        persistenceService.deleteShoppingItem(element.getId());
    }

    public void golesteLista() {
        listaCumparaturi.clear();
        persistenceService.deleteAllShoppingItems();
    }

    public void stergeElementeCumparate() {
        listaCumparaturi.removeIf(ElementListaCumparaturi::isCumparat);
        persistenceService.deleteBoughtShoppingItems();
    }

    public List<Reteta> cauta(String text, String categorie) {
        return retete.stream()
                .filter(r -> {
                    boolean potrivireText = text == null || text.isEmpty()
                            || r.getNume().toLowerCase().contains(text.toLowerCase())
                            || r.getDescriere().toLowerCase().contains(text.toLowerCase());
                    boolean potrivireCategorie = categorie == null
                            || categorie.equals("Toate categoriile")
                            || r.getCategorie().equals(categorie);
                    return potrivireText && potrivireCategorie;
                })
                .collect(Collectors.toList());
    }
}
