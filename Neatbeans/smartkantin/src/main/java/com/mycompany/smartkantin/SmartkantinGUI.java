package com.mycompany.smartkantin;

import static com.mycompany.smartkantin.Smartkantin.*;

import java.awt.*;
import java.util.Map;
import javax.swing.*;

// GUI sederhana (Swing) - memakai class & data dari Smartkantin.java
public class SmartkantinGUI extends JFrame {

    private final DefaultListModel<String> modelMenu = new DefaultListModel<>();
    private final DefaultListModel<String> modelKeranjang = new DefaultListModel<>();
    private final JList<String> listMenu = new JList<>(modelMenu);
    private final JLabel lblTotal = new JLabel("TOTAL: Rp0");
    private final JTextArea areaOmset = new JTextArea();

    public SmartkantinGUI() {
        super("Smart Kantin");

        // Data awal
        menu.add(new Makanan("Ayam Geprek", "Geprek Pak Eko", 15000));
        menu.add(new Minuman("Es Teh", "Geprek Pak Eko", 4000));
        menu.add(new Makanan("Bakso", "Bakso Bu Ani", 13000));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pesan", panelPesan());
        tabs.addTab("Tambah Menu", panelTambah());
        tabs.addTab("Rekap Omset", panelOmset());
        tabs.addChangeListener(e -> refreshOmset());

        add(tabs);
        refreshMenu();
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ---------- TAB PESAN ----------
    private JPanel panelPesan() {
        JTextField tfJumlah = new JTextField("1", 5);
        JButton btnPesan = new JButton("Tambah ke Keranjang");
        JTextField tfUang = new JTextField(10);
        JButton btnBayar = new JButton("Bayar");
        JList<String> listKeranjang = new JList<>(modelKeranjang);

        btnPesan.addActionListener(e -> {
            int idx = listMenu.getSelectedIndex();
            if (idx < 0) {
                pesan("Pilih menu dulu!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int jml = Integer.parseInt(tfJumlah.getText().trim());
                if (jml < 1) {
                    pesan("Jumlah minimal 1!", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                keranjang.merge(menu.get(idx), jml, Integer::sum);
                tfJumlah.setText("1");
                refreshKeranjang();
            } catch (NumberFormatException ex) {
                pesan("Jumlah harus berupa angka!", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBayar.addActionListener(e -> {
            if (keranjang.isEmpty()) {
                pesan("Keranjang kosong!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (tfUang.getText().trim().isEmpty()) {
                pesan("Kolom uang belum diisi!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double uang = Double.parseDouble(tfUang.getText().trim());
                double total = hitungTotal();
                if (uang < total) {
                    pesan("Uang kurang!", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                for (Map.Entry<Menu, Integer> en : keranjang.entrySet())
                    omset.merge(en.getKey().getTenant(), en.getKey().getHarga() * en.getValue(), Double::sum);
                pesan("Berhasil! Kembalian: " + rp(uang - total), JOptionPane.INFORMATION_MESSAGE);
                keranjang.clear();
                tfUang.setText("");
                refreshKeranjang();
            } catch (NumberFormatException ex) {
                pesan("Uang harus berupa angka!", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel barisPesan = new JPanel();
        barisPesan.add(new JLabel("Jumlah:"));
        barisPesan.add(tfJumlah);
        barisPesan.add(btnPesan);

        JPanel barisBayar = new JPanel();
        barisBayar.add(new JLabel("Uang:"));
        barisBayar.add(tfUang);
        barisBayar.add(btnBayar);

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(new JLabel("Daftar Menu (pilih satu):"), BorderLayout.NORTH);
        atas.add(new JScrollPane(listMenu), BorderLayout.CENTER);
        atas.add(barisPesan, BorderLayout.SOUTH);

        JPanel tengah = new JPanel(new BorderLayout());
        tengah.add(new JLabel("Keranjang:"), BorderLayout.NORTH);
        tengah.add(new JScrollPane(listKeranjang), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new GridLayout(2, 1));
        bawah.add(lblTotal);
        bawah.add(barisBayar);

        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.add(atas);
        JPanel bagianBawah = new JPanel(new BorderLayout());
        bagianBawah.add(tengah, BorderLayout.CENTER);
        bagianBawah.add(bawah, BorderLayout.SOUTH);
        panel.add(bagianBawah);
        return panel;
    }

    // ---------- TAB TAMBAH MENU ----------
    private JPanel panelTambah() {
        JTextField tfNama = new JTextField();
        JTextField tfTenant = new JTextField();
        JTextField tfHarga = new JTextField();
        JComboBox<String> cbJenis = new JComboBox<>(new String[]{"Makanan", "Minuman"});
        JButton btnSimpan = new JButton("Simpan");

        btnSimpan.addActionListener(e -> {
            String nama = tfNama.getText().trim();
            String tenant = tfTenant.getText().trim();
            String hargaStr = tfHarga.getText().trim();

            if (nama.isEmpty() || tenant.isEmpty() || hargaStr.isEmpty()) {
                pesan("Semua kolom harus diisi!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double harga = Double.parseDouble(hargaStr);
                boolean makanan = cbJenis.getSelectedItem().equals("Makanan");
                menu.add(makanan ? new Makanan(nama, tenant, harga) : new Minuman(nama, tenant, harga));
                refreshMenu();
                tfNama.setText("");
                tfTenant.setText("");
                tfHarga.setText("");
                pesan("Menu berhasil ditambahkan!", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                pesan("Harga harus berupa angka!", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) { // dari setHarga()
                pesan(ex.getMessage(), JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("Nama menu:"));   form.add(tfNama);
        form.add(new JLabel("Nama tenant:")); form.add(tfTenant);
        form.add(new JLabel("Harga:"));       form.add(tfHarga);
        form.add(new JLabel("Jenis:"));       form.add(cbJenis);
        form.add(new JLabel(""));             form.add(btnSimpan);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    // ---------- TAB OMSET ----------
    private JPanel panelOmset() {
        areaOmset.setEditable(false);
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshOmset());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(areaOmset), BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- HELPER ----------
    private void refreshMenu() {
        modelMenu.clear();
        for (int i = 0; i < menu.size(); i++) {
            Menu m = menu.get(i);
            modelMenu.addElement(String.format("[%s] %s (%s) %s",
                    m.getTenant(), m.getNama(), m.jenis(), rp(m.getHarga())));
        }
    }

    private void refreshKeranjang() {
        modelKeranjang.clear();
        for (Map.Entry<Menu, Integer> en : keranjang.entrySet()) {
            Menu m = en.getKey();
            modelKeranjang.addElement(String.format("[%s] %s x%d = %s",
                    m.getTenant(), m.getNama(), en.getValue(), rp(m.getHarga() * en.getValue())));
        }
        lblTotal.setText("TOTAL: " + rp(hitungTotal()));
    }

    private void refreshOmset() {
        StringBuilder sb = new StringBuilder();
        if (omset.isEmpty()) sb.append("Belum ada transaksi.");
        for (String t : omset.keySet()) sb.append(t).append(": ").append(rp(omset.get(t))).append("\n");
        areaOmset.setText(sb.toString());
    }

    private double hitungTotal() {
        double total = 0;
        for (Map.Entry<Menu, Integer> en : keranjang.entrySet())
            total += en.getKey().getHarga() * en.getValue();
        return total;
    }

    private String rp(double nilai) {
        return String.format("Rp%,.0f", nilai);
    }

    private void pesan(String teks, int tipe) {
        JOptionPane.showMessageDialog(this, teks, "Smart Kantin", tipe);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartkantinGUI().setVisible(true));
    }
}
