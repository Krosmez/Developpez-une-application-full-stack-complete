import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { PostNewComponent } from './post-new.component';
import { PostService } from '../../../core/services/post.service';
import { SubscriptionService } from '../../../core/services/subscription.service';
import { Subject } from '../../../core/models';

describe('PostNewComponent', () => {
  let component: PostNewComponent;
  let fixture: ComponentFixture<PostNewComponent>;
  let postService: { createPost: jest.Mock };
  let subscriptionService: { getSubscriptions: jest.Mock };
  let router: { navigate: jest.Mock };

  const subjects: Subject[] = [{ id: 1, name: 'Java', description: 'd' }];

  beforeEach(async () => {
    postService = { createPost: jest.fn() };
    subscriptionService = {
      getSubscriptions: jest.fn().mockReturnValue(of(subjects)),
    };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [PostNewComponent],
      providers: [
        { provide: PostService, useValue: postService },
        { provide: SubscriptionService, useValue: subscriptionService },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(PostNewComponent, { set: { template: '' } })
      .compileComponents();

    fixture = TestBed.createComponent(PostNewComponent);
    component = fixture.componentInstance;
  });

  it('should load the subscribed subjects on init', () => {
    component.ngOnInit();
    expect(component.subjects).toBe(subjects);
  });

  it('should set an error message when subjects cannot be loaded', () => {
    subscriptionService.getSubscriptions.mockReturnValue(
      throwError(() => new Error()),
    );
    component.ngOnInit();
    expect(component.errorMessage).toBe('Impossible de charger les thèmes.');
  });

  it('goBack should navigate to /feed', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/feed']);
  });

  it('should not submit when the form is invalid', () => {
    component.onSubmit();
    expect(postService.createPost).not.toHaveBeenCalled();
  });

  it('should create the post and navigate to its detail page', () => {
    component.form.setValue({ subjectId: 1, title: 'Hello', content: 'Body' });
    postService.createPost.mockReturnValue(of({ id: 99 }));

    component.onSubmit();

    expect(postService.createPost).toHaveBeenCalledWith({
      subjectId: 1,
      title: 'Hello',
      content: 'Body',
    });
    expect(router.navigate).toHaveBeenCalledWith(['/posts', 99]);
  });

  it('should display a fallback error message when creation fails', () => {
    component.form.setValue({ subjectId: 1, title: 'Hello', content: 'Body' });
    postService.createPost.mockReturnValue(throwError(() => ({})));

    component.onSubmit();

    expect(component.errorMessage).toBe(
      "Erreur lors de la création de l'article.",
    );
    expect(component.isLoading).toBe(false);
  });
});
