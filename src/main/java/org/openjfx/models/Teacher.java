
package org.openjfx.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jatin
 */
@XmlRootElement(name = "teacher")
@XmlAccessorType (XmlAccessType.FIELD)
public class Teacher {

     /******************************************/
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
     /******************************************/
    
     /******************************************/
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
     /******************************************/
    
     /******************************************/
    private String timetable;

    public String getTimetable() {
        return timetable;
    }

    public void setTimetable(String timetable) {
        this.timetable = timetable;
    }    
     /******************************************/
    
    @Override
    public String toString() {
        return this.name;
    }
}
