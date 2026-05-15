import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChip, MatChipSet } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { PostService } from '../../../core/services/post.service';
import { PostDetail } from '../../../core/models';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [
    DatePipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatChip,
    MatChipSet,
    MatDividerModule,
    CdkTextareaAutosize
  ],
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.scss']
})
export class PostDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private postService = inject(PostService);
  private fb = inject(FormBuilder);

  post: PostDetail | null = null;
  isLoading = true;
  errorMessage = '';
  commentForm: FormGroup;
  isSubmittingComment = false;

  constructor() {
    this.commentForm = this.fb.group({
      content: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.postService.getPost(id).subscribe({
      next: post => {
        this.post = post;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Article introuvable.';
        this.isLoading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/feed']);
  }

  submitComment(): void {
    if (this.commentForm.invalid || !this.post) return;
    this.isSubmittingComment = true;
    this.postService.addComment(this.post.id, this.commentForm.value).subscribe({
      next: () => {
        this.isSubmittingComment = false;
        this.commentForm.reset();
        this.postService.getPost(this.post!.id).subscribe(updated => (this.post = updated));
      },
      error: () => {
        this.isSubmittingComment = false;
      }
    });
  }
}
