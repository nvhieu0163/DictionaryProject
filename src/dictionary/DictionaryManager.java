package dictionary;

import javafx.util.Pair;

import java.util.List;

public class DictionaryManager {
    private final static String DATABASE_NAME = "test";
    private static Dictionary dictionary = null;

    public DictionaryManager() {
        if (dictionary == null) {
            dictionary = new Dictionary(DATABASE_NAME);
        }
    }

    public Word getWordByID(int wordId) {
        return dictionary.getWordByID(wordId);
    }

    public List<Pair<Word, String>> getWordStartWith(String prefix) {
        return dictionary.getWordStartWith(prefix);
    }

    public void deleteWordByID(int wordID) {
        dictionary.deleteWordByID(wordID);
    }
}
