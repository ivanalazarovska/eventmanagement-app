import { Routes } from '@angular/router';
import { RegisterComponent } from './features/authentication/register/register.component';
import { EventsViewComponent } from './features/events/events-view/events-view.component';
import { LoginComponent } from './features/authentication/login/login.component';
import { EventCreateComponent } from './features/events/event-create/event-create.component';
import { EventEditComponent } from './features/events/event-edit/event-edit.component';
import { EventDetailsComponent } from './features/events/event-details/event-details.component';
import { EventsCalendarComponent } from './features/events/events-calendar/events-calendar.component';
import { AuthGuard } from './auth.guard';
import { HomeComponent } from './features/home/home/home.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',  // Default redirect to login page
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'events/create-event',
    component: EventCreateComponent,
    canActivate: [AuthGuard] // Protect route with AuthGuard
  },
  {
    path: 'events/edit-event/:id',
    component: EventEditComponent,
    canActivate: [AuthGuard] // Protect route with AuthGuard
  },
  {
    path: 'events/:id',
    component: EventDetailsComponent,
    canActivate: [AuthGuard] // Protect route with AuthGuard
  },
  {
    path: 'events',
    component: EventsViewComponent,
    canActivate: [AuthGuard] // Protect route with AuthGuard
  },
  {
    path: 'events-calendar',
    component: EventsCalendarComponent,
    canActivate: [AuthGuard] // Protect route with AuthGuard
  }
];
