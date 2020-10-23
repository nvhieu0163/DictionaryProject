package gui;

import dictionary.Dictionary;
import dictionary.DictionaryManager;
import dictionary.Word;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    public void addWordFromFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose File Word Input");
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        File file = fc.showOpenDialog(window);

        if (file != null) {
            loading.setVisible(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                int numOfInserted = dict.loadFileIntoDatabase(file);
                Platform.runLater(() -> {
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
                });
            });
            pause.play();

        }
        System.out.println(file);
    }

    public void removeWordResultById(int id) {
        wordPosObservableList.removeIf(wordPos -> wordPos.getKey().getId() == id);
    }

    public void showWordDetail(int id, String posTag) {
        Scene home = this.resultList.getScene();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/wordDetail.fxml"));
        try {
            Parent wordDetail = loader.load();
            Scene scene = new Scene(wordDetail);
            scene.getStylesheets().add("/css/style.css");

            //access the controller and call a method
            WordDetailController controller = loader.getController();
            Word w = dict.getWordByID(id);
            controller.setData(w, posTag, home, this);

            //This line gets the Stage information
            Stage window = (Stage) home.getWindow();

            window.setScene(scene);
//            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
