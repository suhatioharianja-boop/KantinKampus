package com.mycompany.smartkantin;

// Subclass (inheritance + override)
public class Minuman extends Menu {
    private static final long serialVersionUID = 1L;

    public Minuman(String nama, String tenant, double harga) {
        super(nama, tenant, harga);
    }

    @Override
    public String jenis() { return "Minuman"; }
}
