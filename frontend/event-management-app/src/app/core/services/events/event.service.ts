import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event, ParticipationStatus } from '../../models/event';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private baseUrl = 'http://localhost:9090/api/events';

  constructor(private http: HttpClient) {}

  authHeaders(): HttpHeaders{
    const token = localStorage.getItem('token');
        if (!token) {
        console.warn('No auth token found!');
      }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return headers;
  }

  //getting events
  getAllEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.baseUrl, { headers: this.authHeaders() });
  }

  getTodayEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.baseUrl}/today`, { headers: this.authHeaders() });
  }

  getUpcomingEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.baseUrl}/upcoming`, { headers: this.authHeaders() });
  }

  getPastEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.baseUrl}/past`, { headers: this.authHeaders() });
  }

  getCurrentEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.baseUrl}/current`, { headers: this.authHeaders() });
  }

  getEventDetails(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.baseUrl}/${id}/details`, { headers: this.authHeaders() });
  }

  rateEvent(eventId: number, rating: number): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/${eventId}/add-rating`,
      {},
      {
        headers: this.authHeaders(),
        params: { rate: rating },
        responseType: 'text' as 'text'
      }
    );
  }

addComment(eventId: number, comment: string): Observable<any> {
  return this.http.post(
    `${this.baseUrl}/${eventId}/comments`,
    comment,
    {
      headers: this.authHeaders(),
      responseType: 'text'
     }
  );
}




//other crud operations
  getEventById(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.baseUrl}/${id}`, { headers: this.authHeaders() });
  }

  createEvent(data: FormData): Observable<Event> {
    console.log(data)
    return this.http.post<Event>(this.baseUrl, data, { headers: this.authHeaders() });
  }

  updateEvent(id: number, data: FormData): Observable<Event> {
    return this.http.put<Event>(`${this.baseUrl}/${id}`, data, { headers: this.authHeaders() });
  }

  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.authHeaders() });
  }

  setParticipationStatus(eventId: number, status: ParticipationStatus): Observable<string> {
    return this.http.post(
      `${this.baseUrl}/${eventId}/likeOrDecline`,
      null,
      {
        headers: this.authHeaders(),
        params: {
          status
        },
        responseType: 'text'
      }
    );
  }

  register(eventId: number, userIds: number[]) {
    return this.http.post(`${this.baseUrl}/${eventId}/register`, userIds, {
      responseType: 'text',
      headers: this.authHeaders()
    });
  }
}


