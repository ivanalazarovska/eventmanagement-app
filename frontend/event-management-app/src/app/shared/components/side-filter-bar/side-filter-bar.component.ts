import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { EventStatus, EventType } from '../../../core/models/event';

@Component({
  selector: 'app-side-filter-bar',
  standalone: false,
  templateUrl: './side-filter-bar.component.html',
  styleUrls: ['./side-filter-bar.component.scss']
})
export class SideFilterBarComponent implements OnInit {
  @Input() eventTypes: EventType[] = [];
  @Input() selectedStatus: EventStatus | null = null;    // For Available, Booked, Cancelled
  @Input() selectedTimeStatus: string | null = null;     // For Upcoming, Past
  @Input() selectedEventTypes: { [key in EventType]: boolean } = {} as any;
  @Input() selectedDate: Date | null = null;
  @Input() selectedCapacity: number | null = null;
  @Input() selectedTime: string | null = null;
  @Input() dateDisabled: boolean = false;

  @Output() filtersChanged = new EventEmitter<any>();

  constructor() {}

  ngOnInit(): void {
    // Initialize selectedEventTypes if undefined
    this.eventTypes.forEach(type => {
      if (!(type in this.selectedEventTypes)) {
        this.selectedEventTypes[type] = false;
      }
    });
  }

  get allEventTypesSelected(): boolean {
    return this.eventTypes.every(type => this.selectedEventTypes[type]);
  }

  get someEventTypesSelected(): boolean {
    return this.eventTypes.some(type => this.selectedEventTypes[type]) && !this.allEventTypesSelected;
  }

  applyFilters(): void {
    this.filtersChanged.emit({
      selectedStatus: this.selectedStatus,
      selectedTimeStatus: this.selectedTimeStatus,
      selectedEventTypes: this.selectedEventTypes,
      selectedDate: this.selectedDate,
      selectedCapacity: this.selectedCapacity,
      selectedTime: this.selectedTime
    });
  }

  toggleAllEventTypes(checked: boolean): void {
    const newSelected = { ...this.selectedEventTypes };
    this.eventTypes.forEach(type => {
      newSelected[type] = checked;
    });
    this.selectedEventTypes = newSelected;
    this.applyFilters();
  }

  toggleEventType(type: EventType, checked: boolean): void {
    this.selectedEventTypes = { ...this.selectedEventTypes, [type]: checked };
    this.applyFilters();
  }
}
