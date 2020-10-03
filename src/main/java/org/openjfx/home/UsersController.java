
package org.openjfx.home;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.openjfx.fiverr5.App;
import org.openjfx.models.User;
import org.openjfx.models.WrapperDAO;

/**
 * FXML Controller class
 *
 * @author Jatin
 */
public class UsersController implements Initializable {

    @FXML
    private Button homeButton;
    @FXML
    private Button deleteButton1;
    @FXML
    private TextField nameTextBox;
    @FXML
    private Button addButton;
    @FXML
    private PasswordField passwordFiled;
    @FXML
    private ListView<User> userListView;
    @FXML
    private ComboBox<String> roleComboBox;

    ObservableList<User> userList;
    ObservableList<String> userRoles;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        userRoles = FXCollections.observableArrayList();
        userRoles.add("Admin");
        userRoles.add("Teacher");
        roleComboBox.setItems(userRoles);

        userList = WrapperDAO.getList(User.class, "users.xml");
        if (userList == null) {
            userList = FXCollections.observableArrayList();            
        } 
        populateListView();
    }

    @FXML
    private void homeButtonClicked(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void ondeleteButtonClicked(ActionEvent event) {
        User userToDel = userListView.getSelectionModel().getSelectedItem();
        if (userToDel == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("No User is selected");
            alert.showAndWait();
            return;
        }
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUsername().equalsIgnoreCase(userToDel.getUsername())) {
                userList.remove(i);
                break;
            }
        }
        WrapperDAO.create(User.class, userList, "users", "users.xml");
    }

    @FXML
    private void addButtonClicked(ActionEvent event) {
        String name = nameTextBox.getText();
        if (name == null || name.equals("")) {
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Name cannot be empty!!");
            alert.showAndWait();
            return;
        }
        if (!checkForDuplicate(name)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("User already exists");
            alert.showAndWait();
            return;
        }

        String password = passwordFiled.getText();
        if (password == null || password.equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Password cannot be empty!!");
            alert.showAndWait();
            return;
        }

        if (roleComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Role cannot be empty!!");
            alert.showAndWait();
            return;
        }
        String role = roleComboBox.getValue();
        User user = new User();
        user.setUsername(name);
        user.setPassword(new String(Base64.getEncoder().encode(password.getBytes())));
        user.setRole(roleComboBox.getValue());

        userList.add(user);
        WrapperDAO.create(User.class, userList, "users", "users.xml");

        nameTextBox.clear();
        passwordFiled.clear();
        roleComboBox.getSelectionModel().clearSelection();
    }

    private void populateListView() {
        userListView.setItems(userList);
        userListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new UsersController.userCell<>();
            }
        });
    }

    //check for subject with duplicate name
    private boolean checkForDuplicate(String name) {
        AtomicBoolean result = new AtomicBoolean(true);
        userList.forEach((c) -> {
            if (name.equalsIgnoreCase(c.getUsername())) {
                result.set(false);
            }
        });
        return result.get();
    }

    //cell factory
    static class userCell<T> extends ListCell<T> {

        HBox hbox = new HBox();
        //CheckBox cb = new CheckBox();
        Label label = new Label();
        ImageView iv = new ImageView(new Image(getClass().getClassLoader().getResource("images/user.png").toString(), 25, 25, false, false));

        public userCell() {
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
                label.setText(((User) getItem()).getUsername());
                setGraphic(hbox);
            }
        }
    }
}
