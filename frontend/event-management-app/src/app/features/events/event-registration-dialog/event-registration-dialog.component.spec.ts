import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventRegistrationDialogComponent } from './event-registration-dialog.component';

describe('EventRegistrationDialogComponent', () => {
  let component: EventRegistrationDialogComponent;
  let fixture: ComponentFixture<EventRegistrationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventRegistrationDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventRegistrationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
