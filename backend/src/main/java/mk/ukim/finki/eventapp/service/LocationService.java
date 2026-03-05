package mk.ukim.finki.eventapp.service;

import mk.ukim.finki.eventapp.model.Location;
import mk.ukim.finki.eventapp.model.dtos.LocationDto;

import java.util.List;

public interface LocationService {
    Location createLocation(LocationDto locationDto);
    List<LocationDto> getAllLocations();
    LocationDto getLocationById(Long id);
    Location updateLocation(Long id, LocationDto locationDto);
    void deleteLocation(Long id);
}
