
package org.openjfx.models;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jatin
 */
@XmlRootElement(name = "period")
@XmlAccessorType (XmlAccessType.FIELD)
public class Period {
    
    private String period1;
    private String period2;
    private String period3;
    private String period4;
    private String period5;
    private String period6;
    private String period7;
    private String period8;
    private String period9;
    private String period10;
    
    public String getPeriod1() {
        return period1;
    }

    public void setPeriod1(String period1) {
        this.period1 = period1;
    }

    public String getPeriod2() {
        return period2;
    }

    public void setPeriod2(String period2) {
        this.period2 = period2;
    }

    public String getPeriod3() {
        return period3;
    }

    public void setPeriod3(String period3) {
        this.period3 = period3;
    }

    public String getPeriod4() {
        return period4;
    }

    public void setPeriod4(String period4) {
        this.period4 = period4;
    }

    public String getPeriod5() {
        return period5;
    }

    public void setPeriod5(String period5) {
        this.period5 = period5;
    }

    public String getPeriod6() {
        return period6;
    }

    public void setPeriod6(String period6) {
        this.period6 = period6;
    }

    public String getPeriod7() {
        return period7;
    }

    public void setPeriod7(String period7) {
        this.period7 = period7;
    }

    public String getPeriod8() {
        return period8;
    }

    public void setPeriod8(String period8) {
        this.period8 = period8;
    }

    public String getPeriod9() {
        return period9;
    }

    public void setPeriod9(String period9) {
        this.period9 = period9;
    }

    public String getPeriod10() {
        return period10;
    }

    public void setPeriod10(String period10) {
        this.period10 = period10;
    }

    @Override
    public String toString() {
        String[] periodNamesArray = new String[10];
        periodNamesArray[0] = getPeriod1();
        periodNamesArray[1] = getPeriod2();
        periodNamesArray[2] = getPeriod3();
        periodNamesArray[3] = getPeriod4();
        periodNamesArray[4] = getPeriod5();
        periodNamesArray[5] = getPeriod6();
        periodNamesArray[6] = getPeriod7();
        periodNamesArray[7] = getPeriod8();
        periodNamesArray[8] = getPeriod9();
        periodNamesArray[9] = getPeriod10();        
        return Arrays.toString(periodNamesArray);
    }
    
}
