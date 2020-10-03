package org.openjfx.home;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.openjfx.fiverr5.App;
import org.openjfx.fiverr5.Singleton;
import org.openjfx.models.Subject;
import org.openjfx.models.WrapperDAO;

/**
 * FXML Controller class
 *
 * @author Jatin
 */
public class AddSubjectsController implements Initializable {

    @FXML
    private Button homeButton;
    @FXML
    private Button deleteButton1;
    @FXML
    private ListView<Subject> subjectsListView;
    @FXML
    private ColorPicker colorSelector;
    @FXML
    private TextField nameTextBox;
    @FXML
    private Button addButton;

    ObservableList<Subject> subjectList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Singleton.getInstance().getLoggedInUserRole().equals("Teacher")) {
            deleteButton1.setDisable(true);
            addButton.setDisable(true);
        }
        subjectList = WrapperDAO.getList(Subject.class, "subjects.xml");
        if (subjectList == null) {
            subjectList = FXCollections.observableArrayList();           
        } 
        populateListView();
    }

    @FXML
    private void homeButtonClicked(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
            Logger.getLogger(AddSubjectsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void ondeleteButtonClicked(ActionEvent event) {
        Subject subjectToDel = subjectsListView.getSelectionModel().getSelectedItem();
        if (subjectToDel == null) {            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("No Subject is selected");
            alert.showAndWait();
            return;
        }
        for (int i = 0; i < subjectList.size(); i++) {
            if (subjectList.get(i).getName().equalsIgnoreCase(subjectToDel.getName())) {
                subjectList.remove(i);
                break;
            }
        }
        WrapperDAO.create(Subject.class, subjectList, "subjects", "subjects.xml");
    }

    @FXML
    private void addButtonClicked(ActionEvent event) {
        String name = nameTextBox.getText();
        if (name == null || name == "") {  
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Subject name cannot be empty");
            alert.showAndWait();
            return;
        }
        if (!checkForDuplicate(name)) {            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Subject already exists");
            alert.showAndWait();
            return;
        }
        Color color = colorSelector.getValue();
        Subject subject = new Subject();
        subject.setName(name);
        subject.setColor(toHexString(color));

        subjectList.add(subject);
        WrapperDAO.create(Subject.class, subjectList, "subjects", "subjects.xml");

        nameTextBox.clear();
    }

    private void populateListView() {
        subjectsListView.setItems(subjectList);
        subjectsListView.setCellFactory(new Callback<ListView<Subject>, ListCell<Subject>>() {
            @Override
            public ListCell<Subject> call(ListView<Subject> param) {
                return new AddSubjectsController.subjectCell<>();
            }
        });
    }

    //check for subject with duplicate name
    private boolean checkForDuplicate(String name) {
        AtomicBoolean result = new AtomicBoolean(true);
        subjectList.forEach((c) -> {
            if (name.equalsIgnoreCase(c.getName())) {
                result.set(false);
            }
        });
        return result.get();
    }

    //Convert Color to Hex
    private static String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed() * 255)) << 24;
        int g = ((int) Math.round(color.getGreen() * 255)) << 16;
        int b = ((int) Math.round(color.getBlue() * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
        return String.format("#%08X", (r + g + b + a));
    }

    //cell factory
    static class subjectCell<T> extends ListCell<T> {

        HBox hbox = new HBox();
        //CheckBox cb = new CheckBox();
        Label label = new Label();
        ImageView iv = new ImageView(new Image(getClass().getClassLoader().getResource("images/subject.png").toString(), 25, 25, false, false));

        public subjectCell() {
            super();
            double size = 20;
            hbox.getChildren().addAll(iv, label);
            HBox.setMargin(label, new Insets(5, 5, 5, 5));       
            ListCell<String> thisCell = (ListCell<String>) this;
            final int selectedCell = 0;
            int targetCell = 0;

        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                setGraphic(null);
            } else {
                label.setText(((Subject) getItem()).getName());
                setGraphic(hbox);
            }
        }
    }
}
