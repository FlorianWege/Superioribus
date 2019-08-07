package moonlightflower.com.superioribus2.vocab;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import moonlightflower.com.superioribus2.R;

public class VocabListSetView extends LinearLayout {
    private VocabSet _set;

    private View _view_root;
    private Button _button_group;
    private TextView _textView_group;
    private Button _button_link;
    private Drawable _defaultBackground;

    public interface Listener {
        void onGroupClicked();
        void onGroupLongClicked();
        void onLinkClicked();
    }

    public void setExpandedStyle(boolean expanded) {
        if (expanded) {
            _button_group.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.list_group_background_pressed, null));
        } else {
            _button_group.setBackground(_defaultBackground);
        }
    }

    public VocabListSetView(Context context, VocabSet set, final Listener listener) {
        super(context);

        _set = set;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _view_root = inflater.inflate(R.layout.vocab_list_set, this);

        _button_group = (Button) _view_root.findViewById(R.id.button_group);

        _button_group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onGroupClicked();
                }
            }
        });
        _button_group.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener != null) {
                    listener.onGroupLongClicked();
                }

                return true;
            }
        });

        _textView_group = (TextView) _view_root.findViewById(R.id.textView_group);

        String sourceLangShort = _set.getSourceLang().getShortName();
        String targetLangShort = _set.getTargetLang().getShortName();
        String name = _set.getName();
        int sizeCount = _set.getEntries().size();

        _textView_group.setText(context.getString(R.string.vocabs_set_list_caption, sourceLangShort, targetLangShort, name, sizeCount));

        _button_link = (Button) findViewById(R.id.button_link);

        _button_link.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onLinkClicked();
                }
            }
        });

        _defaultBackground = _button_group.getBackground();

        if (_set.getEntries().isEmpty()) {
            //_button_group.setClickable(false);
            //_button_group.setEnabled(false);

            _button_group.setBackground(getResources().getDrawable(R.drawable.list_group_background_disabled));

            //_button_link.setVisibility(View.GONE);
        }
    }
}