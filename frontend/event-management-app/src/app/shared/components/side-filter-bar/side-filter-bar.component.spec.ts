import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideFilterBarComponent } from './side-filter-bar.component';

describe('SideFilterBarComponent', () => {
  let component: SideFilterBarComponent;
  let fixture: ComponentFixture<SideFilterBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SideFilterBarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SideFilterBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
