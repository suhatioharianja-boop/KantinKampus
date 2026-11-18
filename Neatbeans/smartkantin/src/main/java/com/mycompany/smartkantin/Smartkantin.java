package com.mycompany.smartkantin;

import java.util.*;

// Superclass: atribut private + getter/setter (encapsulation)
class Menu {
    private String nama, tenant;
    private double harga;

    Menu(String nama, String tenant, double harga) {
        this.nama = nama;
        this.tenant = tenant;
        setHarga(harga);
    }

    String getNama() { return nama; }
    String getTenant() { return tenant; }
    double getHarga() { return harga; }

    void setHarga(double harga) {
        if (harga <= 0) throw new IllegalArgumentException("Harga harus lebih dari 0!");
        this.harga = harga;
    }

    String jenis() { return "Menu"; }
}

// Subclass (inheritance + override)
class Makanan extends Menu {
    Makanan(String n, String t, double h) { super(n, t, h); }
    @Override String jenis() { return "Makanan"; }
}

class Minuman extends Menu {
    Minuman(String n, String t, double h) { super(n, t, h); }
    @Override String jenis() { return "Minuman"; }
}

public class Smartkantin {
    static Scanner in = new Scanner(System.in);
    static ArrayList<Menu> menu = new ArrayList<>();
    static LinkedHashMap<Menu, Integer> keranjang = new LinkedHashMap<>();
    static HashMap<String, Double> omset = new HashMap<>();

    static int angka(String teks) {
        while (true) {
            System.out.print(teks);
            try { return Integer.parseInt(in.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Harus berupa angka!"); }
        }
    }

    static String teks(String label) {
        System.out.print(label);
        return in.nextLine().trim();
    }

    static void lihatMenu() {
        for (int i = 0; i < menu.size(); i++) {
            Menu m = menu.get(i);
            System.out.printf("%d. [%s] %s (%s) Rp%,.0f%n", i + 1, m.getTenant(), m.getNama(), m.jenis(), m.getHarga());
        }
    }

    static void pesan() {
        lihatMenu();
        int no = angka("Nomor menu: "), jml = angka("Jumlah: ");
        if (no < 1 || no > menu.size() || jml < 1) System.out.println("Input tidak valid!");
        else keranjang.merge(menu.get(no - 1), jml, Integer::sum);
    }

    static void bayar() {
        if (keranjang.isEmpty()) { System.out.println("Keranjang kosong!"); return; }
        double total = 0;
        for (Map.Entry<Menu, Integer> e : keranjang.entrySet()) {
            double sub = e.getKey().getHarga() * e.getValue();
            total += sub;
            System.out.printf("[%s] %s x%d = Rp%,.0f%n", e.getKey().getTenant(), e.getKey().getNama(), e.getValue(), sub);
        }
        System.out.printf("TOTAL: Rp%,.0f%n", total);
        int uang = angka("Uang dibayar: ");
        if (uang < total) { System.out.println("Uang kurang!"); return; }
        for (Map.Entry<Menu, Integer> e : keranjang.entrySet())
            omset.merge(e.getKey().getTenant(), e.getKey().getHarga() * e.getValue(), Double::sum);
        System.out.printf("Kembalian: Rp%,.0f%n", uang - total);
        keranjang.clear();
    }

    static void tambahMenu() {
        String n = teks("Nama menu: "), t = teks("Nama tenant: ");
        int h = angka("Harga: "), j = angka("1 = Makanan, 2 = Minuman: ");
        try { menu.add(j == 1 ? new Makanan(n, t, h) : new Minuman(n, t, h)); }
        catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }
    }
}
