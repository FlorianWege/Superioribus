package moonlightflower.com.superioribus2.exercise;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.zip.Inflater;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.shared.storage.Storage;

public class ExerciseResult {
    private boolean _results[];
    private int _scoreFactor;

    private Activity _activity;
    private LinearLayout _layout;

    private Context _context;
    private Storage _storage;

    private LinearLayout _layout_gemGrid;

    public void apply(Activity activity, LinearLayout layout, FrameLayout rootLayout) {
        _activity = activity;
        _layout = layout;

        _context = _layout.getContext();

        _storage = Storage.getInstance(_context);

        Util.lockViews(_layout, false);

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View resultView = inflater.inflate(R.layout.exercise_result, null);

        rootLayout.addView(resultView);

        Point p = Util.getScreenSize(_activity);

        TranslateAnimation anim = new TranslateAnimation(0F, 0F, -p.y, 0F);

        anim.setInterpolator(new BounceInterpolator());
        anim.setDuration(1000);
        anim.setFillAfter(true);

        resultView.startAnimation(anim);

        TextView textView_score_label = (TextView) resultView.findViewById(R.id.textView_score_label);

        textView_score_label.setTextColor(_context.getResources().getColor(android.R.color.black));

        TextView textView_score = (TextView) resultView.findViewById(R.id.textView_score);

        int score = 0;

        for (int i = 0; i < _results.length; i++) {
            score += (_results[i] == true) ? 1 : 0;
        }

        int maxScore = _results.length;

        textView_score.setTextColor(_context.getResources().getColor(android.R.color.black));
        textView_score.setText(String.format("%d/%d (%d%%)", score, maxScore, (int) ((double) score) * 100 / maxScore));

        _storage.addXP(score*_scoreFactor);

        TextView textView_xp_label = (TextView) resultView.findViewById(R.id.textView_xp_label);

        textView_xp_label.setTextColor(_context.getResources().getColor(android.R.color.black));

        TextView textView_xp = (TextView) resultView.findViewById(R.id.textView_xp);

        textView_xp.setTextColor(_context.getResources().getColor(android.R.color.black));
        textView_xp.setText(String.format("%d (+%d*%d)", _storage.getXP(), score, _scoreFactor));

        Button button_return = (Button) resultView.findViewById(R.id.button_return);

        button_return.setTextColor(_context.getResources().getColor(android.R.color.black));

        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });

        _layout_gemGrid = (LinearLayout) resultView.findViewById(R.id._layout_gemGrid);

        int rowsCount = _results.length / 5;

        if (_results.length % 5 != 0) {
            rowsCount++;
        }

        if (rowsCount > 0) {
            LinearLayout[] rowLayouts = new LinearLayout[rowsCount];

            for (int i = 0; i < rowsCount; i++) {
                rowLayouts[i] = new LinearLayout(_context);

                _layout_gemGrid.addView(rowLayouts[i]);
            }

            for (int i = 0; i < _results.length; i++) {
                View gemView = inflater.inflate(R.layout.exercise_result_gem, null);

                int col = i % 5;
                int row = i / 5;

                if (!_results[i]) {
                    View gem = gemView.findViewById(R.id.gem);

                    gem.setBackground(_context.getResources().getDrawable(R.drawable.exercise_result_gem_red));
                }

                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                lParams.weight = 1F;

                gemView.setLayoutParams(lParams);

                rowLayouts[row].addView(gemView, col);
            }
        }
    }

    public ExerciseResult(boolean results[], int scoreFactor) {
        _results = results;
        _scoreFactor = scoreFactor;
    }
}