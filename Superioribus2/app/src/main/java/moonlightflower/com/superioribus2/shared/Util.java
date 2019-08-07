package moonlightflower.com.superioribus2.shared;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moonlightflower.com.superioribus2.R;

public class Util {
    private static Map<View, Integer> _lockC = new HashMap<>();

    public static void addEnabled(View v, boolean flag) {
        int prevVal = _lockC.containsKey(v) ? _lockC.get(v) : 0;

        _lockC.put(v, flag ? prevVal - 1 : prevVal + 1);

        if (_lockC.get(v) > 0) {
            v.setEnabled(false);
        } else {
            _lockC.remove(v);
            v.setEnabled(true);
        }
    }

    public static void lockViews(View v, boolean flag) {
        if (v instanceof ViewGroup) {
            ViewGroup vGroup = (ViewGroup) v;

            for (int i = 0; i < vGroup.getChildCount(); i++) {
                lockViews(vGroup.getChildAt(i), flag);
            }
        }

        if (flag) {
            addEnabled(v, true);
        } else {
            addEnabled(v, false);
        }
    }

    public static void lockViews(Activity activity, boolean flag) {
        try {
            View decorView = activity.getWindow().getDecorView();

            lockViews(decorView, flag);
        } catch (Exception e) {
            Log.e(new Util().getClass().getSimpleName(), e.toString(), e);
        }
    }

    public static void printToast(final Context context, final String s, final int toastDur) {
        Log.e(new Util().getClass().getSimpleName(), s);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, toastDur).show();
            }
        });
    }

    private static Util instance = new Util();

    public static void printException(Context context, Exception e) {
        try {
            printToast(context, (e == null) ? "null" : e.toString(), Toast.LENGTH_SHORT);
        } catch (Exception e2) {
            Log.e(instance.getClass().getSimpleName(), (e2 == null) ? "null" : e2.toString());
        }
    }

    public static String inStreamToString(InputStream inStream) throws IOException {
        String line = "";
        StringBuffer lines = new StringBuffer("");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        while ((line = reader.readLine()) != null) {
            lines.append(line);
        }

        return lines.toString();
    }

    public static Point getScreenSize(Activity activity) {
        Point p = new Point();

        activity.getWindowManager().getDefaultDisplay().getSize(p);

        return p;
    }

    @Nullable
    public static Context getContext() {
        try {
            return (Context) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (Exception e) {
            Log.e("getContext", e.getMessage(), e);
        }

        return null;
    }

    public static boolean hasPermission(Context context, String name) {
        return (ContextCompat.checkSelfPermission(context, name) != PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermission(Activity activity, String name) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, name)) {
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{name}, 0);
        }
    }

    public static class PermissionException extends Throwable {
        private SecurityException _e;

        public SecurityException getException() {
            return _e;
        }

        public String getMessage() {
            return getException().getMessage();
        }

        public PermissionException(SecurityException e) {
            _e = e;
        }
    }
}