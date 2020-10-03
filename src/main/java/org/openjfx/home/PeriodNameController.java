package org.openjfx.home;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.openjfx.fiverr5.App;
import org.openjfx.fiverr5.Singleton;
import org.openjfx.models.Period;
import org.openjfx.models.WrapperDAO;

/**
 * FXML Controller class
 *
 * @author Jatin
 */
public class PeriodNameController implements Initializable {

    @FXML
    private Button homeButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField periodTextField1;
    @FXML
    private TextField periodTextField2;
    @FXML
    private TextField periodTextField3;
    @FXML
    private TextField periodTextField4;
    @FXML
    private TextField periodTextField5;
    @FXML
    private TextField periodTextField6;
    @FXML
    private TextField periodTextField7;
    @FXML
    private TextField periodTextField8;
    @FXML
    private TextField periodTextField9;
    @FXML
    private TextField periodTextField10;

    ObservableList<Period> periodList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Singleton.getInstance().getLoggedInUserRole().equals("Teacher")) {
            resetButton.setDisable(true);
            saveButton.setDisable(true);
        }
        periodList = WrapperDAO.getList(Period.class, "periods.xml");
        if (periodList == null) {
            periodList = FXCollections.observableArrayList();            
            setDefaultPeriods();
        } else {            
            periodTextField1.setText(periodList.get(0).getPeriod1());
            periodTextField2.setText(periodList.get(0).getPeriod2());
            periodTextField3.setText(periodList.get(0).getPeriod3());
            periodTextField4.setText(periodList.get(0).getPeriod4());
            periodTextField5.setText(periodList.get(0).getPeriod5());
            periodTextField6.setText(periodList.get(0).getPeriod6());
            periodTextField7.setText(periodList.get(0).getPeriod7());
            periodTextField8.setText(periodList.get(0).getPeriod8());
            periodTextField9.setText(periodList.get(0).getPeriod9());
            periodTextField10.setText(periodList.get(0).getPeriod10());
        }
    }

    @FXML
    private void homeButtonClicked(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException ex) {
            Logger.getLogger(PeriodNameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onresetButtonClicked(ActionEvent event) {
        setDefaultPeriods();
    }

    @FXML
    private void onsaveButtonClicked(ActionEvent event) {
        Period period = new Period();
        if (periodTextField1.getText() == null || periodTextField1.getText().equals("")) {
            period.setPeriod1("1");
        } else {
            period.setPeriod1(periodTextField1.getText());
        }
        if (periodTextField2.getText() == null || periodTextField2.getText().equals("")) {
            period.setPeriod2("2");
        } else {
            period.setPeriod2(periodTextField2.getText());
        }
        if (periodTextField3.getText() == null || periodTextField3.getText().equals("")) {
            period.setPeriod3("3");
        } else {
            period.setPeriod3(periodTextField3.getText());
        }
        if (periodTextField4.getText() == null || periodTextField4.getText().equals("")) {
            period.setPeriod4("4");
        } else {
            period.setPeriod4(periodTextField4.getText());
        }
        if (periodTextField5.getText() == null || periodTextField5.getText().equals("")) {
            period.setPeriod5("5");
        } else {
            period.setPeriod5(periodTextField5.getText());
        }
        if (periodTextField6.getText() == null || periodTextField6.getText().equals("")) {
            period.setPeriod6("6");
        } else {
            period.setPeriod6(periodTextField6.getText());
        }
        if (periodTextField7.getText() == null || periodTextField7.getText().equals("")) {
            period.setPeriod7("7");
        } else {
            period.setPeriod7(periodTextField7.getText());
        }
        if (periodTextField8.getText() == null || periodTextField8.getText().equals("")) {
            period.setPeriod8("8");
        } else {
            period.setPeriod8(periodTextField8.getText());
        }
        if (periodTextField9.getText() == null || periodTextField9.getText().equals("")) {
            period.setPeriod9("9");
        } else {
            period.setPeriod9(periodTextField9.getText());
        }
        if (periodTextField10.getText() == null || periodTextField10.getText().equals("")) {
            period.setPeriod10("10");
        } else {
            period.setPeriod10(periodTextField10.getText());
        }
        periodList.add(period);
        WrapperDAO.create(Period.class, periodList, "periods", "periods.xml");
    }

    private void setDefaultPeriods() {
        periodTextField1.setText("1");
        periodTextField2.setText("2");
        periodTextField3.setText("3");
        periodTextField4.setText("4");
        periodTextField5.setText("5");
        periodTextField6.setText("6");
        periodTextField7.setText("7");
        periodTextField8.setText("8");
        periodTextField9.setText("9");
        periodTextField10.setText("10");
    }
}
