import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Prazos } from './prazos';

describe('Prazos', () => {
  let component: Prazos;
  let fixture: ComponentFixture<Prazos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Prazos],
    }).compileComponents();

    fixture = TestBed.createComponent(Prazos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
