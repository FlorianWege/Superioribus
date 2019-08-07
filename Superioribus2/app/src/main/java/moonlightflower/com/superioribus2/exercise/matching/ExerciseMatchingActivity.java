package moonlightflower.com.superioribus2.exercise.matching;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.exercise.ExerciseActivity;
import moonlightflower.com.superioribus2.exercise.ExerciseResult;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.shared.storage.Storage;
import moonlightflower.com.superioribus2.vocab.VocabEntry;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class ExerciseMatchingActivity extends MyActivity {
    private FrameLayout _layout_root;
    private LinearLayout _layout_exercise;

    private Storage _storage;
    private VocabSet _set;
    private int _minEntryIndex;
    private int _maxEntryIndex;
    private int _curEntryIndex;
    private int _curSize;
    private boolean _results[];

    private Map<View, VocabEntry> _viewEntryMap = new HashMap<>();

    private LinearLayout _layout_source;
    private LinearLayout _layout_target;

    private TextView _textView_set;
    private TextView _textView_num;
    private TextView _textView_localNum;

    private ViewFlipper _switcher_check;
    private Button _button_check;
    private Button _button_next;
    private Button _button_finish;

    private class SourceClipData extends ClipData {
        private View _sourceView;

        public SourceClipData(CharSequence label, String[] mimeTypes, Item item) {
            super(label, mimeTypes, new ClipData.Item("item"));
        }

        public SourceClipData(View sourceView) {
            this("sourceClip", new String[1], null);

            _sourceView = sourceView;
        }
    }

    private class TargetClipData extends ClipData {
        private View _sourceView;

        private TargetClipData(CharSequence label, String[] mimeTypes, Item item) {
            super(label, mimeTypes, new ClipData.Item("item"));
        }

        public TargetClipData(View sourceView) {
            this("targetClip", new String[1], null);

            _sourceView = sourceView;
        }
    }

    private ClipData _dragData = null;

    private void addEntry(VocabEntry entry) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View sourceView = inflater.inflate(R.layout.matching_entry, null);
        final View targetView = inflater.inflate(R.layout.matching_entry, null);

        _viewEntryMap.put(sourceView, entry);
        _viewEntryMap.put(targetView, entry);

        TextView sourceTextView = (TextView) sourceView.findViewById(R.id.textView);
        TextView targetTextView = (TextView) targetView.findViewById(R.id.textView);

        sourceTextView.setText(entry.getSource());
        targetTextView.setText(entry.getTarget());

        sourceTextView.setTextColor(getResources().getColor(android.R.color.black));
        targetTextView.setTextColor(getResources().getColor(android.R.color.black));

        sourceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    _dragData = new SourceClipData(sourceView);

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(sourceView);

                    sourceView.startDrag(ClipData.newPlainText(sourceView.toString(), sourceView.toString()), shadowBuilder, sourceView, 0);
                }

                return true;
            }
        });

        sourceView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (!(_dragData instanceof SourceClipData)) {
                    return false;
                }

                SourceClipData data = (SourceClipData) _dragData;

                if (((SourceClipData) _dragData)._sourceView == sourceView) {
                    return false;
                }

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        sourceView.setBackground(getResources().getDrawable(R.drawable.matching_background_drag_accept));
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        sourceView.setBackground(null);
                        break;
                    case DragEvent.ACTION_DROP:
                        sourceView.setBackground(null);

                        int pos = _layout_source.indexOfChild(sourceView);

                        _layout_source.removeView(data._sourceView);
                        _layout_source.addView(data._sourceView, pos);

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        sourceView.setBackground(null);
                        break;
                }

                return true;
            }
        });

        targetView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    _dragData = new TargetClipData(targetView);

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(targetView);

                    targetView.startDrag(ClipData.newPlainText("tag", targetView.toString()), shadowBuilder, targetView, 0);
                }

                return true;
            }
        });

        targetView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (!(_dragData instanceof TargetClipData)) {
                    return false;
                }

                TargetClipData data = (TargetClipData) _dragData;

                if (data._sourceView == targetView) {
                    return false;
                }

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        targetView.setBackground(getResources().getDrawable(R.drawable.matching_background_drag_accept));
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        targetView.setBackground(null);
                        break;
                    case DragEvent.ACTION_DROP:
                        targetView.setBackground(null);

                        int pos = _layout_target.indexOfChild(targetView);

                        _layout_target.removeView(data._sourceView);
                        _layout_target.addView(data._sourceView, pos);

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        targetView.setBackground(null);
                        break;
                }

                return true;
            }
        });

        _layout_source.addView(sourceView);
        _layout_target.addView(targetView);
    }

    private boolean hasNext() {
        if (_curEntryIndex + _curSize - 1 < _maxEntryIndex) {
            return true;
        }

        return false;
    }

    private void next() {
        _curEntryIndex += _curSize;

        if (_curEntryIndex > _maxEntryIndex) {
        } else {
            _curSize = Math.min(4, _maxEntryIndex - _curEntryIndex + 1);

            int localLastIndex = _curEntryIndex - _minEntryIndex + _curSize - 1;

            _textView_set.setText(String.format("set: %s", _set.getName()));
            _textView_num.setText(String.format("#%d-%d", _minEntryIndex + 1, _maxEntryIndex + 1));
            _textView_localNum.setText(String.format("%d-%d/%d", _curEntryIndex - _minEntryIndex + 1, localLastIndex + 1, _maxEntryIndex - _minEntryIndex + 1));

            _layout_source.removeAllViews();
            _layout_target.removeAllViews();
            _viewEntryMap.clear();

            List<VocabEntry> entries = _set.getEntries();

            for (int i = _curEntryIndex; i <= localLastIndex; i++) {
                addEntry(entries.get(i));
            }

            List<View> sourceViews = new ArrayList<>();

            for (int i = 0; i < _layout_source.getChildCount(); i++) {
                sourceViews.add(_layout_source.getChildAt(i));
            }

            Collections.shuffle(sourceViews);

            List<View> targetViews = new ArrayList<>();

            for (int i = 0; i < _layout_target.getChildCount(); i++) {
                targetViews.add(_layout_target.getChildAt(i));
            }

            Collections.shuffle(targetViews);

            _layout_source.removeAllViews();
            _layout_target.removeAllViews();

            for (View view : sourceViews) {
                _layout_source.addView(view);
            }
            for (View view : targetViews) {
                _layout_target.addView(view);
            }

            _switcher_check.setDisplayedChild(_switcher_check.indexOfChild(_button_check));
        }
    }

    void check() {
        for (int i = 0; i < _layout_source.getChildCount(); i++) {
            View sourceView = _layout_source.getChildAt(i);
            View targetView = _layout_target.getChildAt(i);

            sourceView.setOnTouchListener(null);
            targetView.setOnTouchListener(null);
            sourceView.setOnDragListener(null);
            targetView.setOnDragListener(null);

            VocabEntry sourceEntry = _viewEntryMap.get(sourceView);
            VocabEntry targetUserEntry = _viewEntryMap.get(targetView);

            String source = sourceEntry.getSource();
            String target = sourceEntry.getTarget();
            String targetUser = targetUserEntry.getTarget();

            if (targetUser.equals(target)) {
                sourceView.setBackground(getResources().getDrawable(R.drawable.matching_background_success));
                targetView.setBackground(getResources().getDrawable(R.drawable.matching_background_success));
                _results[_curEntryIndex + i - _minEntryIndex] = true;
            } else {
                sourceView.setBackground(getResources().getDrawable(R.drawable.matching_background_failure));
                targetView.setBackground(getResources().getDrawable(R.drawable.matching_background_failure));
                _results[_curEntryIndex + i - _minEntryIndex] = false;
            }
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

        setContentView(R.layout.activity_exercise_matching);

        _layout_root = (FrameLayout) findViewById(R.id.layout_root);
        _layout_exercise = (LinearLayout) findViewById(R.id.layout_exercise);

        _textView_set = (TextView) findViewById(R.id.textView_set);
        _textView_num = (TextView) findViewById(R.id.textView_num);
        _textView_localNum = (TextView) findViewById(R.id.textView_localNum);

        _layout_source = (LinearLayout) findViewById(R.id.layout_source);
        _layout_target = (LinearLayout) findViewById(R.id.layout_target);

        _switcher_check = (ViewFlipper) findViewById(R.id.switcher_check);
        _button_check = (Button) findViewById(R.id.button_check);
        _button_next = (Button) findViewById(R.id.button_next);
        _button_finish = (Button) findViewById(R.id.button_finish);

        Intent intent = getIntent();

        if (intent != null) {
            int setIndex = intent.getIntExtra("setIndex", 0);

            _storage = Storage.getInstance(getApplicationContext());

            _set = _storage.getSets().get(setIndex);

            _minEntryIndex = intent.getIntExtra("minEntryIndex", 0);
            _maxEntryIndex = intent.getIntExtra("maxEntryIndex", 0);

            _curEntryIndex = _minEntryIndex - 1;
            _curSize = 1;
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
        ExerciseResult result = new ExerciseResult(_results, 1);

        result.apply(this, _layout_exercise, _layout_root);
    }
}
