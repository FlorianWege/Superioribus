package moonlightflower.com.superioribus2.main;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import moonlightflower.com.superioribus2.R;

public class AccountView extends LinearLayout {
    private Context _context;
    private Account _acc;

    public interface Listener {
        void onClick(Account acc);
    }

    private Listener _listener;

    private View _view_root;

    private TextView _textView_name;

    public AccountView(Context context, Account acc, Listener listener) {
        super(context);

        _context = context;
        _acc = acc;
        _listener = listener;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _view_root = inflater.inflate(R.layout.dialog_pick_account_entry, this);

        _textView_name = (TextView) _view_root.findViewById(R.id.textView_name);

        _textView_name.setText(acc.name);

        _textView_name.setClickable(true);
        _textView_name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_listener != null) {
                    _listener.onClick(_acc);
                }
            }
        });
    }
}