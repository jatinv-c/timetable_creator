
package org.openjfx.models;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jatin
 */
@XmlRootElement(name = "teachersList")
@XmlAccessorType (XmlAccessType.FIELD)
public class TeachersList {
    
    @XmlElement(name = "teacher")
    private List<Teacher> teachers;

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }
    
}
