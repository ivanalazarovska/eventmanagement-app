import { AuthService } from './../../../core/services/auth/auth.service';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navabar',
  standalone: false,

  templateUrl: './navabar.component.html',
  styleUrl: './navabar.component.scss'
})
export class NavabarComponent {
  isAuthenticated: boolean = false;

  constructor(private router: Router, private authService: AuthService){}

  ngOnInit() {
    this.isAuthenticated = this.authService.isAuthenticated();
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    this.authService.setAuthenticated(false);
    this.isAuthenticated = false;
    this.router.navigate(['/login']);
  }

  openCalendar(): void {
    this.router.navigate(['events-calendar']);
  }
}
