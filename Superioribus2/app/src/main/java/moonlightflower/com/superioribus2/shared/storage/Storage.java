package moonlightflower.com.superioribus2.shared.storage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import moonlightflower.com.superioribus2.shared.lang.Lang;
import moonlightflower.com.superioribus2.shared.ListenerAction;
import moonlightflower.com.superioribus2.shared.ListenerSet;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.vocab.VocabEntry;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class Storage {
    public static class StorageException extends Exception {
        public StorageException(Exception e) {
            super(e);
        }
    }

    private static class DataBase extends SQLiteOpenHelper {
        private static String escapeSQLName(String s) {
            return "[" + s + "]";
        }

        private static String[] escapeSQLName(String[] sArr) {
            String[] ret = new String[sArr.length];
            int c = 0;

            for (String s : sArr) {
                ret[c++] = escapeSQLName(s);
            }

            return ret;
        }

        private static Cursor SQLquery(SQLiteDatabase db, String table, String[] cols) {
            return db.query(false, escapeSQLName(table), escapeSQLName(cols), null, null, null, null, null, null);
        }

        public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private class SetHandler {
            private VocabSet _set;

            private SQLiteStatement _stmt_vocabs_createTable = null;
            private SQLiteStatement _stmt_vocabs_add = null;
            private SQLiteStatement _stmt_vocabs_remove = null;

            private final String VOCABS_TABLE;
            private final String VOCABS_COL_SOURCE = "source";
            private final String VOCABS_COL_TARGET = "target";

            public SetHandler(VocabSet set) {
                _set = set;

                VOCABS_TABLE = String.format("memo_%s_vocabs", _set.getName());
            }

            private void create() throws StorageException {
                try {
                    SQLiteDatabase db = getWritableDatabase();

                    if (_stmt_vocabs_createTable == null) {
                        _stmt_vocabs_createTable = db.compileStatement(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                                        "%s NVARCHAR NOT NULL," +
                                        "%s NVARCHAR NOT NULL)", escapeSQLName(VOCABS_TABLE),
                                escapeSQLName(VOCABS_COL_SOURCE), escapeSQLName(VOCABS_COL_TARGET)));
                    }

                    _stmt_vocabs_createTable.execute();
                } catch (SQLException e) {
                    throw new StorageException(e);
                }
            }

            private Set<VocabEntry> _vocabsInDB = new HashSet<>();

            private List<VocabEntry> getVocabs() throws StorageException {
                try {
                    create();

                    SQLiteDatabase db = getWritableDatabase();

                    Cursor c = SQLquery(db, VOCABS_TABLE, new String[]{VOCABS_COL_SOURCE, VOCABS_COL_TARGET});

                    if (c.getCount() > 0) {
                        c.moveToFirst();

                        List<VocabEntry> ret = new ArrayList<>();

                        do {
                            String source = c.getString(0);
                            String target = c.getString(1);

                            VocabEntry vocab = new VocabEntry(source, target);

                            ret.add(vocab);

                            _vocabsInDB.add(vocab);
                        } while (c.moveToNext());

                        //c.close();

                        return ret;
                    }

                    //c.close();
                } catch (Exception e) {
                    throw new StorageException(e);
                }

                return null;
            }

            private void addVocab(VocabEntry vocab) throws StorageException {
                try {
                    if (!_vocabsInDB.contains(vocab)) {
                        SQLiteDatabase db = getWritableDatabase();

                        create();

                        if (_stmt_vocabs_add == null)
                            _stmt_vocabs_add = db.compileStatement(String.format("INSERT INTO %s(%s, %s) VALUES (?, ?)", escapeSQLName(VOCABS_TABLE),
                                    escapeSQLName(VOCABS_COL_SOURCE), escapeSQLName(VOCABS_COL_TARGET)));

                        _stmt_vocabs_add.clearBindings();
                        _stmt_vocabs_add.bindString(1, vocab.getSource());
                        _stmt_vocabs_add.bindString(2, vocab.getTarget());

                        _stmt_vocabs_add.executeInsert();

                        _vocabsInDB.add(vocab);
                    }
                } catch (SQLException e) {
                    throw new StorageException(e);
                }
            }

            private void removeVocab(VocabEntry vocab) throws StorageException {
                try {
                    if (_vocabsInDB.contains(vocab)) {
                        SQLiteDatabase db = getWritableDatabase();

                        create();

                        if (_stmt_vocabs_remove == null)
                            _stmt_vocabs_remove = db.compileStatement(String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", escapeSQLName(VOCABS_TABLE), escapeSQLName(VOCABS_COL_SOURCE), escapeSQLName(VOCABS_COL_TARGET)));

                        _stmt_vocabs_remove.clearBindings();
                        _stmt_vocabs_remove.bindString(1, vocab.getSource());
                        _stmt_vocabs_remove.bindString(2, vocab.getTarget());

                        _stmt_vocabs_remove.executeUpdateDelete();

                        _vocabsInDB.remove(vocab);
                    }
                } catch (SQLException e) {
                    throw new StorageException(e);
                }
            }
        }

        private Map<VocabSet, SetHandler> _setHandlerMap = new HashMap<>();

        public SetHandler getSetHandler(VocabSet set) {
            if (!_setHandlerMap.containsKey(set)) _setHandlerMap.put(set, new SetHandler(set));

            return _setHandlerMap.get(set);
        }

        private SQLiteStatement _stmt_sets_createTable = null;
        private SQLiteStatement _stmt_sets_add = null;
        private SQLiteStatement _stmt_sets_remove = null;
        private SQLiteStatement _stmt_sets_dropTable = null;

        private final String SETS_TABLE = "vocabSets";
        private final String SETS_COL_ROW = "row";
        private final String SETS_COL_SOURCE_LANG = "sourceLang";
        private final String SETS_COL_TARGET_LANG = "targetLang";
        private final String SETS_COL_NAME = "name";

        private void create() throws StorageException {
            try {
                SQLiteDatabase db = getWritableDatabase();

                if (_stmt_sets_createTable == null) {
                    _stmt_sets_createTable = db.compileStatement(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                                    "%s INT UNSIGNED NOT NULL," +
                                    "%s NVARCHAR NOT NULL," +
                                    "%s NVARCHAR NOT NULL," +
                                    "%s NVARCHAR NOT NULL)", escapeSQLName(SETS_TABLE),
                            escapeSQLName(SETS_COL_ROW), escapeSQLName(SETS_COL_SOURCE_LANG), escapeSQLName(SETS_COL_TARGET_LANG), escapeSQLName(SETS_COL_NAME)));
                }

                _stmt_sets_createTable.execute();
            } catch (SQLException e) {
                throw new StorageException(e);
            }
        }

        private Map<VocabSet, Integer> _setRows = new HashMap<>();

        public List<VocabSet> getSets() throws StorageException {
            try {
                create();

                SQLiteDatabase db = getReadableDatabase();

                try {
                    Cursor c = SQLquery(db, SETS_TABLE, new String[]{SETS_COL_ROW, SETS_COL_SOURCE_LANG, SETS_COL_TARGET_LANG, SETS_COL_NAME});

                    if (c.getCount() > 0) {
                        c.moveToFirst();

                        List<VocabSet> ret = new ArrayList<>();

                        do {
                            int row = c.getInt(0);
                            String sourceLangS = c.getString(1);
                            String targetLangS = c.getString(2);
                            String name = c.getString(3);

                            Lang sourceLang = Lang.getFromShort(sourceLangS);
                            Lang targetLang = Lang.getFromShort(targetLangS);

                            if (sourceLang == null) continue;
                            if (targetLang == null) continue;

                            VocabSet set = new VocabSet(sourceLang, targetLang, name);

                            ret.add(set);

                            _setRows.put(set, row);
                            _setsInDB.add(set);

                            List<VocabEntry> vocabs = getSetHandler(set).getVocabs();

                            if (vocabs != null) {
                                for (VocabEntry vocab : vocabs) {
                                    set.add(vocab);
                                }
                            }
                        } while (c.moveToNext());

                        return ret;
                    }

                    //c.close();
                } catch (SQLException e) {
                    throw new StorageException(e);
                }
            } catch (SQLException e) {
                throw new StorageException(e);
            }

            return null;
        }

        private Set<VocabSet> _setsInDB = new HashSet<>();

        public void addSet(final VocabSet set) throws StorageException {
            try {
                if (!_setsInDB.contains(set)) {
                    SQLiteDatabase db = getWritableDatabase();

                    try {
                        Log.e(getClass().getSimpleName(), "insertSetA " + set);

                        Cursor countCursor = db.rawQuery(String.format("SELECT (%s) from %s ORDER BY %s ASC;", escapeSQLName(SETS_COL_ROW), escapeSQLName(SETS_TABLE), escapeSQLName(SETS_COL_ROW)), null);

                        int row = 0;

                        if (countCursor.getCount() > 0) {
                            countCursor.moveToFirst();

                            do {
                                if (countCursor.getInt(0) != row) break;

                                row++;
                            } while (countCursor.moveToNext());
                        }

                        //countCursor.close();

                        Log.e(getClass().getSimpleName(), "insertSetB " + set);

                        if (_stmt_sets_add == null)
                            _stmt_sets_add = db.compileStatement(String.format("INSERT INTO %s(row, sourceLang, targetLang, name) VALUES (?, ?, ?, ?)", escapeSQLName(SETS_TABLE)));

                        _stmt_sets_add.clearBindings();
                        _stmt_sets_add.bindLong(1, row);
                        _stmt_sets_add.bindString(2, set.getSourceLang().getShortName());
                        _stmt_sets_add.bindString(3, set.getTargetLang().getShortName());
                        _stmt_sets_add.bindString(4, set.getName());

                        _stmt_sets_add.executeInsert();

                        Log.e(getClass().getSimpleName(), "insertSetC " + set);

                        _setRows.put(set, row);
                        _setsInDB.add(set);

                        for (VocabEntry vocab : set.getEntries()) {
                            getSetHandler(set).addVocab(vocab);
                        }
                    } catch (SQLException e) {
                        throw new StorageException(e);
                    }
                }
            } catch (SQLException e) {
                throw new StorageException(e);
            }
        }

        public void removeSet(VocabSet set) throws StorageException {
            try {
                if (_setsInDB.contains(set)) {
                    SQLiteDatabase db = getWritableDatabase();

                    try {
                        if (!_setRows.containsKey(set))
                            throw new StorageException(new Exception("set " + set + "has no associated row"));

                        int row = _setRows.get(set);

                        if (_stmt_sets_remove == null)
                            _stmt_sets_remove = db.compileStatement(String.format("DELETE FROM %s WHERE row = ?", escapeSQLName(SETS_TABLE)));

                        _stmt_sets_remove.clearBindings();
                        _stmt_sets_remove.bindLong(1, row);

                        _stmt_sets_remove.executeUpdateDelete();

                        _setRows.remove(set);
                        _setsInDB.remove(set);
                    } catch (SQLException e) {
                        throw new StorageException(e);
                    }
                }
            } catch (SQLException e) {
                throw new StorageException(e);
            }
        }

        public synchronized void clear() throws StorageException {
            try {
                create();

                if (_stmt_sets_dropTable == null)
                    _stmt_sets_dropTable = getWritableDatabase().compileStatement(String.format("DROP TABLE %s", escapeSQLName(SETS_TABLE)));

                _stmt_sets_dropTable.execute();

                _setsInDB.clear();
                _setRows.clear();
            } catch (SQLException e) {
                throw e;
            }
        }

        private Context _context;

        public DataBase(Context context, String name, int version) {
            super(context, name, null, version);

            _context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    private DataBase _db;

    private List<VocabSet> _vocabSets = new ArrayList<>();

    public List<VocabSet> getSets() {
        return _vocabSets;
    }

    private Context _context;
    private boolean _initing = false;

    public void addSet(final VocabSet set) throws StorageException {
        if (!_initing) {
            SQLiteDatabase db = _db.getWritableDatabase();

            _db.addSet(set);

            //db.close();
        }

        if (!_vocabSets.contains(set)) {
            _vocabSets.add(set);

            for (Listener listener : _listeners) {
                listener.onAdd(set);
            }
        }

        set.addListener(new VocabSet.Listener() {
            @Override
            public void onAdd(VocabEntry val) {
                SQLiteDatabase db = _db.getWritableDatabase();

                try {
                    _db.getSetHandler(set).addVocab(val);
                } catch (StorageException e) {
                    e.printStackTrace();
                }

                //db.close();
            }

            @Override
            public void onRemove(VocabEntry val) {
                SQLiteDatabase db = _db.getWritableDatabase();

                try {
                    _db.getSetHandler(set).removeVocab(val);
                } catch (StorageException e) {
                    e.printStackTrace();
                }

                //db.close();
            }
        });
    }

    public void removeSet(VocabSet val) throws Exception {
        if (_vocabSets.contains(val)) {
            _vocabSets.remove(val);

            for (Listener listener : _listeners) {
                listener.onRemove(val);
            }
        }

        SQLiteDatabase db = _db.getWritableDatabase();

        _db.removeSet(val);

        //db.close();
    }

    private Map<Uri, Bitmap> _imgMap = new HashMap<>();

    public static class LoadBitmapTask extends AsyncTask<Void, Void, Object> {
        private Context _context;
        private URL _url;

        private interface Listener {
            void fail(Exception e);

            void ready(Bitmap bitmap);
        }

        private LoadBitmapTask.Listener _listener;

        @Override
        protected Object doInBackground(Void... params) {
            try {
                return BitmapFactory.decodeStream(_url.openConnection().getInputStream());
            } catch (IOException e) {
                Util.printException(_context, e);

                return e;
            }
        }

        @Override
        public void onPostExecute(Object val) {
            if (val instanceof Exception) {
                _listener.fail((Exception) val);
            } else {
                _listener.ready((Bitmap) val);
            }
        }

        private LoadBitmapTask(Context context, URL url, LoadBitmapTask.Listener listener) {
            _context = context;
            _url = url;
            _listener = listener;
        }
    }

    public interface ImgListener {
        void fail(Exception exception);

        void ready(Bitmap bitmap);
    }

    private SQLiteStatement _stmt_addImg;

    public Uri getUriFromUrl(URL url) {
        Uri.Builder builder = new Uri.Builder()
                .scheme(url.getProtocol())
                .authority(url.getAuthority())
                .appendPath(url.getPath());

        return builder.build();
    }

    public void getImg(Context context, final URL url, final ImgListener listener) {
        final Uri uri = getUriFromUrl(url);
        //Log.e(getClass().getSimpleName(), "getImg " + uri);

        if (_imgMap.containsKey(uri)) {
            //Log.e(getClass().getSimpleName(), "getImg inMap " + uri);

            if (listener != null) {
                listener.ready(_imgMap.get(uri));
            }
        } else {
            //Log.e(getClass().getSimpleName(), "getImg new " + uri);

            new LoadBitmapTask(context, url, new LoadBitmapTask.Listener() {
                @Override
                public void fail(Exception e) {
                    //Log.e(getClass().getSimpleName(), "getImg new fail " + uri);

                    if (listener != null) {
                        listener.fail(e);
                    }
                }

                @Override
                public void ready(Bitmap bitmap) {
                    //Log.e(getClass().getSimpleName(), "getImg new rdy " + uri);

                    _imgMap.put(uri, bitmap);

                    if (listener != null) {
                        listener.ready(bitmap);
                    }

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    byte[] bArray = bos.toByteArray();

                    SQLiteDatabase db = _db.getWritableDatabase();

                    //if (_stmt_addImg == null)
                    _stmt_addImg = db.compileStatement("INSERT INTO imgs(uri, blob) VALUES (?, ?)");

                    _stmt_addImg.clearBindings();
                    _stmt_addImg.bindString(1, uri.toString());
                    _stmt_addImg.bindBlob(2, bArray);

                    _stmt_addImg.executeInsert();

                    //db.close();
                }
            }).execute();
        }
    }

    ;

    public boolean clearImgs() throws Exception {
        try {
            SQLiteDatabase db = _db.getWritableDatabase();

            SQLiteStatement stmt_dropImgTable = db.compileStatement("DROP TABLE imgs");

            stmt_dropImgTable.execute();

            //db.close();

            _imgMap.clear();

            return true;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);

            throw e;
        }
    }

    public void clearSets() throws Exception {
        List<VocabSet> sets = new ArrayList<>(_vocabSets);

        for (VocabSet set : sets) {
            removeSet(set);
        }
    }

    private static Storage _instance = null;

    public static Storage getInstance(Context context) {
        if (_instance == null) {
            _instance = new Storage(context);

            _instance.load();
        }

        return _instance;
    }

    public interface Listener {
        void onAdd(VocabSet set);

        void onRemove(VocabSet set);
    }

    private List<Listener> _listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        if (!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        if (_listeners.contains(listener)) {
            _listeners.remove(listener);
        }
    }

    public class Options {
        private Map<String, Object> _vals = new HashMap<>();

        public int getI(String key, int defVal) {
            if (!_vals.containsKey(key)) {
                return defVal;
            }

            return (Integer) _vals.get(key);
        }

        public void setI(String key, int val) {
            _vals.put(key, val);
        }
    }

    private Options _options = new Options();

    public Options getOptions() {
        return _options;
    }

    public static class Acc {
        private Account _acc;

        private Account getAccount() {
            return _acc;
        }

        private static Map<Account, Acc> _map = new HashMap<>();

        public String getName() {
            return getAccount().name;
        }

        public String getPrefKey() {
            return Integer.toHexString(getName().hashCode());
        }

        private Acc(Account acc) {
            _acc = acc;
        }

        public static Acc get(Account acc) {
            if (!_map.containsKey(acc)) {
                _map.put(acc, new Acc(acc));

            }

            return _map.get(acc);
        }

        private static class DefaultAcc extends Acc {
            public String getName() {
                return "<default>";
            }

            private DefaultAcc() {
                super(null);
            }
        }

        public static final Acc DEFAULT = new DefaultAcc();
    }

    public interface StatsChangedListener {
        void accChanged(Acc val);

        void xpChanged(int prevVal, int val);
    }

    private ListenerSet<StatsChangedListener> _statsChangedListeners = new ListenerSet<>();

    public void addStatsChangedListener(StatsChangedListener val) {
        _statsChangedListeners.add(val);
    }

    private Acc _curAcc = Acc.DEFAULT;

    public Acc getAcc() {
        return _curAcc;
    }

    public void setAcc(Acc val) {
        if (_curAcc == val) return;

        _curAcc = val;
        SharedPreferences.Editor editor = _prefs.edit();

        editor.putString("acc_type", (val.getAccount() == null) ? null : val.getAccount().type);
        editor.putString("acc_name", (val.getAccount() == null) ? null : val.getAccount().name);

        editor.commit();

        _statsChangedListeners.fire(new ListenerAction<StatsChangedListener>() {
            @Override
            public void fire(StatsChangedListener el) {
                el.accChanged(_curAcc);
            }
        });

        loadXP();
    }

    private int _xp = 0;

    public int getXP() {
        return _xp;
    }

    private void setXP(final int val) {
        final int prevVal = _xp;

        _xp = val;

        SharedPreferences.Editor editor = _prefs.edit();

        editor.putInt(String.format("xp_%s", _curAcc.getPrefKey()), _xp);

        editor.commit();

        _statsChangedListeners.fire(new ListenerAction<StatsChangedListener>() {
            @Override
            public void fire(StatsChangedListener el) {
                el.xpChanged(prevVal, val);
            }
        });
    }

    private void loadAcc() {
        String type = _prefs.getString("acc_type", null);
        String name = _prefs.getString("acc_name", null);

        Log.e(getClass().getSimpleName(), "load acc " + type + ";"+name);

        if (type == null) return;
        if (name == null) return;

        AccountManager accountManager = AccountManager.get(_context);

        try {
            Account[] accounts = accountManager.getAccountsByType(type);
            Account newAccount = null;

            for (Account account : accounts) {
                if (account.name.equals(name)) {
                    newAccount = account;

                    break;
                }
            }

            if (newAccount != null) {
                setAcc(Acc.get(newAccount));
            }
        } catch (SecurityException e) {

        }
    }

    private void loadXP() {
        final int prevVal = _xp;

        final int val = _prefs.getInt(String.format("xp_%s", _curAcc.getPrefKey()), 0);

        _xp = val;

        _statsChangedListeners.fire(new ListenerAction<StatsChangedListener>(){
            @Override
            public void fire(StatsChangedListener el) {
                el.xpChanged(prevVal, val);
            }
        });
    }

    public void addXP(int val) {
        setXP(getXP() + val);
    }

    private SharedPreferences _prefs;

    private void loadImgs() throws Exception {
        SQLiteDatabase db = _db.getWritableDatabase();

        Cursor c = db.query(false, "imgs", new String[]{"uri", "blob"}, null, null, null, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();

            do {
                String uriS = c.getString(0);
                byte[] blob = c.getBlob(1);

                Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

                _imgMap.put(Uri.parse(uriS), bitmap);
            } while (c.moveToNext());

            //c.close();
        }

        //c.close();
        //db.close();
    }

    private void load() {
        _initing = true;

        _db = new DataBase(_context, "super.db", null, 1);

        try {
            List<VocabSet> sets = _db.getSets();

            if (sets != null) {
                for (VocabSet set : sets) {
                    addSet(set);
                }
            }
        } catch (StorageException e) {
            Log.e(getClass().getSimpleName(), "could not load vocab sets");
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }

        _prefs = _context.getSharedPreferences("storage", Context.MODE_PRIVATE);

        loadAcc();
        loadXP();

        SQLiteDatabase db = _db.getWritableDatabase();

        SQLiteStatement stmt_createImgTable = db.compileStatement("CREATE TABLE IF NOT EXISTS imgs(uri NVARCHAR, blob BLOB)");

        stmt_createImgTable.execute();

        //db.close();

        try {
            loadImgs();
        } catch (Exception e) {
            Util.printException(_context, e);
        }

        _initing = false;
    }

    Storage(Context context) {
        _context = context;
    }
}