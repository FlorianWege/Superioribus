package moonlightflower.com.superioribus2.exercise;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Set;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.ConfirmDialog;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class ExerciseVocabSetView extends LinearLayout {
    private VocabSet _set;

    public interface Listener {
        void onClick(int minEntryIndex, int maxEntryIndex);
    }

    private Listener _listener;

    private View _view_root;
    private Button _button;
    private Spinner _spinner;
    private TextView _textView_group;

    private int _groupSize = 30;
    private int _totalSize;

    private int calcMinIndex(int pos) {
        return pos*_groupSize;
    }

    private int calcMaxIndex(int pos) {
        final int totalSize = _set.getEntries().size();

        return Math.min(totalSize, (pos+1)*_groupSize) - 1;
    }

    public ExerciseVocabSetView(Context context, VocabSet set, Listener listener) {
        super(context);

        _set = set;
        _listener = listener;

        _totalSize = _set.getEntries().size();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _view_root = inflater.inflate(R.layout.exercise_vocab_set, this);

        _button = (Button) _view_root.findViewById(R.id.button_group);
        _spinner = (Spinner) _view_root.findViewById(R.id.spinner);
        _textView_group = (TextView) _view_root.findViewById(R.id.textView_group);

        String sourceLangShort = _set.getSourceLang().getShortName();
        String targetLangShort = _set.getTargetLang().getShortName();
        String name = _set.getName();
        int sizeCount = _set.getEntries().size();

        _textView_group.setText(context.getString(R.string.vocabs_set_list_caption, sourceLangShort, targetLangShort, name, sizeCount));

        _button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_listener != null) {
                    int pos = _spinner.getSelectedItemPosition();

                    _listener.onClick(calcMinIndex(pos), calcMaxIndex(pos));
                }
            }
        });

        _spinner.setAdapter(new SpinnerAdapter() {
            @Override
            public View getDropDownView(final int pos, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = inflater.inflate(R.layout.exercise_vocab_set_spinner, null);

                TextView textView = (TextView) view.findViewById(R.id.textView);

                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setText(String.format("%d-%d", calcMinIndex(pos) + 1, calcMaxIndex(pos) + 1));

                return view;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public int getCount() {
                return _totalSize / _groupSize + (_totalSize % _groupSize > 0 ? 1 : 0);
            }

            @Override
            public Object getItem(int i) {
                return i;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(final int pos, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = inflater.inflate(R.layout.exercise_vocab_set_spinner, null);

                TextView textView = (TextView) view.findViewById(R.id.textView);

                //textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setText(String.format("%d-%d", calcMinIndex(pos) + 1, calcMaxIndex(pos) + 1));

                return view;
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
                return false;
            }
        });

        if (_set.getEntries().isEmpty()) {
            _button.setClickable(false);
            _button.setEnabled(false);

            _button.setBackground(getResources().getDrawable(R.drawable.list_group_background_disabled));

            _spinner.setVisibility(View.INVISIBLE);
        }
    }
}
