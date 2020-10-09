package dictionary;

public class Meaning {
    private int id;
    private String posTag;
    private String explanations;
    private String phrases;

    public Meaning() {
    }

    public Meaning(int id, String posTag, String explanations, String phrases) {
        this.id = id;
        this.posTag = posTag;
        this.explanations = explanations;
        this.phrases = phrases;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public String getExplanations() {
        return explanations;
    }

    public void setExplanations(String explanations) {
        this.explanations = explanations;
    }

    public String getPhrases() {
        return phrases;
    }

    public void setPhrases(String phrases) {
        this.phrases = phrases;
    }

    @Override
    public String toString() {
        return "Meaning{" +
                "posTag='" + posTag + '\'' +
                '}';
    }
}
