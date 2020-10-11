package dictionary;

import javafx.util.Pair;

import java.sql.*;
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

    public boolean checkWordExisted(String word) {
        boolean existed = false;
        try {
            String sqlQuery = "SELECT 1 FROM WORD WHERE content = ?;";
            PreparedStatement stmt = database.prepareStatement(sqlQuery);
            stmt.setString(1, word);
            ResultSet rs = stmt.executeQuery();

            existed = rs.next();
            rs.close();
            stmt.close();
            return existed;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return existed;
    }

    public boolean insertWord(Word word) {
        if (checkWordExisted(word.getContent())) {
            // System.out.println("Word " + word.getContent() + " existed.");
            return false;
        } else {
            boolean inserted = false;
            try {
                String sqlQuery = "INSERT INTO WORD (content, pronunciation) VALUES (?, ?);";
                PreparedStatement stmt = database.prepareStatement(sqlQuery);
                stmt.setString(1, word.getContent());
                if (word.getPronunciation() != null) {
                    stmt.setString(2, word.getPronunciation());
                } else {
                    stmt.setNull(2, Types.VARCHAR);
                }

                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                word.setId(rs.getInt("last_insert_rowid()"));

                insertMeanings(word.getId(), word.getMeanings());

                stmt.close();
                database.commit();
                inserted = true;
                // System.out.println("Word " + word.getContent() + " created successfully");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            return inserted;
        }
    }

    private void insertMeanings(int wordId, List<Meaning> meanings) {
        try {
            String sqlQuery = "INSERT INTO MEANING (wordID, posTag, explanations, phrases) VALUES (?, ?, ?, ?);";
            PreparedStatement stmt = database.prepareStatement(sqlQuery);

            meanings.forEach(meaning -> {
                try {
                    stmt.setInt(1, wordId);
                    stmt.setString(2, meaning.getPosTag());
                    stmt.setString(3, meaning.getExplanations());
                    stmt.setString(4, meaning.getPhrases());

                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    meaning.setId(rs.getInt("last_insert_rowid()"));
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            });

            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        // System.out.println("Meanings created successfully");
    }

    public Word getWordByID(int wordID) {
        Word word = new Word();
        try {
            String sqlQuery = "SELECT * FROM WORD WHERE id = ?;";
            PreparedStatement stmt = database.prepareStatement(sqlQuery);
            stmt.setInt(1, wordID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                word.setId(wordID);
                word.setContent(rs.getString("content"));
                word.setPronunciation(rs.getString("pronunciation"));

                word.setMeanings(getMeaningsByWordID(wordID));
            } else {
                word = null;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        // System.out.println("getWordByID " + wordID + " done successfully");
        return word;
    }

    private List<Meaning> getMeaningsByWordID(int wordID) {
        List<Meaning> meanings = new ArrayList<>();
        try {
            String sqlQuery = "SELECT id, posTag, explanations, phrases FROM MEANING WHERE wordID = ?;";
            PreparedStatement stmt = database.prepareStatement(sqlQuery);
            stmt.setInt(1, wordID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                meanings.add(new Meaning(
                        rs.getInt("id"),
                        rs.getString("posTag"),
                        rs.getString("explanations"),
                        rs.getString("phrases")
                ));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        // System.out.println("getMeaningByWordID " + wordID + " done successfully");

        return meanings;
    }

    public List<Pair<Word, String>> getWordStartWith(String prefix) {
        List<Pair<Word, String>> result = new ArrayList<>();

        try {
            String sqlQuery = "SELECT W.id, content, POSTag " +
                    "FROM WORD W JOIN MEANING M on W.id = M.wordID " +
                    "WHERE content LIKE ? " +
                    "LIMIT 100;";
            PreparedStatement stmt = database.prepareStatement(sqlQuery);
            stmt.setString(1, prefix + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new Pair<>(
                        new Word(rs.getInt("id"), rs.getString("content"), null, null),
                        rs.getString("POSTag")
                ));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        // System.out.println("getWordStartWith " + prefix + " done successfully");

        return result;
    }

    public void deleteWordByID(int wordID) {
        try {
            String sqlQueryDelMeaning = "DELETE FROM MEANING WHERE wordID = ?;";
            PreparedStatement stmt = database.prepareStatement(sqlQueryDelMeaning);
            stmt.setInt(1, wordID);
            stmt.executeUpdate();

            String sqlQueryDelWord = "DELETE FROM WORD WHERE id = ?;";
            stmt = database.prepareStatement(sqlQueryDelWord);
            stmt.setInt(1, wordID);
            stmt.executeUpdate();

            stmt.close();
            database.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        // System.out.println("deleteWordByID " + wordID + " done successfully");
    }

    public static void main(String[] args) {
        SQLDatabase database = new SQLDatabase("test");
        database.initDatabase();
//        Word w = new Word(
//                -1, "curling", "/'kə:liɳ/",
//                List.of(
//                        new Meaning(
//                                -1,
//                                "danh từ",
//                                "- (Ê-cốt) môn đánh bi đá trên tuyết",
//                                "!sth"
//                        ),
//                        new Meaning(
//                                -1,
//                                "tính từ",
//                                "- quăn, xoắn",
//                                null
//                        )
//                )
//        );
//        boolean r = database.insertWord(w);
//        System.out.println(r);
//        Word word = database.getWordByID(1);
//        System.out.println(word);
//        List<Pair<Word, String>> r2 = database.getWordStartWith("cu");
//        System.out.println(r2);
//        database.deleteWordByID(1);
    }

}
