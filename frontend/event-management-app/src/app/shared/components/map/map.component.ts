import { Component, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { LocationService } from '../../../core/services/location/location.service';
import { Location } from '../../../core/models/location';

@Component({
  standalone: false,
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit {

  showIframe = false;
  iframeUrl!: SafeResourceUrl;
  private map!: L.Map;

  private readonly finkiCoords:  [number, number] = [41.9981, 21.4280];
  finkiIcon = L.icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
    iconSize: [25, 41],       // size of the icon
    iconAnchor: [12, 41],     // point of the icon which will correspond to marker's location
    popupAnchor: [1, -34],    // point from which the popup should open relative to the iconAnchor
    shadowSize: [41, 41]      // size of the shadow
  });

  constructor(private sanitizer: DomSanitizer, private locationService: LocationService) {}

  ngAfterViewInit(): void {
    // Initialize the map centered on Finki UKIM campus
    this.map = L.map('map').setView(this.finkiCoords, 18);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    // Load other locations from backend first
    this.locationService.getLocations().subscribe((locations: Location[]) => {
      locations.forEach(loc => {
        // Skip if location is exactly Finki to avoid duplicate marker
        if (loc.latitude === this.finkiCoords[0] && loc.longitude === this.finkiCoords[1]) {
          return; // Skip Finki location here
        }

        const marker = L.marker([loc.latitude, loc.longitude]).addTo(this.map);
        marker.bindPopup(`${loc.name} (Capacity: ${loc.capacity})`);
      });

      // Add Finki marker after all others to ensure it's on top
      const finkiMarker = L.marker(this.finkiCoords, { icon: this.finkiIcon }).addTo(this.map).bindPopup('FINKI UKIM Location');


      finkiMarker.on('click', () => {
        this.iframeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
          'https://map.finki.ukim.mk/#18/42.00465/21.40903'
        );
        this.showIframe = true;
        setTimeout(() => this.map.invalidateSize(), 300);
      });

      finkiMarker.on('mouseover', () => finkiMarker.openPopup());
      finkiMarker.on('mouseout', () => finkiMarker.closePopup());
    });
  }
}
