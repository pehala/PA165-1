package cz.fi.muni.carshop.services;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cz.fi.muni.carshop.CarShopStorage;
import cz.fi.muni.carshop.entities.Car;
import cz.fi.muni.carshop.enums.CarTypes;
import cz.fi.muni.carshop.exceptions.RequestedCarNotFoundException;

public class CarShopStorageServiceImpl implements CarShopStorageService {

    @Override
    public Optional<Car> isCarAvailable(Color color, CarTypes type) {
        Map<CarTypes, List<Car>> allCars = CarShopStorage.getInstancce().getCars();
        List<Car> carsOfSameType = allCars.get(type);
        return carsOfSameType.stream().filter(car -> car.getColor().equals(color)).findAny();
    }

    @Override
    public List<Car> getCheaperCarsOfSameTypeAndYear(Car referenceCar) {
        Map<CarTypes, List<Car>> allCars = CarShopStorage.getInstancce().getCars();
        List<Car> carsOfSameType = allCars.get(referenceCar.getType());
        return carsOfSameType.stream().filter(car -> referenceCar.getConstructionYear() == car.getConstructionYear()
                && car.getPrice() < referenceCar.getPrice()).collect(Collectors.toList());
    }

    @Override
    public void sellCar(Car referenceCar) throws RequestedCarNotFoundException {
        Map<CarTypes, List<Car>> allCars = CarShopStorage.getInstancce().getCars();
        if ( !allCars.containsKey(referenceCar.getType()) ) throw new RequestedCarNotFoundException("Car was not found");

        List<Car> cars = allCars.get(referenceCar.getType());
        Optional<Car> car= cars.stream()
            .filter(referenceCar::equals)
            .findFirst();
        if (car.isPresent()) {
            cars.remove(car.get());
        } else {
            throw new RequestedCarNotFoundException("Car was not found");
        }
    }

    @Override
    public void addCarToStorage(Car car) {
        if (car.getPrice() < 0) throw new IllegalArgumentException("Car price cannot be negative!");

        CarShopStorage.getInstancce().getCars().computeIfAbsent(car.getType(), x -> new ArrayList<>()).add(car);
    }

}
