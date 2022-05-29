import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FetchFilesComponent } from './fetch-files.component';

describe('FetchFilesComponent', () => {
  let component: FetchFilesComponent;
  let fixture: ComponentFixture<FetchFilesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FetchFilesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FetchFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
