package moonlightflower.com.superioribus2.vocab;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;

import moonlightflower.com.superioribus2.shared.FileView;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.MyDialog;
import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.Util;

public class VocabImportDialog extends MyDialog {
    private Button _button_up;
    private EditText _editText_path;
    private ListView _listView;

    private File _dir;

    public interface Listener extends MyDialog.Listener {
        void ready(File file);
    }

    private Listener _listener;

    public void setListener(Listener val) {
        super.setListener(val);

        _listener = val;
    }

    private class Adapter implements ListAdapter {
        private Context _context;
        private File[] _files = new File[0];

        public Adapter(Context context) {
            _context = context;

            if (!_activity.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Util.printToast(_context, getString(R.string.permission_external_storage_read_fail), Toast.LENGTH_SHORT);

                _files = new File[0];
            } else {
                Log.e(getClass().getSimpleName(), "adapterA");
                _files = _dir.listFiles(new FilenameFilter() {
                    private boolean acceptDBFile(File file) {
                        if (file.isFile() && file.getName().endsWith(".db")) {
                            return true;
                        }

                        return false;
                    }

                    private boolean acceptDir(File dir) {
                        Log.e(getClass().getSimpleName(), "dirA " + dir);
                        File[] subFiles = dir.listFiles();
                        Log.e(getClass().getSimpleName(), "dirAA " + dir);

                        if (subFiles == null) {
                            return false;
                        }
                        Log.e(getClass().getSimpleName(), "dirB" + dir);
                        for (File subFile : subFiles) {
                            Log.e(getClass().getSimpleName(), "dirC" + dir);
                            if (acceptDBFile(subFile) || (subFile.isDirectory() && acceptDir(subFile))) {
                                return true;
                            }
                        }

                        return false;
                    }

                    @Override
                    public boolean accept(File dir, String fileName) {
                        Log.e(getClass().getSimpleName(), "acceptA");
                        File file = new File(dir, fileName);

                        if (acceptDBFile(file)) {
                            Log.e(getClass().getSimpleName(), "acceptAA");
                            return true;
                        }
                        Log.e(getClass().getSimpleName(), "acceptB");
                        if (file.isDirectory() && acceptDir(file)) {
                            Log.e(getClass().getSimpleName(), "acceptBB");
                            return true;
                        };
                        Log.e(getClass().getSimpleName(), "acceptC");
                        return false;
                    }
                });
                Log.e(getClass().getSimpleName(), "adapterB");
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return (_files == null) ? 0 : _files.length;
        }

        @Override
        public Object getItem(int i) {
            return _files[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Object item = getItem(i);

            if (item instanceof File) {
                return new FileView(_context, (File) item, new FileView.Listener() {
                    @Override
                    public void click(File file) {
                        if (file.isDirectory()) {
                            setDir(file);
                        } else {
                            if (_listener != null) {
                                _listener.ready(file);
                            }

                            dismiss();
                        }
                    }
                });
            }

            return null;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return (getCount() == 0);
        }
    }

    //@TargetApi(Build.VERSION_CODES.M)
    private void setDir(File dir) {
        if (dir.equals(HOME_DIR.getParentFile())) {
            Util.printToast(_activity, getString(R.string.vocabs_import_try_leave_external_storage), Toast.LENGTH_SHORT);

            return;
        }

        if (dir.list() == null) {
            Util.printToast(_activity, getString(R.string.vocabs_import_change_dir_fail, dir), Toast.LENGTH_SHORT);

            return;
        }

        _dir = dir;

        _listView.setAdapter(new Adapter(_activity));

        _editText_path.setText(_dir.toString());
    }

    //private static final File HOME_DIR = Environment.getExternalStorageDirectory();
    private static File HOME_DIR;

    //@TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().setTitle(getString(R.string.vocabs_import_title));

        View view = inflater.inflate(R.layout.dialog_vocab_import, container, false);
        Log.e(getClass().getSimpleName(), "impA");
        _button_up = (Button) view.findViewById(R.id.button_up);

        _button_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File parentFile = _dir.getParentFile();

                if (parentFile != null) {
                    setDir(parentFile);
                }
            }
        });

        _editText_path = (EditText) view.findViewById(R.id.editText_dirPath);
        _listView = (ListView) view.findViewById(R.id.listView);

        _activity.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e(getClass().getSimpleName(), "impB");
        setDir(HOME_DIR);
        Log.e(getClass().getSimpleName(), "impC");
        return view;
    }

    private MyActivity _activity;

    public void setArgs(MyActivity val, Listener listener) {
        _activity = val;
        setListener(listener);

        HOME_DIR = _activity.getExternalFilesDir("dictFiles");

        HOME_DIR.mkdirs();


    }
}