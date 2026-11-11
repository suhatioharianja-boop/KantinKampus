package kantinkampus;

import java.util.ArrayList;

/**
 * Satu stand/tenant di kantin beserta daftar menunya.
 */
public class Tenant {

    private int idTenant;
    private String namaTenant;
    private ArrayList<Menu> daftarMenu = new ArrayList<>();

    public Tenant(int idTenant, String namaTenant) {
        this.idTenant = idTenant;
        this.namaTenant = namaTenant;
    }

    public int getIdTenant() {
        return idTenant;
    }

    public String getNamaTenant() {
        return namaTenant;
    }

    public ArrayList<Menu> getDaftarMenu() {
        return daftarMenu;
    }

    public void tambahMenu(Menu menu) {
        daftarMenu.add(menu);
    }

    // Mengembalikan true jika menu ditemukan dan berhasil dihapus
    public boolean hapusMenu(int idMenu) {
        Menu menu = cariMenu(idMenu);
        if (menu == null) {
            return false;
        }
        daftarMenu.remove(menu);
        return true;
    }

    // Mengembalikan null jika tidak ditemukan
    public Menu cariMenu(int idMenu) {
        for (Menu m : daftarMenu) {
            if (m.getIdMenu() == idMenu) {
                return m;
            }
        }
        return null;
    }
}
