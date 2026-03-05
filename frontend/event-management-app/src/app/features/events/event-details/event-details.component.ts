import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Event, EventStatus, ParticipationStatus } from '../../../core/models/event';
import { EventService } from '../../../core/services/events/event.service';
import { EventRegistrationDialogComponent } from '../event-registration-dialog/event-registration-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-event-details',
  standalone: false,
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.scss']
})
export class EventDetailsComponent implements OnInit {
  event: Event | undefined;
  participationStatuse = ParticipationStatus;
  userResponse: ParticipationStatus | null = null;
  newComment: string = '';
  comments: { user: string, text: string }[] = [];
  eventStatuses = EventStatus;

  selectedRating: number = 0;
  hoveredRating: number = 0;

  constructor(private route: ActivatedRoute, private eventService: EventService, private dialog: MatDialog) {}

  ngOnInit(): void {
    const eventId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEvent(eventId);
  }

  loadEvent(id: number): void {
  this.eventService.getEventById(id).subscribe({
    next: (eventData) => {
      this.event = {
        ...eventData,
        averageRating: Number(eventData.averageRating),
      };
      this.userResponse = eventData.userParticipationStatus;
      console.log(this.event)
    },
    error: (error) => {
      console.error('Failed to load event:', error);
    }
  });
}

  respond(response: ParticipationStatus) {
      this.userResponse = response;
      console.log(this.userResponse);
      if (response === ParticipationStatus.GOING) {
        const dialogRef = this.dialog.open(EventRegistrationDialogComponent, {
          width: '600px',
          data: {
            event: this.event,
          }
        });

        dialogRef.afterClosed().subscribe((result) => {
          if (result) {
            console.log('Registered users:', result);
            this.loadEvent(this.event!.id);
          }
        });

        return;
      };

      if (response === ParticipationStatus.INTERESTED || response === ParticipationStatus.DECLINED) {
        if (!this.event) {
          console.error('No event loaded.');
          return;
        }

        const username = 'student1'; // Replace with actual logged-in user

        this.eventService.setParticipationStatus(this.event.id, response)
          .subscribe({
            next: (res) => {
              console.log(`Participation status updated to ${response}`);
              console.log('Server response:', res);
              const eventId = Number(this.route.snapshot.paramMap.get('id'));
              this.loadEvent(eventId);
            },
            error: (err) => {
              console.error('Failed to update participation status', err);
            },
          });
      }
  }

  addRating(rating: number): void {
  this.selectedRating = rating;

  console.log(rating)
    this.eventService.rateEvent(this.event!.id, rating).subscribe(() => {
      this.loadEvent(this.event!.id);
    });

  }

  addComment() {
    const trimmedComment = this.newComment.trim();
    if (!trimmedComment || !this.event) return;

    this.eventService.addComment(this.event.id, trimmedComment).subscribe({
      next: () => {
        this.loadEvent(this.event!.id);
        this.newComment = '';
      },
      error: (err) => {
        console.error('Failed to post comment:', err);
      }
    });
  }

  isSameDay(start: string, end: string): boolean {
    const startDate = new Date(start);
    const endDate = new Date(end);
    return startDate.toDateString() === endDate.toDateString();
  }
}
