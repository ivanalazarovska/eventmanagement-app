import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Event, EventStatus } from '../../../core/models/event';

@Component({
  selector: 'app-event-card',
  standalone: false,

  templateUrl: './event-card.component.html',
  styleUrl: './event-card.component.scss'
})
export class EventCardComponent {
  @Input() event: Event | null = null; // Event data
  @Input() canEditDelete: boolean = false;
  @Output() editEvent = new EventEmitter<Event>();
  @Output() arrowPressed = new EventEmitter<Event>();
  @Output() deleteEvent = new EventEmitter<Event>();


  eventStatus = EventStatus;


  onArrowClick(event: MouseEvent) {
    event.stopPropagation();
    if(this.event){
      console.log("from carddd")
      console.log(this.event.id)
      this.arrowPressed.emit(this.event);
    }
  }

  onEditClick(event: MouseEvent): void {
    event.stopPropagation();
    if (this.event) {
      this.editEvent.emit(this.event);
    }
  }

  onDeleteClick(event: MouseEvent): void {
    event.stopPropagation();
    if(this.event){
      this.deleteEvent.emit(this.event);
    }
  }

    isSameDay(start: string, end: string): boolean {
      const startDate = new Date(start);
      const endDate = new Date(end);
      return startDate.toDateString() === endDate.toDateString();
  }
}
