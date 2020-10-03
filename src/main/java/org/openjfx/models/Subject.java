
package org.openjfx.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jatin
 */
@XmlRootElement(name = "subject")
@XmlAccessorType (XmlAccessType.FIELD)
public class Subject {

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
    
    @Override
    public String toString() {
        return this.name;
    }
    
}
