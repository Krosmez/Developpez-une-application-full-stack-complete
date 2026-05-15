import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { FeedService } from '../../core/services/feed.service';
import { FeedItem } from '../../core/models';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [
    DatePipe,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChip,
    MatChipSet
  ],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit {
  items: FeedItem[] = [];
  sort: 'asc' | 'desc' = 'desc';
  isLoading = true;
  errorMessage = '';

  constructor(private feedService: FeedService) {}

  ngOnInit(): void {
    this.loadFeed();
  }

  toggleSort(): void {
    this.sort = this.sort === 'desc' ? 'asc' : 'desc';
    this.loadFeed();
  }

  private loadFeed(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.feedService.getFeed(this.sort).subscribe({
      next: items => {
        this.items = items;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de charger le fil d\'actualité.';
        this.isLoading = false;
      }
    });
  }
}
