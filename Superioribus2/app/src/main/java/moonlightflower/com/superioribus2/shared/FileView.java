package moonlightflower.com.superioribus2.shared;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

import moonlightflower.com.superioribus2.R;

public class FileView extends LinearLayout {
    private File _file;

    private View _view_root;
    private Button _button_name;

    public interface Listener {
        void click(File file);
    }

    public void updateStyle() {
        if (_file.isDirectory()) {
            //_button_name.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.list_dir_background, null));
        } else {
            //_button_name.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.list_file_background, null));
        }
    }

    private long getFileSize(File file) {
        long sum = (file.isFile() && file.getName().endsWith(".db")) ? file.length() : 0;

        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();

            if (subFiles != null) {
                for (File subFile : subFiles) {
                    sum += getFileSize(subFile);
                }
            };
        }

        return sum;
    }

    private String getFileSizeS(File file) {
        double len = getFileSize(file);

        if (len < 1024) {
            return String.format("%dB", (long) len);
        }

        len /= 1024;

        if (len < 1024) {
            return String.format("%.2fKB", len);
        }

        len /= 1024;

        if (len < 1024) {
            return String.format("%.2fMB", len);
        }

        return String.format("%.2fGB", len);
    }

    public FileView(Context context, File file, final Listener clickListener) {
        super(context);

        _file = file;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _view_root = inflater.inflate(_file.isDirectory() ? R.layout.dir : R.layout.file, this);

        _button_name = (Button) _view_root.findViewById(R.id.button_name);

        _button_name.setText(context.getString(R.string.vocabs_import_file_caption, _file.getName(), getFileSizeS(_file)));

        _button_name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.click(_file);
            }
        });

        updateStyle();
    }
}