import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { FeedComponent } from './feed.component';
import { FeedItem } from '../../core/models';
import { FeedService } from '../../core/services/feed.service';

describe('FeedComponent', () => {
  let component: FeedComponent;
  let fixture: ComponentFixture<FeedComponent>;
  let feedService: { getFeed: jest.Mock };

  const items = [
    { post: { id: 1 }, subject: { id: 1 } },
  ] as unknown as FeedItem[];

  beforeEach(async () => {
    feedService = { getFeed: jest.fn().mockReturnValue(of(items)) };

    await TestBed.configureTestingModule({
      imports: [FeedComponent],
      providers: [{ provide: FeedService, useValue: feedService }],
    })
      .overrideComponent(FeedComponent, { set: { template: '' } })
      .compileComponents();

    fixture = TestBed.createComponent(FeedComponent);
    component = fixture.componentInstance;
  });

  it('should load the feed on init with the default desc sort', () => {
    component.ngOnInit();
    expect(feedService.getFeed).toHaveBeenCalledWith('desc');
    expect(component.items).toBe(items);
    expect(component.isLoading).toBe(false);
  });

  it('should toggle the sort order and reload', () => {
    component.ngOnInit();
    component.toggleSort();
    expect(component.sort).toBe('asc');
    expect(feedService.getFeed).toHaveBeenLastCalledWith('asc');

    component.toggleSort();
    expect(component.sort).toBe('desc');
  });

  it('should set an error message when loading fails', () => {
    feedService.getFeed.mockReturnValue(throwError(() => new Error('boom')));
    component.ngOnInit();
    expect(component.errorMessage).toBe(
      "Impossible de charger le fil d'actualité.",
    );
    expect(component.isLoading).toBe(false);
  });
});
