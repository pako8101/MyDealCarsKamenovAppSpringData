package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.entities.Picture;
import softuni.exam.models.entities.dtos.PictureSeedDto;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.PictureService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class PictureServiceImpl implements PictureService {
    private static String PICTURES_FILE_PATH = "C:\\Users\\Plamen Kamenov\\Desktop\\MyPrograming\\exercises\\SpringData\\MyDealAppSpringData\\MyDealKamenovAppSpringData\\src\\main\\resources\\files\\json\\pictures.json";
    private final PictureRepository pictureRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final CarService carService;
    private final Gson gson;
@Autowired
    public PictureServiceImpl(PictureRepository pictureRepository, ModelMapper modelMapper,
                              ValidationUtil validationUtil, CarService carService, Gson gson) {
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    this.carService = carService;

    this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count()>0;
    }

    @Override
    public String readPicturesFromFile() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
    StringBuilder sb = new StringBuilder();

        PictureSeedDto[]pictureSeedDtos = gson.fromJson(readPicturesFromFile(), PictureSeedDto[].class);
        Arrays.stream(pictureSeedDtos)
                .filter(pictureSeedDto -> {
                    boolean isValid = validationUtil.isValid(pictureSeedDto);
                    sb.append(isValid ? String.format("Successfully import picture - %s",pictureSeedDto.getName())
                            : "Invalid picture")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(pictureSeedDto -> {
                    Picture picture = modelMapper.map(pictureSeedDto, Picture.class);
                    picture.setCar(carService.findById(pictureSeedDto.getCar()));
                    return picture;
                })
                .forEach(pictureRepository::save);


        return sb.toString();
    }
}
