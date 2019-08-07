package moonlightflower.com.superioribus2.vocab;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import moonlightflower.com.superioribus2.shared.MyDialog;
import moonlightflower.com.superioribus2.R;

public class VocabAddVocabDialog extends MyDialog {
    private Button _button_add;
    private Button _button_cancel;
    private EditText _editText_source;
    private EditText _editText_target;

    public interface Listener extends  MyDialog.Listener {
        void ready(VocabEntry result);
    }

    private Listener _listener;

    public void setListener(Listener val) {
        super.setListener(val);

        _listener = val;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().setTitle(getString(R.string.vocabs_entry_add_title));

        View view = inflater.inflate(R.layout.dialog_add_vocab, container, false);

        _editText_source = (EditText) view.findViewById(R.id.editText_source);
        _editText_target = (EditText) view.findViewById(R.id.editText_target);

        _button_add = (Button) view.findViewById(R.id.button_add);

        _button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_listener != null) {
                    String sourceText = _editText_source.getText().toString();
                    String targetText = _editText_target.getText().toString();

                    if ((sourceText == null) || sourceText.isEmpty()) {
                        _editText_source.setError(getString(R.string.vocabs_entry_add_source_missing));
                        _editText_source.selectAll();
                        _editText_source.requestFocus();

                        return;
                    }
                    if ((targetText == null) || targetText.isEmpty()) {
                        _editText_target.setError(getString(R.string.vocabs_entry_add_target_missing));
                        _editText_target.selectAll();
                        _editText_target.requestFocus();

                        return;
                    }

                    VocabEntry entry = new VocabEntry(sourceText, targetText);

                    if (_set.containsEqual(entry)) {
                        Toast.makeText(_activity, getString(R.string.vocabs_entry_add_duplicate), Toast.LENGTH_SHORT).show();
                    } else {
                        _listener.ready(entry);

                        dismiss();
                    }
                }
            }
        });

        _button_cancel = (Button) view.findViewById(R.id.button_cancel);

        _button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private Activity _activity;
    private VocabSet _set;

    public void setArgs(Activity val, VocabSet set, Listener listener) {
        _activity = val;
        _set = set;
        setListener(listener);
    }
}