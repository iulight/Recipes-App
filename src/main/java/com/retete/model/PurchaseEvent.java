package com.retete.model;

import java.time.LocalDateTime;

public class PurchaseEvent {
    private long id;
    private String shoppingItemId;
    private String itemName;
    private double cantitate;
    private String unitate;
    private String categorie;
    private double pretTotal;
    private LocalDateTime boughtAt;

    public PurchaseEvent() {}

    public PurchaseEvent(String shoppingItemId, String itemName, double cantitate,
                         String unitate, String categorie, double pretTotal, LocalDateTime boughtAt) {
        this.shoppingItemId = shoppingItemId;
        this.itemName = itemName;
        this.cantitate = cantitate;
        this.unitate = unitate;
        this.categorie = categorie;
        this.pretTotal = pretTotal;
        this.boughtAt = boughtAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getShoppingItemId() { return shoppingItemId; }
    public void setShoppingItemId(String shoppingItemId) { this.shoppingItemId = shoppingItemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getCantitate() { return cantitate; }
    public void setCantitate(double cantitate) { this.cantitate = cantitate; }

    public String getUnitate() { return unitate; }
    public void setUnitate(String unitate) { this.unitate = unitate; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getPretTotal() { return pretTotal; }
    public void setPretTotal(double pretTotal) { this.pretTotal = pretTotal; }

    public LocalDateTime getBoughtAt() { return boughtAt; }
    public void setBoughtAt(LocalDateTime boughtAt) { this.boughtAt = boughtAt; }

    public String getCantitateAfisare() {
        if (cantitate == (long) cantitate) {
            return (long) cantitate + " " + unitate;
        }
        return String.format("%.1f %s", cantitate, unitate);
    }
}
