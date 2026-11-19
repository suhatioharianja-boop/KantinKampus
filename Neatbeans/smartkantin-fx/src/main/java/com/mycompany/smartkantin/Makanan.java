package com.mycompany.smartkantin;

// Subclass (inheritance + override)
public class Makanan extends Menu {
    private static final long serialVersionUID = 1L;

    public Makanan(String nama, String tenant, double harga) {
        super(nama, tenant, harga);
    }

    @Override
    public String jenis() { return "Makanan"; }
}
