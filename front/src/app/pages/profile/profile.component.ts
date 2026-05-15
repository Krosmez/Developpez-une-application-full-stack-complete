import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { UserService } from '../../core/services/user.service';
import { SubscriptionService } from '../../core/services/subscription.service';
import { AuthService } from '../../core/services/auth.service';
import { UserProfile, Subject, UpdateUserRequest } from '../../core/models';

const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z\d]).{8,}$/;

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatIconModule
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  private userService = inject(UserService);
  private subscriptionService = inject(SubscriptionService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  profile: UserProfile | null = null;
  subscriptions: Subject[] = [];
  isLoading = true;
  isSaving = false;
  errorMessage = '';
  successMessage = '';
  form: FormGroup;
  unsubscribingIds = new Set<number>();

  constructor() {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      bio: [''],
      password: ['', Validators.pattern(PASSWORD_PATTERN)]
    });
  }

  ngOnInit(): void {
    forkJoin({
      profile: this.userService.getMe(),
      subscriptions: this.subscriptionService.getSubscriptions()
    }).subscribe({
      next: ({ profile, subscriptions }) => {
        this.profile = profile;
        this.subscriptions = subscriptions;
        this.form.patchValue({
          email: profile.email,
          username: profile.username,
          bio: profile.bio ?? ''
        });
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de charger le profil.';
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid || !this.profile) return;
    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload: UpdateUserRequest = {
      email: this.form.get('email')!.value,
      username: this.form.get('username')!.value
    };
    const bio = this.form.get('bio')!.value;
    if (bio) payload.bio = bio;
    const password = this.form.get('password')!.value;
    if (password) payload.password = password;

    this.userService.updateProfile(this.profile.id, payload).subscribe({
      next: updated => {
        this.profile = updated;
        this.form.get('password')!.reset();
        this.successMessage = 'Profil mis à jour avec succès.';
        this.isSaving = false;
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Erreur lors de la mise à jour.';
        this.isSaving = false;
      }
    });
  }

  unsubscribe(subject: Subject): void {
    if (this.unsubscribingIds.has(subject.id)) return;
    this.unsubscribingIds.add(subject.id);
    this.subscriptionService.unsubscribe(subject.id).subscribe({
      next: updated => {
        this.subscriptions = updated;
        this.unsubscribingIds.delete(subject.id);
      },
      error: () => this.unsubscribingIds.delete(subject.id)
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
