import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedPasswordInputComponent } from './shared-password-input.component';

describe('SharedPasswordInputComponent', () => {
  let component: SharedPasswordInputComponent;
  let fixture: ComponentFixture<SharedPasswordInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SharedPasswordInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SharedPasswordInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
