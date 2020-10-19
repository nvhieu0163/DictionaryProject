package dictionary;

public class DictionaryManager {
    private final static String DATABASE_NAME = "test2";
    private static Dictionary dictionary = null;

    private DictionaryManager() {
    }

    public static Dictionary getDictionary() {
        if (dictionary == null) {
            dictionary = new Dictionary(DATABASE_NAME);
        }

        return dictionary;
    }
}
