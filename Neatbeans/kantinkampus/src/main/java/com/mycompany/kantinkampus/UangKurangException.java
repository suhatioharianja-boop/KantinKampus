package kantinkampus;

/**
 * Exception buatan sendiri: dilempar saat uang pembeli kurang dari total belanja.
 */
public class UangKurangException extends Exception {

    public UangKurangException(double total, double dibayar) {
        super("Uang kurang " + Utilitas.rupiah(total - dibayar)
                + " (total " + Utilitas.rupiah(total)
                + ", dibayar " + Utilitas.rupiah(dibayar) + ")");
    }
}
