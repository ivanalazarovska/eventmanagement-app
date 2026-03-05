import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedPasswordInputComponent } from './components/shared-password-input/shared-password-input.component';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';
import { NavabarComponent } from './components/navabar/navabar.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { FormsModule } from '@angular/forms';
import {MatTimepickerModule} from '@angular/material/timepicker';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { SideFilterBarComponent } from './components/side-filter-bar/side-filter-bar.component';
import { MapComponent } from './components/map/map.component';


@NgModule({
  declarations: [
    SharedPasswordInputComponent,
    NavabarComponent,
    SideFilterBarComponent,
    MapComponent
  ],
  imports: [
    RouterModule,
    CommonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatMenuModule,
    MatTooltipModule,
    MatCardModule,
    MatGridListModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    FormsModule,
    MatTimepickerModule,
    MatCheckboxModule
  ],
  exports: [
    SharedPasswordInputComponent,
    NavabarComponent,
    SideFilterBarComponent,
    MapComponent
  ]
})
export class SharedModule { }
