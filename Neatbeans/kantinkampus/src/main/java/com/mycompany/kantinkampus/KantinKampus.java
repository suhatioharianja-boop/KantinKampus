package kantinkampus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Logika program KantinKampus (versi terminal / konsol).
 * Dijalankan dari Main.java lewat method jalankan().
 */
public class KantinKampus {

    private static final String FILE_DATA = "data_kantin.txt";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private static final Scanner input = new Scanner(System.in);
    private static final ArrayList<Tenant> daftarTenant = new ArrayList<>();
    private static final LaporanPendapatan laporan = new LaporanPendapatan();
    private static Pesanan keranjang = new Pesanan();

    private static int idTenantBerikut = 1;
    private static int idMenuBerikut = 1;
    private static int idTransaksiBerikut = 1;

    public static void jalankan() {
        if (!PenyimpananData.muat(FILE_DATA, daftarTenant, laporan)) {
            isiDataContoh();
        }
        hitungIdBerikut();

        boolean jalan = true;
        while (jalan) {
            System.out.println();
            System.out.println(Utilitas.garis('=', 44));
            System.out.println("            KANTIN KAMPUS");
            System.out.println(Utilitas.garis('=', 44));
            System.out.println("1. Masuk sebagai Admin / Pengelola");
            System.out.println("2. Masuk sebagai Pembeli");
            System.out.println("0. Keluar");
            int pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1:
                    if (loginAdmin()) {
                        menuAdmin();
                    }
                    break;
                case 2:
                    menuPembeli();
                    break;
                case 0:
                    jalan = false;
                    break;
                default:
                    System.out.println("Pilihan tidak tersedia.");
            }
        }
        PenyimpananData.simpan(FILE_DATA, daftarTenant, laporan);
        System.out.println("Data tersimpan. Terima kasih!");
    }

    // ===================== DATA AWAL =====================

    // Data contoh agar program bisa langsung dicoba saat file data belum ada
    private static void isiDataContoh() {
        Tenant geprek = new Tenant(1, "Ayam Geprek");
        geprek.tambahMenu(new Makanan(1, "Ayam Geprek", 15000, 2));
        geprek.tambahMenu(new Makanan(2, "Nasi Putih", 4000, 0));
        geprek.tambahMenu(new Minuman(3, "Es Teh Manis", 4000, "Dingin"));
        Tenant bakso = new Tenant(2, "Bakso Pak Budi");
        bakso.tambahMenu(new Makanan(4, "Bakso Urat", 13000, 1));
        bakso.tambahMenu(new Minuman(5, "Teh Hangat", 3000, "Panas"));
        daftarTenant.add(geprek);
        daftarTenant.add(bakso);
    }

    // Menentukan nomor ID berikutnya dari data yang sudah ada
    private static void hitungIdBerikut() {
        for (Tenant t : daftarTenant) {
            idTenantBerikut = Math.max(idTenantBerikut, t.getIdTenant() + 1);
            for (Menu m : t.getDaftarMenu()) {
                idMenuBerikut = Math.max(idMenuBerikut, m.getIdMenu() + 1);
            }
        }
        for (Transaksi trx : laporan.getDaftarTransaksi()) {
            idTransaksiBerikut = Math.max(idTransaksiBerikut, trx.getIdTransaksi() + 1);
        }
    }

    // ===================== INPUT =====================

    private static String bacaTeks(String label) {
        System.out.print(label);
        return input.nextLine().trim();
    }

    // Teks wajib diisi; tanda "|" diganti karena dipakai sebagai pemisah di file
    private static String bacaTeksWajib(String label) {
        while (true) {
            String teks = bacaTeks(label).replace("|", "/");
            if (!teks.isEmpty()) {
                return teks;
            }
            System.out.println("Tidak boleh kosong.");
        }
    }

    private static int bacaInt(String label) {
        while (true) {
            String teks = bacaTeks(label);
            try {
                return Integer.parseInt(teks);
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka bulat. Coba lagi.");
            }
        }
    }

    private static double bacaDouble(String label) {
        while (true) {
            String teks = bacaTeks(label);
            try {
                return Double.parseDouble(teks);
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka. Coba lagi.");
            }
        }
    }

    private static boolean konfirmasi(String pertanyaan) {
        String jawab = bacaTeks(pertanyaan + " (y/n): ");
        return jawab.equalsIgnoreCase("y");
    }

    // ===================== LOGIN ADMIN =====================

    private static boolean loginAdmin() {
        for (int percobaan = 1; percobaan <= 3; percobaan++) {
            String username = bacaTeks("Username: ");
            String password = bacaTeks("Password: ");
            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                System.out.println("Login berhasil.");
                return true;
            }
            System.out.println("Username atau password salah (" + percobaan + "/3).");
        }
        System.out.println("Terlalu banyak percobaan. Kembali ke menu utama.");
        return false;
    }

    // ===================== MENU ADMIN =====================

    private static void menuAdmin() {
        boolean jalan = true;
        while (jalan) {
            System.out.println();
            System.out.println("--- MENU ADMIN ---");
            System.out.println("1. Lihat tenant dan menu");
            System.out.println("2. Tambah tenant");
            System.out.println("3. Hapus tenant");
            System.out.println("4. Kelola menu tenant");
            System.out.println("5. Laporan pendapatan harian");
            System.out.println("0. Logout");
            int pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1:
                    tampilkanTenantDanMenu();
                    break;
                case 2:
                    tambahTenant();
                    break;
                case 3:
                    hapusTenant();
                    break;
                case 4:
                    kelolaMenu();
                    break;
                case 5:
                    tampilkanLaporan();
                    break;
                case 0:
                    jalan = false;
                    break;
                default:
                    System.out.println("Pilihan tidak tersedia.");
            }
        }
    }

    private static void tampilkanTenantDanMenu() {
        System.out.println();
        System.out.println("=== DAFTAR TENANT DAN MENU ===");
        if (daftarTenant.isEmpty()) {
            System.out.println("Belum ada tenant.");
            return;
        }
        for (Tenant t : daftarTenant) {
            System.out.println();
            System.out.println("[Tenant " + t.getIdTenant() + "] " + t.getNamaTenant());
            if (t.getDaftarMenu().isEmpty()) {
                System.out.println("   (belum ada menu)");
                continue;
            }
            System.out.printf("   %-4s %-10s %-22s %-14s %s%n", "ID", "Kategori", "Nama", "Harga", "Keterangan");
            for (Menu m : t.getDaftarMenu()) {
                System.out.printf("   %-4d %-10s %-22s %-14s %s%n", m.getIdMenu(), m.getKategori(),
                        m.getNama(), Utilitas.rupiah(m.getHarga()), m.getDeskripsi());
            }
        }
    }

    private static Tenant cariTenant(int idTenant) {
        for (Tenant t : daftarTenant) {
            if (t.getIdTenant() == idTenant) {
                return t;
            }
        }
        return null;
    }

    private static boolean namaTenantSudahAda(String nama) {
        for (Tenant t : daftarTenant) {
            if (t.getNamaTenant().equalsIgnoreCase(nama)) {
                return true;
            }
        }
        return false;
    }

    private static void tambahTenant() {
        String nama = bacaTeksWajib("Nama tenant baru: ");
        if (namaTenantSudahAda(nama)) {
            System.out.println("Gagal: nama tenant sudah dipakai.");
            return;
        }
        daftarTenant.add(new Tenant(idTenantBerikut, nama));
        System.out.println("Tenant \"" + nama + "\" berhasil ditambahkan dengan ID " + idTenantBerikut + ".");
        idTenantBerikut++;
        PenyimpananData.simpan(FILE_DATA, daftarTenant, laporan);
    }

    private static void hapusTenant() {
        tampilkanTenantDanMenu();
        if (daftarTenant.isEmpty()) {
            return;
        }
        int id = bacaInt("ID tenant yang dihapus (0 = batal): ");
        if (id == 0) {
            return;
        }
        Tenant tenant = cariTenant(id);
        if (tenant == null) {
            System.out.println("Tenant dengan ID tersebut tidak ditemukan.");
            return;
        }
        if (konfirmasi("Hapus tenant \"" + tenant.getNamaTenant() + "\" beserta semua menunya?")) {
            daftarTenant.remove(tenant);
            // Item di keranjang milik tenant ini ikut dibersihkan
            keranjang.getDaftarItem().removeIf(item -> item.getIdTenant() == id);
            System.out.println("Tenant berhasil dihapus. Riwayat transaksi tetap tersimpan.");
            PenyimpananData.simpan(FILE_DATA, daftarTenant, laporan);
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }

    private static void kelolaMenu() {
        tampilkanTenantDanMenu();
        if (daftarTenant.isEmpty()) {
            return;
        }
        int id = bacaInt("ID tenant yang menunya dikelola (0 = batal): ");
        if (id == 0) {
            return;
        }
        Tenant tenant = cariTenant(id);
        if (tenant == null) {
            System.out.println("Tenant dengan ID tersebut tidak ditemukan.");
            return;
        }
        boolean jalan = true;
        while (jalan) {
            System.out.println();
            System.out.println("--- KELOLA MENU: " + tenant.getNamaTenant() + " ---");
            System.out.println("1. Tambah menu");
            System.out.println("2. Ubah menu");
            System.out.println("3. Hapus menu");
            System.out.println("0. Kembali");
            int pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1:
                    tambahMenu(tenant);
                    break;
                case 2:
                    ubahMenu(tenant);
                    break;
                case 3:
                    hapusMenu(tenant);
                    break;
                case 0:
                    jalan = false;
                    break;
                default:
                    System.out.println("Pilihan tidak tersedia.");
            }
        }
    }

    private static double bacaHargaPositif(String label) {
        while (true) {
            double harga = bacaDouble(label);
            if (harga > 0) {
                return harga;
            }
            System.out.println("Harga harus lebih dari 0.");
        }
    }

    private static void tambahMenu(Tenant tenant) {
        String nama = bacaTeksWajib("Nama menu: ");
        double harga = bacaHargaPositif("Harga (angka, tanpa titik): ");
        int jenis;
        do {
            jenis = bacaInt("Kategori (1 = Makanan, 2 = Minuman): ");
        } while (jenis != 1 && jenis != 2);

        Menu menu;
        if (jenis == 1) {
            int pedas;
            do {
                pedas = bacaInt("Tingkat pedas (0 = tidak pedas, 1-3): ");
            } while (pedas < 0 || pedas > 3);
            menu = new Makanan(idMenuBerikut, nama, harga, pedas);
        } else {
            int suhu;
            do {
                suhu = bacaInt("Suhu (1 = Panas, 2 = Dingin): ");
            } while (suhu != 1 && suhu != 2);
            menu = new Minuman(idMenuBerikut, nama, harga, suhu == 1 ? "Panas" : "Dingin");
        }
        tenant.tambahMenu(menu);
        System.out.println("Menu \"" + nama + "\" berhasil ditambahkan dengan ID " + idMenuBerikut + ".");
        idMenuBerikut++;
        PenyimpananData.simpan(FILE_DATA, daftarTenant, laporan);
    }

    private static void ubahMenu(Tenant tenant) {
        if (tenant.getDaftarMenu().isEmpty()) {
            System.out.println("Tenant ini belum punya menu.");
            return;
        }
        int id = bacaInt("ID menu yang diubah (0 = batal): ");
        if (id == 0) {
            return;
        }
        Menu menu = tenant.cariMenu(id);
        if (menu == null) {
            System.out.println("Menu dengan ID tersebut tidak ditemukan di tenant ini.");
            return;
        }
        String namaBaru = bacaTeks("Nama baru [" + menu.getNama() + "] (Enter = tidak diubah): ")
                .replace("|", "/");
        if (!namaBaru.isEmpty()) {
            menu.setNama(namaBaru);
        }
        while (true) {
            String hargaBaru = bacaTeks("Harga baru [" + Utilitas.rupiah(menu.getHarga())
                    + "] (Enter = tidak diubah): ");
            if (hargaBaru.isEmpty()) {
                break;
            }
            try {
                menu.setHarga(Double.parseDouble(hargaBaru));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Harga harus berupa angka.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Menu berhasil diperbarui.");
        PenyimpananData.simpan(FILE_DATA, daftarTenant, laporan);
    }

    private static void hapusMenu(Tenant tenant) {
        if (tenant.getDaftarMenu().isEmpty()) {
            System.out.println("Tenant ini belum punya menu.");
            return;
        }
        int id = bacaInt("ID menu yang dihapus (0 = batal): ");
        if (id == 0) {
            return;
        }
        if (tenant.hapusMenu(id)) {
            keranjang.getDaftarItem().removeIf(
                    item -> item.getIdTenant() == tenant.getIdTenant() && item.getIdMenu() == id);
            System.out.println("Menu berhasil dihapus.");
            PenyimpananData.simpan(FILE_DATA, daftarTenant, laporan);
        } else {
            System.out.println("Menu dengan ID tersebut tidak ditemukan di tenant ini.");
        }
    }

    private static void tampilkanLaporan() {
        String teks = bacaTeks("Tanggal laporan (yyyy-MM-dd, Enter = hari ini): ");
        LocalDate tanggal;
        if (teks.isEmpty()) {
            tanggal = LocalDate.now();
        } else {
            try {
                tanggal = LocalDate.parse(teks);
            } catch (DateTimeParseException e) {
                System.out.println("Format tanggal salah. Contoh: 2026-11-10");
                return;
            }
        }
        System.out.println();
        laporan.tampilkan(tanggal);
    }

    // ===================== MENU PEMBELI =====================

    private static void menuPembeli() {
        boolean jalan = true;
        while (jalan) {
            System.out.println();
            System.out.println("--- MENU PEMBELI ---");
            System.out.println("1. Lihat tenant dan menu");
            System.out.println("2. Tambah menu ke keranjang");
            System.out.println("3. Lihat keranjang");
            System.out.println("4. Hapus item dari keranjang");
            System.out.println("5. Checkout dan bayar");
            System.out.println("0. Kembali");
            int pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1:
                    tampilkanTenantDanMenu();
                    break;
                case 2:
                    tambahKeKeranjang();
                    break;
                case 3:
                    lihatKeranjang();
                    break;
                case 4:
                    hapusDariKeranjang();
                    break;
                case 5:
                    checkout();
                    break;
                case 0:
                    jalan = false;
                    break;
                default:
                    System.out.println("Pilihan tidak tersedia.");
            }
        }
    }

    private static void tambahKeKeranjang() {
        tampilkanTenantDanMenu();
        if (daftarTenant.isEmpty()) {
            return;
        }
        int idTenant = bacaInt("ID tenant (0 = batal): ");
        if (idTenant == 0) {
            return;
        }
        Tenant tenant = cariTenant(idTenant);
        if (tenant == null) {
            System.out.println("Tenant dengan ID tersebut tidak ditemukan.");
            return;
        }
        int idMenu = bacaInt("ID menu: ");
        Menu menu = tenant.cariMenu(idMenu);
        if (menu == null) {
            System.out.println("Menu dengan ID tersebut tidak ditemukan di tenant ini.");
            return;
        }
        int jumlah = bacaInt("Jumlah porsi: ");
        try {
            ItemPesanan item = new ItemPesanan(tenant, menu, jumlah);
            keranjang.tambahItem(item);
            System.out.println(jumlah + " x " + menu.getNama() + " masuk keranjang (subtotal "
                    + Utilitas.rupiah(item.hitungSubtotal()) + ").");
        } catch (IllegalArgumentException e) {
            System.out.println("Gagal: " + e.getMessage());
        }
    }

    private static void lihatKeranjang() {
        System.out.println();
        System.out.println("=== KERANJANG BELANJA ===");
        if (keranjang.isKosong()) {
            System.out.println("Keranjang masih kosong.");
            return;
        }
        System.out.print(keranjang.getRincian());
        System.out.println(Utilitas.garis('-', 56));
        System.out.printf("%-44s %11s%n", "TOTAL BELANJA", Utilitas.rupiah(keranjang.hitungTotal()));
    }

    private static void hapusDariKeranjang() {
        if (keranjang.isKosong()) {
            System.out.println("Keranjang masih kosong.");
            return;
        }
        ArrayList<ItemPesanan> daftar = keranjang.getDaftarItem();
        System.out.println();
        for (int i = 0; i < daftar.size(); i++) {
            ItemPesanan item = daftar.get(i);
            System.out.printf("%d. %s - %s (%d porsi)%n", i + 1, item.getNamaTenant(),
                    item.getNamaMenu(), item.getJumlah());
        }
        int nomor = bacaInt("Nomor item yang dihapus (0 = batal): ");
        if (nomor == 0) {
            return;
        }
        if (keranjang.hapusItem(nomor - 1)) {
            System.out.println("Item dihapus dari keranjang.");
        } else {
            System.out.println("Nomor item tidak valid.");
        }
    }

    private static void checkout() {
        if (keranjang.isKosong()) {
            System.out.println("Keranjang masih kosong, tidak ada yang dibayar.");
            return;
        }
        lihatKeranjang();
        Transaksi transaksi = new Transaksi(idTransaksiBerikut, keranjang);
        while (true) {
            double uang = bacaDouble("Uang yang dibayarkan (0 = batalkan checkout): ");
            if (uang == 0) {
                System.out.println("Checkout dibatalkan. Keranjang tetap tersimpan.");
                return;
            }
            try {
                transaksi.prosesPembayaran(uang);
                break;
            } catch (UangKurangException e) {
                System.out.println("Pembayaran gagal: " + e.getMessage());
            }
        }
        System.out.println();
        System.out.print(transaksi.cetak());
        laporan.tambahTransaksi(transaksi);
        idTransaksiBerikut++;
        keranjang = new Pesanan();
        PenyimpananData.simpan(FILE_DATA, daftarTenant, laporan);
    }
}
