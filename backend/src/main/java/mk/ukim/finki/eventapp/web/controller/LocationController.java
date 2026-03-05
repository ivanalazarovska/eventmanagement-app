package mk.ukim.finki.eventapp.web.controller;

import mk.ukim.finki.eventapp.model.dtos.LocationDto;
import mk.ukim.finki.eventapp.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // Create a new Location
    @PostMapping("/create")
    public ResponseEntity<String> createLocation(@RequestBody LocationDto locationDto) {
         locationService.createLocation(locationDto);
        return ResponseEntity.ok().body("Location created successfully");
    }

    // Get all Locations
    @GetMapping("/")
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        List<LocationDto> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    // Get a Location by ID
    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        LocationDto location = locationService.getLocationById(id);
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update a Location
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateLocation(@PathVariable Long id, @RequestBody LocationDto locationDto) {
        locationService.updateLocation(id, locationDto);
        return ResponseEntity.ok().body("Location updated successfully");
    }

    // Delete a Location
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
