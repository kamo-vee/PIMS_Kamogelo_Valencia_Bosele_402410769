package pims.model;

import java.util.Date;
import java.util.List;

public class Sale {
    private int id;
    private Date saleDate;
    private double total;
    private int cashierId;
    private List<SaleItem> items;

    public Sale() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getSaleDate() { return saleDate; }
    public void setSaleDate(Date saleDate) { this.saleDate = saleDate; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public int getCashierId() { return cashierId; }
    public void setCashierId(int cashierId) { this.cashierId = cashierId; }

    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
}