package com.mycompany.smartkantin;

import static com.mycompany.smartkantin.Smartkantin.*;

public class Main {
    public static void main(String[] args) {
        menu.add(new Makanan("Ayam Geprek", "Geprek Pak Eko", 15000));
        menu.add(new Minuman("Es Teh", "Geprek Pak Eko", 4000));
        menu.add(new Makanan("Bakso", "Bakso Bu Ani", 13000));
        int pilih;
        do {
            System.out.println("\n1.Lihat menu 2.Pesan 3.Bayar 4.Tambah menu 5.Rekap omset 0.Keluar");
            pilih = angka("Pilih: ");
            switch (pilih) {
                case 1: lihatMenu(); break;
                case 2: pesan(); break;
                case 3: bayar(); break;
                case 4: tambahMenu(); break;
                case 5: for (String t : omset.keySet()) System.out.printf("%s: Rp%,.0f%n", t, omset.get(t)); break;
                case 0: System.out.println("Terima kasih!"); break;
                default: System.out.println("Pilihan tidak ada!");
            }
        } while (pilih != 0);
    }
}
