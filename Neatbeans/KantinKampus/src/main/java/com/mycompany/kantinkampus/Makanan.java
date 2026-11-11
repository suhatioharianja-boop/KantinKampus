package kantinkampus;

/**
 * Turunan Menu untuk makanan (Inheritance).
 */
public class Makanan extends Menu {

    private int tingkatPedas; // 0 = tidak pedas, 1-3 = makin pedas

    public Makanan(int idMenu, String nama, double harga, int tingkatPedas) {
        super(idMenu, nama, harga, "Makanan");
        setTingkatPedas(tingkatPedas);
    }

    public int getTingkatPedas() {
        return tingkatPedas;
    }

    public void setTingkatPedas(int tingkatPedas) {
        if (tingkatPedas < 0 || tingkatPedas > 3) {
            throw new IllegalArgumentException("Tingkat pedas harus 0 sampai 3.");
        }
        this.tingkatPedas = tingkatPedas;
    }

    @Override
    public String getDeskripsi() {
        if (tingkatPedas == 0) {
            return "Tidak pedas";
        }
        return "Pedas level " + tingkatPedas;
    }
}
