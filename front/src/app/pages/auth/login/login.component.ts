import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  form: FormGroup;
  errorMessage = '';
  noticeMessage = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      identifier: ['', Validators.required],
      password: ['', Validators.required]
    });
    if (this.route.snapshot.queryParamMap.get('reason') === 'profile-updated') {
      this.noticeMessage =
        'Votre profil a été mis à jour. Veuillez vous reconnecter.';
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isLoading = true;
    this.errorMessage = '';
    this.authService.login(this.form.value).subscribe({
      next: () => this.router.navigate(['/feed']),
      error: err => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Identifiants incorrects.';
      }
    });
  }
}
