package softuni.exam.service;

import jakarta.xml.bind.JAXBException;
import softuni.exam.models.entities.Seller;
import softuni.exam.models.entities.dtos.CarIdDto;

import java.io.IOException;

//ToDo - Before start App implement this Service and set areImported to return false
public interface SellerService {
    
    boolean areImported();

    String readSellersFromFile() throws IOException;

    String importSellers() throws IOException, JAXBException;

    Seller findById(Long id);
}
