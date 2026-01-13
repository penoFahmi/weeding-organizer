package com.zeynitabq.weddingorganizer.model;

public class PackageModel {
    private int id;
    private String name;        // Nama Paket
    private double price;       // Harga
    private String type;        // Indoor / Outdoor
    private String extras;      // Fasilitas Tambahan (Gabungan Checkbox)
    private String imageUri;    // Lokasi Foto

    // Constructor Kosong
    public PackageModel() {
    }

    // Constructor Lengkap
    public PackageModel(int id, String name, double price, String type, String extras, String imageUri) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.extras = extras;
        this.imageUri = imageUri;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getExtras() { return extras; }
    public void setExtras(String extras) { this.extras = extras; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}