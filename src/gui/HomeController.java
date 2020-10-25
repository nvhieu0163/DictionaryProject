package gui;

import dictionary.Dictionary;
import dictionary.DictionaryManager;
import dictionary.Meaning;
import dictionary.Word;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Pane loading;
    @FXML
    private ListView<Pair<Word, String>> resultList;
    @FXML
    private TextField searchBox;

    Dictionary dict = DictionaryManager.getDictionary();
    private String searchText = "";
    private final ObservableList<Pair<Word, String>> wordPosObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultList.setItems(wordPosObservableList);
        resultList.setCellFactory(resultListView -> new ResultListCell());
        resultList.setOnMouseClicked(event -> {
            if (!resultList.getSelectionModel().isEmpty()) {
                Pair<Word, String> selectedWord = resultList.getSelectionModel().getSelectedItem();
                resultList.getSelectionModel().clearSelection();
                showWordDetail(selectedWord.getKey().getId(), selectedWord.getValue());
            }
        });

        doSearchQuery("");
    }

    public void actionQuit(ActionEvent actionEvent) {
        Alert alert = new Alert(
                Alert.AlertType.NONE,
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

    public void actionSearch(KeyEvent keyEvent) {
        String input = searchBox.getText();
        if (!input.equals(searchText)) {
            searchText = input;
            doSearchQuery(input);
        }
    }

    private void doSearchQuery(String query) {
//        System.out.println("query = " + query);
        List<Pair<Word, String>> queryResult = dict.getWordStartWith(query);
        wordPosObservableList.setAll(queryResult);
    }

    public void addWordFromFile(ActionEvent actionEvent)  {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose File Word Input");
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        File file = fc.showOpenDialog(window);

        if (file != null) {
            loading.setVisible(true);
            int numOfInserted = dict.loadFileIntoDatabase(file);
            doSearchQuery(searchBox.getText());
            loading.setVisible(false);
            Alert alert = new Alert(
                    Alert.AlertType.NONE,
                    String.format("%d words inserted.", numOfInserted),
                    ButtonType.OK
            );
            alert.setTitle("Done!");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        System.out.println(file);
    }

    public void removeWordResultById(int id) {
        wordPosObservableList.removeIf(wordPos -> wordPos.getKey().getId() == id);
    }

    public void insertOneWord() {
        Word newWord = new Word();
        List<Meaning> meanings = new ArrayList<>();

        Dialog<String> newDialog = new Dialog<>();
        newDialog.setTitle("Add New Word");
        newDialog.setHeaderText("Enter word");

        ButtonType insertButtonType = new ButtonType("Insert ", ButtonBar.ButtonData.OK_DONE);
        newDialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20); // set khoang cach tren va duoi
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20, 150, 20, 20)); // top - right - bottom - left

        TextField content = new TextField();
        TextField pronunciation = new TextField();
        TextField posTag = new TextField();
        TextField explanation = new TextField();
        TextField phrase = new TextField();

        gridPane.add(new Label("New Word: "),0 ,0); // cot , hang
        gridPane.add(content, 1, 0);
        gridPane.add(new Label("Pronunciation: "),0 ,1);
        gridPane.add(pronunciation, 1, 1);
        gridPane.add(new Label("Part Of Speech:"),0, 2);
        gridPane.add(posTag, 1, 2);
        gridPane.add(new Label("Explanation: "),0 ,3);
        gridPane.add(explanation, 1, 3);
        gridPane.add(new Label("Phrase: "),0 ,4);
        gridPane.add(phrase, 1, 4);

        Node editButton = newDialog.getDialogPane().lookupButton(insertButtonType);
        editButton.setDisable(true);

        content.textProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.trim().isEmpty());
        });

        newDialog.getDialogPane().setContent(gridPane);

        newDialog.setResultConverter(dialogButton -> {
            if (dialogButton == insertButtonType) {
                newWord.setId(-1);
                newWord.setContent(content.getText());
                newWord.setPronunciation(pronunciation.getText());

                meanings.add(new Meaning(-1, posTag.getText(), explanation.getText(), phrase.getText()));
                newWord.setMeanings(meanings);

                if (dict.insertWord(newWord)) {
                    return "Add Word Successfully";
                }
            } else {
                return "Cancel Edit";
            }
            return "Edit Unsuccessfully";
        });
        newDialog.showAndWait();

    }

    public void showWordDetail(int id, String posTag) {
        Scene home = this.resultList.getScene();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/wordDetail.fxml"));
        try {
            Parent wordDetail = loader.load();
            Scene scene = new Scene(wordDetail);
            scene.getStylesheets().add("/css/style.css");

            //access the controller
            WordDetailController controller = loader.getController();

            Word w = dict.getWordByID(id);
            controller.setData(w, posTag, home,this);

            //This line gets the Stage information
            Stage window = (Stage) home.getWindow();

            window.setScene(scene);
//            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
