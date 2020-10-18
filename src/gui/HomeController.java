package gui;

import dictionary.Dictionary;
import dictionary.DictionaryManager;
import dictionary.Word;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    public ScrollPane resultPane;
    DictionaryManager dict = new DictionaryManager();
    public TextField searchBox;
    private String searchText = "";

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

    public void actionSearch(KeyEvent keyEvent) {
        String input = searchBox.getText();
        if (!input.equals(searchText)) {
            searchText = input;
            doSearchQuery(input);
        }
    }

    private void doSearchQuery(String query) {
        List<Pair<Word, String>> queryResult = dict.getWordStartWith(query);
        VBox wordList = new VBox();

        queryResult.forEach(pair -> {
            Word w = pair.getKey();
            String p = pair.getValue();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("searchResult.fxml"));
            try {
                Node wordItem = loader.load();
                SearchResultController controller = loader.getController();
                controller.setData(w.getId(), w.getContent(), p);

                wordList.getChildren().add(wordItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        resultPane.setContent(wordList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doSearchQuery("");
    }
}
