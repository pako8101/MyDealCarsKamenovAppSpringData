package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.entities.Offer;
import softuni.exam.models.entities.dtos.CarSeedDto;
import softuni.exam.models.entities.dtos.OfferRootDto;
import softuni.exam.models.entities.dtos.OfferSeedDto;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.OfferService;

import jakarta.xml.bind.JAXBException;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final CarService carService;
    private final SellerService sellerService;

    private final ValidationUtil validationUtil;
    private static final String PATH ="C:\\Users\\Plamen Kamenov\\Desktop\\MyPrograming\\exercises\\SpringData\\MyDealAppSpringData\\MyDealKamenovAppSpringData\\src\\main\\resources\\files\\xml\\offers.xml";

    public OfferServiceImpl(OfferRepository offerRepository, ModelMapper modelMapper, XmlParser xmlParser, CarService carService, SellerService sellerService, ValidationUtil validationUtil) {
        this.offerRepository = offerRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.carService = carService;
        this.sellerService = sellerService;
        this.validationUtil = validationUtil;
    }


    @Override
    public boolean areImported() {
        return this.offerRepository.count()>0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(Path.of(PATH));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        xmlParser.fromFile(PATH, OfferRootDto.class)
                .getOffers()
                .stream()
                .filter(offerSeedDto -> {
                    boolean isValid = this.validationUtil.isValid(offerSeedDto);
                  return   validDto(offerSeedDto,sb,isValid);
                })
                .map(offerSeedDto -> {
                    Offer offer = modelMapper.map(offerSeedDto, Offer.class);
                    offer.setSeller(sellerService.findById(offerSeedDto.getSeller().getId()));
               offer.setCar(carService.findById(offerSeedDto.getCar().getId()));
               return offer;

                }).forEach(offerRepository::save);




        return sb.toString().trim();
    }
    private static boolean validDto(OfferSeedDto offerSeedDto, StringBuilder sb, boolean isValid) {
        sb.append(isValid ? String.format("Successfully imported offer - %s - %s",
                        offerSeedDto.getAddedOn(), offerSeedDto.isHasGoldStatus()) :
                        "Invalid offer")
                .append(System.lineSeparator());
        return isValid;
    }
}
