package dictionary;

import javafx.util.Pair;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLDatabase {
    private Connection database;

    public SQLDatabase(String name) {
        String databaseName = String.format("jdbc:sqlite:%s.db", name);

        try {
            Class.forName("org.sqlite.JDBC");
            database = DriverManager.getConnection(databaseName);

            System.out.println("Opened database successfully");
            database.setAutoCommit(false);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            database = null;
            System.out.println("Cannot open database. Application exited.");
            System.exit(0);
        }
    }

    public void initDatabase() {
        Statement stmt = null;

        try {
            stmt = database.createStatement();

            String sqlDropWord = "DROP TABLE IF EXISTS WORD;";
            stmt.executeUpdate(sqlDropWord);
            String sqlWord = "CREATE TABLE WORD (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "content VARCHAR(100) NOT NULL, " +
                    "pronunciation VARCHAR(100) NULL " +
                    ");";
            stmt.executeUpdate(sqlWord);

            String sqlDropMeaning = "DROP TABLE IF EXISTS MEANING;";
            stmt.executeUpdate(sqlDropMeaning);
            String sqlMeaning = "CREATE TABLE MEANING (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "wordID INTEGER NOT NULL, " +
                    "posTag VARCHAR(100) NULL, " +
                    "explanations TEXT NULL, " +
                    "phrases TEXT NULL" +
                    ");";
            stmt.executeUpdate(sqlMeaning);

            stmt.close();
            database.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Database created successfully");
    }

    public void insertWord(Word word) {
        Statement stmt = null;

        try {
            String sql = "INSERT INTO WORD (content, pronunciation) VALUES ('%s', '%s');";
            database.prepareStatement(sql);
            stmt = database.createStatement();

            String sql = "INSERT INTO WORD (content, pronunciation, POSTag) " +
                    String.format("VALUES ('%s', '%s', '%s');", word.getContent(), word.getPronunciation(), word.getPosTag());
            stmt.executeUpdate(sql);
            ResultSet rs = stmt.getGeneratedKeys();
            word.setId(rs.getInt("last_insert_rowid()"));

            if (word.getMeaning() != null) {
                insertMeaning(word.getId(), word.getMeaning());
            }

            stmt.close();
            database.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Words created successfully");
    }

    private void insertMeaning(int wordId, Meaning meaning) {
        Statement stmt = null;

        try {
            stmt = database.createStatement();

            String sql = "INSERT INTO MEANING (wordID) " +
                    String.format("VALUES ('%d');", wordId);
            stmt.executeUpdate(sql);
            ResultSet rs = stmt.getGeneratedKeys();
            meaning.setId(rs.getInt("last_insert_rowid()"));

            if (meaning.getPhrases() != null) {
                insertPhrase(meaning.getId(), meaning.getPhrases());
            }

            if (meaning.getExplanation() != null) {
                insertExplanation(meaning.getId(), meaning.getExplanation());
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Meanings created successfully");
    }

    private void insertPhrase(int meaningID, List<Pair<String, String>> phrases) {
        Statement stmt = null;

        try {
            stmt = database.createStatement();

            for (Pair<String, String> phrase : phrases) {
                String sql = "INSERT INTO PHRASE (meaningID, phrase, `translate`) " +
                        String.format("VALUES ('%d', '%s', '%s');", meaningID, phrase.getKey(), phrase.getValue());
                stmt.executeUpdate(sql);
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Phrases created successfully");
    }

    private void insertExplanation(int meaningID, List<Explanation> explanations) {
        Statement stmt = null;

        try {
            stmt = database.createStatement();

            for (Explanation explanation : explanations) {
                String sql = "INSERT INTO EXPLANATION (meaningID, explanation) " +
                        String.format("VALUES ('%d', '%s');", meaningID, explanation.getExplanation());
                stmt.executeUpdate(sql);
                ResultSet rs = stmt.getGeneratedKeys();
                explanation.setId(rs.getInt("last_insert_rowid()"));

                if (explanation.getExamples() != null) {
                    insertExample(explanation.getId(), explanation.getExamples());
                }
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Explanations created successfully");
    }

    private void insertExample(int explanationID, List<Pair<String, String>> examples) {
        Statement stmt = null;

        try {
            stmt = database.createStatement();

            for (Pair<String, String> example : examples) {
                String sql = "INSERT INTO EXAMPLE (explanationID, example, translate) " +
                        String.format("VALUES ('%d', '%s', '%s');", explanationID, example.getKey(), example.getValue());
                stmt.executeUpdate(sql);
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Examples created successfully");
    }

    public Word getWordByID(int wordID) {
        Word word = new Word();
        Statement stmt = null;
        try {
            stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT * FROM WORD WHERE id = %d;", wordID
            ));

            if (rs.next()) {
                word.setId(wordID);
                word.setContent(rs.getString("content"));
                word.setPronunciation(rs.getString("pronunciation"));
                word.setPosTag(rs.getString("POSTag"));

                word.setMeaning(getMeaningByWordID(wordID));
            } else {
                word = null;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("getWordByID " + wordID + " done successfully");
        return word;
    }

    private Meaning getMeaningByWordID(int wordID) {
        Meaning meaning = new Meaning();
        Statement stmt = null;
        try {
            stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT id FROM MEANING WHERE wordID = %d;", wordID
            ));

            if (rs.next()) {
                int meaningID = rs.getInt("id");
                meaning.setId(meaningID);

                meaning.setExplanation(getExplanationByMeaningID(meaningID));
                meaning.setPhrases(getPhraseByMeaningID(meaningID));
            } else {
                meaning = null;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("getMeaningByWordID " + wordID + " done successfully");
        return meaning;
    }

    private List<Pair<String, String>> getPhraseByMeaningID(int meaningID) {
        List<Pair<String, String>> phrases = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT phrase, translate FROM PHRASE WHERE meaningID = %d;", meaningID
            ));

            while (rs.next()) {
                String phrase = rs.getString("phrase");
                String translate = rs.getString("translate");

                phrases.add(new Pair<>(phrase, translate));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("getPhraseByMeaningID " + meaningID + " done successfully");

        if (phrases.size() != 0) {
            return phrases;
        } else {
            return null;
        }
    }

    private List<Explanation> getExplanationByMeaningID(int meaningID) {
        List<Explanation> explanations = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT id, explanation FROM EXPLANATION WHERE meaningID = %d;", meaningID
            ));

            while (rs.next()) {
                Explanation explanation = new Explanation();
                int explanationID = rs.getInt("id");

                explanation.setId(explanationID);
                explanation.setExplanation(rs.getString("explanation"));
                explanation.setExamples(getExampleByExplanationID(explanationID));

                explanations.add(explanation);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("getExplanationByMeaningID " + meaningID + " done successfully");
        if (explanations.size() != 0) {
            return explanations;
        } else {
            return null;
        }
    }

    private List<Pair<String, String>> getExampleByExplanationID(int explanationID) {
        List<Pair<String, String>> examples = new ArrayList<>();
        Statement stmt = null;
        try {
            stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT example, translate FROM EXAMPLE WHERE explanationID = %d;", explanationID
            ));

            while (rs.next()) {
                String example = rs.getString("example");
                String translate = rs.getString("translate");

                examples.add(new Pair<>(example, translate));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("getExampleByExplanationID " + explanationID + " done successfully");

        if (examples.size() != 0) {
            return examples;
        } else {
            return null;
        }
    }

    public List<Word> getWordStartWith(String prefix) {
        List<Word> result = new ArrayList<>();

        Statement stmt = null;
        try {
            stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT id, content, POSTag FROM WORD WHERE content LIKE '%s%%';",
                    prefix
            ));

            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("content");
                String POSTag = rs.getString("POSTag");

                result.add(new Word(id, content, null, POSTag, null));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("getWordStartWith " + prefix + " done successfully");

        return result;
    }

    public void deleteWordByID(int wordID) {
        Statement stmt = null;
        try {
            stmt = database.createStatement();
            stmt.executeUpdate(String.format(
                    "DELETE FROM WORD WHERE id = %d;", wordID
            ));

            stmt.close();
            database.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("deleteWordByID " + wordID + " done successfully");
    }

    public static void main(String[] args) {
        SQLDatabase database = new SQLDatabase("test");
        database.initDatabase();
//        Word w = new Word(
//                -1, "word 1", "pronunciation 1", "POSTag 1",
//                new Meaning(
//                        -1,
//                        List.of(
//                                new Explanation(
//                                        -1, "Explanation 1.1",
//                                        List.of(
//                                                new Pair<>("apple1", "Tao1"),
//                                                new Pair<>("apple2", "Tao12")
//                                        )
//                                ),
//                                new Explanation(
//                                        -1, "Explanation 1.2", null
//                                )
//                        ),
//                        List.of(
//                                new Pair<>("Phrase1", "Cum Tu 1"),
//                                new Pair<>("Phrase2", "Cum Tu 2")
//                        )
//                )
//        );
//        database.insertWord(w);
//        Word word = database.getWordByID();
//        List<Word> r = database.getWordStartWith("wo");
//        database.deleteWordByID(5);
//        r = database.getWordStartWith("wo");
//        System.out.println(r);
    }

}
