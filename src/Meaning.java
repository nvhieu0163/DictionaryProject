import java.util.List;

public class Meaning {
    private int id;
    private List<String> explanation;

    public Meaning() {
    }

    public Meaning(int id, List<String> explanation) {
        this.id = id;
        this.explanation = explanation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getExplanation() {
        return explanation;
    }

    public void setExplanation(List<String> explanation) {
        this.explanation = explanation;
    }
}
