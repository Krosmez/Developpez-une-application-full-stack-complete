import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';

import { PostService } from '../../../core/services/post.service';
import { SubscriptionService } from '../../../core/services/subscription.service';
import { Subject } from '../../../core/models';

@Component({
  selector: 'app-post-new',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    CdkTextareaAutosize,
  ],
  templateUrl: './post-new.component.html',
  styleUrls: ['./post-new.component.scss'],
})
export class PostNewComponent implements OnInit {
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private postService = inject(PostService);
  private subscriptionService = inject(SubscriptionService);

  subjects: Subject[] = [];
  form: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor() {
    this.form = this.fb.group({
      subjectId: [null, Validators.required],
      title: ['', Validators.required],
      content: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.subscriptionService.getSubscriptions().subscribe({
      next: (subjects) => (this.subjects = subjects),
      error: () => (this.errorMessage = 'Impossible de charger les thèmes.'),
    });
  }

  goBack(): void {
    this.router.navigate(['/feed']);
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isLoading = true;
    this.errorMessage = '';
    this.postService.createPost(this.form.value).subscribe({
      next: (res) => this.router.navigate(['/posts', res.id]),
      error: (err) => {
        this.isLoading = false;
        this.errorMessage =
          err.error?.message || "Erreur lors de la création de l'article.";
      },
    });
  }
}
