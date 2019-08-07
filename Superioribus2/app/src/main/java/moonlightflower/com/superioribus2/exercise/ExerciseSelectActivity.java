package moonlightflower.com.superioribus2.exercise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.exercise.imaging.ExerciseImagingActivity;
import moonlightflower.com.superioribus2.exercise.matching.ExerciseMatchingActivity;
import moonlightflower.com.superioribus2.exercise.typing.ExerciseTypingActivity;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.storage.Storage;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class ExerciseSelectActivity extends MyActivity {
    private TextView _textView_set;
    private TextView _textView_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise_select);

        _textView_set = (TextView) findViewById(R.id.textView_set);
        _textView_num = (TextView) findViewById(R.id.textView_num);

        Intent intent = getIntent();

        if (intent != null) {
            int setIndex = intent.getIntExtra("setIndex", 0);

            VocabSet set = Storage.getInstance(getApplicationContext()).getSets().get(setIndex);

            int minEntryIndex = intent.getIntExtra("minEntryIndex", 0);
            int maxEntryIndex = intent.getIntExtra("maxEntryIndex", 0);

            _textView_set.setText(String.format("Set: %s", set.getName()));
            _textView_num.setText(String.format("#%d-%d", minEntryIndex, maxEntryIndex));

            ((Button) findViewById(R.id.button_unknown)).setBackground(getResources().getDrawable(R.drawable.button_background_disabled));
        }

        LinearLayout layout_root = (LinearLayout) findViewById(R.id.layout_root);

        new XpBaseline(getActivity(), layout_root);
    }

    public void button_exercises_onClick(View sender) {
        Intent intent = new Intent(this, ExerciseActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_typing_onClick(View sender) {
        Intent intent = new Intent(this, ExerciseTypingActivity.class);

        intent.putExtras(getIntent());

        startActivity(intent);
    }

    public void button_matching_onClick(View sender) {
        Intent intent = new Intent(this, ExerciseMatchingActivity.class);

        intent.putExtras(getIntent());

        startActivity(intent);
    }

    public void button_imaging_onClick(View sender) {
        Intent intent = new Intent(this, ExerciseImagingActivity.class);

        intent.putExtras(getIntent());

        startActivity(intent);
    }
}