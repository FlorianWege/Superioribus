package moonlightflower.com.superioribus2.vocab;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VocabSetAdapter implements ListAdapter {
    private Context _context;
    private VocabSet _set;
    private Listener _listener;

    private Set<VocabEntry> _checkedEntries = new HashSet<>();

    public Set<VocabEntry> getCheckedEntries() {
        return new HashSet(_checkedEntries);
    }

    private Set<VocabEntry> _markedEntries = new HashSet<>();

    public Set<VocabEntry> getMarkedEntries() {
        return new HashSet(_markedEntries);
    }

    public interface Listener {
        void onEntryClicked(VocabEntry entry);
        void onEntryLongClicked(VocabEntry entry);
        void onEntryChecked(VocabEntry entry, boolean checked);
        void onCheckedEntriesChanged();
    }

    private boolean _entryUseCheckBox;

    public VocabSetAdapter(Context context, VocabSet set, Listener listener, boolean entryUseCheckBox) {
        _context = context;
        _set = set;
        _listener = listener;

        _set.addListener(new VocabSet.Listener() {
            @Override
            public void onAdd(VocabEntry val) {

            }

            @Override
            public void onRemove(VocabEntry val) {
                if (_checkedEntries.contains(val)) {
                    _checkedEntries.remove(val);

                    if (_listener != null) {
                        _listener.onCheckedEntriesChanged();
                    }
                }
                if (_markedEntries.contains(val)) {
                    _markedEntries.remove(val);
                }
            }
        });

        _entryUseCheckBox = entryUseCheckBox;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return _set.getEntries().size();
    }

    @Override
    public Object getItem(int i) {
        return _set.getEntries().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private Map<Integer, View> _itemViews = new HashMap<>();

    public void clearMarks() {
        List<VocabEntry> markedEntries = new ArrayList<>(_markedEntries);

        for (VocabEntry entry : markedEntries) {
            markEntry(entry, false);
        }
    }

    public void markEntry(VocabEntry entry, boolean marked) {
        if (_set.getEntries().contains(entry)) {
            int index = _set.getEntries().indexOf(entry);

            View itemView = _itemViews.get(index);

            if (itemView instanceof VocabListEntryView) {
                ((VocabListEntryView) itemView).setMarked(marked);
            }
        }
    }

    public void clearChecks() {
        List<VocabEntry> checkedEntries = new ArrayList<>(_checkedEntries);

        for (VocabEntry entry : checkedEntries) {
            checkEntry(entry, false);
        }
    }

    public void checkEntry(VocabEntry entry, boolean checked) {
        if (_set.getEntries().contains(entry)) {
            int index = _set.getEntries().indexOf(entry);

            View itemView = _itemViews.get(index);

            if (itemView instanceof VocabListEntryView) {
                ((VocabListEntryView) itemView).setChecked(checked);
            }
        }
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        Object item = getItem(index);
        View itemView = null;

        if (item instanceof VocabEntry) {
            VocabEntry entry = (VocabEntry) item;

            VocabListEntryView entryView = new VocabListEntryView(_context, entry, new VocabListEntryView.Listener() {
                @Override
                public void onClick(final VocabEntry entry) {
                    if (_listener != null) {
                        _listener.onEntryClicked(entry);
                    }
                }

                @Override
                public void onLongClick(VocabEntry entry) {
                    if (_listener != null) {
                        _listener.onEntryLongClicked(entry);
                    }
                }

                @Override
                public void onCheck(VocabEntry entry, boolean check) {
                    if (check) {
                        _checkedEntries.add(entry);

                        if (_listener != null) {
                            _listener.onEntryChecked(entry, true);
                        }
                    } else {
                        _checkedEntries.remove(entry);

                        if (_listener != null) {
                            _listener.onEntryChecked(entry, false);
                        }
                    }

                    if (_listener != null) {
                        _listener.onCheckedEntriesChanged();
                    }
                }

                @Override
                public void onMarked(VocabEntry entry, boolean marked) {
                    if (marked) {
                        _markedEntries.add(entry);
                    } else {
                        _markedEntries.remove(entry);
                    }
                }
            }, _entryUseCheckBox, _checkedEntries.contains(entry), _markedEntries.contains(entry));

            itemView = entryView;
        }

        _itemViews.put(index, itemView);

        return itemView;
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
