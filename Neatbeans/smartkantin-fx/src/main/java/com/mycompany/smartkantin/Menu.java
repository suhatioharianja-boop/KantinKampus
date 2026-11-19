package com.mycompany.smartkantin;

import java.io.Serializable;

// Superclass: atribut private + getter/setter (encapsulation)
// implements Serializable agar objek bisa "dibekukan" ke file (Pertemuan 11)
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nama, tenant;
    private double harga;

    public Menu(String nama, String tenant, double harga) {
        this.nama = nama;
        this.tenant = tenant;
        setHarga(harga);
    }

    public String getNama() { return nama; }
    public String getTenant() { return tenant; }
    public double getHarga() { return harga; }

    public void setHarga(double harga) {
        if (harga <= 0) throw new IllegalArgumentException("Harga harus lebih dari 0!");
        this.harga = harga;
    }

    public String jenis() { return "Menu"; }
}
