package org.openjfx.home;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openjfx.fiverr5.App;
import org.openjfx.fiverr5.Singleton;
import org.openjfx.models.Period;
import org.openjfx.models.Subject;
import org.openjfx.models.Teacher;
import org.openjfx.models.TimeTable;
import org.openjfx.models.WrapperDAO;

/**
 * FXML Controller class
 *
 * @author Jatin
 */
public class TimetableController implements Initializable {

    @FXML
    private GridPane timetableGrid;
    @FXML
    private Button editButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button deleteButton;

    private final int NO_OF_COLUMNS = 9;
    private final int NO_OF_ROWS = 9;

    private ObservableList<Subject> subjectList;
    private ObservableList<Teacher> teacherList;
    private ObservableList<TimeTable> timetableList;
    private ArrayList<ArrayList<String>> timeTableArrayList;
    private ArrayList<ArrayList<String>> timeTableArrayListBackup; //To determine if a teacher is removed

    private Map<String, String> subjectColorMap;
    private Map<String, String> teacherColorMap;
    private Map<String, String> teacherTimeTableMap;
    private TimeTable timeTable;
    private boolean teacherView;
    private String timeTableName;
    private Period period;
    List<String> periodsNameList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        timeTableName = Singleton.getInstance().getTimeTableName();

        if (Singleton.getInstance().getLoggedInUserRole().equals("Teacher")) {
            editButton.setDisable(true);
            exportButton.setDisable(true);
            deleteButton.setDisable(true);
        }

        //For custom period names
        try {
            ObservableList<Period> periodList = WrapperDAO.getList(Period.class, "periods.xml");
            if (periodList != null) {
                period = periodList.get(0);
                periodsNameList = new ArrayList();
                String data = period.toString();
                String[] periodnames = data.substring(1, data.length() - 1).replace(" ", "").split(",");
                for (String periodname : periodnames) {
                    periodsNameList.add(periodname);
                }
            }
        } catch (Exception e) {
            //continue
        }

        makeTimeTableStructure();

        subjectList = WrapperDAO.getList(Subject.class, "subjects.xml");
        if (subjectList == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Please add at least one Subject and Teacher");
            alert.showAndWait();
        }
        subjectColorMap = new HashMap<>();
        subjectList.forEach((c) -> {
            subjectColorMap.put(c.getName(), c.getColor());
        });

        teacherList = WrapperDAO.getList(Teacher.class, "teachers.xml");
        if (teacherList == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Please add at least one Subject and Teacher");
            alert.showAndWait();
        }

        teacherColorMap = new HashMap<>();
        teacherTimeTableMap = new HashMap<>();
        teacherList.forEach((c) -> {
            teacherColorMap.put(c.getName(), c.getColor());
            teacherTimeTableMap.put(c.getName(), c.getTimetable());
        });
        Teacher teacher = new Teacher();
        teacher.setName("empty");
        teacherList.add(teacher);

