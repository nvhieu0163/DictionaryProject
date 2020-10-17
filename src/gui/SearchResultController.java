package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Objects;

public class SearchResultController {
    public int id;
    public Text content;
    public Text posTag;

    public void setData(int id, String content, String posTag) {
        this.id = id;
        this.content.setText(content);
        this.posTag.setText(Objects.requireNonNullElse(posTag, ""));
    }

    public Pair<Integer, String> getData() {
        return new Pair<>(id, posTag.getText().equals("") ? null : posTag.getText());
    }

    public void showWordDetail(MouseEvent mouseEvent) {

    }
}
