import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Event as AppEvent, EventStatus, EventType } from '../../../core/models/event';
import { EventService } from '../../../core/services/events/event.service';
import { Router } from '@angular/router';
import { LocationService } from '../../../core/services/location/location.service';
import { Location } from '../../../core/models/location';

interface LocationWithDisabled extends Location {
  disabled?: boolean;
}

@Component({
  standalone: false,
  selector: 'app-event-create',
  templateUrl: './event-create.component.html',
  styleUrls: ['./event-create.component.scss'],
})
export class EventCreateComponent implements OnInit {
  createEventForm!: FormGroup;
  eventTypes = Object.values(EventType);
  eventStatuses = Object.values(EventStatus);
  selectedImageFile: File | null = null;
  errorMessage: string | null = null;
  locations: LocationWithDisabled[] = [];
  existingEvents: AppEvent[] = [];

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private router: Router,
    private locationService: LocationService
  ) {}

  ngOnInit(): void {
    this.createEventForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      locationId: [{ value: null, disabled: true }, Validators.required],
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

    // Enable/disable locationId control based on date/time validity
    this.createEventForm.get('startDate')?.valueChanges.subscribe(() => this.toggleLocationControl());
    this.createEventForm.get('startTime')?.valueChanges.subscribe(() => this.toggleLocationControl());
    this.createEventForm.get('endDate')?.valueChanges.subscribe(() => this.toggleLocationControl());
    this.createEventForm.get('endTime')?.valueChanges.subscribe(() => this.toggleLocationControl());

    // Load locations
    this.locationService.getLocations().subscribe((locations) => {
      this.locations = locations;
      this.updateLocationAvailability();
    });

    // Load existing events
    this.eventService.getAllEvents().subscribe((events) => {
      this.existingEvents = events;
      this.updateLocationAvailability();
    });

    // Update capacity validators when location changes
    this.createEventForm.get('locationId')?.valueChanges.subscribe((selectedLocationId) => {
      const location = this.locations.find((loc) => loc.id === selectedLocationId);
      if (location) {
        this.createEventForm.get('capacity')?.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(location.capacity),
        ]);
      } else {
        this.createEventForm.get('capacity')?.setValidators([Validators.required, Validators.min(1)]);
      }
      this.createEventForm.get('capacity')?.updateValueAndValidity();
    });

    // Watch date/time changes to update location availability
    this.createEventForm.valueChanges.subscribe(() => {
      this.updateLocationAvailability();
    });
  }

  toggleLocationControl(): void {
    const startDate = this.createEventForm.get('startDate')?.value;
    const startTime = this.createEventForm.get('startTime')?.value;
    const endDate = this.createEventForm.get('endDate')?.value;
    const endTime = this.createEventForm.get('endTime')?.value;

    if (startDate && startTime && endDate && endTime) {
      this.createEventForm.get('locationId')?.enable();
    } else {
      this.createEventForm.get('locationId')?.disable();
      this.createEventForm.get('locationId')?.setValue(null);
    }
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedImageFile = input.files[0];
    }
  }

  combineDateTime(date: any, time: any): Date | null {
    if (!date || !time) return null;
    const combined = new Date(date);
    combined.setHours(time.getHours(), time.getMinutes(), 0, 0);
    return combined;
  }

  updateLocationAvailability(): void {
    const startDate = this.createEventForm.get('startDate')?.value;
    const startTime = this.createEventForm.get('startTime')?.value;
    const endDate = this.createEventForm.get('endDate')?.value;
    const endTime = this.createEventForm.get('endTime')?.value;

    if (!startDate || !startTime || !endDate || !endTime) {
      // Enable all locations if date/time not fully specified
      this.locations.forEach((loc) => (loc.disabled = false));
      return;
    }

    const startDateTime = this.combineDateTime(startDate, startTime);
    const endDateTime = this.combineDateTime(endDate, endTime);

    if (!startDateTime || !endDateTime) {
      this.locations.forEach((loc) => (loc.disabled = false));
      return;
    }

    this.locations.forEach((location) => {
      const hasConflict = this.existingEvents.some((event) => {
        if (event.locationId !== location.id) return false;

        const eventStart = new Date(event.startTime);
        const eventEnd = new Date(event.endTime);

        // Check overlapping intervals
        return eventStart < endDateTime && eventEnd > startDateTime;
      });

      location.disabled = hasConflict;
    });

    // If the currently selected location is disabled, reset it
    const selectedLocationId = this.createEventForm.get('locationId')?.value;
    const selectedLoc = this.locations.find((loc) => loc.id === selectedLocationId);
    if (selectedLoc?.disabled) {
      this.createEventForm.get('locationId')?.setValue(null);
    }
  }

  createEvent(): void {
    if (this.createEventForm.invalid) {
      this.errorMessage = 'Please fill in all required fields correctly.';
      return;
    }

    const formValue = this.createEventForm.value;

    if (!formValue.locationId) {
      this.errorMessage = 'Please select a location.';
      return;
    }

    const formData = new FormData();
    formData.append('name', formValue.name);
    formData.append('description', formValue.description || '');
    formData.append('organizer', formValue.organizer || '');
    formData.append('startTime', this.combineDateTime(formValue.startDate, formValue.startTime)?.toISOString() || '');
    formData.append('endTime', this.combineDateTime(formValue.endDate, formValue.endTime)?.toISOString() || '');
    formData.append('price', formValue.price.toString());
    formData.append('capacity', formValue.capacity.toString());
    formData.append('type', formValue.type);
    formData.append('status', formValue.status);
    formData.append('locationId', formValue.locationId);

    if (this.selectedImageFile) {
      formData.append('image', this.selectedImageFile, this.selectedImageFile.name);
    }

    this.eventService.createEvent(formData).subscribe({
      next: (createdEvent) => {
        if (createdEvent) {
          this.createEventForm.reset();
          this.selectedImageFile = null;
          this.errorMessage = null;
          this.router.navigate(['/events']);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.error || 'Failed to create event';
      },
    });
  }
}