        if (Singleton.getInstance().isTeacherView()) {
            //show Time table for teacher
            saveTeacherDataToList();
            populateDatainGridFromList();
            editButton.setDisable(true);
        } else {
            timetableList = WrapperDAO.getList(TimeTable.class, "timetables.xml");

            for (int i = 0; i < timetableList.size(); i++) {
                TimeTable rimeTableTemp = timetableList.get(i);
                if (rimeTableTemp.getName().equalsIgnoreCase(timeTableName)) {
                    timeTable = rimeTableTemp;
                    break;
                }
            }

            if (!timeTable.getData().equals("")) {                
                saveExistingDataToList();
                populateDatainGridFromList();
            }
        }

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void oneditButtonClicked(ActionEvent event) {
        makeTimeTableStructure();
        int numCols = 11;
        int numRows = 6;

        Runnable task = () -> {
            for (int i = 1; i < numCols; i++) {
                for (int j = 1; j < numRows; j++) {
                    addComboBoxToGrid(i, j);
                }
            }
        };
        Thread thread = new Thread(task);
        task.run();

        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        editButton.setDisable(true);
        exportButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void onexportButtonClicked(ActionEvent event) {
        if (timeTableArrayList == null) {
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Select an Option.");
//        alert.setContentText("Choose your option.");

        ButtonType saveLocal = new ButtonType("Save File");
        ButtonType openDrive = new ButtonType("Save File & Open Google Drive");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(saveLocal, openDrive, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == saveLocal) {
            exportFile();
        } else if (result.get() == openDrive) {
            exportFile();
            try {
                Desktop.getDesktop().browse(new URI("https://drive.google.com/drive/my-drive"));
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(TimetableController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    @FXML
    private void onsaveButtonClicked(ActionEvent event) {
        saveDataToList();

        String timeTableData = getStringFromList();
        timetableList.forEach((c) -> {
            if (c.getName().equals(timeTableName)) {
                c.setData(timeTableData);
            }
        });
        WrapperDAO.create(TimeTable.class, timetableList, "timetables", "timetables.xml");        

        makeTimeTableStructure();
        populateDatainGridFromList();

        //Update Teacher TimeTable data
        if (timeTableArrayListBackup != null) {
            updateTeacherDatafromBackup();
        }
        for (int i = teacherList.size() - 1; i >= 0; i--) {
            if (teacherList.get(i).toString().equals("empty")) {
                teacherList.remove(i);
            }
        }
        WrapperDAO.create(Teacher.class, teacherList, "teachers", "teachers.xml");

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        editButton.setDisable(false);
        exportButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    private void onhomeButtonClicked(ActionEvent event) {
        try {
            Singleton.getInstance().setTeacherView(false);
            App.setRoot("home");
        } catch (IOException ex) {
            Logger.getLogger(TimetableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void oncancelButtonClicked(ActionEvent event) {
        makeTimeTableStructure();
        populateDatainGridFromList();

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        editButton.setDisable(false);
        exportButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    @FXML
    private void ondeleteButtonClicked(ActionEvent event) {
        onTimeTableDeleteUpdatTeachers();

        for (int i = 0; i < timetableList.size(); i++) {
            if (timetableList.get(i).getName().equals(timeTableName)) {
                timetableList.remove(i);
                break;
            }
        }
        WrapperDAO.create(TimeTable.class, timetableList, "timetables", "timetables.xml");
        try {
            Singleton.getInstance().setTeacherView(false);
            App.setRoot("home");
        } catch (IOException ex) {
            Logger.getLogger(TimetableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addComboBoxToGrid(int colIndex, int rowIndex) {
        VBox vbox = new VBox();
        vbox.setId("vbox-" + colIndex + "-" + rowIndex);
        vbox.setSpacing(0);
        if ((colIndex % 2 != 0 && rowIndex % 2 == 0) || (colIndex % 2 == 0 && rowIndex % 2 != 0)) {
            vbox.getStyleClass().add("vboxCellsEven");
        }
        vbox.getStyleClass().add("vboxCells");

        ComboBox<Subject> choiceBox = new ComboBox();
        choiceBox.setId("comboboxSubject");
        choiceBox.setItems(subjectList);
        choiceBox.setCellFactory(new Callback<ListView<Subject>, ListCell<Subject>>() {
            @Override
            public ListCell<Subject> call(ListView<Subject> param) {
                return new TimetableController.subjectCell<>();
            }
        });
        if (timeTableArrayList != null && !timeTableArrayList.isEmpty()) {
            Subject subject;
            String subjectName = timeTableArrayList.get(rowIndex - 1).get(colIndex - 1).split("%")[0];
            if (!subjectName.equals("null")) {
                for (int i = 0; i < subjectList.size(); i++) {
                    if (subjectList.get(i).toString().endsWith(subjectName)) {
                        subject = subjectList.get(i);
                        choiceBox.setValue(subject);
                        break;
                    }
                }
            }
        }

        VBox.setMargin(choiceBox, new Insets(5, 14, 2, 10));
        vbox.getChildren().add(choiceBox);

        ComboBox<Teacher> choiceBox2 = new ComboBox();
        choiceBox2.setId("comboboxTeacher");
        choiceBox2.setItems(teacherList);

        //If teacher is already assigned, change selection to null
        choiceBox2.setOnAction(e -> {
            if (choiceBox2.getValue() == null) {
                return;
            }            
            Teacher teacher = choiceBox2.getValue();
            String data = teacher.getTimetable();
            if (!data.split("\n")[rowIndex - 1].split(",")[colIndex - 1].equals("empty")) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Error!!!!!!");
                alert.setHeaderText(null);
                alert.setContentText("This slot is not avilable for " + teacher.getName());
                alert.showAndWait();
                choiceBox2.getSelectionModel().clearSelection();
            }
        });

        choiceBox2.setCellFactory(new Callback<ListView<Teacher>, ListCell<Teacher>>() {
            @Override
            public ListCell<Teacher> call(ListView<Teacher> param) {
                return new TimetableController.teacherCell<>();
            }
        });
        if (timeTableArrayList != null && !timeTableArrayList.isEmpty()) {
            Teacher teacher;
            String teacherName = timeTableArrayList.get(rowIndex - 1).get(colIndex - 1).split("%")[1];
            if (!teacherName.equals("null")) {
                for (int i = 0; i < teacherList.size(); i++) {
                    if (teacherList.get(i).toString().endsWith(teacherName)) {
                        teacher = teacherList.get(i);
                        choiceBox2.setValue(teacher);
                        break;
                    }
                }
            }
        }

        vbox.getChildren().add(choiceBox2);
        VBox.setMargin(choiceBox2, new Insets(5, 14, 2, 10));
        timetableGrid.add(vbox, colIndex, rowIndex);
    }

    private void saveTeacherDataToList() {
        String timetableData = teacherTimeTableMap.get(timeTableName);
        timeTableArrayList = new ArrayList();
        for (int i = 0; i < 5; i++) {
            timeTableArrayList.add(new ArrayList<>());
        }
        String[] lines = timetableData.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            for (int j = 0; j < values.length; j++) {
                timeTableArrayList.get(i).add(values[j]);
            }
        }
    }

    private void saveDataToList() {
        if (timeTableArrayList != null) {
            timeTableArrayListBackup = new ArrayList<>(timeTableArrayList);
        }

        timeTableArrayList = new ArrayList();
        for (int i = 0; i < 5; i++) {
            timeTableArrayList.add(new ArrayList<>());
        }
        for (Node node : timetableGrid.getChildren()) {
            Integer columnIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (columnIndex == null || columnIndex == 0) {
                continue;
            }
            if (rowIndex == null || rowIndex == 0) {
                continue;
            }

            if (node.getId().contains("vbox")) {
                VBox vb = (VBox) node;
                ComboBox comboBox1 = (ComboBox) vb.getChildren().get(0);
                String cb1Value = comboBox1.getSelectionModel().getSelectedItem() == null ? "null" : comboBox1.getSelectionModel().getSelectedItem().toString();
                ComboBox comboBox2 = (ComboBox) vb.getChildren().get(1);
                String cb2Value = comboBox2.getSelectionModel().getSelectedItem() == null ? "null" : comboBox2.getSelectionModel().getSelectedItem().toString();
                timeTableArrayList.get(rowIndex - 1).add(cb1Value + "%" + cb2Value);
            }
        }
    }

    //On init print time table if data already exists
    private void saveExistingDataToList() {
        timeTableArrayList = new ArrayList();
        for (int i = 0; i < 5; i++) {
            timeTableArrayList.add(new ArrayList<>());
        }
        String data = timeTable.getData();
        String[] lines = data.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            for (int j = 0; j < values.length; j++) {
                timeTableArrayList.get(i).add(values[j]);
            }
        }
    }

    //Populate GridBox with labels from ArrayList
    private void populateDatainGridFromList() {
        int numCols = 11;
        int numRows = 6;
        for (int columnIndex = 1; columnIndex < numCols; columnIndex++) {
            for (int rowIndex = 1; rowIndex < numRows; rowIndex++) {
                VBox vbox = new VBox();
                vbox.setId("vbox-" + columnIndex + "-" + rowIndex);
                vbox.setSpacing(2);
                vbox.setAlignment(Pos.CENTER);
                vbox.getStyleClass().add("vboxViewData");

                String[] value = timeTableArrayList.get(rowIndex - 1).get(columnIndex - 1).split("%");

                Label subjectLabel = new Label(value[0]);
                subjectLabel.setStyle("-fx-text-fill : " + subjectColorMap.get(value[0]) + ";");
                subjectLabel.getStyleClass().add("subjectLabel");
                vbox.getChildren().add(subjectLabel);

                if (Singleton.getInstance().isTeacherView()) {
                    if (!value[0].equals("empty")) {
                        Label ttLabel = new Label(value[1]);
                        ttLabel.setStyle("-fx-text-fill : #29b6f2;");
                        ttLabel.getStyleClass().add("teacherLabel");
                        vbox.getChildren().add(ttLabel);
                    }
                } else {
                    if (value[1].equals("empty")) {
                        value[1] = "null";
                    }
                    Label teacherLabel = new Label(value[1]);
                    teacherLabel.setStyle("-fx-text-fill : " + teacherColorMap.get(value[1]) + ";");
                    teacherLabel.getStyleClass().add("teacherLabel");
                    vbox.getChildren().add(teacherLabel);
                    if (!value[1].equals("null")) {
                        updateTeacherData(columnIndex - 1, rowIndex - 1, value[0] + "%" + timeTableName, value[1]);
                    }
                }
                timetableGrid.add(vbox, columnIndex, rowIndex);
            }
        }
    }

    private void updateTeacherDatafromBackup() {        
        for (int i = 0; i < timeTableArrayList.size(); i++) {
            for (int j = 0; j < timeTableArrayList.get(i).size(); j++) {
                String newValue = timeTableArrayList.get(i).get(j).split("%")[1];
                String oldValue = timeTableArrayListBackup.get(i).get(j).split("%")[1];
                if (oldValue.equals("empty")) {
                    oldValue = "null";
                }
                if (newValue.equals("empty")) {
                    newValue = "null";
                }

                if (newValue.equals(oldValue) || oldValue.equals("null")) {
                    //do nothing
                } else {
                    if ((!oldValue.equals("null") && newValue.equals("null"))
                            || (!oldValue.equals(newValue))) {
                        updateTeacherData(j, i, "empty", oldValue);
                    }
                }
            }
        }
    }

    private void updateTeacherData(int column, int row, String value, String teacherName) {       
        String teacherTTdata = teacherTimeTableMap.get(teacherName);
        String[] lines = teacherTTdata.split("\n");
        lines[row] = replaceOccurance(lines[row], lines[row].split(",")[column], value, column);
        String newData = "";
        for (int i = 0; i < lines.length; i++) {
            newData += lines[i];
            if (i != lines.length - 1) {
                newData += "\n";
            }
        }
        teacherTimeTableMap.put(teacherName, newData);        
        for (int i = 0; i < teacherList.size(); i++) {
            if (teacherList.get(i).getName().equals(teacherName)) {
                teacherList.get(i).setTimetable(newData);
            }
        }
    }

    private String replaceOccurance(String text, String replaceFrom, String replaceTo, int occuranceIndex) {
        String[] values = text.split(",");
        values[occuranceIndex] = replaceTo;
        String changedLine = "";
        for (int i = 0; i < values.length; i++) {
            changedLine += values[i] + ",";
        }
        return changedLine.substring(0, changedLine.length() - 1);
    }

    private void onTimeTableDeleteUpdatTeachers() {
        for (int i = 0; i < teacherList.size(); i++) {
            if (teacherList.get(i).getName().equals("empty")) {
                continue;
            }
            String data = teacherList.get(i).getTimetable();

            String lines[] = data.split("\n");
            for (int j = 0; j < lines.length; j++) {
                String[] values = lines[j].split(",");
                for (int k = 0; k < values.length; k++) {
                    if (values[k].contains(timeTableName)) {
                        lines[j] = replaceOccurance(lines[j], values[k], "empty", k);
                    }
                }
            }

            String newData = "";
            for (int j = 0; j < lines.length; j++) {
                newData += lines[j];
                if (j != lines.length - 1) {
                    newData += "\n";
                }
            }

            teacherList.get(i).setTimetable(newData);
        }

        for (int i = teacherList.size() - 1; i >= 0; i--) {
            if (teacherList.get(i).toString().equals("empty")) {
                teacherList.remove(i);
            }
        }
        WrapperDAO.create(Teacher.class, teacherList, "teachers", "teachers.xml");
    }

    private void exportFile() {
        try {
            String filename = "C:/" + timeTableName + ".xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("FirstSheet");

            HSSFRow rowhead = sheet.createRow((short) 0);
            for (int i = 0; i <= 10; i++) {
                if (i == 0) {
                    rowhead.createCell(i).setCellValue(timeTableName);
                    continue;
                }
                String periodName = i + "";
                if (period != null) {
                    try {
                        periodName = periodsNameList.get(i - 1);
                    } catch (Exception ex) {
                        periodName = i + "";
                    }
                }

                rowhead.createCell(i).setCellValue(periodName);
            }

            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            for (int i = 0; i < timeTableArrayList.size(); i++) {
                HSSFRow row = sheet.createRow((short) i + 1);
                row.createCell(0).setCellValue(days[i]);
                for (int j = 0; j < timeTableArrayList.get(i).size(); j++) {
                    String value = timeTableArrayList.get(i).get(j);
                    row.createCell(j + 1).setCellValue(value.replace("%", " - "));
                }
            }

            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Success!!!");
            alert.setHeaderText(null);
            alert.setContentText("Your files is created. Path to File - " + filename);
            alert.showAndWait();

            Desktop.getDesktop().open(new File(filename));
        } catch (Exception ex) {
            //if exception is raised
        }
    }

    private String getStringFromList() {
        String data = "";
        for (int i = 0; i < timeTableArrayList.size(); i++) {
            for (int j = 0; j < timeTableArrayList.get(i).size(); j++) {
                data += timeTableArrayList.get(i).get(j);
                if (j != timeTableArrayList.get(i).size() - 1) {
                    data += ",";
                }
            }
            if (i != timeTableArrayList.size() - 1) {
                data += "\n";
            }
        }
        return data;
    }    

    private void makeTimeTableStructure() {
        timetableGrid.getChildren().clear();

        VBox vbox2 = new VBox();
        vbox2.setAlignment(Pos.CENTER);
        vbox2.getStyleClass().add("headerBorder");
        Label periodLabel = new Label("Periods -->");
        periodLabel.getStyleClass().add("headerLabel2");
        vbox2.getChildren().add(periodLabel);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        Label daysLabel = new Label("Days");
        daysLabel.getStyleClass().add("headerLabel2");
        Label daysArrowLabel = new Label("-->");
        daysArrowLabel.getStyleClass().add("headerLabel2");
        daysArrowLabel.getStyleClass().add("arrowLabelrotated");
        hbox.getChildren().addAll(daysArrowLabel, daysLabel);
        vbox2.getChildren().add(hbox);

        timetableGrid.add(vbox2, 0, 0);
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < days.length; i++) {
            //For Grid Lines
            VBox vbox = new VBox();
            vbox.getStyleClass().add("headerBorder");
            vbox.setAlignment(Pos.CENTER);
            Label label = new Label(days[i]);
            label.getStyleClass().add("headerLabel");
            vbox.getChildren().add(label);
            timetableGrid.add(vbox, 0, i + 1);
        }

        for (int i = 1; i <= Singleton.getInstance().getNO_OF_PERIODS(); i++) {
            VBox vbox = new VBox();
            vbox.getStyleClass().add("headerBorder");
            vbox.setAlignment(Pos.CENTER);

            String periodname = i + "";

            if (period != null) {
                try {
                    periodname = periodsNameList.get(i - 1);
                } catch (Exception ex) {
                    periodname = i + "";
                }
            }

            Label label = new Label(periodname);
            label.getStyleClass().add("headerLabel");
            vbox.getChildren().add(label);
            timetableGrid.add(vbox, i, 0);

        }
    }

    //Teacher ComboBox cell factory
    static class teacherCell<T> extends ListCell<T> {

        public teacherCell() {
            super();
            ListCell<String> thisCell = (ListCell<String>) this;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(((Teacher) item).getName());
            }
        }
    }

    //Subject ComboBox cell factory
    static class subjectCell<T> extends ListCell<T> {

        public subjectCell() {
            super();
            ListCell<String> thisCell = (ListCell<String>) this;
//            super.setWidth(100); //Set combo box properties from here
        }

        @Override
        protected void setHeight(double value) {
            super.setHeight(value); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setPrefSize(double prefWidth, double prefHeight) {
            super.setPrefSize(prefWidth, prefHeight); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void setWidth(double value) {
            super.setWidth(value); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(((Subject) item).getName());
            } else {
                setText("Select Subject");
            }
        }
    }
}
