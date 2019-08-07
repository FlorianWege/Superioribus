package moonlightflower.com.superioribus2.shared.lang;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import moonlightflower.com.superioribus2.R;
import moonlightflower.com.superioribus2.shared.Util;

public class Lang {
    private static Map<String, Lang> _shortMap = new HashMap<>();

    public static Map<String, Lang> getMap() {
        return new HashMap<>(_shortMap);
    }

    private static class MyLocale {
        public String _shortName;
        public String _displayName;

        public String getLanguage() {
            return _shortName;
        }

        public String getDisplayName() {
            return _displayName;
        }

        public MyLocale(String shortName, String displayName) {
            _shortName = shortName;
            _displayName = displayName;
        }

        public MyLocale(Locale orig) {
            this(orig.getLanguage(), orig.getDisplayName());
        }
    }

    private MyLocale _locale;

    public MyLocale getLocale() {
        return _locale;
    }

    public String getShortName() {
        return (getLocale() == null) ? null : getLocale().getLanguage();
    }

    public String getName() {
        if (getLocale() == null) return null;

        return Util.getContext().getString(R.string.lang_name, getLocale().getDisplayName(), getShortName());
    }

    public static Lang AUTO = new Lang(null) {
        @Override
        public String getName() {
            return "auto";
        }
    };

    public static Lang NONE = new Lang(null) {
        @Override
        public String getName() {
            return "none";
        }
    };

    public String toString() {
        return getName();
    }

    private Lang(MyLocale locale) {
        _locale = locale;
    }

    public static Lang getFromShort(String shortName) {
        return _shortMap.get(shortName);
    }

    static void add(String shortName, String displayName) {
        if (_shortMap.containsKey(shortName)) return;

        MyLocale locale = new MyLocale(shortName, displayName);

        Lang lang = new Lang(locale);

        _shortMap.put(shortName, lang);
    }

