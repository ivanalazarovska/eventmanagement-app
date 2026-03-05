package mk.ukim.finki.eventapp.service.impl;

import mk.ukim.finki.eventapp.model.Location;
import mk.ukim.finki.eventapp.model.dtos.LocationDto;
import mk.ukim.finki.eventapp.repository.LocationRepository;
import mk.ukim.finki.eventapp.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location createLocation(LocationDto dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setCapacity(dto.getCapacity());
        location.setLongitude(dto.getLongitude());
        location.setLatitude(dto.getLatitude());

        return locationRepository.save(location);
    }

    @Override
    public List<LocationDto> getAllLocations() {
        List<LocationDto> locations = locationRepository.findAll().stream().map(location -> {
            LocationDto dto = new LocationDto();
            dto.setId(location.getId());
            dto.setName(location.getName());
            dto.setCapacity(location.getCapacity());
            dto.setLongitude(location.getLongitude());
            dto.setLatitude(location.getLatitude());
            return dto;
        }).toList();

        return locations;
    }

    @Override
    public LocationDto getLocationById(Long id) {
        Optional<Location> optionalLocation = locationRepository.findById(id);
        if (optionalLocation.isEmpty()) {
            return null;
        }

        Location location = optionalLocation.get();
        LocationDto dto = new LocationDto();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setCapacity(location.getCapacity());
        dto.setLongitude(location.getLongitude());
        dto.setLatitude(location.getLatitude());

        return dto;
    }

    @Override
    public Location updateLocation(Long id, LocationDto dto) {
        Optional<Location> optionalLocation = locationRepository.findById(id);
        if (optionalLocation.isEmpty()) {
            return null;
        }

        Location location = optionalLocation.get();
        location.setName(dto.getName());
        location.setCapacity(dto.getCapacity());
        location.setLongitude(dto.getLongitude());
        location.setLatitude(dto.getLatitude());

        return locationRepository.save(location);
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
