package gui;

import dictionary.Word;
import javafx.scene.control.ListCell;
import javafx.util.Pair;

public class ResultListCell extends ListCell<Pair<Word, String>> {
    @Override
    protected void updateItem(Pair<Word, String> wordPos, boolean empty) {
        super.updateItem(wordPos, empty);

        if (empty || wordPos == null) {
            setText(null);
            setGraphic(null);
        } else {
            ResultController controller = new ResultController();
            controller.setData(wordPos.getKey().getContent(), wordPos.getValue());

            setText(null);
            setGraphic(controller.getBox());
        }
    }
}
