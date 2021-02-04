
package org.openjfx.home;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.openjfx.fiverr5.App;
import org.openjfx.models.Teacher;
import org.openjfx.models.User;
import org.openjfx.models.WrapperDAO;

/**
 * FXML Controller class
 *
 * @author Work
 */
public class TeacherDetailsController implements Initializable {

    @FXML
    private Button homeButton;
    @FXML
    private TableView<List<String>> tableView;
    @FXML
    private TableColumn<List<String>, String> nameTC;
    @FXML
    private TableColumn<List<String>, String> monTC;
    @FXML
    private TableColumn<List<String>, String> tueTC;
    @FXML
    private TableColumn<List<String>, String> wedTC;
    @FXML
    private TableColumn<List<String>, String> thuTC;
    @FXML
    private TableColumn<List<String>, String> friTC;
    @FXML
    private TableColumn<List<String>, String> weeklyTC;

    ObservableList<Teacher> teacherList;
    ObservableList<List<String>> detailsList;

    /**
     * We use a table to store all the teacher details. The data will be stored
     * in a list. This list will contain as many items as the no of teachers.
     * Each item will in turn will be a new list which will have teacher name at
     * first index, daily details in index 1 - 5 and weekly details in index 6
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        teacherList = WrapperDAO.getList(Teacher.class, "teachers.xml");

        //check if teachers are there or not
        if (teacherList == null) {
            teacherList = FXCollections.observableArrayList();
            return;
        }

        detailsList = FXCollections.observableArrayList();

        for (int i = 0; i < teacherList.size(); i++) {
            Teacher teacher = teacherList.get(i);

            List<String> teacherDetail = new ArrayList<>();
            teacherDetail.add(teacher.getName());

            String[] lines = teacher.getTimetable().split("\n");

            int dailyHours = 0;
            int weeklyHours = 0;

            for (int j = 0; j < lines.length; j++) {
                for (String period : lines[j].split(",")) {
                    if (!period.equals("empty")) {
                        dailyHours++;
                        weeklyHours++;
                    }
                }
                teacherDetail.add("No.of Periods - " + dailyHours + "\nNo.of Hours - " + dailyHours);
                dailyHours = 0;
            }
            teacherDetail.add("No.of Periods - " + weeklyHours + "\nNo.of Hours - " + weeklyHours);
            detailsList.add(teacherDetail);
        }

        System.out.println(detailsList);

        //bind the value in the list to the table columns
        nameTC.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        monTC.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        tueTC.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        wedTC.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        thuTC.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        friTC.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        weeklyTC.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));
        
        //set the list as a data source for the table
        tableView.setItems(detailsList);
    }

    @FXML
    private void homeButtonClicked(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
            Logger.getLogger(TeacherDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
