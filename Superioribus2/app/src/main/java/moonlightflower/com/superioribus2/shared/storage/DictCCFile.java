package moonlightflower.com.superioribus2.shared.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import moonlightflower.com.superioribus2.vocab.VocabEntry;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class DictCCFile {
    private class DB extends SQLiteOpenHelper {
        private static final String DB_MAIN_FT = "main_ft";
        private static final String DB_MAIN_FT_CONTENT = "main_ft_content";
        private static final String DB_MAIN_FT_SEGDIR = "main_ft_segdir";
        private static final String DB_MAIN_FT_SEGMENTS = "main_ft_segments";
        private static final String DB_MAIN_FT_SINGLEWORDS = "singlewords";
        private static final String DB_MAIN_FT_SUBJECTS = "subjects";

        private static final String COL_TERM_1 = "term1";
        private static final String COL_TERM_2 = "term2";

        public DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        private String cleanTerm(String s) {
            s = s.replaceAll("\\<.+\\>", "");
            s = s.replaceAll(String.format("\\{.+\\}"), "");
            s = s.replaceAll(String.format("\\(.+\\)"), "");
            s = s.replaceAll(String.format("\\[.+\\]"), "");

            s = s.replaceAll("^\\s+", "");
            s = s.replaceAll("\\s+$", "");
            s = s.replaceAll("\\s+", " ");

            if (s.matches("^\\s+$")) return "";

            return s;
        }

        public List<VocabEntry> get() {
            Cursor cursor = getReadableDatabase().rawQuery(String.format("SELECT %s, %s from %s", COL_TERM_1, COL_TERM_2, DB_MAIN_FT), null);

            List<VocabEntry> entries = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {
                    String term1 = cursor.getString(0);
                    String term2 = cursor.getString(1);

                    term1 = cleanTerm(term1);
                    term2 = cleanTerm(term2);

                    if (term1.isEmpty() || term2.isEmpty()) continue;

                    VocabEntry entry = new VocabEntry(term1, term2);

                    entries.add(entry);
                } while (cursor.moveToNext()) ;
            }

            return entries;
        }
    }

    private DB _db;

    public List<VocabEntry> get() {
        return _db.get();
    }

    private File _file;

    public DictCCFile(Context context, File file) {
        _file = file;

        _db = new DB(context, file.toString(), null, 1);
    }
}