import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  constructor(private router: Router) {}
  openEvents(): void{
      this.router.navigate(['/events'])
  }

  openCreateEvent(): void{
    this.router.navigate(['/events/create-event'])
  }

  openRegisterForm(): void{
    this.router.navigate(['/register'])
  }
}
