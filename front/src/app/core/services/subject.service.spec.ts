import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { SubjectService } from './subject.service';
import { environment } from '../../../environments/environment';

describe('SubjectService', () => {
  let service: SubjectService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SubjectService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(SubjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAllSubjects should GET the subjects list', () => {
    const subjects = [{ id: 1, name: 'Java', description: 'd' }];
    service.getAllSubjects().subscribe((res) => expect(res).toBe(subjects));

    const req = httpMock.expectOne(`${environment.apiUrl}/subjects`);
    expect(req.request.method).toBe('GET');
    req.flush(subjects);
  });
});
