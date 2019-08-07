package moonlightflower.com.superioribus2.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import moonlightflower.com.superioribus2.main.MainActivity;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.exercise.options.OptionsActivity;
import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.storage.Storage;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class ExerciseActivity extends MyActivity {
    private ViewFlipper _viewSwitcher_sets;

    private ListView _listView;
    private ExerciseVocabSetsAdapter _adapter;

    private void loadVocabSets() {
        //_listView.setAdapter(null);
        _listView.setAdapter(_adapter);

        if (_adapter.getSets().isEmpty()) {
            _viewSwitcher_sets.setDisplayedChild(_viewSwitcher_sets.indexOfChild(findViewById(R.id.textView_noSets)));
        } else {
            _viewSwitcher_sets.setDisplayedChild(_viewSwitcher_sets.indexOfChild(findViewById(R.id.listView)));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise);

        _viewSwitcher_sets = (ViewFlipper) findViewById(R.id.viewSwitcher_sets);

        _viewSwitcher_sets.setDisplayedChild(_viewSwitcher_sets.indexOfChild(findViewById(R.id.textView_noSets)));

        _listView = (ListView) findViewById(R.id.listView);

        _adapter = new ExerciseVocabSetsAdapter(getApplicationContext(), new ExerciseVocabSetsAdapter.Listener() {
            @Override
            public void onSetsChanged() {
                _listView.setAdapter(null);
                _listView.setAdapter(_adapter);
            }

            @Override
            public void onSetClicked(int setIndex, int minEntryIndex, int maxEntryIndex) {
                Intent intent = new Intent(getApplicationContext(), ExerciseSelectActivity.class);

                intent.putExtra("setIndex", setIndex);
                intent.putExtra("minEntryIndex", minEntryIndex);
                intent.putExtra("maxEntryIndex", maxEntryIndex);

                startActivity(intent);
            }
        });

        loadVocabSets();

        LinearLayout layout_root = (LinearLayout) findViewById(R.id.layout_root);

        new XpBaseline(getActivity(), layout_root);
    }

    public void button_main_onClick(View sender) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_options_onClick(View sender) {
        Intent intent = new Intent(this, OptionsActivity.class);

        startActivity(intent);
    }
}