    static {
        add("aa", "Afar");
        add("ab", "Abchasisch");
        add("ae", "Avestisch");
        add("af", "Afrikaans");
        add("ak", "Akan");
        add("am", "Amharisch");
        add("an", "Aragonisch");
        add("ar", "Arabisch");
        add("as", "Assamesisch");
        add("av", "Avarisch");
        add("ay", "Aymara");
        add("az", "Aserbaidschanisch");
        add("ba", "Baschkirisch");
        add("be", "Belorussisch");
        add("bg", "Bulgarisch");
        add("bh", "Biharisch");
        add("bi", "Bislamisch");
        add("bm", "Bambara");
        add("bn", "Bengalisch");
        add("bo", "Tibetanisch");
        add("br", "Bretonisch");
        add("bs", "Bosnisch");
        add("ca", "Katalanisch");
        add("ce", "Chechen");
        add("ch", "Chamorro");
        add("co", "Korsisch");
        add("cr", "Cree");
        add("cs", "Tschechisch");
        add("cu", "Church Slavic");
        add("cv", "Chuvash");
        add("cy", "Walisisch");
        add("da", "Dänisch");
        add("de", "Deutsch");
        add("dv", "Divehi");
        add("dz", "Bhutani");
        add("ee", "Ewe");
        add("el", "Griechisch");
        add("en", "Englisch");
        add("eo", "Esperanto");
        add("es", "Spanisch");
        add("et", "Estnisch");
        add("eu", "Baskisch");
        add("fa", "Persisch");
        add("ff", "Fulah");
        add("fi", "Finnisch");
        add("fj", "Fidschi");
        add("fo", "Faröisch");
        add("fr", "Französisch");
        add("fy", "Friesisch");
        add("ga", "Irisch");
        add("gd", "Schottisches Gälisch");
        add("gl", "Galizisch");
        add("gn", "Guarani");
        add("gu", "Gujaratisch");
        add("gv", "Manx");
        add("ha", "Haussa");
        add("he", "Hebräisch");
        add("hi", "Hindi");
        add("ho", "Hiri Motu");
        add("hr", "Kroatisch");
        add("ht", "Haitisch");
        add("hu", "Ungarisch");
        add("hy", "Armenisch");
        add("hz", "Herero");
        add("ia", "Interlingua");
        add("id", "Indonesisch");
        add("ie", "Interlingue");
        add("ig", "Igbo");
        add("ii", "Sichuan Yi");
        add("ik", "Inupiak");
        add("in", "Indonesisch");
        add("io", "Ido");
        add("is", "Isländisch");
        add("it", "Italienisch");
        add("iu", "Inuktitut");
        add("iw", "Hebräisch");
        add("ja", "Japanisch");
        add("ji", "Jiddish");
        add("jv", "Javanisch");
        add("ka", "Georgisch");
        add("kg", "Kongo");
        add("ki", "Kikuyu");
        add("kj", "Kwanyama");
        add("kk", "Kasachisch");
        add("kl", "Grönländisch");
        add("km", "Kambodschanisch");
        add("kn", "Kannada");
        add("ko", "Koreanisch");
        add("kr", "Kanuri");
        add("ks", "Kaschmirisch");
        add("ku", "Kurdisch");
        add("kv", "Komi");
        add("kw", "Cornish");
        add("ky", "Kirgisisch");
        add("la", "Lateinisch");
        add("lb", "Letzeburgisch");
        add("lg", "Ganda");
        add("li", "Limburgisch");
        add("ln", "Lingalisch");
        add("lo", "Laotisch");
        add("lt", "Litauisch");
        add("lu", "Luba-Katanga");
        add("lv", "Lettisch");
        add("mg", "Malagasisch");
        add("mh", "Marshall");
        add("mi", "Maorisch");
        add("mk", "Mazedonisch");
        add("ml", "Malaysisch");
        add("mn", "Mongolisch");
        add("mo", "Moldavisch");
        add("mr", "Marathi");
        add("ms", "Malay");
        add("mt", "Maltesisch");
        add("my", "Burmesisch");
        add("na", "Nauruisch");
        add("nb", "Norwegisch, Bokmål");
        add("nd", "Nord-Ndebele");
        add("ne", "Nepalisch");
        add("ng", "Ndonga");
        add("nl", "Niederländisch");
        add("nn", "Norwegisch, Nynorsk");
        add("no", "Norwegisch");
        add("nr", "Süd-Ndebele");
        add("nv", "Navajo");
        add("ny", "Nyanja");
        add("oc", "Okzitanisch");
        add("oj", "Ojibwa");
        add("om", "Oromo (Afan)");
        add("or", "Orija");
        add("os", "Ossetisch");
        add("pa", "Pundjabisch");
        add("pi", "Pali");
        add("pl", "Polnisch");
        add("ps", "Paschtu (Pushto)");
        add("pt", "Portugiesisch");
        add("qu", "Quechua");
        add("rm", "Rätoromanisch");
        add("rn", "Kirundisch");
        add("ro", "Rumänisch");
        add("ru", "Russisch");
        add("rw", "Ruanda");
        add("sa", "Sanskrit");
        add("sc", "Sardisch");
        add("sd", "Zinti");
        add("se", "Nord-Sami");
        add("sg", "Sango");
        add("si", "Singhalesisch");
        add("sk", "Slowakisch");
        add("sl", "Slowenisch");
        add("sm", "Samoanisch");
        add("sn", "Schonisch");
        add("so", "Somalisch");
        add("sq", "Albanisch");
        add("sr", "Serbisch");
        add("ss", "Swasiländisch");
        add("st", "Sesothisch");
        add("su", "Sudanesisch");
        add("sv", "Schwedisch");
        add("sw", "Suaheli");
        add("ta", "Tamilisch");
        add("te", "Telugu");
        add("tg", "Tadschikisch");
        add("th", "Thai");
        add("ti", "Tigrinja");
        add("tk", "Turkmenisch");
        add("tl", "Tagalog");
        add("tn", "Sezuan");
        add("to", "Tongaisch");
        add("tr", "Türkisch");
        add("ts", "Tsongaisch");
        add("tt", "Tatarisch");
        add("tw", "Twi");
        add("ty", "Tahitisch");
        add("ug", "Uigurisch");
        add("uk", "Ukrainisch");
        add("ur", "Urdu");
        add("uz", "Usbekisch");
        add("ve", "Venda");
        add("vi", "Vietnamesisch");
        add("vo", "Volapük");
        add("wa", "Wallonisch");
        add("wo", "Wolof");
        add("xh", "Xhosa");
        add("yi", "Jiddish");
        add("yo", "Joruba");
        add("za", "Zhuang");
        add("zh", "Chinesisch");
        add("zu", "Zulu");

        for (String localeS : Locale.getISOLanguages()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (_shortMap.containsKey(localeS)) continue;

                Locale locale = Locale.forLanguageTag(localeS);

                Lang lang = new Lang(new MyLocale(locale));

                _shortMap.put(localeS, lang);
            }
        }
    }
}