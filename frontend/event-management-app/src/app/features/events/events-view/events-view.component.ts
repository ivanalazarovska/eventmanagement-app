import { Component, OnInit } from '@angular/core';
import { Event, EventStatus, EventType } from '../../../core/models/event';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { EventService } from '../../../core/services/events/event.service';
import { OwlOptions } from 'ngx-owl-carousel-o';

@Component({
  selector: 'app-events-view',
  standalone: false,
  templateUrl: './events-view.component.html',
  styleUrls: ['./events-view.component.scss']
})
export class EventsViewComponent implements OnInit {
  todayEvents: Event[] = [];
  pastEvents: Event[] = [];
  upcomingEvents: Event[] = [];
  currentEvents: Event[] = [];
  filteredTodayEvents: Event[] = [];
  filteredPastEvents: Event[] = [];
  filteredUpcomingEvents: Event[] = [];

  eventTypes = Object.values(EventType);

  selectedStatus: EventStatus | null = null;
  selectedTime: string | null = null;
  selectedDate: Date | null = null;
  selectedCapacity: number | null = null;
  selectedEventTypes: { [key in EventType]: boolean } = {} as any;

  showCreateForm = false;
  showEditForm = false;
  selectedEvent: any = null;

  errorMessage: string | null = null;

  loggedInUserEmail: string = "";

  defaultImage = 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQkYKEdUuzbD126hCKbp7n49fcumh6PEWZyqQ&s';

  constructor(private router: Router, private eventService: EventService) {}

  ngOnInit(): void {
    this.eventTypes.forEach(type => {
      this.selectedEventTypes[type] = false;
    });
    this.loggedInUserEmail = localStorage.getItem('email') ?? "";
    this.loadEvents();
  }

  loadEvents(): void {
    combineLatest([
      this.eventService.getTodayEvents(),
      this.eventService.getUpcomingEvents(),
      this.eventService.getPastEvents(),
      this.eventService.getCurrentEvents()
    ]).subscribe({
      next: ([todayEvents, upcomingEvents, pastEvents, currentEvents]) => {
        this.todayEvents = todayEvents;
        this.upcomingEvents = upcomingEvents;
        this.pastEvents = pastEvents;
        this.currentEvents = currentEvents;

        this.filteredTodayEvents = [...this.todayEvents];
        this.filteredUpcomingEvents = [...this.upcomingEvents];
        this.filteredPastEvents = [...this.pastEvents];
      },
      error: (err) => {
        console.error('Failed to load events', err);
        this.errorMessage = 'Could not load events. Please try again later.';
      }
    });
  }

 applyFilters(filters: any): void {
  this.selectedStatus = filters.selectedStatus;
  this.selectedTime = filters.selectedTime;
  this.selectedEventTypes = filters.selectedEventTypes;
  this.selectedDate = filters.selectedDate;
  this.selectedCapacity = filters.selectedCapacity;

  const selectedTypes = Object.entries(this.selectedEventTypes)
    .filter(([_, selected]) => selected)
    .map(([type]) => type);

  const matchesFilters = (event: Event): boolean => {
    // Status filter
    const matchesStatus = !this.selectedStatus || this.selectedStatus === event.status;

    // Capacity filter
    const matchesCapacity = !this.selectedCapacity || event.capacity >= this.selectedCapacity;

    // Event Types filter
    const matchesEventTypes =
      selectedTypes.length === 0 || selectedTypes.includes(event.type);

    // Date filter
    const eventDate = new Date(event.startTime);
    const selected = this.selectedDate!;
    const matchesDate = !selected || (
      eventDate.getFullYear() === selected.getFullYear() &&
      eventDate.getMonth() === selected.getMonth() &&
      eventDate.getDate() === selected.getDate()
    );

    // Time filter
    if (!this.selectedTime) {
      return matchesStatus && matchesCapacity && matchesEventTypes && matchesDate;
    }
    const [selHours, selMinutes] = this.selectedTime.split(':').map(Number);
    const matchesTime = eventDate.getHours() === selHours && eventDate.getMinutes() === selMinutes;

    return matchesStatus && matchesCapacity && matchesEventTypes && matchesDate && matchesTime;
  };

  this.filteredTodayEvents = this.todayEvents.filter(matchesFilters);
  this.filteredUpcomingEvents = this.upcomingEvents.filter(matchesFilters);
  this.filteredPastEvents = this.pastEvents.filter(matchesFilters);
}


  resetFilters(): void {
    this.selectedStatus = null;
    this.selectedTime = null;
    this.selectedDate = null;
    this.selectedCapacity = null;
    this.eventTypes.forEach(type => {
      this.selectedEventTypes[type] = false;
    });
    this.filteredTodayEvents = [...this.todayEvents];
    this.filteredUpcomingEvents = [...this.upcomingEvents];
    this.filteredPastEvents = [...this.pastEvents];
  }

  openCreateEvent(): void {
    this.router.navigate(['events/create-event']);
  }

  openEditEvent(event: Event): void {
    this.router.navigate(['events/edit-event', event.id]);
  }

  openEventDetails(event: Event): void {
    console.log("from viewss");
    console.log(event.id);
    this.router.navigate(['/events', event.id]);
  }

  handleEventDelete(event: Event): void {
    if (!event.id) {
      console.error('Event ID is missing!');
      return;
    }
    this.eventService.deleteEvent(event.id).subscribe({
    next: () => {
      console.log(`Deleted event with id ${event.id}`);

      this.todayEvents = this.todayEvents.filter(e => e.id !== event.id);
      this.filteredTodayEvents = this.filteredTodayEvents.filter(e => e.id !== event.id);

      this.upcomingEvents = this.upcomingEvents.filter(e => e.id !== event.id);
      this.filteredUpcomingEvents = this.filteredUpcomingEvents.filter(e => e.id !== event.id);

      this.pastEvents = this.pastEvents.filter(e => e.id !== event.id);
      this.filteredPastEvents = this.filteredPastEvents.filter(e => e.id !== event.id);

      this.errorMessage = null;
    },
    error: (err) => {
      console.error('Error deleting event:', err);
      this.errorMessage = 'Failed to delete event. Please try again.';
    }
  });
  }
}
