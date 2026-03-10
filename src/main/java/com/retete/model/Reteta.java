package com.retete.model;

import java.util.ArrayList;
import java.util.List;

public class Reteta {
    private String id;
    private String nume;
    private String categorie;
    private String timpPreparare;
    private String descriere;
    private List<Ingredient> ingrediente;
    private String instructiuni;
    private int portii;
    private String imagine; // URL to food image

    public Reteta() {
        this.ingrediente = new ArrayList<>();
    }

    public Reteta(String id, String nume, String categorie, String timpPreparare,
                  String descriere, String instructiuni, int portii) {
        this.id = id;
        this.nume = nume;
        this.categorie = categorie;
        this.timpPreparare = timpPreparare;
        this.descriere = descriere;
        this.instructiuni = instructiuni;
        this.portii = portii;
        this.ingrediente = new ArrayList<>();
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getTimpPreparare() { return timpPreparare; }
    public void setTimpPreparare(String timp) { this.timpPreparare = timp; }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    public List<Ingredient> getIngrediente() { return ingrediente; }
    public void setIngrediente(List<Ingredient> ingrediente) { this.ingrediente = ingrediente; }

    public String getInstructiuni() { return instructiuni; }
    public void setInstructiuni(String instructiuni) { this.instructiuni = instructiuni; }

    public int getPortii() { return portii; }
    public void setPortii(int portii) { this.portii = portii; }

    public String getImagine() { return imagine; }
    public void setImagine(String imagine) { this.imagine = imagine; }

    public void adaugaIngredient(Ingredient ingredient) {
        this.ingrediente.add(ingredient);
    }

    @Override
    public String toString() {
        return nume;
    }
}
