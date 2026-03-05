import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Location } from '../../models/location';

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  private apiUrl = 'http://localhost:9090/api/locations'; // Backend URL

  constructor(private http: HttpClient) {}

  authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // Get all locations
  getLocations(): Observable<Location[]> {
    return this.http.get<Location[]>(`${this.apiUrl}/`, {
      headers: this.authHeaders()
    });
  }

  // Get location by ID
  getLocationById(id: number): Observable<Location> {
    return this.http.get<Location>(`${this.apiUrl}/${id}`, {
      headers: this.authHeaders()
    });
  }

  // Create a new location
  createLocation(location: Location): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/create`, location, {
      headers: this.authHeaders(),
      responseType: 'text' as 'json'  // because backend returns String
    });
  }

  // Update existing location by ID
  updateLocation(id: number, location: Location): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/update/${id}`, location, {
      headers: this.authHeaders(),
      responseType: 'text' as 'json'  // backend returns String
    });
  }

  // Delete location by ID
  deleteLocation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, {
      headers: this.authHeaders()
    });
  }
}
