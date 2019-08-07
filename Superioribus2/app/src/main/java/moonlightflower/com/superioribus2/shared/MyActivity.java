package moonlightflower.com.superioribus2.shared;

import android.Manifest;
import android.accounts.Account;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.main.PickAccountDialog;
import moonlightflower.com.superioribus2.shared.storage.Storage;
import moonlightflower.com.superioribus2.vocab.VocabAddSetDialog;
import moonlightflower.com.superioribus2.vocab.VocabSet;

public class MyActivity extends AppCompatActivity {
    public MyActivity getActivity() {
        return this;
    }

    protected Storage _storage;

    public static class MyFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            return inflater.inflate(R.layout.overlay, container, false);
        }
    }

    private FrameLayout _frameLayout;
    private View _innerView;

    private ProgressBar _progressBar;

    private int _progressBar_showC = 0;

    private void showProgressOverlay(boolean show) {
        if (show) {
            _progressBar_showC++;
        } else {
            _progressBar_showC--;
        }

        if (_progressBar_showC > 0) {
            _progressBar.setVisibility(View.VISIBLE);
        } else {
            _progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setContentView(View view) {
        _frameLayout = new FrameLayout(getActivity());

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View overlay = inflater.inflate(R.layout.overlay, null);

        _innerView = view;

        _frameLayout.addView(_innerView);
        _frameLayout.addView(overlay);

        _progressBar = (ProgressBar) overlay.findViewById(R.id.progressBar);

        _progressBar.setVisibility(View.INVISIBLE);

        super.setContentView(_frameLayout);
    }

    @Override
    public View findViewById(int id) {
        return _innerView.findViewById(id);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(layoutResID, null);

        setContentView(view);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _storage = Storage.getInstance(this);
    }

    @Override
    public void startActivity(Intent intent) {
        Util.lockViews(this, false);
        showProgressOverlay(true);

        super.startActivity(intent);
        showProgressOverlay(false);
    }

    private boolean _stopped = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (_stopped) {
            _stopped = false;
            Util.lockViews(this, true);
            showProgressOverlay(false);
        }
    }

    @Override
    protected void onPause() {
        if (!_stopped) {
            _stopped = true;
            Util.lockViews(this, true);
            showProgressOverlay(true);
        }

        super.onPause();
    }

    public void showDialog(final MyDialog dialog) {
        showProgressOverlay(true);

        Util.lockViews(getActivity(), false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show(getFragmentManager(), dialog.toString());

                dialog.addListener(new MyDialog.Listener() {
                    @Override
                    public void onDismiss() {
                        Util.lockViews(getActivity(), true);

                        showProgressOverlay(false);
                    }
                });
            }
        }, 100);
    }

    public boolean hasPermission(String name) {
        return (ContextCompat.checkSelfPermission(this, name) == PackageManager.PERMISSION_GRANTED);
    }

    public void acceptPermission(String permission) {
        if (permission.equals(Manifest.permission.GET_ACCOUNTS)) {
            PickAccountDialog dialog = new PickAccountDialog();

            dialog.setArgs(this, new PickAccountDialog.Listener() {
                @Override
                public void onDismiss() {

                }

                @Override
                public void ready(Account acc) {
                    _storage.setAcc(Storage.Acc.get(acc));

                    Util.printToast(getActivity(), getString(R.string.account_set), Toast.LENGTH_SHORT);
                }
            });

            showDialog(dialog);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                acceptPermission(permissions[i]);
            }
        }
    }

    public void requestPermission(String name) {
        if (Build.VERSION.SDK_INT >= 23) {
            //there are runtime permission inquires, yay
            if (hasPermission(name)) {
                acceptPermission(name);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{name}, 0);
            }
        }
    }
}