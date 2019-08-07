package moonlightflower.com.superioribus2.shared;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import moonlightflower.com.superioribus2.shared.storage.Storage;

public class Pixabay {
    //private static final String API_KEY = "614837-cb0f739704434f8ba1c0edd38<";
    private static final String API_KEY = "614837-cb0f739704434f8ba1c0edd38";

    private static String urlEncode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "UTF-8");
    }

    private static void getPicsUrls(Context context, GetPicsUrls.Listener listener, String query) {
        new GetPicsUrls(context, listener, query).execute();
    }

    private static class GetPicsUrls extends AsyncTask<Void, Void, Object> {
        public interface Listener {
            void fail(Exception exception);
            void ready(List<URL> urls);
        }

        private Context _context;
        private GetPicsUrls.Listener _listener;
        private String _query;

        public GetPicsUrls(Context context, GetPicsUrls.Listener listener, String query) {
            _context = context;
            _listener = listener;
            _query = query;
        }

        @Override
        protected Object doInBackground(Void... voids) {
            try {
                String params = "key=" + URLEncoder.encode(API_KEY, "UTF-8");

                params += "&q=" + urlEncode(_query);

                URL url = new URL("https://pixabay.com/api/?" + params);

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
            if (val instanceof Exception) {
                Util.printException(_context, (Exception) val);

                if (_listener != null) {
                    _listener.fail((Exception) val);
                }
            } else if (val instanceof JSONObject) {
                JSONObject json = (JSONObject) val;

                try {
                    JSONArray hits = json.getJSONArray("hits");
                    List<URL> picUrls = new ArrayList<>();

                    for (int i = 0; i < hits.length(); i++) {
                        JSONObject hit = (JSONObject) hits.get(i);

                        String s = hit.getString("previewURL");

                        picUrls.add(new URL(s));
                    }

                    if (_listener != null) {
                        _listener.ready(picUrls);
                    }
                } catch (Exception e) {
                    Util.printException(_context, e);

                    if (_listener != null) {
                        _listener.fail(e);
                    }
                }
            }
        }
    }

    private static Map<String, URL> _queryImgMap = new HashMap<>();

    public static void getQuery(final Context context, final String query, final Storage.ImgListener imgListener) {
        Log.e(Pixabay.class.getSimpleName(), "query " + query);

        if (_queryImgMap.containsKey(query)) {
            Log.e(Pixabay.class.getSimpleName(), "query inMap " + query);
            Storage.getInstance(context).getImg(context, _queryImgMap.get(query), new Storage.ImgListener() {
                @Override
                public void fail(Exception exception) {
                    Log.e(Pixabay.class.getSimpleName(), "query inMap fail " + query);

                    _queryImgMap.remove(query);

                    getQuery(context, query, imgListener);
                }

                @Override
                public void ready(Bitmap bitmap) {
                    Log.e(Pixabay.class.getSimpleName(), "query inMap rdy " + query);

                    if (imgListener != null) {
                        imgListener.ready(bitmap);
                    }
                }
            });
        } else {
            Log.e(Pixabay.class.getSimpleName(), "query new " + query);
            Pixabay.getPicsUrls(context, new Pixabay.GetPicsUrls.Listener() {
                @Override
                public void fail(Exception exception) {
                    Log.e(Pixabay.class.getSimpleName(), "query new fail " + query);

                    if (imgListener != null) {
                        imgListener.fail(exception);
                    }
                }

                @Override
                public void ready(List<URL> urls) {
                    Log.e(Pixabay.class.getSimpleName(), "query new rdy " + query);

                    if (imgListener != null) {
                        if (urls.isEmpty()) {
                            Log.e(Pixabay.class.getSimpleName(), "query new rdy empty " + query);
                            imgListener.fail(new Exception("empty"));
                        } else {
                            Log.e(Pixabay.class.getSimpleName(), "query new rdy notempty " + query);

                            final URL url = urls.get(0);

                            Storage.getInstance(context).getImg(context, url, new Storage.ImgListener() {
                                @Override
                                public void fail(Exception exception) {
                                    Log.e(Pixabay.class.getSimpleName(), "query new rdy fail " + query);

                                    _queryImgMap.remove(query);

                                    if (imgListener != null) {
                                        imgListener.fail(exception);
                                    }
                                }

                                @Override
                                public void ready(Bitmap bitmap) {
                                    Log.e(Pixabay.class.getSimpleName(), "query new rdy rdy " + query);

                                    _queryImgMap.put(query, url);

                                    if (imgListener != null) {
                                        imgListener.ready(bitmap);
                                    }
                                }
                            });
                        }
                    }
                }
            }, query);
        }
    }
}