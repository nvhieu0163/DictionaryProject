package gui;

import dictionary.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WordDetailController implements Initializable {
    @FXML
    private ListView<Pair<String, Integer>> wordDetail;

    Dictionary dict = DictionaryManager.getDictionary();

    private final TextToSpeech textToSpeech = new TextToSpeech("");
    private Scene prevScene = null;
    private HomeController homeController = null;
    private Word word = null;
    private String posTag = null;
    private final ObservableList<Pair<String, Integer>> meaningObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wordDetail.setItems(meaningObservableList);
        wordDetail.setCellFactory(meaningListView -> new MeaningListCell());
    }

    public void setData(Word w, String posTag, Scene prevScene, HomeController controller) {
        this.word = w;
        this.posTag = posTag;
        this.prevScene = prevScene;
        this.homeController = controller;

        show();
    }

    private void show() {
        meaningObservableList.setAll(List.of(
                new Pair<>(word.getContent(), MeaningController.TYPE_CONTENT),
                new Pair<>(word.getPronunciation(), MeaningController.TYPE_PRONUNCIATION)
        ));

        Meaning wordMeaning = word.getMeaningByPOSTag(posTag);
        if (wordMeaning != null) {
            if (wordMeaning.getPosTag() != null && !wordMeaning.getPosTag().equals("")) {
                meaningObservableList.add(
                        new Pair<>(wordMeaning.getPosTag(), MeaningController.TYPE_POS_TAG)
                );
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
                    meaningObservableList.add(new Pair<>(line.substring(2), MeaningController.TYPE_PHRASE_EXPLANATION));
                } else {
                    meaningObservableList.add(new Pair<>(line.substring(2), MeaningController.TYPE_EXPLANATION));
                }
            }
            case '=' -> {
                String[] parts = line.substring(1).split("\\+ ");
                meaningObservableList.addAll(List.of(
                        new Pair<>(parts[0], MeaningController.TYPE_EXAMPLE),
                        new Pair<>(parts[1], MeaningController.TYPE_TRANSLATE)
                ));
            }
            case '!' -> meaningObservableList.add(new Pair<>(line.substring(1), MeaningController.TYPE_PHRASE));
            default -> meaningObservableList.add(new Pair<>(line, MeaningController.TYPE_LINE));
        }
    }

    public void actionDelete(ActionEvent actionEvent) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this word?",
                ButtonType.YES, ButtonType.NO
        );
        alert.setTitle("Confirmation!");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
//                ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
                dict.deleteWordByID(word.getId());
                homeController.removeWordResultById(word.getId());

                actionHome(actionEvent);
            }
        });

    }

    public void actionSpeak(ActionEvent actionEvent) {
        textToSpeech.SpeakText(word.getContent());
    }

    public void actionEdit(ActionEvent actionEvent) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Pronunciation");
        dialog.setHeaderText(null);

        ButtonType doneButtonType = new ButtonType("Xong nè ", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, doneButtonType);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20); // set khoang cach tren va duoi
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20, 150, 20, 20)); // top - right - bottom - left

        TextField pronunciation = new TextField();

        gridPane.add(new Label("New Pronunciation: "),0 ,0);
        gridPane.add(pronunciation, 1, 0);

        Node editButton = dialog.getDialogPane().lookupButton(doneButtonType);
        editButton.setDisable(true);

        pronunciation.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == doneButtonType) {
                if (dict.editPronunciationByID(word.getId(), pronunciation.getText())) {
                    actionHome(actionEvent);
                    return "Edit Successfully";
                }
            } else {
                return "Cancel Edit";
            }
            return "Edit Unsuccessfully";
        });
        dialog.showAndWait();
    }

    public void actionHome(ActionEvent actionEvent) {
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(prevScene);
//        window.show();
    }
}
