
package org.openjfx.fiverr5;

import java.io.File;

/**
 *
 * @author Jatin
 */
public class Singleton {

    private static Singleton singleton;

    private static final int NO_OF_PERIODS = 10;
    private static final String DIR_PATH = "ProjectData/";
    private String selectedTimeTableName;
    private boolean teacherView;
    private String timeTableName;
    private String loggedInUser;
    private String loggedInUserRole;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }

    public int getNO_OF_PERIODS() {
        return NO_OF_PERIODS;
    }

    public String getDIR_PATH() {
        File dir = new File(DIR_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return DIR_PATH;
    }

    ////////////////////////////////////////////////
    public String getSelectedTimeTableName() {
        return selectedTimeTableName;
    }

    public void setSelectedTimeTableName(String selectedTimeTableName) {
        this.selectedTimeTableName = selectedTimeTableName;
    }
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    public boolean isTeacherView() {
        return teacherView;
    }

    public void setTeacherView(boolean teacherView) {
        this.teacherView = teacherView;
    }
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    public String getTimeTableName() {
        return timeTableName;
    }

    public void setTimeTableName(String timeTablename) {
        this.timeTableName = timeTablename;
    }
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    public String getLoggedInUserRole() {
        return loggedInUserRole;
    }

    public void setLoggedInUserRole(String loggedInUserRole) {
        this.loggedInUserRole = loggedInUserRole;
    }
    ////////////////////////////////////////////////

}
