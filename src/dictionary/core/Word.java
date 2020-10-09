package dictionary;

import java.util.List;

public class Word {
    private int id;
    private String content;
    private String pronunciation;

    private List<Meaning> meanings;

    public Word() {
    }

    public Word(int id, String content, String pronunciation, List<Meaning> meanings) {
        this.id = id;
        this.content = content;
        this.pronunciation = pronunciation;
        this.meanings = meanings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }

    @Override
    public String toString() {
        return "Word{" +
                "content='" + content + '\'' +
                ", pronunciation='" + pronunciation + '\'' +
                ", meanings=" + meanings +
                '}';
    }
}
