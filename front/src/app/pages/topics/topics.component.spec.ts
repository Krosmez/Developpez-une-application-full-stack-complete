import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { Subject } from '../../core/models';
import { SubjectService } from '../../core/services/subject.service';
import { SubscriptionService } from '../../core/services/subscription.service';
import { TopicsComponent } from './topics.component';

describe('TopicsComponent', () => {
  let component: TopicsComponent;
  let fixture: ComponentFixture<TopicsComponent>;
  let subjectService: { getAllSubjects: jest.Mock };
  let subscriptionService: {
    getSubscriptions: jest.Mock;
    subscribe: jest.Mock;
  };

  const subjects: Subject[] = [
    { id: 1, name: 'Java', description: 'd' },
    { id: 2, name: 'Angular', description: 'd' },
  ];

  beforeEach(async () => {
    subjectService = {
      getAllSubjects: jest.fn().mockReturnValue(of(subjects)),
    };
    subscriptionService = {
      getSubscriptions: jest.fn().mockReturnValue(of([subjects[0]])),
      subscribe: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [TopicsComponent],
      providers: [
        { provide: SubjectService, useValue: subjectService },
        { provide: SubscriptionService, useValue: subscriptionService },
      ],
    })
      .overrideComponent(TopicsComponent, { set: { template: '' } })
      .compileComponents();

    fixture = TestBed.createComponent(TopicsComponent);
    component = fixture.componentInstance;
  });

  it('should load subjects and mark the subscribed ids on init', () => {
    component.ngOnInit();
    expect(component.subjects).toBe(subjects);
    expect(component.subscribedIds.has(1)).toBe(true);
    expect(component.subscribedIds.has(2)).toBe(false);
    expect(component.isLoading).toBe(false);
  });

  it('should set an error message when loading fails', () => {
    subjectService.getAllSubjects.mockReturnValue(
      throwError(() => new Error()),
    );
    component.ngOnInit();
    expect(component.errorMessage).toBe('Impossible de charger les thèmes.');
    expect(component.isLoading).toBe(false);
  });

  it('should subscribe to a new subject and update the subscribed ids', () => {
    component.ngOnInit();
    subscriptionService.subscribe.mockReturnValue(of(subjects));

    component.subscribe(subjects[1]);

    expect(subscriptionService.subscribe).toHaveBeenCalledWith(2);
    expect(component.subscribedIds.has(2)).toBe(true);
    expect(component.pendingIds.has(2)).toBe(false);
  });

  it('should ignore subscribing to an already subscribed subject', () => {
    component.ngOnInit();
    component.subscribe(subjects[0]);
    expect(subscriptionService.subscribe).not.toHaveBeenCalled();
  });

  it('should clear the pending id when subscribing fails', () => {
    component.ngOnInit();
    subscriptionService.subscribe.mockReturnValue(throwError(() => ({})));
    component.subscribe(subjects[1]);
    expect(component.pendingIds.has(2)).toBe(false);
  });
});
