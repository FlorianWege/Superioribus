package moonlightflower.com.superioribus2.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import build.BuildInfo;
import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.exercise.ExerciseActivity;
import moonlightflower.com.superioribus2.exercise.XpBaseline;
import moonlightflower.com.superioribus2.shared.lang.Lang;
import moonlightflower.com.superioribus2.shared.lang.LangAdapter;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.vocab.VocabActivity;

import java.util.List;

public class MainActivity extends MyActivity {
    private LinearLayout _layout_root;

    private TextView _textView_header;
    private TextView _textView_sdk;
    private TextView _textView_buildNo;

    private View _layout_translator;

    private TextView _textView_sourceLang;
    private TextView _textView_targetLang;

    private LangAdapter _adapter_source;
    private LangAdapter _adapter_target;

    private Spinner _spinner_source;
    private Spinner _spinner_target;

    private ProgressBar _progressBar_translator_lang_source;
    private ProgressBar _progressBar_translator_lang_target;

    private Lang _sourceLang = null;
    private Lang _targetLang = null;

    private EditText _editText_source;
    private EditText _editText_target;

    private Button _button_swap;
    private ToggleButton _button_translate;

    private ProgressBar _progressBar_translator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //requestPermission(Manifest.permission.GET_ACCOUNTS);

        _layout_root = (LinearLayout) findViewById(R.id.layout_root);

        _textView_header = (TextView) findViewById(R.id.textView_header);

        _textView_header.setTypeface(Typeface.createFromAsset(getAssets(), "ComingSoon.ttf"));

        _textView_sdk = (TextView) findViewById(R.id.textView_sdk);

        _textView_sdk.setText(getString(R.string.main_sdk, Build.VERSION.SDK_INT));

        _textView_buildNo = (TextView) findViewById(R.id.textView_buildNo);

        _textView_buildNo.setText(getString(R.string.main_buildNo, BuildInfo.BUILD_TIME));

        _layout_translator = findViewById(R.id.layout_translator);

        _textView_sourceLang = (TextView) findViewById(R.id.textView_sourceLang);
        _textView_targetLang = (TextView) findViewById(R.id.textView_targetLang);

        _spinner_source = (Spinner) findViewById(R.id.spinner_translation_lang_source);
        _spinner_target = (Spinner) findViewById(R.id.spinner_translation_lang_target);

        _progressBar_translator_lang_source = (ProgressBar) findViewById(R.id.progressBar_translation_lang_source);
        _progressBar_translator_lang_target = (ProgressBar) findViewById(R.id.progressBar_translation_lang_target);

        _progressBar_translator_lang_source.setVisibility(View.INVISIBLE);
        _progressBar_translator_lang_target.setVisibility(View.INVISIBLE);

        _adapter_source = new LangAdapter(getApplicationContext(), new LangAdapter.ItemClickListener() {
            @Override
            public void itemsChanged() {
                _spinner_source.setAdapter(null);

                _spinner_source.setAdapter(_adapter_source);
            }

            @Override
            public void itemClick(int pos) {
                _adapter_source.setSelectedLang((Lang) _spinner_source.getSelectedItem());

                _spinner_source.setSelected(true);
                _spinner_source.setSelection(pos, true);
            }
        });
        _adapter_target = new LangAdapter(getApplicationContext(), new LangAdapter.ItemClickListener() {
            @Override
            public void itemsChanged() {
                _spinner_target.setAdapter(null);

                _spinner_target.setAdapter(_adapter_target);
            }

            @Override
            public void itemClick(int pos) {
                _spinner_target.setSelection(pos);
            }
        });

        _spinner_source.setAdapter(_adapter_source);
        _spinner_target.setAdapter(_adapter_target);

        _spinner_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                Lang lang = (Lang) _adapter_source.getItem(pos);

                _adapter_source.setSelectedLang(lang);

                _textView_sourceLang.setText(lang.getName());

