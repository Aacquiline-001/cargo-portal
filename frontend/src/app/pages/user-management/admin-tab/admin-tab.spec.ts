import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTab } from './admin-tab';

describe('AdminTab', () => {
  let component: AdminTab;
  let fixture: ComponentFixture<AdminTab>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTab],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminTab);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
