package org.openjfx.home;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import org.openjfx.fiverr5.App;
import org.openjfx.fiverr5.Singleton;
import org.openjfx.models.Teacher;
import org.openjfx.models.TimeTable;
import org.openjfx.models.WrapperDAO;

/**
 * FXML Controller class
 *
 * @author Jatin
 */
public class HomeController implements Initializable {

    @FXML
    private Button newButton;
    @FXML
    private TilePane tilePane;
    @FXML
    private Button teacherButton;
    @FXML
    private Button subjectButton;
    @FXML
    private Button userButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ImageView teacherImageView;
    @FXML
    private ImageView subjectImageView;
    @FXML
    private ImageView userImageView;
    @FXML
    private ImageView titleImageview;
    ObservableList<TimeTable> timetableList;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ImageView cartoonImageView;
    @FXML
    private Button periodsButton;
    @FXML
    private ImageView periodImageView;
    @FXML
    private Button helpButton;
    @FXML
    private ImageView helpImageView;
    @FXML
    private Button teacherDetails;
    @FXML
    private Button emailTT;

    int timetableNo = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        loadImages();

        if (Singleton.getInstance().getLoggedInUserRole().equals("Teacher")) {
            newButton.setDisable(true);
            userButton.setDisable(true);
        }
        timetableList = WrapperDAO.getList(TimeTable.class, "timetables.xml");
        if (timetableList == null) {
            timetableList = FXCollections.observableArrayList();
        }
        populateTilePane();
    }

    @FXML
    private void newButtonClicked(ActionEvent event) {
        timetableNo = timetableList.size() + 1;
        TextInputDialog dialog = new TextInputDialog("TimeTable " + timetableNo);
        dialog.setTitle("Timetable");
        dialog.setHeaderText("Enter Name");
//        dialog.setContentText("Please enter your name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        String input = result.get();

        if (!checkForDuplicate(input)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Timetable with same name already exists");
            alert.showAndWait();
            return;
        }

        TimeTable timeTable = new TimeTable();
        timeTable.setName(input);
        timeTable.setData("");
        timetableList.add(timeTable);

        WrapperDAO.create(TimeTable.class, timetableList, "timetables", "timetables.xml");

        addItemToTilePane(input);

    }

    @FXML
    private void onteacherButtonClicked(ActionEvent event) {
        try {
            App.setRoot("add-teachers");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onsubjectButtonclicked(ActionEvent event) {
        try {
            App.setRoot("add-subjects");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onuserButtonClicked(ActionEvent event) {
        try {
            App.setRoot("users");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onlogoutButtonClicked(ActionEvent event) {
        Singleton.getInstance().setLoggedInUser("");
        try {
            App.setRoot("log-in");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onperiodsButtonClicked(ActionEvent event) {
        try {
            App.setRoot("period-name");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onhelpButtonButton(ActionEvent event) {
        try {
            File file = new File(getClass().getClassLoader().getResource("html/help.html").toString().substring(6));
            Desktop.getDesktop().open(new File("help.html"));
        } catch (Exception ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateTilePane() {
        timetableList.forEach((c) -> {
            addItemToTilePane(c.getName());
        });
    }

    //check for timetable with duplicate name
    private boolean checkForDuplicate(String name) {
        AtomicBoolean result = new AtomicBoolean(true);
        timetableList.forEach((c) -> {
            if (name.equalsIgnoreCase(c.getName())) {
                result.set(false);
            }
        });
        return result.get();
    }

    private void addItemToTilePane(String name) {
        Button button = new Button(name);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Singleton.getInstance().setSelectedTimeTableName(name);
                try {
                    Singleton.getInstance().setTimeTableName(name);
                    App.setRoot("timetable");
                } catch (IOException ex) {
                    Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        button.getStyleClass().add("timetableButton");
        tilePane.getChildren().add(button);
    }

    private void loadImages() {
        Image image = new Image(getClass().getClassLoader().getResource("images/teacher.png").toString());
        teacherImageView.setImage(image);
        teacherImageView.setCache(true);

        image = new Image(getClass().getClassLoader().getResource("images/subject.png").toString());
        subjectImageView.setImage(image);
        subjectImageView.setCache(true);

        image = new Image(getClass().getClassLoader().getResource("images/user.png").toString());
        userImageView.setImage(image);
        userImageView.setCache(true);

        image = new Image(getClass().getClassLoader().getResource("images/timetable.png").toString());
        titleImageview.setImage(image);
        titleImageview.setCache(true);

        image = new Image(getClass().getClassLoader().getResource("images/period.png").toString());
        periodImageView.setImage(image);
        periodImageView.setCache(true);

        image = new Image(getClass().getClassLoader().getResource("images/cartoon.png").toString());
        cartoonImageView.setImage(image);
        cartoonImageView.setCache(true);

        image = new Image(getClass().getClassLoader().getResource("images/help.png").toString());
        helpImageView.setImage(image);
        helpImageView.setCache(true);
    }

    @FXML
    private void onteacherDetailsClicked(ActionEvent event) {        
        try {
            App.setRoot("teacher-details");
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onemailTTClicked(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("https://accounts.google.com/login?service=mail&lp=1"));
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(TimetableController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
