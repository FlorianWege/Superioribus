package moonlightflower.com.superioribus2.exercise;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moonlightflower.com.superioribus2.shared.storage.Storage;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class ExerciseVocabSetsAdapter implements ListAdapter {
    private Context _context;
    private Listener _listener;
    private Storage _storage;

    public interface Listener {
        void onSetsChanged();
        void onSetClicked(int setIndex, int minEntryIndex, int maxEntryIndex);
    }

    public List<VocabSet> getSets() {
        return _storage.getSets();
    }

    public ExerciseVocabSetsAdapter(Context context, Listener listener) {
        _context = context;
        _listener = listener;

        _storage = Storage.getInstance(_context);

        _storage.addListener(new Storage.Listener() {
            @Override
            public void onAdd(VocabSet set) {
                if (_listener != null) {
                    _listener.onSetsChanged();
                }
            }

            @Override
            public void onRemove(VocabSet set) {
                if (_listener != null) {
                    _listener.onSetsChanged();
                }
            }
        });
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
        return _storage.getSets().size();
    }

    @Override
    public Object getItem(int i) {
        return _storage.getSets().get(i);
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

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        Object item = getItem(index);
        View itemView = null;

        if (item instanceof VocabSet) {
            final VocabSet set = (VocabSet) item;

            ExerciseVocabSetView setView = new ExerciseVocabSetView(_context, set, new ExerciseVocabSetView.Listener() {
                @Override
                public void onClick(int minEntryIndex, int maxEntryIndex) {
                    if (_listener != null) {
                        _listener.onSetClicked(index, minEntryIndex, maxEntryIndex);
                    }
                }
            });

            itemView = setView;
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
