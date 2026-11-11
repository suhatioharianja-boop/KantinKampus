package kantinkampus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Menyimpan semua transaksi dan merekap pendapatan per hari.
 */
public class LaporanPendapatan {

    private ArrayList<Transaksi> daftarTransaksi = new ArrayList<>();

    public void tambahTransaksi(Transaksi transaksi) {
        daftarTransaksi.add(transaksi);
    }

    public ArrayList<Transaksi> getDaftarTransaksi() {
        return daftarTransaksi;
    }

    // Total pendapatan semua tenant pada tanggal tertentu
    public double omsetHarian(LocalDate tanggal) {
        double total = 0;
        for (Transaksi t : daftarTransaksi) {
            if (t.getTanggal().equals(tanggal)) {
                total += t.getPesanan().hitungTotal();
            }
        }
        return total;
    }

    // Pendapatan tiap tenant pada tanggal tertentu: nama tenant -> omset
    public HashMap<String, Double> omsetPerTenant(LocalDate tanggal) {
        HashMap<String, Double> hasil = new LinkedHashMap<>();
        for (Transaksi t : daftarTransaksi) {
            if (!t.getTanggal().equals(tanggal)) {
                continue;
            }
            for (ItemPesanan item : t.getPesanan().getDaftarItem()) {
                double sebelumnya = hasil.getOrDefault(item.getNamaTenant(), 0.0);
                hasil.put(item.getNamaTenant(), sebelumnya + item.hitungSubtotal());
            }
        }
        return hasil;
    }

    public int jumlahTransaksi(LocalDate tanggal) {
        int jumlah = 0;
        for (Transaksi t : daftarTransaksi) {
            if (t.getTanggal().equals(tanggal)) {
                jumlah++;
            }
        }
        return jumlah;
    }

    public void tampilkan(LocalDate tanggal) {
        System.out.println(Utilitas.garis('=', 50));
        System.out.println("LAPORAN PENDAPATAN TANGGAL " + tanggal);
        System.out.println(Utilitas.garis('=', 50));
        int jumlah = jumlahTransaksi(tanggal);
        if (jumlah == 0) {
            System.out.println("Belum ada transaksi pada tanggal ini.");
            return;
        }
        System.out.println("Jumlah transaksi : " + jumlah);
        System.out.println(Utilitas.garis('-', 50));
        HashMap<String, Double> perTenant = omsetPerTenant(tanggal);
        for (String namaTenant : perTenant.keySet()) {
            System.out.printf("%-30s %17s%n", namaTenant, Utilitas.rupiah(perTenant.get(namaTenant)));
        }
        System.out.println(Utilitas.garis('-', 50));
        System.out.printf("%-30s %17s%n", "TOTAL SEMUA TENANT", Utilitas.rupiah(omsetHarian(tanggal)));
    }
}
