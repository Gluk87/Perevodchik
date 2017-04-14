package forlife.yaperevod;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

//Класс по управлению нашей БД
public class DB {
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "dbhistory"; //таблица с Историей
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FROM = "db_textfrom";
    public static final String COLUMN_TO = "db_texttu";
    public static final String COLUMN_LANG = "db_lang";
    public static final String COLUMN_FAV = "db_favor";
    public static final String COLUMN_DAT = "db_dat";

    private static final String DB_FAV_TABLE = "dbfavor"; //таблица с Избранным
    public static final String COLUMN_FAV_ID = "_id";
    public static final String COLUMN_FAV_FROM = "db_textfrom";
    public static final String COLUMN_FAV_TO = "db_texttu";
    public static final String COLUMN_FAV_LANG = "db_lang";
    public static final String COLUMN_FAV_DAT = "db_dat";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_FROM + " text, " +
                    COLUMN_TO + " text, " +
                    COLUMN_LANG + " text, " +
                    COLUMN_FAV + " integer, " +
                    COLUMN_DAT + " date" +
                    ");";

    private static final String DB_FAV_CREATE =
            "create table " + DB_FAV_TABLE + "(" +
                    COLUMN_FAV_ID + " integer primary key autoincrement, " +
                    COLUMN_FAV_FROM + " text, " +
                    COLUMN_FAV_TO + " text, " +
                    COLUMN_FAV_LANG + " text, " +
                    COLUMN_FAV_DAT + " date" +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE с обратной сортировкой
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, COLUMN_ID + " desc");
    }

    // получить все данные из таблицы DB_FAV_TABLE с обратной сортировкой
    public Cursor getAllDataFav() {
        return mDB.query(DB_FAV_TABLE, null, null, null, null, null, COLUMN_ID + " desc");
    }

    // добавить запись в DB_TABLE
    public void addRec(String txt1, String txt2, String txt3, int img) {
        ContentValues cv = new ContentValues();

        long date = System.currentTimeMillis();

        cv.put(COLUMN_FROM, txt1);
        cv.put(COLUMN_TO, txt2);
        cv.put(COLUMN_LANG, txt3);
        cv.put(COLUMN_FAV, img);
        cv.put(COLUMN_DAT, date);
        mDB.insert(DB_TABLE, null, cv);

        //удаляем повторяющиеся записи
        mDB.delete(DB_TABLE, COLUMN_FROM + " = '" + txt1 + "' and " + COLUMN_TO + " = '" + txt2 + "' and " +
                   COLUMN_LANG + " = '" + txt3 + "' and " + COLUMN_DAT + " < " + date, null);

    }

    // добавить запись в DB_FAV_TABLE
    public void addRecFav(String txt1, String txt2, String txt3) {
        ContentValues cv = new ContentValues();
        long date = System.currentTimeMillis();

        cv.put(COLUMN_FAV_FROM, txt1);
        cv.put(COLUMN_FAV_TO, txt2);
        cv.put(COLUMN_FAV_LANG, txt3);
        cv.put(COLUMN_FAV_DAT, date);
        mDB.insert(DB_FAV_TABLE, null, cv);

        //удаляем повторяющиеся записи
        mDB.delete(DB_FAV_TABLE, COLUMN_FAV_FROM + " = '" + txt1 + "' and " + COLUMN_FAV_TO + " = '" + txt2 + "' and " +
                COLUMN_FAV_LANG + " = '" + txt3 + "' and " + COLUMN_FAV_DAT + " < " + date, null);

    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // удалить запись из DB_TABLE
    public void delRecFav(long id) {
        mDB.delete(DB_FAV_TABLE, COLUMN_ID + " = " + id, null);
    }

    //чистим таблицу DB_TABLE
    public void trunc_table() {mDB.execSQL("delete from "+DB_TABLE+";"); }

    //чистим таблицу DB_FAV_TABLE
    public void trunc_fav_table() {mDB.execSQL("delete from "+DB_FAV_TABLE+";"); }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            db.execSQL(DB_FAV_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
