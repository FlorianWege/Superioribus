package moonlightflower.com.superioribus2.main;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import moonlightflower.com.superioribus2.shared.lang.Lang;
import moonlightflower.com.superioribus2.shared.Util;

public class Yandex {
    private static final String API_KEY = "trnsl.1.1.20170110T133020Z.7718cb0b85bc7d53.cf91fb688a60614e9cefb868188e94f897493155";

    private String urlEncode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "UTF-8");
    }

    public static void getLangs(Context context, GetLangsTask.Listener listener) {
        new GetLangsTask(context, listener).execute();
    }

    public static class GetLangsTask extends AsyncTask<Void, Void, Object> {
        public interface Listener {
            void finished(Exception exception);
            void ready(List<Lang> sourceLangs, List<Lang> targetLangs);
        }

        private Context _context;
        private GetLangsTask.Listener _listener;

        public GetLangsTask(Context context, GetLangsTask.Listener listener) {
            _context = context;
            _listener = listener;
        }

        @Override
        protected Object doInBackground(Void... voids) {
            try {
                String params = "key=" + URLEncoder.encode(API_KEY, "UTF-8");

                URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/getLangs?ui=en&" + params);

                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                con.setRequestMethod("GET");

                con.setConnectTimeout(5000);
                //con.setReadTimeout(5000);

                Log.e(getClass().getSimpleName(), String.format("request url: %s", url));

                con.connect();

                int responseCode = con.getResponseCode();

                if (responseCode != 200) {
                    String errS = Util.inStreamToString(con.getErrorStream());

                    Util.printToast(_context, errS, Toast.LENGTH_SHORT);

                    JSONObject json = new JSONObject(errS);

                    String codeS = json.get("code").toString();

                    Util.printToast(_context, codeS, Toast.LENGTH_SHORT);

                    throw new Exception(String.format("server response code: %d", responseCode));
                }

                InputStream inStream = con.getInputStream();

                String retS = Util.inStreamToString(inStream);

                //Util.printToast(_context, retS, Toast.LENGTH_LONG);

                con.disconnect();

                JSONObject ret = new JSONObject(retS);

                return ret;
            } catch (Exception e) {
                return e;
            }
        }

        @Override
        public void onPostExecute(Object val) {
            if (_listener != null) {
                _listener.finished(val instanceof Exception ? (Exception) val : null);
            }

            if (val instanceof JSONObject) {
                JSONObject json = (JSONObject) val;

                try {
                    JSONArray dirs = json.getJSONArray("dirs");
                    JSONObject langs = json.getJSONObject("langs");

                    List<Lang> sourceLangs = new ArrayList<>();
                    List<Lang> targetLangs = new ArrayList<>();

                    for (int i = 0; i < dirs.length(); i++) {
                        String s = dirs.getString(i);

                        String[] langComb = s.split(Pattern.quote("-"));

                        if (langComb.length > 1) {
                            String sourceLangShort = langComb[0];
                            String targetLangShort = langComb[1];

                            Lang sourceLang = Lang.getFromShort(sourceLangShort);
                            Lang targetLang = Lang.getFromShort(targetLangShort);

                            //String sourceLangS = langs.getString(sourceLangShort);
                            //String targetLangS = langs.getString(targetLangShort);

                            //Lang sourceLang = Lang.get(sourceLangS);
                            //Lang targetLang = Lang.get(targetLangS);

                            //sourceLang.setShortName(sourceLangShort);
                            //targetLang.setShortName(targetLangShort);

                            if (sourceLang != null && !sourceLangs.contains(sourceLang)) sourceLangs.add(sourceLang);
                            if (targetLang != null && !targetLangs.contains(targetLang)) targetLangs.add(targetLang);
                        }
                    }

                    if (_listener != null) {
                        _listener.ready(sourceLangs, targetLangs);
                    }

                    Util.printToast(_context, "got " + sourceLangs.size() + " langs", Toast.LENGTH_SHORT);
                } catch (JSONException e) {
                    Util.printException(_context, e);
                }
            }
        }
    }

    public static void detect(Context context, String text, DetectTask.Listener listener) {
        new DetectTask(context, text, listener).execute();
    }

    public static class DetectTask extends AsyncTask<Void, Void, Object> {
        public interface Listener {
            void finished(Exception exception);
            void ready(Lang result);
        }

        private Context _context;
        private String _text;
        private DetectTask.Listener _listener;

        public DetectTask(Context context, String text, DetectTask.Listener listener) {
            _context = context;
            _text = text;
            _listener = listener;
        }

        @Override
        protected Object doInBackground(Void... voids) {
            try {
                String textParam = "text=" + URLEncoder.encode(_text, "UTF-8");

                String keyParam = "key=" + URLEncoder.encode(API_KEY, "UTF-8");

                String params = String.format("%s&%s", keyParam, textParam);

                URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/detect?" + params);

                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                con.setRequestMethod("GET");

                con.setConnectTimeout(30000);
                con.setReadTimeout(30000);

                Log.e(getClass().getSimpleName(), String.format("request url: %s", url));

                con.connect();

                int responseCode = con.getResponseCode();

                if (responseCode != 200) {
                    String errS = Util.inStreamToString(con.getErrorStream());

                    Util.printToast(_context, errS, Toast.LENGTH_SHORT);

                    JSONObject json = new JSONObject(errS);

                    Integer code = json.getInt("code");

                    String codeS = code.toString();

                    switch (code) {
                        case 200: {
                            //everything ok

                            break;
                        }
                        default: {
                            Util.printToast(_context, codeS, Toast.LENGTH_SHORT);
                        }
                    }

                    throw new Exception(String.format("server response code: %d", responseCode));
                }

                InputStream inStream = con.getInputStream();

                String retS = Util.inStreamToString(inStream);

                //Util.printToast(_context, retS, Toast.LENGTH_LONG);

                con.disconnect();

                JSONObject ret = new JSONObject(retS);

                return ret;
            } catch (Exception e) {
                return e;
            }
        }

        @Override
        public void onPostExecute(Object val) {
            if (_listener != null) {
                _listener.finished(val instanceof Exception ? (Exception) val : null);
            }

            if (val instanceof JSONObject) {
                JSONObject json = (JSONObject) val;

                try {
                    String langS = json.getString("lang");

                    if (_listener != null) {
                        _listener.ready(Lang.getFromShort(langS));
                    }
                } catch (JSONException e) {
                    Util.printException(_context, e);
                }
            }
        }
    }

    public static void translate(Context context, Lang sourceLang, Lang targetLang, String text, TranslateTask.Listener listener) {
        new TranslateTask(context, sourceLang, targetLang, text, listener).execute();
    }

    public static class TranslateTask extends AsyncTask<Void, Void, Object> {
        public interface Listener {
            void finished(Exception exception);
            void ready(String result);
        }

        private Context _context;
        private Lang _sourceLang;
        private Lang _targetLang;
        private String _text;
        private Listener _listener;

        public TranslateTask(Context context, Lang sourceLang, Lang targetLang, String text, Listener listener) {
            _context = context;
            _sourceLang = sourceLang;
            _targetLang = targetLang;
            _text = text;
            _listener = listener;
        }

        @Override
        protected Object doInBackground(Void... voids) {
            try {
                String keyParam = "key=" + URLEncoder.encode(API_KEY, "UTF-8");
                String textParam = "text=" + URLEncoder.encode(_text, "UTF-8");

                String langParam = "lang=" + ((_sourceLang == Lang.AUTO) ? String.format("%s", _targetLang.getShortName()) : String.format("%s-%s", _sourceLang.getShortName(), _targetLang.getShortName()));

                String formatParam = "format=" + "plain";

                String params = String.format("%s&%s&%s&%s", keyParam, textParam, langParam, formatParam);

                URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?" + params);

                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                con.setRequestMethod("GET");

                con.setConnectTimeout(30000);
                con.setReadTimeout(30000);

                Log.e(getClass().getSimpleName(), String.format("request url: %s", url));

                con.connect();

                int responseCode = con.getResponseCode();

                if (responseCode != 200) {
                    String errS = Util.inStreamToString(con.getErrorStream());

                    Util.printToast(_context, errS, Toast.LENGTH_SHORT);

                    JSONObject json = new JSONObject(errS);

                    Integer code = json.getInt("code");

                    String codeS = code.toString();

                    switch (code) {
                        case 200: {
                            //everything ok

                            break;
                        }
                        case 422: {
                            Util.printToast(_context, String.format("%s", codeS), Toast.LENGTH_SHORT);

                            break;
                        }
                        case 501: {
                            Util.printToast(_context, String.format("%s (%s->%s)", codeS, (_sourceLang == Lang.AUTO) ? _sourceLang.getName() : _sourceLang.getShortName(), _targetLang.getShortName()), Toast.LENGTH_SHORT);

                            break;
                        }
                        default: {
                            Util.printToast(_context, codeS, Toast.LENGTH_SHORT);
                        }
                    }

                    throw new Exception(String.format("server response code: %d", responseCode));
                }

                InputStream inStream = con.getInputStream();

                String retS = Util.inStreamToString(inStream);

                //Util.printToast(_context, retS, Toast.LENGTH_LONG);

                con.disconnect();

                JSONObject ret = new JSONObject(retS);

                return ret;
            } catch (Exception e) {
                return e;
            }
        }

        @Override
        public void onPostExecute(Object val) {
            if (_listener != null) {
                _listener.finished(val instanceof Exception ? (Exception) val : null);
            }

            if (val instanceof JSONObject) {
                JSONObject json = (JSONObject) val;

                try {
                    JSONArray texts = json.getJSONArray("text");

                    if (texts.length() > 0) {
                        if (_listener != null) {
                            _listener.ready(texts.getString(0));
                        }
                    }
                } catch (JSONException e) {
                    Util.printException(_context, e);
                }
            }
        }
    }
}
