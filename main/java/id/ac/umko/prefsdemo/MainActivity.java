package id.ac.umko.prefsdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class MainActivity extends Activity {

    private PrefsManager prefsManager;
    private CatatanDb db;

    private LinearLayout root;
    private TextView tvSapaan, tvDaftar, tvXml;
    private EditText etNama, etSandi, etCatatan;
    private Switch swGelap;
    private RadioGroup rgFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = new PrefsManager(this);
        db = new CatatanDb(this);

        root = (LinearLayout) findViewById(R.id.root);
        tvSapaan = (TextView) findViewById(R.id.tvSapaan);
        tvDaftar = (TextView) findViewById(R.id.tvDaftar);
        tvXml = (TextView) findViewById(R.id.tvXml);
        etNama = (EditText) findViewById(R.id.etNama);
        etSandi = (EditText) findViewById(R.id.etSandi);
        etCatatan = (EditText) findViewById(R.id.etCatatan);
        swGelap = (Switch) findViewById(R.id.swGelap);
        rgFont = (RadioGroup) findViewById(R.id.rgFont);

        // Simpan pengaturan -> SharedPreferences (lewat PrefsManager, pakai apply())
        findViewById(R.id.btnSimpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int font = 1;
                int id = rgFont.getCheckedRadioButtonId();
                if (id == R.id.rbKecil) font = 0;
                else if (id == R.id.rbBesar) font = 2;
                prefsManager.simpan(etNama.getText().toString().trim(),
                        swGelap.isChecked(), font);
                // Kata sandi SENGAJA tidak disimpan di mana pun.
                etSandi.setText("");
                Toast.makeText(MainActivity.this, "Pengaturan disimpan", Toast.LENGTH_SHORT).show();
                terapkanPengaturan();
            }
        });

        // Tambah catatan -> database SQLite
        findViewById(R.id.btnTambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isi = etCatatan.getText().toString().trim();
                if (isi.length() == 0) return;
                db.tambah(isi);
                etCatatan.setText("");
                muatCatatan();
            }
        });

        findViewById(R.id.btnHapus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.hapusSemua();
                muatCatatan();
            }
        });

        // Bukti: tampilkan isi file XML SharedPreferences
        findViewById(R.id.btnXml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvXml.setText(bacaFileXml());
                tvXml.setVisibility(View.VISIBLE);
            }
        });
    }

    /** Dibaca setiap Activity tampil lagi, jadi tampilan selalu sesuai pengaturan terakhir. */
    @Override
    protected void onResume() {
        super.onResume();
        etNama.setText(prefsManager.getNama());
        swGelap.setChecked(prefsManager.isTemaGelap());
        int f = prefsManager.getUkuranFont();
        rgFont.check(f == 0 ? R.id.rbKecil : f == 2 ? R.id.rbBesar : R.id.rbSedang);
        terapkanPengaturan();
        muatCatatan();
    }

    private void terapkanPengaturan() {
        boolean gelap = prefsManager.isTemaGelap();
        int[] ukuran = {13, 16, 20};
        int base = ukuran[prefsManager.getUkuranFont()];

        int bg = gelap ? Color.parseColor("#121212") : Color.WHITE;
        int fg = gelap ? Color.parseColor("#EEEEEE") : Color.parseColor("#212121");
        root.setBackgroundColor(bg);
        gayai(root, fg, base);

        String nama = prefsManager.getNama();
        tvSapaan.setText(nama.length() == 0 ? "Halo!" : "Halo, " + nama + "!");
        tvSapaan.setTextSize(base + 8);
    }

    /** Terapkan warna & ukuran font ke semua teks (kecuali tombol biasa). */
    private void gayai(View v, int warna, int ukuran) {
        if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            for (int i = 0; i < g.getChildCount(); i++) gayai(g.getChildAt(i), warna, ukuran);
        }
        boolean tombolBiasa = (v instanceof Button) && !(v instanceof CompoundButton);
        if (v instanceof TextView && !tombolBiasa) {
            TextView t = (TextView) v;
            t.setTextColor(warna);
            t.setHintTextColor(Color.GRAY);
            t.setTextSize(ukuran);
        }
    }

    private void muatCatatan() {
        List<String> daftar = db.semua();
        if (daftar.isEmpty()) {
            tvDaftar.setText("(belum ada catatan)");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : daftar) sb.append("• ").append(s).append("\n");
            tvDaftar.setText(sb.toString().trim());
        }
    }

    private String bacaFileXml() {
        File f = new File(getApplicationInfo().dataDir + "/shared_prefs/" + PrefsManager.FILE + ".xml");
        if (!f.exists()) return "(file belum ada - tekan Simpan dulu)";
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            br.close();
        } catch (Exception e) {
            return "Gagal membaca: " + e.getMessage();
        }
        return "shared_prefs/" + PrefsManager.FILE + ".xml\n" + sb.toString();
    }
}
