package core;

import javafx.util.Pair;

import java.util.List;

public class Explanation {
    private int id;
    private String explanation;
    private List<Pair<String, String>> examples;

    public Explanation() {
    }

    public Explanation(int id, String explanation, List<Pair<String, String>> examples) {
        this.id = id;
        this.explanation = explanation;
        this.examples = examples;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<Pair<String, String>> getExamples() {
        return examples;
    }

    public void setExamples(List<Pair<String, String>> examples) {
        this.examples = examples;
    }
}
