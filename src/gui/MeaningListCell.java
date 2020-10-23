package gui;

import javafx.scene.control.ListCell;
import javafx.util.Pair;

public class MeaningListCell extends ListCell<Pair<String, Integer>> {
    @Override
    protected void updateItem(Pair<String, Integer> line, boolean empty) {
        super.updateItem(line, empty);

        if (empty || line == null) {
            setText(null);
            setGraphic(null);
        } else {
            MeaningController controller = new MeaningController(line.getValue());
            controller.setData(line.getKey());

            setText(null);
            setGraphic(controller.getBox());
        }
    }
}
