package moonlightflower.com.superioribus2.vocab;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VocabMultiSetsExpandableListAdapter implements ExpandableListAdapter {
    private List<VocabSet> _sets = new ArrayList<>();

    public List<VocabSet> getSets() {
        return _sets;
    }

    public void add(VocabSet val) {
        _sets.add(val);
    }

    public void remove(VocabSet val) {
        _sets.remove(val);
    }

    public void clear() {
        _sets.clear();
    }

    private Context _context;

    public interface Listener {
        void onSetGroupClicked(VocabSet set);
        void onSetGroupLongClicked(VocabSet set);
        void onSetLinkClicked(VocabSet set);
    }

    private Listener _listener = null;
    private boolean _entryUseCheckBox;

    public VocabMultiSetsExpandableListAdapter(Context context, Listener listener, boolean entryUseCheckBox) {
        _context = context;
        _listener = listener;
        _entryUseCheckBox = entryUseCheckBox;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return _sets.size();
    }

    @Override
    public int getChildrenCount(int i) {
        Object group = getGroup(i);

        if (group instanceof VocabSet) {
            return ((VocabSet) group).getEntries().size();
        }

        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return _sets.get(i);
    }

    @Override
    public Object getChild(int groupIndex, int childIndex) {
        Object group = getGroup(groupIndex);

        if (group instanceof VocabSet) {
            return ((VocabSet) group).getEntries().get(childIndex);
        }

        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private Map<Integer, View> _groupViews = new HashMap<>();

    @Override
    public View getGroupView(int groupIndex, boolean isExpanded, View view, ViewGroup viewGroup) {
        Object group = getGroup(groupIndex);
        View groupView = null;

        if (group instanceof VocabSet) {
            final VocabSet set = (VocabSet) group;

            final VocabListSetView setView = new VocabListSetView(_context, set, new VocabListSetView.Listener() {
                @Override
                public void onGroupClicked() {
                    if (_listener != null) {
                        _listener.onSetGroupClicked(set);
                    }
                }

                @Override
                public void onGroupLongClicked() {
                    if (_listener != null) {
                        _listener.onSetGroupLongClicked(set);
                    }
                }

                @Override
                public void onLinkClicked() {
                    if (_listener != null) {
                        _listener.onSetLinkClicked(set);
                    }
                }
            });

            setView.setExpandedStyle(isExpanded);

            groupView = setView;
        }

        _groupViews.put(groupIndex, groupView);

        return groupView;
    }

    private Map<Pair<Integer, Integer>, View> _childViews = new HashMap<>();

    @Override
    public View getChildView(int groupIndex, int childIndex, boolean b, View view, ViewGroup viewGroup) {
        Object child = getChild(groupIndex, childIndex);
        View childView = null;

        if (child instanceof VocabEntry) {
            childView = new VocabListEntryView(_context, (VocabEntry) child, null, _entryUseCheckBox, false, false);
        }

        _childViews.put(Pair.create(groupIndex, childIndex), childView);

        return childView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return (getGroupCount() == 0);
    }

    @Override
    public void onGroupExpanded(int groupIndex) {
        View view = _groupViews.get(groupIndex);

        if (view instanceof VocabListSetView) {
            ((VocabListSetView) view).setExpandedStyle(true);
        }
    }

    @Override
    public void onGroupCollapsed(int groupIndex) {
        View view = _groupViews.get(groupIndex);

        if (view instanceof VocabListSetView) {
            ((VocabListSetView) view).setExpandedStyle(false);
        }
    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
