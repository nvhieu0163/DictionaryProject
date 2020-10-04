package dictionary.core;

import dictionary.data.SQLDatabase;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {
    private final SQLDatabase database;

    public Dictionary() {
        database = new SQLDatabase("test");
    }

    public Dictionary(boolean initDatabase) {
        database = new SQLDatabase("test");
        if (initDatabase) {
            database.initDatabase();
        }
    }

    public Dictionary(String databaseName) {
        database = new SQLDatabase(databaseName);
    }

    public Dictionary(String databaseName, boolean initDatabase) {
        database = new SQLDatabase(databaseName);
        if (initDatabase) {
            database.initDatabase();
        }
    }

    public Word getWordByID(int wordId) {
        return database.getWordByID(wordId);
    }

    public void insertWord(Word word) {
        database.insertWord(word);
    }

    public void insertWords(List<Word> words) {
        words.forEach(database::insertWord);
//        for (Word word: words) {
//            database.insertWord(word);
//        }
    }

    public List<Word> getWordStartWith(String prefix) {
        return database.getWordStartWith(prefix);
    }

    public void deleteWordByID(int wordID) {
        database.deleteWordByID(wordID);
    }

    public List<Word> loadWordFromFile(File inputFile) {
        List<Word> results = new ArrayList<>();

        try {
            FileReader fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);

            String line;
            String pronunciation = "";
            String posTag = "";
            String word = "";
            Word newWord = null;
            Meaning newMeaning = null;
            String explanation = "";
            List<Explanation> explanations = null;
            List<Pair<String, String>> phrases = null;
            List<Pair<String, String>> examples = null;
            while((line=br.readLine())!=null) {
                switch (line.charAt(0)) {
                    case '@' -> {
                        if (newWord != null) {
                            results.add(newWord);
                        }
                        int firstSlash = line.indexOf('/');
                        int secondSlash = line.indexOf('/', firstSlash);
                        word = line.substring(1, firstSlash - 1);
                        pronunciation = line.substring(firstSlash, secondSlash);
                    }
                    case '*' -> {
                        if (newWord != null) {
                            results.add(newWord);
                        }
                        posTag = line.substring(2);
                        newWord = new Word(word, pronunciation, posTag);
                    }
//                    case '-' -> {
//                        if ()
//                    }
                }
            }
            fr.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    public static void main(String[] args) {
        File f = new File("input/anhviet0-10K.txt");
        Dictionary dict = new Dictionary("test");

        List<Word> words = dict.loadWordFromFile(f);
        System.out.println(words);
    }
}
