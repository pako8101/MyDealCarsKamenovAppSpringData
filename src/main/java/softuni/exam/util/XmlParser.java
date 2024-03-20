package softuni.exam.util;

import jakarta.xml.bind.JAXBException;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public interface XmlParser {
    <T> T fromFile(String file,Class<T> object) throws JAXBException, FileNotFoundException;
}
