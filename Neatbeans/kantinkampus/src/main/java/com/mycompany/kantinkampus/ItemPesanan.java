package kantinkampus;

/**
 * Satu baris pesanan. Nama tenant, nama menu, dan harga disalin saat dipesan
 * (snapshot), jadi riwayat transaksi tetap benar walaupun menu/tenant dihapus
 * atau harganya diubah di kemudian hari.
 */
public class ItemPesanan {

    private int idTenant;
    private String namaTenant;
    private int idMenu;
    private String namaMenu;
    private double hargaSatuan;
    private int jumlah;

    // Dipakai saat pembeli memesan
    public ItemPesanan(Tenant tenant, Menu menu, int jumlah) {
        this(tenant.getIdTenant(), tenant.getNamaTenant(),
                menu.getIdMenu(), menu.getNama(), menu.getHarga(), jumlah);
    }

    // Dipakai juga saat membaca data dari file
    public ItemPesanan(int idTenant, String namaTenant, int idMenu,
            String namaMenu, double hargaSatuan, int jumlah) {
        this.idTenant = idTenant;
        this.namaTenant = namaTenant;
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.hargaSatuan = hargaSatuan;
        setJumlah(jumlah);
    }

    public double hitungSubtotal() {
        return hargaSatuan * jumlah;
    }

    public int getIdTenant() {
        return idTenant;
    }

    public String getNamaTenant() {
        return namaTenant;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pesanan harus lebih dari 0.");
        }
        this.jumlah = jumlah;
    }

    public void tambahJumlah(int tambahan) {
        setJumlah(this.jumlah + tambahan);
    }
}
