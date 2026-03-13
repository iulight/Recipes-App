package com.retete.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.UUID;

public class ElementListaCumparaturi {
    private String id;
    private String nume;
    private double cantitate;
    private String unitate;
    private String dinReteta;
    private String categorie;
    private double pret;
    private BooleanProperty cumparat;

    public ElementListaCumparaturi(String nume, double cantitate, String unitate, String dinReteta) {
        this.id = UUID.randomUUID().toString();
        this.nume = nume;
        this.cantitate = cantitate;
        this.unitate = unitate;
        this.dinReteta = dinReteta;
        this.categorie = "";
        this.pret = 0;
        this.cumparat = new SimpleBooleanProperty(false);
    }

    public ElementListaCumparaturi(String id, String nume, double cantitate, String unitate,
                                    String dinReteta, String categorie, double pret, boolean cumparat) {
        this.id = id;
        this.nume = nume;
        this.cantitate = cantitate;
        this.unitate = unitate;
        this.dinReteta = dinReteta;
        this.categorie = categorie != null ? categorie : "";
        this.pret = pret;
        this.cumparat = new SimpleBooleanProperty(cumparat);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public double getCantitate() { return cantitate; }
    public void setCantitate(double cantitate) { this.cantitate = cantitate; }

    public String getUnitate() { return unitate; }
    public void setUnitate(String unitate) { this.unitate = unitate; }

    public String getDinReteta() { return dinReteta; }
    public void setDinReteta(String dinReteta) { this.dinReteta = dinReteta; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie != null ? categorie : ""; }

    public double getPret() { return pret; }
    public void setPret(double pret) { this.pret = pret; }

    public boolean isCumparat() { return cumparat.get(); }
    public void setCumparat(boolean cumparat) { this.cumparat.set(cumparat); }
    public BooleanProperty cumparatProperty() { return cumparat; }

    public String getAfisare() {
        if (cantitate == (long) cantitate) {
            return String.format("%s %s %s", (long) cantitate, unitate, nume);
        }
        return String.format("%.1f %s %s", cantitate, unitate, nume);
    }
}
