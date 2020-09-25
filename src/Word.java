public class Word {
    private int id;
    private String content;
    private String pronunciation;
    private String posTag;

    private Meaning meaning;

    public Word() {
    }

    public Word(String content, String pronunciation, String posTag) {
        this.id = -1;
        this.content = content;
        this.pronunciation = pronunciation;
        this.posTag = posTag;
        this.meaning = null;
    }

    public Word(int id, String content, String pronunciation, String posTag, Meaning meaning) {
        this.id = id;
        this.content = content;
        this.pronunciation = pronunciation;
        this.posTag = posTag;
        this.meaning = meaning;
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

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public Meaning getMeaning() {
        return meaning;
    }

    public void setMeaning(Meaning meaning) {
        this.meaning = meaning;
    }
}
