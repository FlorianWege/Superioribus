package moonlightflower.com.superioribus2.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import moonlightflower.com.superioribus2.R;

public class ConfirmDialog extends MyDialog {
    private TextView _textView_title;
    private Button _button_ok;
    private Button _button_abort;

    public interface Listener extends MyDialog.Listener {
        void onDecline();
        void onAccept();
    }

    private Listener _listener;

    public void setListener(Listener val) {
        super.setListener(val);

        _listener = val;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_confirm, container, false);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //getDialog().setTitle(_title);

        _textView_title = (TextView) view.findViewById(R.id.textView_title);

        _textView_title.setText(_title);
        //_textView_title.setVisibility(View.GONE);

        _button_ok = (Button) view.findViewById(R.id.button_ok);

        _button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                if (_listener != null) {
                    _listener.onAccept();
                }
            }
        });

        _button_abort = (Button) view.findViewById(R.id.button_abort);

        _button_abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                if (_listener != null) {
                    _listener.onDecline();
                }
            }
        });

        return view;
    }

    private String _title;

    public void setArgs(String title, Listener listener) {
        _title = title;
        setListener(listener);
    }
}
