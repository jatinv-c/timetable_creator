
package org.openjfx.fiverr5;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.openjfx.models.User;
import org.openjfx.models.WrapperDAO;

/**
 * FXML Controller class
 *
 * @author Jatin
 */
public class LogInController implements Initializable {

    @FXML
    private PasswordField passwordFiled;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Button loginButton;

    ObservableList<User> userList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Shadow effect for Button
//        DropShadow shadow = new DropShadow();
//        loginButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
//        new EventHandler<MouseEvent>() {
//          @Override
//          public void handle(MouseEvent e) {
//            loginButton.setEffect(shadow);
//          }
//        });
//        loginButton.addEventHandler(MouseEvent.MOUSE_EXITED,
//        new EventHandler<MouseEvent>() {
//          @Override
//          public void handle(MouseEvent e) {
//            loginButton.setEffect(null);
//          }
//        });
    }

    @FXML
    private void onloginButtonClicked(ActionEvent event) {        
        String username = usernameTextField.getText();
        if (username == null || username.equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Username or Password are both required!!");
            alert.showAndWait();
            return;
        }

        String password = passwordFiled.getText();
        if (password == null || password.equals("")) {
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Username or Password are both required!!");
            alert.showAndWait();            
            return;
        }

        if (username.equals("root") && password.equals("root")) {
            try {
                Singleton.getInstance().setLoggedInUser("root");
                Singleton.getInstance().setLoggedInUserRole("root");                
                App.setRoot("home");
            } catch (IOException ex) {
                Logger.getLogger(LogInController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            userList = WrapperDAO.getList(User.class, "users.xml");
            if (userList == null) {

            } else {                
                for (User user : userList) {
                    if (user.getUsername().equals(username)) {
                        if (new String(Base64.getDecoder().decode(user.getPassword())).equals(password)) {
                            try {
                                Singleton.getInstance().setLoggedInUser(username);                                
                                Singleton.getInstance().setLoggedInUserRole(user.getRole());                                
                                App.setRoot("home");
                                return;
                            } catch (IOException ex) {
                                Logger.getLogger(LogInController.class.getName()).log(Level.SEVERE, null, ex);
                                return;
                            }
                        }
                    }
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!!!!!!");
            alert.setHeaderText(null);
            alert.setContentText("Username or Password is incorrect!!");
            alert.showAndWait();
        }
    }

}
