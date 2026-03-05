import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs'; // Import MatTabsModule
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SharedModule } from '../../shared/shared.module';
import { EventsViewComponent } from './events-view/events-view.component';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, provideNativeDateAdapter } from '@angular/material/core';
import { FormsModule } from '@angular/forms';
import {MatTimepickerModule} from '@angular/material/timepicker';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { EventCardComponent } from './event-card/event-card.component';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import {MatBadgeModule} from '@angular/material/badge';
import {MatExpansionModule} from '@angular/material/expansion';
import { EventEditComponent } from './event-edit/event-edit.component';
import { EventCreateComponent } from './event-create/event-create.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EventDetailsComponent } from './event-details/event-details.component';
import { EventRegistrationDialogComponent } from './event-registration-dialog/event-registration-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { FullCalendarModule } from '@fullcalendar/angular';
import { EventsCalendarComponent } from './events-calendar/events-calendar.component';
import { CarouselModule } from 'ngx-bootstrap/carousel';


@NgModule({
  declarations: [
    EventsViewComponent,
    EventCardComponent,
    EventEditComponent,
    EventCreateComponent,
    EventDetailsComponent,
    EventRegistrationDialogComponent,
    EventsCalendarComponent
  ],
  imports: [
    CommonModule,
    MatTabsModule, // Ensure MatTabsModule is imported
    MatProgressSpinnerModule,
    SharedModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    FormsModule,
    MatTimepickerModule,
    MatCheckboxModule,
    MatCardModule,
    MatButtonModule,
    MatBadgeModule,
    MatExpansionModule,
    ReactiveFormsModule,
    RouterModule,
    MatDialogModule,
    FullCalendarModule,
    CarouselModule
],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class EventsModule { }


