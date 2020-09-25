package data;

import core.Word;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
                    "pronunciation VARCHAR(100) NULL, " +
                    "POSTag INT NULL" +
                    ");";
            stmt.executeUpdate(sqlWord);

            String sqlDropMeaning = "DROP TABLE IF EXISTS MEANING;";
            stmt.executeUpdate(sqlDropMeaning);
            String sqlMeaning = "CREATE TABLE MEANING (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "wordID INT NOT NULL, " +
                    "CONSTRAINT meaning_word " +
                    "FOREIGN KEY (wordID) " +
                    "REFERENCES WORD (id) " +
                    "ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(sqlMeaning);

            String sqlDropPhrase = "DROP TABLE IF EXISTS PHRASE;";
            stmt.executeUpdate(sqlDropPhrase);
            String sqlPhrase = "CREATE TABLE PHRASE (" +
                    "meaningID INT NOT NULL, " +
                    "phrase TEXT NOT NULL, " +
                    "translate TEXT NOT NULL, " +
                    "CONSTRAINT phrase_meaning " +
                    "FOREIGN KEY (meaningID) " +
                    "REFERENCES MEANING (id) " +
                    "ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(sqlPhrase);

            String sqlDropExplanation = "DROP TABLE IF EXISTS EXPLANATION;";
            stmt.executeUpdate(sqlDropExplanation);
            String sqlExplanation = "CREATE TABLE EXPLANATION (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "meaningID INT NOT NULL, " +
                    "explanation TEXT NOT NULL, " +
                    "CONSTRAINT explanation_meaning " +
                    "FOREIGN KEY (meaningID) " +
                    "REFERENCES MEANING (id) " +
                    "ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(sqlExplanation);

            String sqlDropExample = "DROP TABLE IF EXISTS EXAMPLE";
            stmt.executeUpdate(sqlDropExample);
            String sqlExample = "CREATE TABLE EXAMPLE (" +
                    "explanationID INT NOT NULL, " +
                    "example TEXT NOT NULL, " +
                    "translate TEXT NOT NULL, " +
                    "CONSTRAINT example_explanation " +
                    "FOREIGN KEY (explanationID) " +
                    "REFERENCES EXPLANATION (id) " +
                    "ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(sqlExample);

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
            stmt = database.createStatement();

            String sql = "INSERT INTO WORD (content, pronunciation, POSTag) " +
                    String.format("VALUES ('%s', '%s', '%s');", word.getContent(), word.getPronunciation(), word.getPosTag());
            stmt.executeUpdate(sql);

            stmt.close();
            database.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Words created successfully");
    }


    public static void main(String[] args) {
        SQLDatabase database = new SQLDatabase("test");
//        database.initDatabase();
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
//        database.insertWord(w);
//        database.insertWord(w);
//        database.insertWord(w);
//        database.insertWord(w);
//        database.insertWord(w);
//        database.insertWord(w);
//        database.insertWord(w);
//        database.insertWord(w);
    }

}
