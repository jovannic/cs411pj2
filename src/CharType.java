/**
 * Created by Jovanni on 2/21/14.
 */
public enum CharType {
    SPACE, SLASH, OP, DIGIT, SYMBOL, QUOTE;

    private static CharType[] cclass = new CharType[128];

    public static CharType typeOf(char c) {
        return c < cclass.length ? cclass[c] : null;
    }

    public static boolean is(char c, CharType t) {
        return typeOf(c) == t;
    }

    static {
        setAll(" \t\r\n", CharType.SPACE, cclass);
        setAll("!&()*+,\\-./;<=>[]{|}", CharType.OP, cclass);
        setAll("/", CharType.SLASH, cclass); // override op
        setAll("_a-zA-Z0-9", CharType.SYMBOL, cclass);
        setAll("0-9", CharType.DIGIT, cclass); // override symbol
        setAll("\"", CharType.QUOTE, cclass);
    }

    private static void setAll(String keys, CharType value, CharType[] array) {
        char last = '\0';
        boolean escape = false;

        int n = keys.length();
        for (int i = 0; i < n; ++i) {
            char c = keys.charAt(i);

            if (escape) {
                array[c] = value;
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } if (c == '-' && last != '\0') {
                c = keys.charAt(++i); // advance & get next
                for (int j = last; j <= c; ++j)
                    array[j] = value;
            } else {
                array[c] = value;
            }

            last = c;
        }
    }
}