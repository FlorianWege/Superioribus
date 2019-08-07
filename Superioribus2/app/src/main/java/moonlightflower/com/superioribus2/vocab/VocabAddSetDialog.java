package moonlightflower.com.superioribus2.vocab;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.shared.lang.Lang;
import moonlightflower.com.superioribus2.shared.lang.LangAdapter;
import moonlightflower.com.superioribus2.shared.MyDialog;
import moonlightflower.com.superioribus2.shared.storage.Storage;

public class VocabAddSetDialog extends MyDialog {
    private Button _button_add;
    private Button _button_cancel;

    private EditText _edit_name;

    private Spinner _spinner_source;
    private Spinner _spinner_target;

    private LangAdapter _adapter_source;
    private LangAdapter _adapter_target;

    private Context _context;
    private Storage _storage;

    public interface Listener extends  MyDialog.Listener {
        void ready(VocabSet result);
    }

    private Listener _listener;

    public void setListener(Listener val) {
        super.setListener(val);

        _listener = val;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().setTitle(getString(R.string.vocabs_set_add_title));

        View view = inflater.inflate(R.layout.dialog_add_vocab_set, container, false);

        _context = view.getContext();
        _storage = Storage.getInstance(_context);

        _edit_name = (EditText) view.findViewById(R.id.edit_name);

        _spinner_source = (Spinner) view.findViewById(R.id.spinner_source);
        _spinner_target = (Spinner) view.findViewById(R.id.spinner_target);

        _adapter_source = new LangAdapter(view.getContext(), new LangAdapter.ItemClickListener() {
            @Override
            public void itemsChanged() {
                _spinner_source.setAdapter(null);

                _spinner_source.setAdapter(_adapter_source);
            }

            @Override
            public void itemClick(int pos) {
                _adapter_source.setSelectedLang((Lang) _spinner_source.getSelectedItem());

                _spinner_source.setSelected(true);
                _spinner_source.setSelection(pos, true);
            }
        });
        _adapter_target = new LangAdapter(view.getContext(), new LangAdapter.ItemClickListener() {
            @Override
            public void itemsChanged() {
                _spinner_target.setAdapter(null);

                _spinner_target.setAdapter(_adapter_target);
            }

            @Override
            public void itemClick(int pos) {
                _spinner_target.setSelection(pos);
            }
        });

        _adapter_source.add(Lang.getMap().values());
        _adapter_target.add(Lang.getMap().values());

        _spinner_source.setAdapter(_adapter_source);
        _spinner_target.setAdapter(_adapter_target);

        _button_add = (Button) view.findViewById(R.id.button_add);

        _button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_listener != null) {
                    Lang sourceLang = (Lang) _spinner_source.getSelectedItem();
                    Lang targetLang = (Lang) _spinner_target.getSelectedItem();

                    if (sourceLang.equals(targetLang)) {
                        Util.printToast(getActivity(), getString(R.string.vocabs_set_add_same_lang), Toast.LENGTH_SHORT);

                        return;
                    }

                    boolean found = false;
                    String name = _edit_name.getText().toString();

                    for (VocabSet set : _storage.getSets()) {
                        if (set.getName().equals(name)) {
                            found = true;
                        }
                    }

                    if (found) {
                        Toast.makeText(_activity, getString(R.string.vocabs_set_add_duplicate), Toast.LENGTH_SHORT).show();
                        _edit_name.selectAll();
                        _edit_name.setError(getString(R.string.vocabs_set_add_unique_name));
                    } else {
                        Log.e(getClass().getSimpleName(), "addSetA");
                        VocabSet set = new VocabSet((Lang) _spinner_source.getSelectedItem(), (Lang) _spinner_target.getSelectedItem(), name);
                        Log.e(getClass().getSimpleName(), "addSetB");
                        dismiss();
Log.e(getClass().getSimpleName(), "addSetC");
                        _listener.ready(set);
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

    public void setArgs(Activity val, Listener listener) {
        _activity = val;
        setListener(listener);
    }
}