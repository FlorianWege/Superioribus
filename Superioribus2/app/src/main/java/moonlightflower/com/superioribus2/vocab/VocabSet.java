package moonlightflower.com.superioribus2.vocab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import moonlightflower.com.superioribus2.shared.lang.Lang;

public class VocabSet {
    public interface Listener {
        void onAdd(VocabEntry val);
        void onRemove(VocabEntry val);
    }

    private List<Listener> _listeners = new ArrayList<>();

    public void addListener(Listener val) {
        if (!_listeners.contains(val)) {
            _listeners.add(val);
        }
    }

    public void removeListener(Listener val) {
        if (_listeners.contains(val)) {
            _listeners.remove(val);
        }
    }

    private List<VocabEntry> _entries = new ArrayList<>();

    public List<VocabEntry> getEntries() {
        return _entries;
    }

    private Listener _mainListener = null;

    public void setListener(Listener val) {
        if (_mainListener != null) {
            removeListener(_mainListener);
        }

        _mainListener = val;

        if (_mainListener != null) {
            addListener(_mainListener);
        }
    }

    public boolean containsEqual(VocabEntry val) {
        for (VocabEntry entry : _entries) {
            if (entry.getSource().equals(val.getSource())) return true;
        }

        return false;
    }

    public void add(VocabEntry val) {
        if (!_entries.contains(val)) {
            _entries.add(val);

            for (Listener listener : _listeners) {
                listener.onAdd(val);
            }
        }
    }

    public void remove(VocabEntry val) throws Exception {
        if (_entries.contains(val)) {
            _entries.remove(val);

            for (Listener listener : _listeners) {
                listener.onRemove(val);
            }
        }
    }

    public void clear() throws Exception {
        List<VocabEntry> entries = new ArrayList<>(_entries);

        for (VocabEntry entry : entries) {
            remove(entry);
        }
    }

    private Lang _sourceLang;

    public Lang getSourceLang() {
        return _sourceLang;
    }

    private Lang _targetLang;

    public Lang getTargetLang() {
        return _targetLang;
    }

    private String _name;

    public String getName() {
        return _name;
    }

    public void sort(Comparator<VocabEntry> comparator) {
        Collections.sort(_entries, comparator);
    }

    public VocabSet(Lang sourceLang, Lang targetLang, String name) {
        _sourceLang = sourceLang;
        _targetLang = targetLang;
        _name = name;
    }
}