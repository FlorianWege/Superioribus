package moonlightflower.com.superioribus2.shared.lang;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LangAdapter extends BaseAdapter {
    private Context _context;

    private List<Lang> _list = new ArrayList<Lang>();

    public interface ItemClickListener {
        void itemsChanged();
        void itemClick(int pos);
    }

    private ItemClickListener _listener;

    public LangAdapter(Context context, ItemClickListener listener) {
        _context = context;

        _listener = listener;

        add(Lang.NONE);
    }

    private Lang _selectedLang = null;
    private Map<Integer, View> _itemViews = new HashMap<>();

    public void setSelectedLang(Lang val){
        if(_selectedLang != null) {
            int pos = _list.indexOf(_selectedLang);

            if(pos != -1) {
                LangView langView = (LangView) _itemViews.get(pos);

                if(langView != null) {
                    langView.setSelected(false);
                }
            }
        }

        _selectedLang = val;

        if(_selectedLang != null) {
            int pos = _list.indexOf(_selectedLang);

            if(pos != -1) {
                LangView langView = (LangView) _itemViews.get(pos);

                if(langView != null) {
                    langView.setSelected(true);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void clear() {
        _list.clear();

        _list.add(Lang.NONE);

        notifyDataSetChanged();

        if (_listener != null) {
            _listener.itemsChanged();
        }
    }

    public List<Lang> getLangs() {
        return new ArrayList<>(_list);
    }

    public void remove(Lang val) {
        if (_list.contains(val)) {
            _list.remove(val);
        }

        if (_list.isEmpty() && !_list.contains(Lang.NONE)) {
            _list.add(Lang.NONE);
        }

        notifyDataSetChanged();

        if (_listener != null) {
            _listener.itemsChanged();
        }
    }

    public void add(Lang val) {
        if (_list.contains(val)) return;

        List<Lang> vals = new ArrayList<>();

        vals.add(val);

        add(vals);
    }

    public void add(Collection<Lang> vals) {
        if (_list.containsAll(vals)) return;

        if (_list.contains(Lang.NONE)) {
            remove(Lang.NONE);
        }

        for (Lang val : vals) {
            if (!_list.contains(val)) {
                _list.add(val);
            }
        }

        Collections.sort(_list, new Comparator<Lang>() {
            @Override
            public int compare(Lang a, Lang b) {
                if (a.equals(Lang.NONE)) {
                    return -1;
                }
                if (a.equals(Lang.AUTO)) {
                    if (b.equals(Lang.NONE)) {
                        return 1;
                    }

                    return -1;
                }
                if (b.equals(Lang.AUTO)) {
                    if (b.equals(Lang.NONE)) {
                        return -1;
                    }

                    return 1;
                }

                return a.getName().compareTo(b.getName());
            }
        });

        notifyDataSetChanged();

        if (_listener != null) {
            _listener.itemsChanged();
        }
    }

    @Override
    public View getDropDownView(final int pos, View convertView, ViewGroup parent) {
        Object item = getItem(pos);
        View itemView = null;

        if (item instanceof Lang) {
            LangView langView = new LangView(_context, (Lang) item, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _listener.itemClick(pos);
                }
            });

            langView.setSelected(item == _selectedLang);

            langView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));

            itemView = langView;
        }

        return itemView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Object getItem(int i) {
        return _list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        Object item = getItem(pos);
        View itemView = null;

        if (item instanceof Lang) {
            LangView langView = new LangView(_context, (Lang) item, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _listener.itemClick(pos);
                }
            });

            langView.setSelected(item == _selectedLang);

            _itemViews.put(pos, itemView);

            itemView = langView;
        }

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