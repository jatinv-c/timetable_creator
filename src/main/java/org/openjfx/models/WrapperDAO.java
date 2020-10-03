
package org.openjfx.models;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.openjfx.fiverr5.Singleton;

/**
 *
 * @author Jatin
 */
public class WrapperDAO {

    private static final String DIR_PATH = Singleton.getInstance().getDIR_PATH();
    static JAXBContext jc;
    
    static void marshal(Class c, ObservableList<?> list, String name, String filename) {
        try {
            String filePath = DIR_PATH + filename;
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            jc = JAXBContext.newInstance(c, WrapperList.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            QName qName = new QName(name);
            WrapperList wrapper = new WrapperList(list);
            JAXBElement<WrapperList> jaxbElement = new JAXBElement<>(qName, WrapperList.class, wrapper);
            marshaller.marshal(jaxbElement, new File(filePath));
        } catch (Exception ex) {
            System.out.println("Problem with marshalling to file");
            Logger.getLogger(WrapperList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static <T> ObservableList<T> unmarshal(Class c, String filename) {
        try {
            String filePath = DIR_PATH + filename;
            if (Files.notExists(Paths.get(filePath))) {
                return null;
            }
            jc = JAXBContext.newInstance(c, WrapperList.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StreamSource xml = new StreamSource(filePath);
            WrapperList<T> wrapper = (WrapperList<T>) unmarshaller.unmarshal(xml, WrapperList.class).getValue();
            return (ObservableList<T>) wrapper.getItems();
        } catch (Exception ex) {
            System.out.println("Problem with unmarshalling the file");
            Logger.getLogger(WrapperDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static <T> void add(T obj, Class c, String filename, String rootNodeName) {
        ObservableList<T> list = getList(c, filename);
        if (list == null) {
            list = FXCollections.observableArrayList();
        }
        list.add(obj);
        create(c, list, rootNodeName, filename);
    }

    public static void create(Class c, ObservableList<?> list, String rootNodeName, String filename) {
        marshal(c, list, rootNodeName, filename);
    }

    public static <T> ObservableList<T> getList(Class c, String filename) {
        ObservableList<T> list = null;
        try {
            list = unmarshal(c, filename);
        } catch (Exception ex) {
            System.out.println("Error creating file : WrapperDAO");
            Logger.getLogger(WrapperDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return list;
    }
}
