package kantinkampus;

import java.util.Locale;

/**
 * Kumpulan method bantu yang dipakai di banyak kelas.
 */
public class Utilitas {

    // Mengubah angka menjadi format rupiah, contoh: 15000 -> Rp15.000
    public static String rupiah(double nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp%,.0f", nilai);
    }

    // Membuat garis pemisah
    public static String garis(char karakter, int panjang) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < panjang; i++) {
            sb.append(karakter);
        }
        return sb.toString();
    }
}
