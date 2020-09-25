import javafx.util.Pair;

import java.util.List;

public class Meaning {
    private int id;
    private List<Explanation> explanation;
    private List<Pair<String, String>> phrases;

    public Meaning() {
    }

    public Meaning(int id, List<Explanation> explanation, List<Pair<String, String>> phrases) {
        this.id = id;
        this.explanation = explanation;
        this.phrases = phrases;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Explanation> getExplanation() {
        return explanation;
    }

    public void setExplanation(List<Explanation> explanation) {
        this.explanation = explanation;
    }

    public List<Pair<String, String>> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<Pair<String, String>> phrases) {
        this.phrases = phrases;
    }
}
