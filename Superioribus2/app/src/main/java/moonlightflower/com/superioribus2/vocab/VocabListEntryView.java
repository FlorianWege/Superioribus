package moonlightflower.com.superioribus2.vocab;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import moonlightflower.com.superioribus2.R;

public class VocabListEntryView extends LinearLayout {
    private VocabEntry _entry;

    private View _view_root;
    private View _view_overlay;
    private TextView _textView_source;
    private TextView _textView_target;
    private CheckBox _checkBox;

    public interface Listener {
        void onClick(VocabEntry entry);
        void onLongClick(VocabEntry entry);
        void onCheck(VocabEntry entry, boolean check);
        void onMarked(VocabEntry entry, boolean marked);
    }

    private Listener _listener = null;

    public void setMarked(boolean marked) {
        if (marked) {
            _view_overlay.setBackground(getResources().getDrawable(R.drawable.list_vocab_overlay_active));
        } else {
            _view_overlay.setBackground(null);
        }

        if (_listener != null) {
            _listener.onMarked(_entry, marked);
        }
    }

    public void setChecked(boolean checked) {
        if (_checkBox.isChecked() != checked) {
            _checkBox.setChecked(checked);

            if (_listener != null) {
                _listener.onCheck(_entry, checked);
            }
        }
    }

    public VocabListEntryView(Context context, VocabEntry entry, Listener listener, boolean useCheckBox, boolean checked, boolean marked) {
        super(context);

        _entry = entry;
        _listener = listener;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _view_root = inflater.inflate(R.layout.vocab_list_set_entry, this);

        _view_overlay = _view_root.findViewById(R.id.view_overlay);

        _textView_source = (TextView) _view_root.findViewById(R.id.textView_source);

        _textView_source.setText(_entry.getSource());

        _textView_target = (TextView) _view_root.findViewById(R.id.textView_target);

        _textView_target.setText(_entry.getTarget());

        _checkBox = (CheckBox) findViewById(R.id.checkBox);

        _checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (_listener != null) {
                    _listener.onCheck(_entry, b);
                }
            }
        });

        _textView_source.setClickable(false);
        _textView_source.setFocusable(false);
        _textView_target.setClickable(false);
        _textView_target.setFocusable(false);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_listener != null) {
                    _listener.onClick(_entry);
                }
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (_listener != null) {
                    _listener.onLongClick(_entry);
                }

                return true;
            }
        });

        if (!useCheckBox) {
            _checkBox.setVisibility(View.GONE);
        }

        setChecked(checked);
        setMarked(marked);
    }
}