
package org.openjfx.home;

import java.io.IOException;
import org.openjfx.models.Teacher;
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
import javax.xml.bind.JAXBException;
import org.openjfx.fiverr5.App;
import org.openjfx.fiverr5.Singleton;
import org.openjfx.models.WrapperDAO;

/**
 * FXML Controller class
 *
 * @author Jatin
 */
public class AddTeachersController implements Initializable {

    @FXML
    private Button homeButton;
    @FXML
    private Button deleteButton1;
    @FXML
    private ListView<Teacher> teachersListView;
    @FXML
    private ColorPicker colorSelector;
    @FXML
    private TextField nameTextBox;
    @FXML
    private Button addButton;
    @FXML
    private Button viewButton;

    ObservableList<Teacher> teacherList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Singleton.getInstance().getLoggedInUserRole().equals("Teacher")) {
            deleteButton1.setDisable(true);
            addButton.setDisable(true);
        }
        teacherList = WrapperDAO.getList(Teacher.class, "teachers.xml");
        if (teacherList == null) {
            teacherList = FXCollections.observableArrayList();            
        } 
        populateListView();        
    }

    @FXML
    private void homeButtonClicked(ActionEvent event) throws JAXBException {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
            Logger.getLogger(AddTeachersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void ondeleteButtonClicked(ActionEvent event) {
        Teacher teacherToDel = teachersListView.getSelectionModel().getSelectedItem();
        if (teacherToDel == null) {           
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("No Teacher is selected");
            alert.showAndWait();
            return;
        }
        for (int i = 0; i < teacherList.size(); i++) {
            if (teacherList.get(i).getName().equalsIgnoreCase(teacherToDel.getName())){
                teacherList.remove(i);
                break;
            }
        }
        WrapperDAO.create(Teacher.class, teacherList, "teachers", "teachers.xml");
    }

    @FXML
    private void addButtonClicked(ActionEvent event) {
        String name = nameTextBox.getText();
        if (name == null || name == "") {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("No Teacher is selected");
            alert.showAndWait();            
            return;
        }
        if (!checkForDuplicate(name)) {            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Teacher already exists");
            alert.showAndWait();
            return;
        }        
        Color color = colorSelector.getValue();
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setColor(toHexString(color));
        teacher.setTimetable(createTeacherTimeTable());
                        
        teacherList.add(teacher);
        WrapperDAO.create(Teacher.class, teacherList, "teachers", "teachers.xml");
        
        nameTextBox.clear();
    }
    
    @FXML
    private void onviewButtonClicked(ActionEvent event) {
        Teacher teacherToView = teachersListView.getSelectionModel().getSelectedItem();
        if (teacherToView == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("No Teacher is selected");
            alert.showAndWait();   
            return;
        }
        Singleton.getInstance().setTimeTableName(teacherToView.getName());
        Singleton.getInstance().setTeacherView(true);
        try {
            App.setRoot("timetable");
        } catch (IOException ex) {
            Logger.getLogger(AddTeachersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateListView() {
        teachersListView.setItems(teacherList);
        teachersListView.setCellFactory(new Callback<ListView<Teacher>, ListCell<Teacher>>() {
            @Override
            public ListCell<Teacher> call(ListView<Teacher> param) {
                return new AddTeachersController.teacherCell<>();
            }
        });
    }
    
    //check for teacher with duplicate name
    private boolean checkForDuplicate(String name){
        AtomicBoolean result = new AtomicBoolean(true);
        teacherList.forEach((c)->{
            if (name.equalsIgnoreCase(c.getName())) {
                result.set(false);
            }
        });
        return result.get();
    }
    
    //create a blank time table for new teacher with all free slots
    private String createTeacherTimeTable(){
        String data="";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < Singleton.getInstance().getNO_OF_PERIODS(); j++) {
                data+="empty";
                if (j!=Singleton.getInstance().getNO_OF_PERIODS()-1) {
                    data+=",";
                }
            }
            if (i!=6) {
                data+="\n";
            }
        }
        return data;
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
    static class teacherCell<T> extends ListCell<T> {

        HBox hbox = new HBox();
        //CheckBox cb = new CheckBox();
        Label label = new Label();
        ImageView iv = new ImageView(new Image(getClass().getClassLoader().getResource("images/teacher.png").toString(), 25, 25, false, false));

        public teacherCell() {
            super();
            double size = 20;
//            iv.setFitHeight(size);
//            iv.setFitWidth(size);
            hbox.getChildren().addAll(iv, label);
            HBox.setMargin(label, new Insets(5, 5, 5, 5));
            //HBox.setMargin(cb, new Insets(5, 5, 5, 5));
            //HBox.setHgrow(pane, Priority.ALWAYS);

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
                label.setText(((Teacher) getItem()).getName());
                setGraphic(hbox);
            }
        }
    }
}
