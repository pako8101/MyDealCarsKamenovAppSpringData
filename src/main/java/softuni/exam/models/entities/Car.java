package softuni.exam.models.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "cars")
public class Car extends BaseEntity{
    @Column(length = 20)
    private String make;
    @Column(length = 20)
    private String model;
    @Column
    private Integer kilometers;
    @Column(name = "registered_on")

    private LocalDate registeredOn;
    @OneToMany(mappedBy = "car",fetch = FetchType.EAGER)
    private Set<Picture> pictures;

    public Car() {
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getKilometers() {
        return kilometers;
    }

    public void setKilometers(Integer kilometers) {
        this.kilometers = kilometers;
    }

    public LocalDate getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(LocalDate registeredOn) {
        this.registeredOn = registeredOn;
    }
}
