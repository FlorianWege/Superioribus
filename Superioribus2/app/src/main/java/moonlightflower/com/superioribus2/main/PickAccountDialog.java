package moonlightflower.com.superioribus2.main;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.MyActivity;
import moonlightflower.com.superioribus2.shared.MyDialog;
import moonlightflower.com.superioribus2.shared.Util;
import moonlightflower.com.superioribus2.shared.storage.Storage;

public class PickAccountDialog extends MyDialog {
    private Context _context;
    private Storage _storage;

    private AccountExpandableListAdapter _adapter;
    private ExpandableListView _listView;

    public interface Listener extends MyDialog.Listener {
        void ready(Account result);
    }

    private Listener _listener;

    public void setListener(Listener val) {
        super.setListener(val);

        _listener = val;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().setTitle(getString(R.string.account_query));

        View view = inflater.inflate(R.layout.dialog_pick_account, container, false);

        _context = view.getContext();
        _storage = Storage.getInstance(_context);

        _listView = (ExpandableListView) view.findViewById(R.id.listView);

        try {
            _adapter = new AccountExpandableListAdapter(_context, new AccountExpandableListAdapter.Listener() {
                @Override
                public void onEntryClicked(Account acc) {
                    if (_listener != null) {
                        _listener.ready(acc);
                    }

                    dismiss();
                }
            });

            _listView.setAdapter(_adapter);

            for (int i = 0; i < _adapter.getTypes().size(); i++) {
                _listView.expandGroup(i);
            }
        } catch (Util.PermissionException e) {
            dismiss();
        }

        return view;
    }

    private MyActivity _activity;

    public void setArgs(MyActivity val, Listener listener) {
        _activity = val;
        setListener(listener);
    }
}