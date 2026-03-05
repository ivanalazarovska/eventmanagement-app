import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { UserRole } from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:9090';

  constructor(private http: HttpClient) {}

  authHeaders(): HttpHeaders{
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return headers;
  }

  // Register a new user
  register(username: string, password: string, repeatPassword: string, name: string, surname: string, email: string, role: UserRole): Observable<any> {
    const body = { username, email, password, repeatPassword, name, surname, role };
    return this.http.post(`${this.apiUrl}/register`, body, {responseType: 'text'}).pipe(
      catchError((error) => {
        console.error('Registration failed:', error); // Log the error for debugging
        return throwError(error);
      })
    );;
  }

  // Login user
  login(email: string, password: string): Observable<any> {
    const body = { email, password };
    return this.http.post(`${this.apiUrl}/login`, body);
  }

  // Logout user
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, null, {headers: this.authHeaders()});
  }

  // Check if the user is authenticated (you can expand this if you're storing token)
  isAuthenticated(): boolean {
    return localStorage.getItem('isAuthenticated') === 'true';
  }

  setAuthenticated(value: boolean): void {
    localStorage.setItem('isAuthenticated', value.toString());
  }
}
