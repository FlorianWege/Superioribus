package moonlightflower.com.superioribus2.exercise;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.shared.storage.Storage;

public class XpBaseline {
    private MyActivity _activity;
    private Context _context;
    private Storage _storage;

    private LinearLayout _layout_stats;
    private TextView _textView_name;
    private TextView _textView_xp;

    private void updateName() {
        Storage.Acc acc = _storage.getAcc();

        _textView_name.setText(acc.getName());
    }

    private void updateXP() {
        _textView_xp.setText(_context.getString(R.string.baseline_xp, _storage.getXP()));
    }

    public XpBaseline(MyActivity activity, LinearLayout layout) {
        _activity = activity;
        _context = layout.getContext();

        _storage = Storage.getInstance(_context);

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View baselineView = inflater.inflate(R.layout.xp_baseline, null);

        layout.addView(baselineView);

        _layout_stats = (LinearLayout) baselineView.findViewById(R.id.layout_stats);

        _layout_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.requestPermission(Manifest.permission.GET_ACCOUNTS);
            }
        });

        _textView_name = (TextView) baselineView.findViewById(R.id.textView_name);

        updateName();

        _textView_xp = (TextView) baselineView.findViewById(R.id.textView_xp);

        updateXP();

        _storage.addStatsChangedListener(new Storage.StatsChangedListener() {
            @Override
            public void accChanged(Storage.Acc val) {
                updateName();
            }

            @Override
            public void xpChanged(int prevVal, int val) {
                updateXP();
            }
        });
    }
}