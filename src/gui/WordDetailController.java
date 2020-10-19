package gui;

import dictionary.Dictionary;
import dictionary.DictionaryManager;
import dictionary.Meaning;
import dictionary.Word;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class WordDetailController {
    public VBox meaning;
    public Text wordContent;
    public Text pronunciation;
    private Scene prevScene = null;
    private Word word = null;
    private String posTag = null;
    Dictionary dict = DictionaryManager.getDictionary();

    public void actionQuit(ActionEvent actionEvent) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to quit?",
                ButtonType.YES, ButtonType.NO
        );
        alert.setTitle("Confirmation!");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
//                ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
                Platform.exit();
            }
        });
    }

    public void setData(int id, String posTag, Scene prevScene) {
        this.word = dict.getWordByID(id);
        this.posTag = posTag;
        this.prevScene = prevScene;

        show();
    }

    private void show() {
        wordContent.setText(word.getContent());
        pronunciation.setText(word.getPronunciation());

        Meaning wordMeaning = word.getMeaningByPOSTag(posTag);
        if (wordMeaning != null) {
            if (wordMeaning.getPosTag() != null && !wordMeaning.getPosTag().equals("")) {
                pushLine(wordMeaning.getPosTag(), "meaningPOSTag.fxml");
            }
            if (wordMeaning.getExplanations() != null) {
                for (String line : wordMeaning.getExplanations().split("\n")) {
                    processLine(line, false);
                }
            }
            if (wordMeaning.getPhrases() != null) {
                for (String line : wordMeaning.getPhrases().split("\n")) {
                    processLine(line, true);
                }
            }
        }
    }

    private void processLine(String line, boolean phrase) {
        switch (line.charAt(0)) {
            case '-' -> {
                if (phrase) {
                    pushLine(line.substring(2), "meaningPhraseExplanation.fxml");
                } else {
                    pushLine(line.substring(2), "meaningExplanation.fxml");
                }
            }
            case '=' -> {
                String[] parts = line.substring(1).split("\\+ ");
                pushLine(parts[0], "meaningExample.fxml");
                pushLine(parts[1], "meaningTranslate.fxml");
            }
            case '!' -> pushLine(line.substring(1), "meaningPhrase.fxml");
            default -> pushLine(line, "meaningLine.fxml");
        }
    }

    private void pushLine(String content, String url) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(url));
        try {
            Node meaningLine = loader.load();
            MeaningController controller = loader.getController();
            controller.setData(content);

            meaning.getChildren().add(meaningLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionEdit(ActionEvent actionEvent) {

    }

    public void actionDelete(ActionEvent actionEvent) {

    }

    public void actionSpeak(ActionEvent actionEvent) {

    }

    public void actionHome(ActionEvent actionEvent) {
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        window.setScene(prevScene);
        window.show();
    }
}
