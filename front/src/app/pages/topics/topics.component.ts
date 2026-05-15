import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { forkJoin } from 'rxjs';
import { SubjectService } from '../../core/services/subject.service';
import { SubscriptionService } from '../../core/services/subscription.service';
import { Subject } from '../../core/models';

@Component({
  selector: 'app-topics',
  standalone: true,
  imports: [
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './topics.component.html',
  styleUrls: ['./topics.component.scss']
})
export class TopicsComponent implements OnInit {
  private subjectService = inject(SubjectService);
  private subscriptionService = inject(SubscriptionService);

  subjects: Subject[] = [];
  subscribedIds = new Set<number>();
  isLoading = true;
  errorMessage = '';
  pendingIds = new Set<number>();

  ngOnInit(): void {
    forkJoin({
      subjects: this.subjectService.getAllSubjects(),
      subscriptions: this.subscriptionService.getSubscriptions()
    }).subscribe({
      next: ({ subjects, subscriptions }) => {
        this.subjects = subjects;
        this.subscribedIds = new Set(subscriptions.map(s => s.id));
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les thèmes.';
        this.isLoading = false;
      }
    });
  }

  subscribe(subject: Subject): void {
    if (this.subscribedIds.has(subject.id) || this.pendingIds.has(subject.id)) return;
    this.pendingIds.add(subject.id);
    this.subscriptionService.subscribe(subject.id).subscribe({
      next: subscriptions => {
        this.subscribedIds = new Set(subscriptions.map(s => s.id));
        this.pendingIds.delete(subject.id);
      },
      error: () => this.pendingIds.delete(subject.id)
    });
  }
}
