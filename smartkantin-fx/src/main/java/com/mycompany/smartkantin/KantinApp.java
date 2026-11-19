package com.mycompany.smartkantin;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KantinApp extends Application {

    // ====== DATA ======
    private ObservableList<Menu> daftarMenu = FXCollections.observableArrayList();
    private HashMap<String, Double> omset = new HashMap<>();
    private LinkedHashMap<Menu, Integer> keranjang = new LinkedHashMap<>();

    // ====== KOMPONEN UI YANG PERLU DIAKSES ULANG ======
    private ListView<String> listKeranjang = new ListView<>();
    private Label lblTotal = new Label("TOTAL: Rp0");
    private ObservableList<Map.Entry<String, Double>> dataOmset = FXCollections.observableArrayList();
    private TextArea areaLog = new TextArea();

    @Override
    public void start(Stage stage) {
        muatData();

        TabPane tabs = new TabPane(tabPesan(), tabKelolaMenu(), tabLaporan());
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        stage.setTitle("SmartKantin");
        stage.setScene(new Scene(tabs, 900, 560));
        stage.show();
    }

    // Dipanggil otomatis saat jendela ditutup -> data tersimpan
    @Override
    public void stop() {
        simpanData();
    }

    // ================= FILE I/O =================
    private void muatData() {
        ArrayList<Menu> sementara = new ArrayList<>();
        try {
            PenyimpananData.muat(sementara, omset);
        } catch (IOException | ClassNotFoundException e) {
            pesan(Alert.AlertType.ERROR, "Gagal memuat data: " + e.getMessage());
        }
        if (sementara.isEmpty()) { // data awal contoh
            sementara.add(new Makanan("Nasi Goreng", "Kantin A", 15000));
            sementara.add(new Makanan("Mie Ayam", "Kantin B", 12000));
            sementara.add(new Minuman("Es Teh", "Kantin A", 4000));
        }
        daftarMenu.addAll(sementara);
    }

    private void simpanData() {
        try {
            PenyimpananData.simpan(new ArrayList<>(daftarMenu), omset);
        } catch (IOException e) {
            pesan(Alert.AlertType.ERROR, "Gagal menyimpan data: " + e.getMessage());
        }
    }

    // ================= TAB 1: PESAN =================
    private Tab tabPesan() {
        TableView<Menu> tabel = buatTabelMenu();

        Spinner<Integer> spJumlah = new Spinner<>(1, 99, 1);
        spJumlah.setEditable(true);
        Button btnTambah = new Button("Tambah ke Keranjang");
        TextField tfUang = new TextField();
        tfUang.setPromptText("Uang dibayar");
        Button btnBayar = new Button("Bayar");
        Button btnKosongkan = new Button("Kosongkan");

        btnTambah.setOnAction(e -> {
            Menu dipilih = tabel.getSelectionModel().getSelectedItem();
            if (dipilih == null) {
                pesan(Alert.AlertType.WARNING, "Pilih menu dulu!");
                return;
            }
            keranjang.merge(dipilih, spJumlah.getValue(), Integer::sum);
            refreshKeranjang();
        });

        btnKosongkan.setOnAction(e -> {
            keranjang.clear();
            refreshKeranjang();
        });

        btnBayar.setOnAction(e -> {
            if (keranjang.isEmpty()) {
                pesan(Alert.AlertType.WARNING, "Keranjang kosong!");
                return;
            }
            double uang;
            try {
                uang = Double.parseDouble(tfUang.getText().trim());
            } catch (NumberFormatException ex) {
                pesan(Alert.AlertType.ERROR, "Uang harus berupa angka!");
                return;
            }
            double total = hitungTotal();
            if (uang < total) {
                pesan(Alert.AlertType.ERROR, "Uang kurang!");
                return;
            }

            // susun struk + update omset per tenant
            StringBuilder struk = new StringBuilder();
            struk.append("=== ")
                 .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                 .append(" ===\n");
            for (Map.Entry<Menu, Integer> en : keranjang.entrySet()) {
                Menu m = en.getKey();
                double sub = m.getHarga() * en.getValue();
                omset.merge(m.getTenant(), sub, Double::sum);
                struk.append(String.format("[%s] %s x%d = %s%n", m.getTenant(), m.getNama(), en.getValue(), rp(sub)));
            }
            struk.append("TOTAL: ").append(rp(total)).append("\n");

            try {
                PenyimpananData.catatLog(struk.toString());
            } catch (IOException ex) {
                pesan(Alert.AlertType.ERROR, "Gagal menulis log: " + ex.getMessage());
            }

            keranjang.clear();
            tfUang.clear();
            refreshKeranjang();
            refreshLaporan();
            simpanData();
            pesan(Alert.AlertType.INFORMATION, "Pembayaran berhasil!\nKembalian: " + rp(uang - total));
        });

        HBox barisTambah = new HBox(8, new Label("Jumlah:"), spJumlah, btnTambah);
        HBox barisBayar = new HBox(8, tfUang, btnBayar, btnKosongkan);

        VBox kanan = new VBox(10, new Label("Keranjang"), listKeranjang, lblTotal, barisBayar);
        kanan.setPadding(new Insets(0, 0, 0, 10));
        kanan.setPrefWidth(380);
        VBox.setVgrow(listKeranjang, Priority.ALWAYS);

        VBox kiri = new VBox(10, new Label("Daftar Menu"), tabel, barisTambah);
        VBox.setVgrow(tabel, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setCenter(kiri);
        root.setRight(kanan);

        Tab tab = new Tab("Pesan", root);
        return tab;
    }

    // ================= TAB 2: KELOLA MENU =================
    private Tab tabKelolaMenu() {
        TableView<Menu> tabel = buatTabelMenu();

        TextField tfNama = new TextField();
        tfNama.setPromptText("Nama menu");
        TextField tfTenant = new TextField();
        tfTenant.setPromptText("Nama tenant");
        TextField tfHarga = new TextField();
        tfHarga.setPromptText("Harga");
        ComboBox<String> cbJenis = new ComboBox<>(FXCollections.observableArrayList("Makanan", "Minuman"));
        cbJenis.setValue("Makanan");
        Button btnTambah = new Button("Tambah Menu");
        Button btnHapus = new Button("Hapus Terpilih");

        btnTambah.setOnAction(e -> {
            String nama = tfNama.getText().trim();
            String tenant = tfTenant.getText().trim();
            if (nama.isEmpty() || tenant.isEmpty()) {
                pesan(Alert.AlertType.WARNING, "Nama menu dan tenant wajib diisi!");
                return;
            }
            try {
                double harga = Double.parseDouble(tfHarga.getText().trim());
                Menu baru = cbJenis.getValue().equals("Makanan")
                        ? new Makanan(nama, tenant, harga)
                        : new Minuman(nama, tenant, harga);
                daftarMenu.add(baru);
                simpanData();
                tfNama.clear();
                tfTenant.clear();
                tfHarga.clear();
            } catch (NumberFormatException ex) {
                pesan(Alert.AlertType.ERROR, "Harga harus berupa angka!");
            } catch (IllegalArgumentException ex) {
                pesan(Alert.AlertType.ERROR, ex.getMessage());
            }
        });

        btnHapus.setOnAction(e -> {
            Menu dipilih = tabel.getSelectionModel().getSelectedItem();
            if (dipilih == null) {
                pesan(Alert.AlertType.WARNING, "Pilih menu yang mau dihapus!");
                return;
            }
            daftarMenu.remove(dipilih);
            keranjang.remove(dipilih);
            refreshKeranjang();
            simpanData();
        });

        HBox form = new HBox(8, tfNama, tfTenant, tfHarga, cbJenis, btnTambah);
        VBox root = new VBox(10, new Label("Kelola Menu"), tabel, form, btnHapus);
        root.setPadding(new Insets(12));
        VBox.setVgrow(tabel, Priority.ALWAYS);

        return new Tab("Kelola Menu", root);
    }

    // ================= TAB 3: LAPORAN =================
    private Tab tabLaporan() {
        TableView<Map.Entry<String, Double>> tabelOmset = new TableView<>(dataOmset);
        TableColumn<Map.Entry<String, Double>, String> cTenant = new TableColumn<>("Tenant");
        cTenant.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getKey()));
        TableColumn<Map.Entry<String, Double>, String> cOmset = new TableColumn<>("Omset");
        cOmset.setCellValueFactory(d -> new SimpleStringProperty(rp(d.getValue().getValue())));
        tabelOmset.getColumns().addAll(cTenant, cOmset);
        tabelOmset.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        areaLog.setEditable(false);

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshLaporan());

        VBox root = new VBox(10,
                new Label("Omset per Tenant"), tabelOmset,
                new Label("Riwayat Transaksi (log_transaksi.txt)"), areaLog,
                btnRefresh);
        root.setPadding(new Insets(12));
        VBox.setVgrow(areaLog, Priority.ALWAYS);

        refreshLaporan();
        return new Tab("Laporan", root);
    }

    // ================= HELPER =================
    private TableView<Menu> buatTabelMenu() {
        TableView<Menu> tabel = new TableView<>(daftarMenu);

        TableColumn<Menu, String> cTenant = new TableColumn<>("Tenant");
        cTenant.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTenant()));
        TableColumn<Menu, String> cNama = new TableColumn<>("Nama");
        cNama.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNama()));
        TableColumn<Menu, String> cJenis = new TableColumn<>("Jenis");
        cJenis.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().jenis()));
        TableColumn<Menu, String> cHarga = new TableColumn<>("Harga");
        cHarga.setCellValueFactory(d -> new SimpleStringProperty(rp(d.getValue().getHarga())));

        tabel.getColumns().addAll(cTenant, cNama, cJenis, cHarga);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tabel;
    }

    private double hitungTotal() {
        double total = 0;
        for (Map.Entry<Menu, Integer> e : keranjang.entrySet())
            total += e.getKey().getHarga() * e.getValue();
        return total;
    }

    private void refreshKeranjang() {
        listKeranjang.getItems().clear();
        for (Map.Entry<Menu, Integer> e : keranjang.entrySet()) {
            Menu m = e.getKey();
            listKeranjang.getItems().add(String.format("[%s] %s x%d = %s",
                    m.getTenant(), m.getNama(), e.getValue(), rp(m.getHarga() * e.getValue())));
        }
        lblTotal.setText("TOTAL: " + rp(hitungTotal()));
    }

    private void refreshLaporan() {
        dataOmset.setAll(omset.entrySet());
        areaLog.setText(PenyimpananData.bacaLog());
    }

    private String rp(double angka) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp%,.0f", angka);
    }

    private void pesan(Alert.AlertType tipe, String isi) {
        Alert a = new Alert(tipe, isi);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
