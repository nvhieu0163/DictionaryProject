package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class MeaningController {
    public final static Integer TYPE_LINE = 0;
    public final static Integer TYPE_CONTENT = 1;
    public final static Integer TYPE_PRONUNCIATION = 2;
    public final static Integer TYPE_POS_TAG = 3;
    public final static Integer TYPE_EXPLANATION = 4;
    public final static Integer TYPE_PHRASE = 5;
    public final static Integer TYPE_PHRASE_EXPLANATION = 6;
    public final static Integer TYPE_EXAMPLE = 7;
    public final static Integer TYPE_TRANSLATE = 8;

    public final static Map<Integer, String> FXML_FILENAME = Map.of(
            TYPE_LINE, "/fxml/meaningLine.fxml",
            TYPE_CONTENT, "/fxml/meaningContent.fxml",
            TYPE_PRONUNCIATION, "/fxml/meaningPronunciation.fxml",
            TYPE_POS_TAG, "/fxml/meaningPOSTag.fxml",
            TYPE_EXPLANATION, "/fxml/meaningExplanation.fxml",
            TYPE_PHRASE, "/fxml/meaningPhrase.fxml",
            TYPE_PHRASE_EXPLANATION, "/fxml/meaningPhraseExplanation.fxml",
            TYPE_EXAMPLE, "/fxml/meaningExample.fxml",
            TYPE_TRANSLATE, "/fxml/meaningTranslate.fxml"
    );

    @FXML
    private Text content;
    @FXML
    private HBox meaningBox;

    public MeaningController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                FXML_FILENAME.get(TYPE_LINE)
        ));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MeaningController(Integer type) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                FXML_FILENAME.get(type)
        ));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(String content) {
        this.content.setText(Objects.requireNonNullElse(content, ""));
    }

    public HBox getBox() {
        return meaningBox;
    }
}
