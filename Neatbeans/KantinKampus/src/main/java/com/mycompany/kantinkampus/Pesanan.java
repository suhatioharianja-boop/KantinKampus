package kantinkampus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Keranjang belanja: kumpulan ItemPesanan dari satu atau banyak tenant.
 */
public class Pesanan {

    private ArrayList<ItemPesanan> daftarItem = new ArrayList<>();

    // Jika menu yang sama sudah ada di keranjang, jumlahnya ditambah
    public void tambahItem(ItemPesanan itemBaru) {
        for (ItemPesanan item : daftarItem) {
            if (item.getIdTenant() == itemBaru.getIdTenant()
                    && item.getIdMenu() == itemBaru.getIdMenu()) {
                item.tambahJumlah(itemBaru.getJumlah());
                return;
            }
        }
        daftarItem.add(itemBaru);
    }

    // idx dimulai dari 0. Mengembalikan true jika berhasil dihapus
    public boolean hapusItem(int idx) {
        if (idx < 0 || idx >= daftarItem.size()) {
            return false;
        }
        daftarItem.remove(idx);
        return true;
    }

    public double hitungTotal() {
        double total = 0;
        for (ItemPesanan item : daftarItem) {
            total += item.hitungSubtotal();
        }
        return total;
    }

    public boolean isKosong() {
        return daftarItem.isEmpty();
    }

    public ArrayList<ItemPesanan> getDaftarItem() {
        return daftarItem;
    }

    // Mengelompokkan item berdasarkan nama tenant (fitur unggulan)
    public HashMap<String, ArrayList<ItemPesanan>> kelompokPerTenant() {
        HashMap<String, ArrayList<ItemPesanan>> hasil = new LinkedHashMap<>();
        for (ItemPesanan item : daftarItem) {
            if (!hasil.containsKey(item.getNamaTenant())) {
                hasil.put(item.getNamaTenant(), new ArrayList<>());
            }
            hasil.get(item.getNamaTenant()).add(item);
        }
        return hasil;
    }

    // Teks rincian pesanan per tenant (dipakai di keranjang dan struk)
    public String getRincian() {
        StringBuilder sb = new StringBuilder();
        HashMap<String, ArrayList<ItemPesanan>> kelompok = kelompokPerTenant();
        for (String namaTenant : kelompok.keySet()) {
            sb.append("Tenant: ").append(namaTenant).append("\n");
            double subtotalTenant = 0;
            for (ItemPesanan item : kelompok.get(namaTenant)) {
                sb.append(String.format("  %-20s %3d x %10s = %11s%n",
                        item.getNamaMenu(), item.getJumlah(),
                        Utilitas.rupiah(item.getHargaSatuan()),
                        Utilitas.rupiah(item.hitungSubtotal())));
                subtotalTenant += item.hitungSubtotal();
            }
            sb.append(String.format("  %-38s %11s%n", "Subtotal " + namaTenant,
                    Utilitas.rupiah(subtotalTenant)));
        }
        return sb.toString();
    }
}
