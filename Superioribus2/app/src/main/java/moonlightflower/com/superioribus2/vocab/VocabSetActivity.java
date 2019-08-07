package moonlightflower.com.superioribus2.vocab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.ConfirmDialog;
import moonlightflower.com.superioribus2.shared.Util;

public class VocabSetActivity extends MyActivity {
    public static VocabSet SET;

    private VocabSet _set;

    private TextView _textView_name;
    private ListView _listView;
    private VocabSetAdapter _adapter;

    private Button _button_delete;

    private void notifyDataSetChanged() {
        _listView.setAdapter(_adapter);
        _textView_name.setText(getString(R.string.vocabs_set_title, _set.getName(), _set.getEntries().size()));
    }

    public void loadEntries() {
        notifyDataSetChanged();

        _set.setListener(new VocabSet.Listener() {
            @Override
            public void onAdd(VocabEntry val) {
                notifyDataSetChanged();
            }

            @Override
            public void onRemove(VocabEntry val) {
                notifyDataSetChanged();
            }
        });
    }

    private ActionMode _actionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _set = SET;

        setContentView(R.layout.activity_vocab_set);

        _listView = (ListView) findViewById(R.id.listView);

        //_listView.setCacheColorHint(0);

        _adapter = new VocabSetAdapter(getApplicationContext(), _set, new VocabSetAdapter.Listener() {
            @Override
            public void onEntryClicked(final VocabEntry entry) {
                Log.e(getClass().getSimpleName(), "clicked " + entry);
                _adapter.clearMarks();

                _adapter.markEntry(entry, true);

                /*ConfirmDialog dialog = new ConfirmDialog();

                dialog.setArgs("Do you really want to delete this item?", new ConfirmDialog.Listener() {
                    @Override
                    public void onReady(boolean ok) {
                        if (ok) {
                            _set.remove(entry);
                        }
                    }

                    @Override
                    public void onDismiss() {

                    }
                });

                showDialog(dialog);*/
            }

            @Override
            public void onEntryLongClicked(VocabEntry entry) {
                Log.e(getClass().getSimpleName(), "longClicked " + entry);
                List<VocabEntry> markedEntries = new ArrayList<>(_adapter.getMarkedEntries());

                if (markedEntries.isEmpty()) {
                    _adapter.markEntry(entry, true);
                } else {
                    VocabEntry first = markedEntries.iterator().next();

                    List<VocabEntry> entries = _set.getEntries();

                    int firstPos = entries.indexOf(first);
                    int targetPos = entries.indexOf(entry);

                    if (targetPos > firstPos) {
                        for (int i = firstPos; i <= targetPos; i++) {
                            _adapter.markEntry(entries.get(i), true);
                        }
                    } else {
                        for (int i = firstPos; i >= targetPos; i--) {
                            _adapter.markEntry(entries.get(i), true);
                        }
                    }
                }
            }

            @Override
            public void onEntryChecked(VocabEntry entry, boolean checked) {
                List<VocabEntry> markedEntries = new ArrayList<>(_adapter.getMarkedEntries());

                for (VocabEntry otherEntry : markedEntries) {
                    if (otherEntry.equals(entry)) continue;

                    _adapter.checkEntry(otherEntry, checked);
                }
            }

            @Override
            public void onCheckedEntriesChanged() {
                final boolean hasSelection = !_adapter.getCheckedEntries().isEmpty();

                _button_delete.setEnabled(hasSelection);

                if (hasSelection) {
                    if (_actionMode == null) {
                        _actionMode = startActionMode(new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                Log.e(getClass().getSimpleName(), "create");

                                mode.getMenuInflater().inflate(R.menu.vocab_set_menu, menu);

                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                Log.e(getClass().getSimpleName(), "prepare");

                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                if (item.getItemId() == R.id.item_range) {
                                    //enableRangeSelection();
                                }
                                if (item.getItemId() == R.id.item_delete) {
                                    deleteItems();
                                }

                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                Log.e(getClass().getSimpleName(), "destroy");
                            }
                        });
                    }
                } else if (_actionMode != null) {
                    _actionMode.finish();

                    _actionMode = null;
                }
            }
        }, true);

        _textView_name = (TextView) findViewById(R.id.textView_name);

        _button_delete = (Button) findViewById(R.id.button_delete);

        loadEntries();
    }

    public void button_vocab_onClick(View sender) {
        Intent intent = new Intent(this, VocabActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_add_onClick(View sender) {
        VocabAddVocabDialog dialog = new VocabAddVocabDialog();

        dialog.setArgs(this, _set, new VocabAddVocabDialog.Listener() {
            @Override
            public void onDismiss() {

            }

            @Override
            public void ready(VocabEntry entry) {
                _set.add(entry);
            }
        });

        showDialog(dialog);
    }

    private void deleteItems() {
        final Set<VocabEntry> checkedEntries = new HashSet(_adapter.getCheckedEntries());

        if (!checkedEntries.isEmpty()) {
            ConfirmDialog dialog = new ConfirmDialog();

            dialog.setArgs(getString(R.string.vocabs_entry_delete_query, checkedEntries.size()), new ConfirmDialog.Listener() {
                @Override
                public void onDecline() {

                }

                @Override
                public void onAccept() {
                    try {
                        for (VocabEntry entry : checkedEntries) {
                            _set.remove(entry);
                        }
                    } catch (Exception e) {
                        Util.printToast(getApplicationContext(), getString(R.string.vocabs_entry_delete_fail), Toast.LENGTH_LONG);

                        Util.printException(getApplicationContext(), e);
                    }
                }

                @Override
                public void onDismiss() {

                }
            });

            dialog.show(getFragmentManager(), "delete");
        }
    }

    public void button_delete_onClick(View sender) {
        deleteItems();
    }
}