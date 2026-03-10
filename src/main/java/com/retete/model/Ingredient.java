package com.retete.model;

public class Ingredient {
    private String nume;
    private double cantitate;
    private String unitate;
    private boolean inListaCumparaturi;

    public Ingredient() {}

    public Ingredient(String nume, double cantitate, String unitate) {
        this.nume = nume;
        this.cantitate = cantitate;
        this.unitate = unitate;
        this.inListaCumparaturi = false;
    }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public double getCantitate() { return cantitate; }
    public void setCantitate(double cantitate) { this.cantitate = cantitate; }

    public String getUnitate() { return unitate; }
    public void setUnitate(String unitate) { this.unitate = unitate; }

    public boolean isInListaCumparaturi() { return inListaCumparaturi; }
    public void setInListaCumparaturi(boolean in) { this.inListaCumparaturi = in; }

    public String getAfisare() {
        if (cantitate == (long) cantitate) {
            return String.format("%s %s %s", (long) cantitate, unitate, nume);
        }
        return String.format("%.1f %s %s", cantitate, unitate, nume);
    }

    @Override
    public String toString() {
        return getAfisare();
    }
}
