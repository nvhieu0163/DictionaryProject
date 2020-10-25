package dictionary;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {
    private final SQLDatabase database;

    Dictionary() {
        database = new SQLDatabase("test");
    }

    Dictionary(String databaseName) {
        database = new SQLDatabase(databaseName);
    }

    Dictionary(String databaseName, boolean initDatabase) {
        database = new SQLDatabase(databaseName);
        if (initDatabase) {
            database.initDatabase();
        }
    }

    public Word getWordByID(int wordId) {
        return database.getWordByID(wordId);
    }

    public boolean insertWord(Word word) {
        return database.insertWord(word);
    }

    public int insertWords(List<Word> words) {
//        words.forEach(database::insertWord);
        int count = 0;
        for (Word word: words) {
            if (database.insertWord(word)) {
                count++;
            }
        }
        return count;
    }

    public List<Pair<Word, String>> getWordStartWith(String prefix) {
        return database.getWordStartWith(prefix);
    }

    public void deleteWordByID(int wordID) {
        database.deleteWordByID(wordID);
    }

    private String getAllString(File inputFile) {
        String content = "";
        try {
            FileReader fr = new FileReader(inputFile);
            BufferedReader reader = new BufferedReader(fr);
            StringBuilder stringBuilder = new StringBuilder();
            reader.readLine();
            String line;
            String ls = "\n";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            stringBuilder.deleteCharAt(0);
            reader.close();
            fr.close();
            content = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private Word parseWordInfo(String wordInfo) {
        int firstSlash = wordInfo.indexOf(" /") + 1;
        int secondSlash = wordInfo.indexOf('/', firstSlash + 1);
        String word;
        String pronunciation;
        if (firstSlash != 0 && secondSlash != -1) {
            word = wordInfo.substring(0, firstSlash - 1);
            pronunciation = wordInfo.substring(firstSlash, secondSlash + 1);
        } else {
            word = wordInfo;
            pronunciation = "";
        }

        return new Word(-1, word, pronunciation, null);
    }

    private Meaning parseMeaningInfo(String posTag, String meaningInfo) {
        String[] splitMeaning = meaningInfo.split("\n!", 2);
        if (splitMeaning.length == 2) {
            return new Meaning(-1, posTag, splitMeaning[0], "!" + splitMeaning[1]);
        } else {
            return new Meaning(-1, posTag, splitMeaning[0], null);
        }
    }

    private List<Word> loadWordFromFile(File inputFile) {
        String content = getAllString(inputFile);

        List<Word> results = new ArrayList<>();
        String[] allRawWords = content.split("\n@");
        for (String wordString : allRawWords) {
            Word newWord;
            String[] splitStrings = wordString.strip().split("\n\\*");

            // no POSTag
            if (splitStrings.length == 1) {
                splitStrings = wordString.strip().split("\n", 2);
                if (splitStrings.length == 1) {
                    continue;
                } else {
                    newWord = parseWordInfo(splitStrings[0]);
                    newWord.setMeanings(List.of(parseMeaningInfo(null, splitStrings[1])));
                }
            } else { // has POSTag
                newWord = parseWordInfo(splitStrings[0]);

                List<Meaning> meanings = new ArrayList<>();
                for (int i = 1; i < splitStrings.length; i++) {
                    String[] splitMeaning = splitStrings[i].split("\n", 2);
                    String posTag = splitMeaning[0].strip();

                    if (splitMeaning.length == 1) {
                        meanings.add(new Meaning(-1, posTag, null, null));
                    } else {
                        meanings.add(parseMeaningInfo(posTag, splitMeaning[1]));
                    }
                }
                newWord.setMeanings(meanings);
            }

            results.add(newWord);
        }

        return results;
    }

    public int loadFileIntoDatabase(File inputFile) {
        List<Word> words = loadWordFromFile(inputFile);
        return insertWords(words);
    }

    public boolean editPronunciationByID( int wordID, String pronunEdited) {
        return database.editPronunciationByID(wordID, pronunEdited);
    }

    public static void main(String[] args) {
        File f = new File("input/anhviet0-100.txt");
        Dictionary dict = new Dictionary("test3", true);

        List<Word> words = dict.loadWordFromFile(f);
        dict.insertWords(words);
    }
}
