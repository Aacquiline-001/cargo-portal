import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentTab } from './agent-tab';

describe('AgentTab', () => {
  let component: AgentTab;
  let fixture: ComponentFixture<AgentTab>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentTab],
    }).compileComponents();

    fixture = TestBed.createComponent(AgentTab);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
