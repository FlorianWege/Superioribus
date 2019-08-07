package moonlightflower.com.superioribus2.main;

import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class AccountTypeView extends LinearLayout {
    private Context _context;

    private AuthenticatorDescription _description;

    public interface Listener {
    }

    private Listener _listener;

    private View _view_root;

    private TextView _textView_name;
    private ImageView _imageView;

    public AccountTypeView(Context context, AuthenticatorDescription description, Listener listener) {
        super(context);

        _context = context;
        _description = description;
        _listener = listener;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _view_root = inflater.inflate(R.layout.dialog_pick_account_type, this);

        _textView_name = (TextView) _view_root.findViewById(R.id.textView_name);

        _textView_name.setText(description.type);

        _imageView = (ImageView) _view_root.findViewById(R.id.imageView);

        _imageView.setImageDrawable(_context.getPackageManager().getDrawable(_description.packageName, _description.iconId, null));
    }
}