                if (!lang.equals(_sourceLang)) {
                    _sourceLang = lang;

                    doTranslationAndDetection(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                _textView_sourceLang.setText("?");
            }
        });

        _spinner_target.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Lang lang = (Lang) _adapter_target.getItem(i);

                _adapter_target.setSelectedLang(lang);

                _textView_targetLang.setText(lang.getName());

                if (!lang.equals(_targetLang)) {
                    _targetLang = lang;

                    doTranslationAndDetection(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                _textView_targetLang.setText("?");
            }
        });

        _editText_source = (EditText) findViewById(R.id.editText_translation_source);

        _editText_source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                doTranslationAndDetection(true);
            }
        });

        _editText_target = (EditText) findViewById(R.id.editText_translation_target);

        _editText_target.setInputType(InputType.TYPE_NULL);

        _button_swap = (Button) findViewById(R.id.button_translation_swap);

        _button_swap.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                swapLangs(true);

                return true;
            }
        });

        _button_translate = (ToggleButton) findViewById(R.id.button_translate);

        loadLangs();

        _progressBar_translator = (ProgressBar) findViewById(R.id.progressBar_translator);

        _progressBar_translator.setVisibility(View.INVISIBLE);

        new XpBaseline(getActivity(), _layout_root);
    }

    private void loadLangs() {
        _progressBar_translator_lang_source.setVisibility(View.VISIBLE);
        _progressBar_translator_lang_target.setVisibility(View.VISIBLE);

        Yandex.getLangs(getApplicationContext(), new Yandex.GetLangsTask.Listener() {
            @Override
            public void finished(Exception exception) {
                if (exception != null) {
                    Util.printException(getApplicationContext(), exception);

                    Util.printToast(getApplicationContext(), getString(R.string.translator_load_langs_fail), Toast.LENGTH_SHORT);
                }

                _progressBar_translator_lang_source.setVisibility(View.INVISIBLE);
                _progressBar_translator_lang_target.setVisibility(View.INVISIBLE);
            }

            @Override
            public void ready(List<Lang> sourceLangs, List<Lang> targetLangs) {
                List<Lang> prevLangs = _adapter_source.getLangs();

                for (Lang lang : prevLangs) {
                    if (!sourceLangs.contains(lang)) {
                        _adapter_source.remove(lang);
                    }
                }

                for (Lang lang : sourceLangs) {
                    _adapter_source.add(lang);
                }

                prevLangs = _adapter_target.getLangs();

                for (Lang lang : prevLangs) {
                    if (!targetLangs.contains(lang)) {
                        _adapter_target.remove(lang);
                    }
                }

                for (Lang lang : targetLangs) {
                    _adapter_target.add(lang);
                }

                _adapter_source.add(Lang.AUTO);

                /*_adapter_source.clear();
                _adapter_target.clear();

                _adapter_source.add(Lang.AUTO);

                _adapter_source.add(sourceLangs);
                _adapter_target.add(targetLangs);*/
            }
        });
    }

    private boolean _translation_locked = false;
    private String _translation_text;

    private void doTranslationAndDetection(boolean delayed) {
        if (delayed && !_button_translate.isChecked()) return;

        _translation_text = _editText_source.getText().toString();

        _editText_target.getText().clear();

        if (delayed) {
            if (_translation_locked) return;

            _translation_locked = true;
            _textView_sourceLang.setText("...");
            _editText_target.setText("...");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    _translation_locked = false;

                    doDetect();
                    doTranslation();
                }
            }, 1000);
        } else {
            doDetect();
            doTranslation();
        }
    }

    private void doDetect() {
        Lang sourceLang = (Lang) _spinner_source.getSelectedItem();

        if (sourceLang.equals(Lang.AUTO)) {
            _textView_sourceLang.setText(getString(R.string.translator_loading));

            if (_translation_text.isEmpty()) {
                _textView_sourceLang.setText(getString(R.string.translator_lang_unknown));
            } else {
                Yandex.detect(getApplicationContext(), _translation_text, new Yandex.DetectTask.Listener() {
                    @Override
                    public void finished(Exception exception) {
                        _textView_sourceLang.setText(getString(R.string.translator_lang_unknown));

                        if (exception != null) {
                            Util.printException(getApplicationContext(), exception);

                            Util.printToast(getApplicationContext(), getString(R.string.translator_detect_fail), Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void ready(Lang result) {
                        _textView_sourceLang.setText((result == null) ? getString(R.string.translator_lang_unknown) : result.getName());
                    }
                });
            }
        } else {
            _textView_sourceLang.setText(sourceLang.getName());
        }
    }

    private void doTranslation() {
        Lang sourceLang = (Lang) _spinner_source.getSelectedItem();
        Lang targetLang = (Lang) _spinner_target.getSelectedItem();

        if (targetLang == null || targetLang == Lang.NONE) {
            _editText_target.getText().clear();

            return;
        }

        _editText_target.setText(getString(R.string.translator_loading));

        String sourceS = _translation_text;

        if (sourceS.isEmpty()) {
            _editText_target.getText().clear();
        } else {
            Yandex.translate(getApplicationContext(), sourceLang, targetLang, sourceS, new Yandex.TranslateTask.Listener() {
                @Override
                public void finished(Exception exception) {
                    _editText_target.getText().clear();

                    if (exception != null) {
                        Util.printException(getApplicationContext(), exception);

                        Util.printToast(getApplicationContext(), getString(R.string.translator_loading), Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void ready(String result) {
                    _editText_target.setText(result);
                }
            });
        }
    }

    public void button_exercises_onClick(View sender) {
        Intent intent = new Intent(this, ExerciseActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    public void button_vocabulary_onClick(View sender) {
        Intent intent = new Intent(this, VocabActivity.class);

        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    private void swapLangs(boolean alsoSwapText) {
        Lang sourceLang = (Lang) _spinner_source.getSelectedItem();
        Lang targetLang = (Lang) _spinner_target.getSelectedItem();

        int sourcePos = -1;

        for (int i = 0; i < _adapter_source.getCount(); i++) {
            if (_adapter_source.getItem(i) == targetLang) {
                sourcePos = i;

                break;
            }
        }

        int targetPos = -1;

        for (int i = 0; i < _adapter_target.getCount(); i++) {
            if (_adapter_target.getItem(i) == sourceLang) {
                targetPos = i;

                break;
            }
        }

        if (sourcePos != -1 && targetPos != -1) {
            _spinner_source.setSelection(sourcePos);
            _spinner_target.setSelection(targetPos);

            if (alsoSwapText) {
                String sourceText = _editText_source.getText().toString();
                String targetText = _editText_target.getText().toString();

                //in this order
                _editText_target.setText(sourceText);

                _editText_source.setText(targetText);
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.translator_swap_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public void button_swap_onClick(View sender) {
        swapLangs(false);
    }

    public void button_translate_onClick(View sender) {
        if (_button_translate.isChecked()) {
            loadLangs();

            doTranslationAndDetection(false);
        }
    }
}