package kantinkampus;

import java.time.LocalDate;

/**
 * Satu transaksi pembayaran. Mengimplementasikan interface Cetak.
 */
public class Transaksi implements Cetak {

    private int idTransaksi;
    private LocalDate tanggal;
    private Pesanan pesanan;
    private double uangDibayar;
    private double kembalian;

    // Transaksi baru (tanggal hari ini)
    public Transaksi(int idTransaksi, Pesanan pesanan) {
        this.idTransaksi = idTransaksi;
        this.tanggal = LocalDate.now();
        this.pesanan = pesanan;
    }

    // Dipakai saat membaca data dari file
    public Transaksi(int idTransaksi, LocalDate tanggal, Pesanan pesanan,
            double uangDibayar, double kembalian) {
        this.idTransaksi = idTransaksi;
        this.tanggal = tanggal;
        this.pesanan = pesanan;
        this.uangDibayar = uangDibayar;
        this.kembalian = kembalian;
    }

    // Melempar UangKurangException jika uang kurang dari total
    public void prosesPembayaran(double uang) throws UangKurangException {
        double total = pesanan.hitungTotal();
        if (uang < total) {
            throw new UangKurangException(total, uang);
        }
        this.uangDibayar = uang;
        this.kembalian = hitungKembalian();
    }

    public double hitungKembalian() {
        return uangDibayar - pesanan.hitungTotal();
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public Pesanan getPesanan() {
        return pesanan;
    }

    public double getUangDibayar() {
        return uangDibayar;
    }

    public double getKembalian() {
        return kembalian;
    }

    @Override
    public String cetak() {
        String garis = Utilitas.garis('=', 56);
        StringBuilder sb = new StringBuilder();
        sb.append(garis).append("\n");
        sb.append("                  STRUK KANTIN KAMPUS\n");
        sb.append(garis).append("\n");
        sb.append(String.format("No. Transaksi : TRX-%04d%n", idTransaksi));
        sb.append("Tanggal       : ").append(tanggal).append("\n");
        sb.append(Utilitas.garis('-', 56)).append("\n");
        sb.append(pesanan.getRincian());
        sb.append(Utilitas.garis('-', 56)).append("\n");
        sb.append(String.format("%-44s %11s%n", "TOTAL", Utilitas.rupiah(pesanan.hitungTotal())));
        sb.append(String.format("%-44s %11s%n", "Dibayar", Utilitas.rupiah(uangDibayar)));
        sb.append(String.format("%-44s %11s%n", "Kembalian", Utilitas.rupiah(kembalian)));
        sb.append(garis).append("\n");
        sb.append("          Terima kasih sudah berbelanja!\n");
        sb.append(garis).append("\n");
        return sb.toString();
    }
}
