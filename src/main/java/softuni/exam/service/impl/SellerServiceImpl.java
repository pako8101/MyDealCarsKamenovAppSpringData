package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.entities.Seller;
import softuni.exam.models.entities.dtos.OfferSeedDto;
import softuni.exam.models.entities.dtos.SellerRootDto;
import softuni.exam.models.entities.dtos.SellerSeedDto;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.SellerService;

import jakarta.xml.bind.JAXBException;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
//    private final CarService carService;
//    private final SellerService sellerService;

    private final ValidationUtil validationUtil;
    private static final String PATH = "C:\\Users\\Plamen Kamenov\\Desktop\\MyPrograming\\exercises\\SpringData\\MyDealAppSpringData\\MyDealKamenovAppSpringData\\src\\main\\resources\\files\\xml\\sellers.xml";

    public SellerServiceImpl(SellerRepository sellerRepository, ModelMapper modelMapper, XmlParser xmlParser, ValidationUtil validationUtil) {
        this.sellerRepository = sellerRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return this.sellerRepository.count()>0;
    }

    @Override
    public String readSellersFromFile() throws IOException {
        return Files.readString(Path.of(PATH));
    }

    @Override
    public String importSellers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        xmlParser.fromFile(PATH, SellerRootDto.class)
                .getSellers()
                .stream()
                .filter(sellerSeedDto -> {
                    boolean isValid = this.validationUtil.isValid(sellerSeedDto);
                    return validDto(sellerSeedDto,sb,isValid);
                })
                .map(sellerSeedDto -> modelMapper.map(sellerSeedDto, Seller.class))
                .forEach(sellerRepository::save);


        return sb.toString().trim();
    }

    @Override
    public Seller findById(Long id) {
        return sellerRepository.findById(id)
                .orElse(null);
    }
    private static boolean validDto(SellerSeedDto sellerSeedDto, StringBuilder sb, boolean isValid) {
        sb.append(isValid ? String.format("Successfully imported seller - %s - %s",
                        sellerSeedDto.getFirstName(), sellerSeedDto.getEmail()) :
                        "Invalid seller")
                .append(System.lineSeparator());
        return isValid;
    }
}
