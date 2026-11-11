package kantinkampus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Menyimpan dan membaca data ke file teks. Satu baris = satu data, dipisah tanda "|".
 *
 * TENANT|idTenant|namaTenant
 * MENU|idTenant|MAKANAN/MINUMAN|idMenu|nama|harga|tingkatPedas atau suhu
 * TRANSAKSI|idTransaksi|tanggal|uangDibayar|kembalian
 * ITEM|idTenant|namaTenant|idMenu|namaMenu|hargaSatuan|jumlah   (milik TRANSAKSI di atasnya)
 */
public class PenyimpananData {

    public static void simpan(String namaFile, ArrayList<Tenant> daftarTenant,
            LaporanPendapatan laporan) {
        try (PrintWriter out = new PrintWriter(new FileWriter(namaFile))) {
            for (Tenant t : daftarTenant) {
                out.println("TENANT|" + t.getIdTenant() + "|" + t.getNamaTenant());
                for (Menu m : t.getDaftarMenu()) {
                    String jenis;
                    String tambahan;
                    if (m instanceof Makanan) {
                        jenis = "MAKANAN";
                        tambahan = String.valueOf(((Makanan) m).getTingkatPedas());
                    } else {
                        jenis = "MINUMAN";
                        tambahan = ((Minuman) m).getSuhu();
                    }
                    out.println("MENU|" + t.getIdTenant() + "|" + jenis + "|" + m.getIdMenu()
                            + "|" + m.getNama() + "|" + m.getHarga() + "|" + tambahan);
                }
            }
            for (Transaksi trx : laporan.getDaftarTransaksi()) {
                out.println("TRANSAKSI|" + trx.getIdTransaksi() + "|" + trx.getTanggal()
                        + "|" + trx.getUangDibayar() + "|" + trx.getKembalian());
                for (ItemPesanan item : trx.getPesanan().getDaftarItem()) {
                    out.println("ITEM|" + item.getIdTenant() + "|" + item.getNamaTenant()
                            + "|" + item.getIdMenu() + "|" + item.getNamaMenu()
                            + "|" + item.getHargaSatuan() + "|" + item.getJumlah());
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal menyimpan data: " + e.getMessage());
        }
    }

    // Mengembalikan true jika file ditemukan dan berhasil dibaca
    public static boolean muat(String namaFile, ArrayList<Tenant> daftarTenant,
            LaporanPendapatan laporan) {
        File file = new File(namaFile);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String baris;
            Transaksi transaksiTerakhir = null;
            while ((baris = in.readLine()) != null) {
                if (baris.trim().isEmpty()) {
                    continue;
                }
                String[] p = baris.split("\\|");
                try {
                    switch (p[0]) {
                        case "TENANT":
                            daftarTenant.add(new Tenant(Integer.parseInt(p[1]), p[2]));
                            break;
                        case "MENU":
                            muatMenu(p, daftarTenant);
                            break;
                        case "TRANSAKSI":
                            transaksiTerakhir = new Transaksi(Integer.parseInt(p[1]),
                                    LocalDate.parse(p[2]), new Pesanan(),
                                    Double.parseDouble(p[3]), Double.parseDouble(p[4]));
                            laporan.tambahTransaksi(transaksiTerakhir);
                            break;
                        case "ITEM":
                            if (transaksiTerakhir != null) {
                                transaksiTerakhir.getPesanan().tambahItem(new ItemPesanan(
                                        Integer.parseInt(p[1]), p[2], Integer.parseInt(p[3]),
                                        p[4], Double.parseDouble(p[5]), Integer.parseInt(p[6])));
                            }
                            break;
                        default:
                            break;
                    }
                } catch (RuntimeException e) {
                    System.out.println("Baris data rusak dilewati: " + baris);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Gagal membaca data: " + e.getMessage());
            return false;
        }
    }

    private static void muatMenu(String[] p, ArrayList<Tenant> daftarTenant) {
        int idTenant = Integer.parseInt(p[1]);
        Tenant tenant = null;
        for (Tenant t : daftarTenant) {
            if (t.getIdTenant() == idTenant) {
                tenant = t;
            }
        }
        if (tenant == null) {
            return;
        }
        int idMenu = Integer.parseInt(p[3]);
        double harga = Double.parseDouble(p[5]);
        if (p[2].equals("MAKANAN")) {
            tenant.tambahMenu(new Makanan(idMenu, p[4], harga, Integer.parseInt(p[6])));
        } else {
            tenant.tambahMenu(new Minuman(idMenu, p[4], harga, p[6]));
        }
    }
}
