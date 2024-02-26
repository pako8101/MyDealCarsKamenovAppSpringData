package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.entities.Car;
import softuni.exam.models.entities.dtos.CarSeedDto;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {
    private static String CARS_FILE_PATH = "C:\\Users\\Plamen Kamenov\\Desktop\\MyPrograming\\exercises\\SpringData\\MyDealAppSpringData\\MyDealKamenovAppSpringData\\src\\main\\resources\\files\\json\\cars.json";
    private final CarRepository carRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.carRepository = carRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        return Files.readString(Path.of(CARS_FILE_PATH));
    }

    @Override
    public String importCars() throws IOException {

        StringBuilder sb = new StringBuilder();
        CarSeedDto[] carSeedDtos = this.gson.
                fromJson(readCarsFileContent(), CarSeedDto[].class);


        Arrays.stream(carSeedDtos)
                .filter(carSeedDto -> {
                    boolean isValid = validationUtil.isValid(carSeedDto);

                    sb.append(isValid ? String.format("Successfully imported - %s - %s",
                                    carSeedDto.getMake(), carSeedDto.getModel()) :
                                    "Invalid car")
                            .append(System.lineSeparator());
                    return isValid;

                }).
                map(carSeedDto -> modelMapper.map(carSeedDto, Car.class))
                .forEach(carRepository::save);

//        List<Car> cars = Arrays.stream(carSeedDtos)
//                .filter(validationUtil::isValid)
//                .map(carSeedDto -> modelMapper.map(carSeedDto,Car.class))
//                .collect(Collectors.toList());


        return sb.toString().trim();
    }

    @Override
    public String getCarsOrderByPicturesCountThenByMake() {
        return this.carRepository.findAllOrderByPicturesCountThenByMake()
                .stream().
                map(car -> this.modelMapper.map(car, CarSeedDto.class))
                .map(CarSeedDto::toString)
                .collect(Collectors.joining()).trim();

    }

    @Override
    public Car findById(Long id) {
        return this.carRepository.findById(id)
                .orElse(null);
    }

}
