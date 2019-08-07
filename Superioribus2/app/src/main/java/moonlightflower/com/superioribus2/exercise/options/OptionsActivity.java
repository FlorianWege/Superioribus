package moonlightflower.com.superioribus2.exercise.options;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.exercise.ExerciseActivity;
import moonlightflower.com.superioribus2.exercise.XpBaseline;
import moonlightflower.com.superioribus2.shared.ConfirmDialog;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.shared.storage.Storage;

public class OptionsActivity extends MyActivity {
    private Storage _storage;

    private LinearLayout _layout_root;
    private SeekBar _seekBar_size;
    private TextView _textView_size;

    private void updateSize() {
        int size = Storage.getInstance(getApplicationContext()).getOptions().getI("exercise_size", 10);

        _textView_size.setText(String.format("%d", size));

        if (_seekBar_size.getProgress() != size) {
            _seekBar_size.setProgress(size);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_options);

        _storage = Storage.getInstance(getApplicationContext());

        _layout_root = (LinearLayout) findViewById(R.id.layout_root);

        _seekBar_size = (SeekBar) findViewById(R.id.seekBar_size);
        _textView_size = (TextView) findViewById(R.id.textView_size);

        _seekBar_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = Math.max(progress, 5);

                Storage.getInstance(getApplicationContext()).getOptions().setI("exercise_size", size);

                updateSize();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        updateSize();

        new XpBaseline(getActivity(), _layout_root);
    }

    public void button_exercises_onClick(View sender) {
        Intent intent = new Intent(this, ExerciseActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_resetXP_onClick(View sender) {
        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setArgs(getString(R.string.reset_xp_query), new ConfirmDialog.Listener() {
            @Override
            public void onDecline() {

            }

            @Override
            public void onAccept() {
                _storage.addXP(-_storage.getXP());
                Util.printToast(getApplicationContext(), getString(R.string.reset_xp_note), Toast.LENGTH_LONG);
            }

            @Override
            public void onDismiss() {

            }
        });

        showDialog(dialog);
    }

    public void button_clearImgCache_onClick(View sender) {
        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setArgs(getString(R.string.clear_img_cache_query), new ConfirmDialog.Listener() {
            @Override
            public void onDecline() {

            }

            @Override
            public void onAccept() {
                try {
                    _storage.clearImgs();

                    Util.printToast(getApplicationContext(), getString(R.string.clear_img_cache_note), Toast.LENGTH_LONG);
                } catch (Exception e) {
                    Util.printToast(getApplicationContext(), getString(R.string.clear_img_cache_fail), Toast.LENGTH_LONG);
                    Util.printException(getApplicationContext(), e);
                }
            }

            @Override
            public void onDismiss() {

            }
        });

        showDialog(dialog);
    }

    public void button_clearVocabs_onClick(View sender) {
        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setArgs(getString(R.string.clear_vocabs_query), new ConfirmDialog.Listener() {
            @Override
            public void onDecline() {

            }

            @Override
            public void onAccept() {
                try {
                    _storage.clearSets();

                    Util.printToast(getApplicationContext(), getString(R.string.clear_vocabs_note), Toast.LENGTH_LONG);
                } catch (Exception e) {
                    Util.printToast(getApplicationContext(), getString(R.string.clear_vocabs_fail), Toast.LENGTH_LONG);
                    Util.printException(getApplicationContext(), e);
                }
            }

            @Override
            public void onDismiss() {

            }
        });

        showDialog(dialog);
    }
}