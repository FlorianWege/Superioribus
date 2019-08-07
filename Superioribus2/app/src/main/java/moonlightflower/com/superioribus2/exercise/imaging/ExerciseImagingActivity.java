package moonlightflower.com.superioribus2.exercise.imaging;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.exercise.ExerciseActivity;
import moonlightflower.com.superioribus2.exercise.ExerciseResult;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.Pixabay;
import moonlightflower.com.superioribus2.shared.storage.Storage;
import moonlightflower.com.superioribus2.vocab.VocabEntry;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class ExerciseImagingActivity extends MyActivity {
    private TextView _textView_set;
    private TextView _textView_num;
    private TextView _textView_localNum;

    private TextView _textView_source;

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

    private LinearLayout[] _layouts;
    private ImageBox[] _boxes;

    private int _curSel = -1;
    private int _size;
    private List<Integer> _targetIndexes;
    private int _localSourceIndex;

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
            _textView_localNum.setText(String.format("%d/%d", _curEntryIndex - _minEntryIndex + 1, _size));

            _textView_source.setText(String.format("%s", entry.getSource()));

            _switcher_check.setDisplayedChild(_switcher_check.indexOfChild(_button_check));

            for (int i = 0; i < _layouts.length; i++) {
                _boxes[i].reset();
            }

            _curSel = -1;
            _button_check.setClickable(false);
            _button_check.setEnabled(false);

            _targetIndexes.clear();

            for (int i = 0; i < _size; i++) {
                _targetIndexes.add(_minEntryIndex + i);
            }

            _targetIndexes.remove((Integer) _curEntryIndex);

            Collections.shuffle(_targetIndexes);

            int localSize = _maxEntryIndex - _minEntryIndex + 1;

            if (localSize > 4) localSize = 4;

            _localSourceIndex = new Random().nextInt(localSize);
            _tick++;

            final int finalTick = _tick;

            for (int i = 0; i < localSize; i++) {
                final int pos = i;

                String query = (pos == _localSourceIndex) ? _set.getEntries().get(_curEntryIndex).getTarget() : _set.getEntries().get(_targetIndexes.get(i)).getTarget();

                Pixabay.getQuery(getApplicationContext(), query, new Storage.ImgListener() {
                    @Override
                    public void fail(Exception exception) {
                        if (_tick > finalTick) return;

                        _boxes[pos].setImg(null);
                    }

                    @Override
                    public void ready(Bitmap bitmap) {
                        if (_tick > finalTick) return;

                        _boxes[pos].setImg(bitmap);
                    }
                });

                _boxes[i]._textView.setText(query);
                Log.e(getClass().getSimpleName(), "change " + i + " to " + query);
            }
        }
    }

    void check() {
        VocabEntry entry = _set.getEntries().get(_curEntryIndex);

        String target = entry.getTarget();

        if (_localSourceIndex == _curSel) {
            Log.e(getClass().getSimpleName(), "equal");
            _boxes[_localSourceIndex]._view.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            _results[_curEntryIndex] = true;
        } else {
            Log.e(getClass().getSimpleName(), "not equal");
            _boxes[_localSourceIndex]._view.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            _boxes[_curSel]._view.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            _results[_curEntryIndex] = false;
        }

        if (hasNext()) {
            _switcher_check.setDisplayedChild(_switcher_check.indexOfChild(_button_next));
        } else {
            _switcher_check.setDisplayedChild(_switcher_check.indexOfChild(_button_finish));
        }
    }

    private class ImageBox {
        private View _view;

        private FrameLayout _layout_border;
        private ImageView _imgView;
        private Button _button;
        private TextView _textView;
        private ProgressBar _progressBar;

        public void reset() {
            _view.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            _imgView.setImageDrawable(null);
            _progressBar.setVisibility(View.VISIBLE);
            _textView.setVisibility(View.INVISIBLE);
            _textView.setText("");
        }

        public void deselect() {
            _view.setBackgroundColor(getResources().getColor(android.R.color.black));
        }

        public void select() {
            _view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
        }

        public void setImg(Bitmap bitmap) {
            if (bitmap != null) {
                _imgView.setImageDrawable(null);
                _progressBar.setVisibility(View.VISIBLE);

                _progressBar.setVisibility(View.INVISIBLE);
                _imgView.setImageBitmap(bitmap);
            } else {
                _imgView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_delete));
                _progressBar.setVisibility(View.INVISIBLE);
                _textView.setVisibility(View.VISIBLE);
                _imgView.setVisibility(View.INVISIBLE);
            }
        }

        public ImageBox() {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            _view = inflater.inflate(R.layout.activity_exercise_imaging_imagebox, null);

            _layout_border = (FrameLayout) _view.findViewById(R.id.layout_border);
            _button = (Button) _view.findViewById(R.id.button);
            _imgView = (ImageView) _view.findViewById(R.id.imageView);
            _textView = (TextView) _view.findViewById(R.id.textView);
            _progressBar = (ProgressBar) _view.findViewById(R.id.progressBar);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise_imaging);

        _textView_set = (TextView) findViewById(R.id.textView_set);
        _textView_num = (TextView) findViewById(R.id.textView_num);
        _textView_localNum = (TextView) findViewById(R.id.textView_localNum);

        _textView_source = (TextView) findViewById(R.id.textView_source);

        _switcher_check = (ViewFlipper) findViewById(R.id.switcher_check);
        _button_check = (Button) findViewById(R.id.button_check);
        _button_next = (Button) findViewById(R.id.button_next);
        _button_finish = (Button) findViewById(R.id.button_finish);

        _button_check.setClickable(false);
        _button_check.setEnabled(false);

        _layouts = new LinearLayout[]{
                (LinearLayout) findViewById(R.id.layout_A),
                (LinearLayout) findViewById(R.id.layout_B),
                (LinearLayout) findViewById(R.id.layout_C),
                (LinearLayout) findViewById(R.id.layout_D)
        };

        _boxes = new ImageBox[_layouts.length];

        for (int i = 0; i < _layouts.length; i++) {
            final int pos = i;

            _boxes[i] = new ImageBox();

            _boxes[i]._button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < _layouts.length; i++) {
                        _boxes[i].deselect();
                    }

                    _boxes[pos].select();
                    _curSel = pos;
                    _button_check.setClickable(true);
                    _button_check.setEnabled(true);
                }
            });

            _boxes[i]._view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            _layouts[i].addView(_boxes[i]._view);
        }

        Intent intent = getIntent();

        if (intent != null) {
            int setIndex = intent.getIntExtra("setIndex", 0);

            _storage = Storage.getInstance(getApplicationContext());

            _set = _storage.getSets().get(setIndex);

            _minEntryIndex = intent.getIntExtra("minEntryIndex", 0);
            _maxEntryIndex = intent.getIntExtra("maxEntryIndex", 0);

            _size = _maxEntryIndex - _minEntryIndex + 1;

            _curEntryIndex = _minEntryIndex - 1;
            _results = new boolean[_size];

            for (int i = 0; i < _results.length; i++) {
                _results[i] = false;
            }

            _targetIndexes = new ArrayList<>(_size);

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