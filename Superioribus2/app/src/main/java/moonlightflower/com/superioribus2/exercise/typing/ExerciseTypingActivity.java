package moonlightflower.com.superioribus2.exercise.typing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.exercise.ExerciseActivity;
import moonlightflower.com.superioribus2.exercise.ExerciseResult;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.Pixabay;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.shared.storage.Storage;
import moonlightflower.com.superioribus2.vocab.VocabEntry;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class ExerciseTypingActivity extends MyActivity {
    private TextView _textView_set;
    private TextView _textView_num;
    private TextView _textView_localNum;

    private View _imgBorder;
    private ImageView _imageView_source;
    private ProgressBar _progressBar_source;
    private TextView _textView_source;
    private TextView _textView_target;
    private EditText _editText_targetUser;
    private EditText _editText_targetUser_measure;

    private ViewFlipper _switcher_check;
    private Button _button_check;
    private Button _button_next;
    private Button _button_finish;

    private Storage _storage;
    private VocabSet _set;
    private int _curEntryIndex;
    private int _minEntryIndex;
    private int _maxEntryIndex;
    private boolean _results[];

    void setImg(Bitmap bitmap) {
        if (bitmap != null) {
            _imageView_source.setImageDrawable(null);
            _progressBar_source.setVisibility(View.VISIBLE);

            _progressBar_source.setVisibility(View.INVISIBLE);
            _imageView_source.setImageBitmap(bitmap);
        } else {
            _imageView_source.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_delete));
            _progressBar_source.setVisibility(View.INVISIBLE);
        }
    }

    private boolean hasNext() {
        if (_curEntryIndex < _maxEntryIndex) {
            return true;
        }

        return false;
    }

    private int _tick = 0;

    void next() {
        _curEntryIndex++;

        if (_curEntryIndex > _maxEntryIndex) {
        } else {
            VocabEntry entry = _set.getEntries().get(_curEntryIndex);

            _textView_set.setText(String.format("set: %s", _set.getName()));
            _textView_num.setText(String.format("#%d-%d", _minEntryIndex + 1, _maxEntryIndex + 1));
            _textView_localNum.setText(String.format("%d/%d", _curEntryIndex - _minEntryIndex + 1, _maxEntryIndex - _minEntryIndex + 1));

            _textView_source.setText(String.format("%s", entry.getSource()));
            _textView_target.setText(String.format("%s", entry.getTarget()));
            _textView_target.setVisibility(View.INVISIBLE);

            _editText_targetUser.getText().clear();
            _editText_targetUser_measure.setText(entry.getTarget());

            _switcher_check.setDisplayedChild(_switcher_check.indexOfChild(_button_check));
            _imgBorder.setBackgroundColor(getResources().getColor(android.R.color.background_dark));

            _imageView_source.setImageDrawable(null);
            _progressBar_source.setVisibility(View.VISIBLE);

            String query = entry.getSource();

            _tick++;

            final int finalTick = _tick;

            Pixabay.getQuery(getApplicationContext(), query, new Storage.ImgListener(){
                @Override
                public void fail(Exception exception) {
                    if (_tick > finalTick) return;

                    setImg(null);
                }

                @Override
                public void ready(Bitmap bitmap)
                {
                    if (_tick > finalTick) return;

                    setImg(bitmap);
                }
            });
        }
    }

    void check() {
        VocabEntry entry = _set.getEntries().get(_curEntryIndex);

        String target = entry.getTarget();
        String targetUser = _editText_targetUser.getText().toString();

        _textView_target.setVisibility(View.VISIBLE);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < targetUser.length(); i++) {
            String color = (target.charAt(i) != targetUser.charAt(i)) ? "red" : "green";

            sb.append(String.format("<font color='%s'>", color));
            sb.append(targetUser.charAt(i));
            sb.append("</font>");
        }

        _editText_targetUser.setText(Html.fromHtml(sb.toString()));

        if (targetUser.equals(target)) {
            _imgBorder.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            _results[_curEntryIndex] = true;
        } else {
            _imgBorder.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            _results[_curEntryIndex] = false;
        }

        if (hasNext()) {
            _switcher_check.setDisplayedChild(_switcher_check.indexOfChild(_button_next));
        } else {
            _switcher_check.setDisplayedChild(_switcher_check.indexOfChild(_button_finish));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise_typing);

        _textView_set = (TextView) findViewById(R.id.textView_set);
        _textView_num = (TextView) findViewById(R.id.textView_num);
        _textView_localNum = (TextView) findViewById(R.id.textView_localNum);

        _imgBorder = findViewById(R.id.layout_imgBorder);
        _imageView_source = (ImageView) findViewById(R.id.imageView_source);
        _progressBar_source = (ProgressBar) findViewById(R.id.progressBar_source);
        _textView_source = (TextView) findViewById(R.id.textView_source);
        _textView_target = (TextView) findViewById(R.id.textView_target);
        _editText_targetUser = (EditText) findViewById(R.id.editText_targetUser);
        _editText_targetUser_measure = (EditText) findViewById(R.id.editText_targetUser_measure);

        _switcher_check = (ViewFlipper) findViewById(R.id.switcher_check);
        _button_check = (Button) findViewById(R.id.button_check);
        _button_next = (Button) findViewById(R.id.button_next);
        _button_finish = (Button) findViewById(R.id.button_finish);

        _progressBar_source.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(_editText_targetUser_measure.getWidth(), FrameLayout.LayoutParams.WRAP_CONTENT);

                lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

                _editText_targetUser.setLayoutParams(lParams);

                handler.postDelayed(this, 10);
            }
        }, 10);

        Intent intent = getIntent();

        if (intent != null) {
            int setIndex = intent.getIntExtra("setIndex", 0);

            _storage = Storage.getInstance(getApplicationContext());

            _set = _storage.getSets().get(setIndex);

            _minEntryIndex = intent.getIntExtra("minEntryIndex", 0);
            _maxEntryIndex = intent.getIntExtra("maxEntryIndex", 0);

            _curEntryIndex = _minEntryIndex - 1;
            _results = new boolean[_maxEntryIndex - _minEntryIndex + 1];

            for (int i = 0; i < _results.length; i++) {
                _results[i] = false;
            }

            next();
        }
    }

    public void button_exercises_onClick(View sender) {
        Intent intent = new Intent(this, ExerciseActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_check_onClick(View sender) {
        check();
    }

    public void button_next_onClick(View sender) {
        next();
    }

    public void button_finish_onClick(View sender) {
        ExerciseResult result = new ExerciseResult(_results, 5);

        result.apply(this, (LinearLayout) findViewById(R.id.layout_exercise), (FrameLayout) findViewById(R.id.layout_root));
    }
}