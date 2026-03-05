import { Component } from '@angular/core';
import { CalendarOptions } from '@fullcalendar/core';
import { EventService } from '../../../core/services/events/event.service';
import DayGridPlugin from '@fullcalendar/daygrid';
import InteractionPlugin from '@fullcalendar/interaction';
import TimeGridPlugin from '@fullcalendar/timegrid';
import { Router } from '@angular/router';

@Component({
  selector: 'app-events-calendar',
  standalone: false,
  templateUrl: './events-calendar.component.html',
  styleUrls: ['./events-calendar.component.scss']
})
export class EventsCalendarComponent {
  calendarOptions: CalendarOptions = {
    initialView: 'dayGridMonth',
    plugins: [DayGridPlugin, InteractionPlugin, TimeGridPlugin],
    events: [],
    eventTimeFormat: {
      hour: 'numeric',
      minute: '2-digit',
      meridiem: 'short'
    },
    eventClick: (info) => {
      console.log('Event clicked', info.event);
      this.router.navigate(['/events/', info.event.id]);
    },
    eventMouseEnter: (info) => {
      console.log('Hovered over event:', info.event.title);
      const eventEl = info.el;
      eventEl.style.backgroundColor = 'rgba(0, 123, 255, 0.2)';
      eventEl.style.borderColor = 'rgba(0, 123, 255, 0.6)';
      eventEl.style.cursor = 'pointer';
      eventEl.style.transition = 'background-color 0.3s ease, border-color 0.3s ease';
    },
    eventMouseLeave: (info) => {
      const eventEl = info.el;
      eventEl.style.backgroundColor = '';
      eventEl.style.borderColor = '';
      eventEl.style.cursor = '';
    },
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    views: {
      timeGridDay: {
        titleFormat: { year: 'numeric', month: 'short', day: 'numeric' }
      },
      timeGridWeek: {
        titleFormat: { year: 'numeric', month: 'short', day: 'numeric' }
      },
      dayGridMonth: {
        titleFormat: { year: 'numeric', month: 'long' }
      }
    }
  };

  constructor(private eventService: EventService, private router: Router) {}

  ngOnInit(): void {
    this.fetchEvents();
  }

  fetchEvents(): void {
    this.eventService.getAllEvents().subscribe(events => {
      this.calendarOptions.events = events.map(event => ({
        id: event.id.toString(),
        title: event.name,
        start: event.startTime,
        end: event.endTime,
        description: event.description || 'No description',
        location: event.location || 'TBD'
      }));
    }, error => {
      console.error('Error fetching events', error);
    });
  }
}
