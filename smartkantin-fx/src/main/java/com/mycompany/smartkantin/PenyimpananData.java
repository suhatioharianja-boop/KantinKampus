package com.mycompany.smartkantin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// Semua urusan File I/O dikumpulkan di sini (Pertemuan 11)
public class PenyimpananData {
    static final String FILE_DATA = "data_kantin.dat";      // file biner (serialization)
    static final String FILE_LOG = "log_transaksi.txt";     // file teks (FileWriter + Scanner)

    // SERIALIZATION: simpan daftar menu + omset ke file .dat
    public static void simpan(ArrayList<Menu> menu, HashMap<String, Double> omset) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_DATA))) {
            out.writeObject(menu);
            out.writeObject(omset);
        }
    }

    // DESERIALIZATION: baca file .dat dan isi kembali menu + omset
    @SuppressWarnings("unchecked")
    public static void muat(ArrayList<Menu> menu, HashMap<String, Double> omset)
            throws IOException, ClassNotFoundException {
        File f = new File(FILE_DATA);
        if (!f.exists()) return; // pertama kali dijalankan, belum ada data
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            menu.addAll((ArrayList<Menu>) in.readObject());
            omset.putAll((HashMap<String, Double>) in.readObject());
        }
    }

    // FILE TEKS (tulis): tambahkan struk ke log_transaksi.txt (true = append)
    public static void catatLog(String teks) throws IOException {
        try (FileWriter tulis = new FileWriter(FILE_LOG, true)) {
            tulis.write(teks + "\n");
        }
    }

    // FILE TEKS (baca): baca seluruh isi log baris demi baris
    public static String bacaLog() {
        File f = new File(FILE_LOG);
        if (!f.exists()) return "Belum ada transaksi.";
        StringBuilder sb = new StringBuilder();
        try (Scanner baca = new Scanner(f)) {
            while (baca.hasNextLine()) sb.append(baca.nextLine()).append("\n");
        } catch (FileNotFoundException e) {
            return "Gagal: file log tidak ditemukan!";
        }
        return sb.toString();
    }
}
