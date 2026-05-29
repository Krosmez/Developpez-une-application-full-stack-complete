import { ActivatedRoute, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { PostDetail } from '../../../core/models';
import { PostDetailComponent } from './post-detail.component';
import { PostService } from '../../../core/services/post.service';

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postService: { getPost: jest.Mock; addComment: jest.Mock };
  let router: { navigate: jest.Mock };

  const post = { id: 5, title: 't', comments: [] } as unknown as PostDetail;

  beforeEach(async () => {
    postService = {
      getPost: jest.fn().mockReturnValue(of(post)),
      addComment: jest.fn().mockReturnValue(of({})),
    };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [PostDetailComponent],
      providers: [
        { provide: PostService, useValue: postService },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '5' } } },
        },
      ],
    })
      .overrideComponent(PostDetailComponent, { set: { template: '' } })
      .compileComponents();

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
  });

  it('should load the post on init', () => {
    component.ngOnInit();
    expect(postService.getPost).toHaveBeenCalledWith(5);
    expect(component.post).toBe(post);
    expect(component.isLoading).toBe(false);
  });

  it('should set an error message when the post cannot be loaded', () => {
    postService.getPost.mockReturnValue(throwError(() => new Error()));
    component.ngOnInit();
    expect(component.errorMessage).toBe('Article introuvable.');
    expect(component.isLoading).toBe(false);
  });

  it('goBack should navigate to /feed', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/feed']);
  });

  it('should not submit a comment when the form is invalid', () => {
    component.ngOnInit();
    component.submitComment();
    expect(postService.addComment).not.toHaveBeenCalled();
  });

  it('should submit a comment, reset the form and reload the post', () => {
    component.ngOnInit();
    component.commentForm.setValue({ content: 'Nice!' });
    const resetSpy = jest.spyOn(component.commentForm, 'reset');

    component.submitComment();

    expect(postService.addComment).toHaveBeenCalledWith(5, {
      content: 'Nice!',
    });
    expect(resetSpy).toHaveBeenCalled();
    expect(component.isSubmittingComment).toBe(false);
    // getPost called once on init + once after the comment reload
    expect(postService.getPost).toHaveBeenCalledTimes(2);
  });

  it('should display an error message when the comment submission fails', () => {
    component.ngOnInit();
    component.commentForm.setValue({ content: 'Nice!' });
    postService.addComment.mockReturnValue(throwError(() => ({})));

    component.submitComment();

    expect(component.commentErrorMessage).toBe(
      "Erreur lors de l'envoi du commentaire.",
    );
    expect(component.isSubmittingComment).toBe(false);
  });
});
