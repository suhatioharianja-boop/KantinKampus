package kantinkampus;

/**
 * Kelas induk (abstract) untuk semua menu.
 * Menerapkan: Encapsulation (atribut private + getter/setter) dan Abstraction.
 */
public abstract class Menu {

    private int idMenu;
    private String nama;
    private double harga;
    private String kategori;

    public Menu(int idMenu, String nama, double harga, String kategori) {
        this.idMenu = idMenu;
        setNama(nama);
        setHarga(harga);
        this.kategori = kategori;
    }

    // Method abstrak: wajib di-override oleh kelas turunan (Polymorphism)
    public abstract String getDeskripsi();

    public int getIdMenu() {
        return idMenu;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama menu tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga harus lebih dari 0.");
        }
        this.harga = harga;
    }

    public String getKategori() {
        return kategori;
    }
}
