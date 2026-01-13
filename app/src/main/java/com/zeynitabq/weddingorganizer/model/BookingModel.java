package com.zeynitabq.weddingorganizer.model;

public class BookingModel {
    private int id;
    private String trxCode;      // Kode Invoice (INV-001)
    private String clientName;   // Nama Klien
    private String clientPhone;  // No HP (Penting buat kontak)
    private String clientAddress;// Alamat
    private String weddingDate;  // Tanggal Acara
    private String pkgName;      // Nama Paket yang dibeli
    private double totalPrice;   // Total Bayar
    private String paymentMethod;// QRIS / Cash / Transfer
    private String status;       // Lunas / DP

    // Constructor Kosong
    public BookingModel() {
    }

    // Constructor Lengkap
    public BookingModel(int id, String trxCode, String clientName, String clientPhone,
                        String clientAddress, String weddingDate, String pkgName,
                        double totalPrice, String paymentMethod, String status) {
        this.id = id;
        this.trxCode = trxCode;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientAddress = clientAddress;
        this.weddingDate = weddingDate;
        this.pkgName = pkgName;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTrxCode() { return trxCode; }
    public void setTrxCode(String trxCode) { this.trxCode = trxCode; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getClientAddress() { return clientAddress; }
    public void setClientAddress(String clientAddress) { this.clientAddress = clientAddress; }

    public String getWeddingDate() { return weddingDate; }
    public void setWeddingDate(String weddingDate) { this.weddingDate = weddingDate; }

    public String getPkgName() { return pkgName; }
    public void setPkgName(String pkgName) { this.pkgName = pkgName; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}