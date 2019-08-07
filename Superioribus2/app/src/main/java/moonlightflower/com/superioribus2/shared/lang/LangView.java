package moonlightflower.com.superioribus2.shared.lang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import moonlightflower.com.superioribus2.R;

public class LangView extends LinearLayout {
    private Context _context;
    private Lang _lang;

    private View _view_root;
    private TextView _textView_name;

    @Override
    public void setSelected(boolean selected) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _view_root = inflater.inflate(selected ? R.layout.lang_selected : R.layout.lang, null);

        this.removeAllViews();

        this.addView(_view_root);

        _view_root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        _textView_name = (TextView) _view_root.findViewById(R.id.textView_name);

        _textView_name.setText(_lang.getName());
    }

    public LangView(Context context, Lang lang, OnClickListener clickListener) {
        super(context);

        _context = context;
        _lang = lang;

        setSelected(false);
    }
}