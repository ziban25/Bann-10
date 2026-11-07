package id.ac.umko.prefsdemo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Kelas pembantu: semua kode SharedPreferences ada di sini,
 * jadi Activity tidak perlu mengulang prefs.edit()...apply().
 * File disimpan di: shared_prefs/catatan_prefs.xml
 */
public class PrefsManager {

    public static final String FILE = "catatan_prefs";
    private static final String KEY_NAMA = "nama_panggilan";
    private static final String KEY_TEMA_GELAP = "tema_gelap";
    private static final String KEY_FONT = "ukuran_font";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public String getNama() {
        return prefs.getString(KEY_NAMA, "");
    }

    public boolean isTemaGelap() {
        return prefs.getBoolean(KEY_TEMA_GELAP, false);
    }

    /** 0 = kecil, 1 = sedang, 2 = besar */
    public int getUkuranFont() {
        return prefs.getInt(KEY_FONT, 1);
    }

    /** apply() = tulis ke disk di thread latar, UI tidak menunggu. */
    public void simpan(String nama, boolean temaGelap, int ukuranFont) {
        prefs.edit()
                .putString(KEY_NAMA, nama)
                .putBoolean(KEY_TEMA_GELAP, temaGelap)
                .putInt(KEY_FONT, ukuranFont)
                .apply();
    }
}
