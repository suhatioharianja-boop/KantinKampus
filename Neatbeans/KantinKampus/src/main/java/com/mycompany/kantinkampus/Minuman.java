package kantinkampus;

/**
 * Turunan Menu untuk minuman (Inheritance).
 */
public class Minuman extends Menu {

    private String suhu; // "Panas" atau "Dingin"

    public Minuman(int idMenu, String nama, double harga, String suhu) {
        super(idMenu, nama, harga, "Minuman");
        this.suhu = suhu;
    }

    public String getSuhu() {
        return suhu;
    }

    @Override
    public String getDeskripsi() {
        return "Disajikan " + suhu.toLowerCase();
    }
}
