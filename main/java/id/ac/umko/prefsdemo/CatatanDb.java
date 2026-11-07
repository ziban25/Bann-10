package id.ac.umko.prefsdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Riwayat kegiatan = data banyak baris, jadi disimpan di database SQLite. */
public class CatatanDb extends SQLiteOpenHelper {

    public CatatanDb(Context context) {
        super(context, "catatan.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE catatan (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "isi TEXT, waktu TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS catatan");
        onCreate(db);
    }

    public void tambah(String isi) {
        ContentValues v = new ContentValues();
        v.put("isi", isi);
        v.put("waktu", new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(new Date()));
        getWritableDatabase().insert("catatan", null, v);
    }

    public List<String> semua() {
        List<String> hasil = new ArrayList<String>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT waktu, isi FROM catatan ORDER BY id DESC", null);
        while (c.moveToNext()) {
            hasil.add(c.getString(0) + "  -  " + c.getString(1));
        }
        c.close();
        return hasil;
    }

    public void hapusSemua() {
        getWritableDatabase().delete("catatan", null, null);
    }
}
