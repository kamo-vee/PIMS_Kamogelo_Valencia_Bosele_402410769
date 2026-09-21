package pims.model;

import java.time.LocalDate;

public class Medicine {
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private LocalDate expiryDate;
    private int supplierId;
    private String supplierName;

    public Medicine() { }

    public Medicine(int id, String name, String category,
                    double price, int quantity, LocalDate expiryDate, int supplierId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    /** Low stock threshold. */
    public boolean isLowStock() { return quantity < 20; }

    /** Expired or expiring within 30 days. */
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    @Override
    public String toString() { return name; }
}