package moonlightflower.com.superioribus2.vocab;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import moonlightflower.com.superioribus2.main.MainActivity;
import moonlightflower.com.superioribus2.shared.ConfirmDialog;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.shared.storage.DictCCFile;
import moonlightflower.com.superioribus2.shared.storage.Storage;

public class VocabActivity extends MyActivity {
    private Storage _storage;
    private Storage.Listener _storageListener = null;

    private VocabMultiSetsExpandableListAdapter _adapter;
    private ExpandableListView _listView;

    private ImageButton _button_clear;
    private ImageButton _button_add;

    private boolean _listWasEmpty = true;

    private void notifyDataSetChanged() {
        if (_adapter.getSets().isEmpty() && !_listWasEmpty) {
            _listWasEmpty = true;
            Util.addEnabled(_button_clear, false);
        } else if (!_adapter.getSets().isEmpty() && _listWasEmpty) {
            _listWasEmpty = false;
            Util.addEnabled(_button_clear, true);
        }

        _listView.setAdapter(_adapter);
    }

    private void loadSets() {
        _adapter.clear();

        List<VocabSet> sets = _storage.getSets();

        for (VocabSet set : sets) {
            _adapter.add(set);
        }

        if (_storageListener == null) {
            _storageListener = new Storage.Listener() {
                @Override
                public void onAdd(VocabSet set) {
                    _adapter.add(set);

                    set.setListener(new VocabSet.Listener() {
                        @Override
                        public void onAdd(VocabEntry val) {
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onRemove(VocabEntry val) {
                            notifyDataSetChanged();
                        }
                    });

                    notifyDataSetChanged();
                }

                @Override
                public void onRemove(VocabSet set) {
                    _adapter.remove(set);

                    set.setListener(null);

                    notifyDataSetChanged();
                }
            };

            _storage.addListener(_storageListener);
        }

        notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _storage = Storage.getInstance(getApplicationContext());

        setContentView(R.layout.activity_vocab);

        _listView = (ExpandableListView) findViewById(R.id.listView);

        _adapter = new VocabMultiSetsExpandableListAdapter(getApplicationContext(), new VocabMultiSetsExpandableListAdapter.Listener() {
            @Override
            public void onSetGroupClicked(VocabSet set) {
                int pos = _adapter.getSets().indexOf(set);

                if (pos != -1) {
                    if (!set.getEntries().isEmpty()) {
                        if (_listView.isGroupExpanded(pos)) {
                            _listView.collapseGroup(pos);
                        } else {
                            _listView.expandGroup(pos);
                        }
                    }
                }
            }

            @Override
            public void onSetGroupLongClicked(final VocabSet set) {
                ConfirmDialog dialog = new ConfirmDialog();

                dialog.setArgs(getString(R.string.vocabs_set_delete_title, set.getName()), new ConfirmDialog.Listener() {
                    @Override
                    public void onDecline() {

                    }

                    @Override
                    public void onAccept() {
                        try {
                            _storage.removeSet(set);

                            Util.printToast(getApplicationContext(), getString(R.string.vocabs_set_delete_note, set.getName()), Toast.LENGTH_LONG);
                        } catch (Exception e) {
                            Util.printToast(getApplicationContext(), getString(R.string.vocabs_set_delete_fail, set.getName()), Toast.LENGTH_LONG);

                            Util.printException(getApplicationContext(), e);
                        }
                    }

                    @Override
                    public void onDismiss() {

                    }
                });

                showDialog(dialog);
            }

            @Override
            public void onSetLinkClicked(VocabSet set) {
                Intent intent = new Intent(VocabActivity.this, VocabSetActivity.class);

                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

                VocabSetActivity.SET = set;

                startActivity(intent);
            }
        }, false);

        Point p = Util.getScreenSize(this);

        int width = p.x / 5;
        int height = p.y / 5;

        width = Math.min(width, height);
        height = Math.min(width, height);

        FrameLayout layout_clear = (FrameLayout) findViewById(R.id.layout_clear);
        FrameLayout layout_add = (FrameLayout) findViewById(R.id.layout_add);

        ViewGroup.LayoutParams params_clear = layout_clear.getLayoutParams();
        ViewGroup.LayoutParams params_add = layout_add.getLayoutParams();

        params_clear.width = width;
        params_clear.height = height;

        params_add.width = width;
        params_add.height = height;

        layout_clear.setLayoutParams(params_clear);
        layout_add.setLayoutParams(params_add);

        _button_clear = (ImageButton) findViewById(R.id.button_clear);
        _button_add = (ImageButton) findViewById(R.id.button_add);

        _listView.setAdapter(_adapter);

        loadSets();
    }

    public void button_main_onClick(View sender) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_import_onClick(View sender) {
        Log.e(getClass().getSimpleName(), "importA");
        VocabImportDialog dialog = new VocabImportDialog();
        Log.e(getClass().getSimpleName(), "importB");
        dialog.setArgs(this, new VocabImportDialog.Listener() {
            @Override
            public void onDismiss() {

            }

            @Override
            public void ready(File file) {
                Util.printToast(getActivity(), getString(R.string.vocabs_import_opening_file_loading, file), Toast.LENGTH_SHORT);

                final List<VocabEntry> entries = new DictCCFile(getApplicationContext(), file).get();

                VocabAddSetDialog dialog = new VocabAddSetDialog();

                dialog.setArgs(VocabActivity.this, new VocabAddSetDialog.Listener() {
                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void ready(VocabSet result) {
                        VocabSet set = result;

                        for (VocabEntry vocab : entries) {
                            set.add(vocab);
                        }

                        set.sort(new Comparator<VocabEntry>() {
                            @Override
                            public int compare(VocabEntry a, VocabEntry b) {
                                return a.getSource().toLowerCase().compareTo(b.getSource().toLowerCase());
                            }
                        });

                        try {
                            _storage.addSet(set);
                        } catch (Storage.StorageException e) {
                            Util.printException(getActivity(), e);
                        }
                    }
                });

                showDialog(dialog);
            }
        });
        Log.e(getClass().getSimpleName(), "importD");
        showDialog(dialog);
        Log.e(getClass().getSimpleName(), "importE");
    }

    public void button_add_onClick(View sender) {
        VocabAddSetDialog dialog = new VocabAddSetDialog();

        dialog.setArgs(this, new VocabAddSetDialog.Listener() {
            @Override
            public void onDismiss() {

            }

            @Override
            public void ready(VocabSet result) {
                Log.e(getClass().getSimpleName(), "addSetX");
                try {
                    _storage.addSet(result);
                } catch (Storage.StorageException e) {
                    Util.printException(getActivity(), e);
                }
                Log.e(getClass().getSimpleName(), "addSetY");
            }
        });

        showDialog(dialog);
    }

    public void button_clear_onClick(View sender) {
        if (!_button_clear.isEnabled()) return;

        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setArgs(getString(R.string.clear_vocabs_query), new ConfirmDialog.Listener() {
            @Override
            public void onDecline() {

            }

            @Override
            public void onAccept() {
                try {
                    _storage.clearSets();

                    Util.printToast(getApplicationContext(), getString(R.string.clear_vocabs_note), Toast.LENGTH_LONG);
                } catch (Exception e) {
                    Util.printToast(getApplicationContext(), getString(R.string.clear_vocabs_fail), Toast.LENGTH_LONG);

                    Util.printException(getApplicationContext(), e);
                }
            }

            @Override
            public void onDismiss() {

            }
        });

        showDialog(dialog);
    }
}