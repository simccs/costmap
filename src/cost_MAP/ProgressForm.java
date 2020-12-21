/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cost_MAP;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressForm {

    private final Stage dialogStage;
    private final ProgressBar pb = new ProgressBar();
    private final ProgressIndicator pin = new ProgressIndicator();

    public ProgressForm() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Importing Data");

        pb.setProgress(-1F);
        pin.setProgress(-1F);

        final VBox hb = new VBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.setPadding(new Insets(2));
        hb.getChildren().addAll(pb, pin);

        Scene scene = new Scene(hb, 200, 100);
        dialogStage.setScene(scene);

    }
    
    public void activateProgressBar(final Task<?> task)  {
//            pb.progressProperty().bind(task.progressProperty());
            pin.progressProperty().bind(task.progressProperty());
            dialogStage.show();
        }

        public Stage getDialogStage() {
            return dialogStage;
        }
}
