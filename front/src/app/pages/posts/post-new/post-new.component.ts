import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { PostService } from '../../../core/services/post.service';
import { SubjectService } from '../../../core/services/subject.service';
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
    CdkTextareaAutosize
  ],
  templateUrl: './post-new.component.html',
  styleUrls: ['./post-new.component.scss']
})
export class PostNewComponent implements OnInit {
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private postService = inject(PostService);
  private subjectService = inject(SubjectService);

  subjects: Subject[] = [];
  form: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor() {
    this.form = this.fb.group({
      subjectId: [null, Validators.required],
      title: ['', Validators.required],
      content: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.subjectService.getAllSubjects().subscribe({
      next: subjects => (this.subjects = subjects),
      error: () => (this.errorMessage = 'Impossible de charger les thèmes.')
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
      next: res => this.router.navigate(['/posts', res.id]),
      error: () => {
        this.isLoading = false;
        this.errorMessage = "Erreur lors de la création de l'article.";
      }
    });
  }
}
