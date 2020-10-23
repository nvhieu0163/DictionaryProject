package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Objects;

public class ResultController {
    @FXML
    private Text content;
    @FXML
    private Text posTag;
    @FXML
    private HBox resultBox;

    public ResultController() {
        FXMLLoader fxmlLoader  = new FXMLLoader(getClass().getResource("/fxml/result.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(String content, String posTag) {
        this.content.setText(content);
        this.posTag.setText(Objects.requireNonNullElse(posTag, ""));
    }

    public HBox getBox() {
        return resultBox;
    }
}
