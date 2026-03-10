package com.retete.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ElementListaCumparaturi {
    private String nume;
    private double cantitate;
    private String unitate;
    private String dinReteta;
    private BooleanProperty cumparat;

    public ElementListaCumparaturi(String nume, double cantitate, String unitate, String dinReteta) {
        this.nume = nume;
        this.cantitate = cantitate;
        this.unitate = unitate;
        this.dinReteta = dinReteta;
        this.cumparat = new SimpleBooleanProperty(false);
    }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public double getCantitate() { return cantitate; }
    public void setCantitate(double cantitate) { this.cantitate = cantitate; }

    public String getUnitate() { return unitate; }
    public void setUnitate(String unitate) { this.unitate = unitate; }

    public String getDinReteta() { return dinReteta; }
    public void setDinReteta(String dinReteta) { this.dinReteta = dinReteta; }

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
