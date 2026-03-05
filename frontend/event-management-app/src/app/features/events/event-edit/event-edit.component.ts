import { LocationService } from '../../../core/services/location/location.service';
import { Location } from '../../../core/models/location';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EventService } from '../../../core/services/events/event.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Event as AppEvent, EventStatus, EventType } from '../../../core/models/event';
import { Component, OnInit } from '@angular/core';

interface LocationWithDisabled extends Location {
  disabled?: boolean;
}

@Component({
  standalone: false,
  selector: 'app-event-edit',
  templateUrl: './event-edit.component.html',
  styleUrls: ['./event-edit.component.scss'],
})

export class EventEditComponent implements OnInit {
  editEventForm!: FormGroup;
  eventTypes = Object.values(EventType);
  eventStatuses = Object.values(EventStatus);
  selectedImageFile: File | null = null;
  errorMessage: string | null = null;
  eventId!: number;
  eventImageUrl: string | null = null;

  locations: LocationWithDisabled[] = [];
  existingEvents: AppEvent[] = [];  // you may want to reuse same event model

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private router: Router,
    private route: ActivatedRoute,
    private locationService: LocationService,  // Inject location service
  ) {}

  ngOnInit(): void {
    this.eventId = Number(this.route.snapshot.paramMap.get('id'));
    this.initForm();

    // Load locations and existing events just like in create
    this.locationService.getLocations().subscribe(locations => {
      this.locations = locations;
      this.loadEvent();  // after locations loaded so we can set locationId properly
    });

    this.eventService.getAllEvents().subscribe(events => {
      this.existingEvents = events;
      this.updateLocationAvailability();
    });

    // Listen to date/time changes for availability (optional)
    this.editEventForm.get('startDate')?.valueChanges.subscribe(() => this.updateLocationAvailability());
    this.editEventForm.get('startTime')?.valueChanges.subscribe(() => this.updateLocationAvailability());
    this.editEventForm.get('endDate')?.valueChanges.subscribe(() => this.updateLocationAvailability());
    this.editEventForm.get('endTime')?.valueChanges.subscribe(() => this.updateLocationAvailability());

    // Location change triggers capacity validators
    this.editEventForm.get('locationId')?.valueChanges.subscribe((locationId) => {
      const location = this.locations.find(loc => loc.id === locationId);
      if (location) {
        this.editEventForm.get('capacity')?.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(location.capacity)
        ]);
      } else {
        this.editEventForm.get('capacity')?.setValidators([
          Validators.required,
          Validators.min(1)
        ]);
      }
      this.editEventForm.get('capacity')?.updateValueAndValidity();
    });
  }

  initForm(): void {
    this.editEventForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      locationId: [{value: null, disabled: true}, Validators.required],  // Use locationId here
      price: [0, [Validators.required, Validators.min(0)]],
      capacity: [0, [Validators.required, Validators.min(1)]],
      type: ['', Validators.required],
      status: ['', Validators.required],
      organizer: [''],
      startDate: [null, Validators.required],
      startTime: [null, Validators.required],
      endDate: [null, Validators.required],
      endTime: [null, Validators.required],
    });
  }

  loadEvent(): void {
    this.eventService.getEventById(this.eventId).subscribe({
      next: (event) => {
        const start = new Date(event.startTime);
        const end = new Date(event.endTime);

        this.eventImageUrl = event.imageUrl;

        this.editEventForm.patchValue({
          name: event.name,
          description: event.description,
          locationId: event.locationId,  // use locationId here
          price: event.price,
          capacity: event.capacity,
          type: event.type,
          status: event.status,
          organizer: event.organizer,
          startDate: start,
          startTime: start,
          endDate: end,
          endTime: end,
        });

        this.editEventForm.get('locationId')?.enable();  // enable after patching value
        this.updateLocationAvailability();
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = 'Failed to load event data';
      }
    });
  }

  updateLocationAvailability(): void {
    const startDate = this.editEventForm.get('startDate')?.value;
    const startTime = this.editEventForm.get('startTime')?.value;
    const endDate = this.editEventForm.get('endDate')?.value;
    const endTime = this.editEventForm.get('endTime')?.value;

    if (!startDate || !startTime || !endDate || !endTime) {
      this.locations.forEach(loc => loc.disabled = false);
      return;
    }

    const startDateTime = this.combineDateTime(startDate, startTime);
    const endDateTime = this.combineDateTime(endDate, endTime);

    this.locations.forEach(location => {
      const hasConflict = this.existingEvents.some(event => {
        if (event.locationId !== location.id || event.id === this.eventId) return false;

        const eventStart = new Date(event.startTime);
        const eventEnd = new Date(event.endTime);

        return eventStart < endDateTime && eventEnd > startDateTime;
      });

      location.disabled = hasConflict;
    });

    // If selected location is disabled, reset
    const selectedLocationId = this.editEventForm.get('locationId')?.value;
    const selectedLoc = this.locations.find(loc => loc.id === selectedLocationId);
    if (selectedLoc?.disabled) {
      this.editEventForm.get('locationId')?.setValue(null);
    }
  }

  combineDateTime(date: any, time: any): Date {
    const combined = new Date(date);
    combined.setHours(time.getHours(), time.getMinutes(), 0, 0);
    return combined;
  }

  updateEvent(): void {
    if (this.editEventForm.invalid) {
      this.errorMessage = 'Please fill in all required fields correctly.';
      return;
    }

    const formValue = this.editEventForm.value;
    const formData = new FormData();

    formData.append('name', formValue.name);
    formData.append('description', formValue.description || '');
    formData.append('organizer', formValue.organizer || '');
    formData.append('startTime', this.combineDateTime(formValue.startDate, formValue.startTime).toISOString());
    formData.append('endTime', this.combineDateTime(formValue.endDate, formValue.endTime).toISOString());
    formData.append('locationId', formValue.locationId);
    formData.append('price', formValue.price.toString());
    formData.append('capacity', formValue.capacity.toString());
    formData.append('type', formValue.type);
    formData.append('status', formValue.status);

    if (this.selectedImageFile) {
      formData.append('image', this.selectedImageFile, this.selectedImageFile.name);
    }

    this.eventService.updateEvent(this.eventId, formData).subscribe({
      next: () => {
        this.errorMessage = null;
        this.router.navigate(['/events']);
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.error || 'Failed to update event';
      }
    });
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedImageFile = input.files[0];
    }
  }

  cancel(): void {
    this.router.navigate(['/events']);
  }
}
