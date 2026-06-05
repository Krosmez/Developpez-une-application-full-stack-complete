import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../../core/services/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: { register: jest.Mock };
  let router: { navigate: jest.Mock };

  const validValue = {
    email: 'john@test.com',
    username: 'john',
    password: 'Abcdef1!',
  };

  beforeEach(async () => {
    authService = { register: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(RegisterComponent, { set: { template: '' } })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
  });

  it('should create with an invalid empty form', () => {
    expect(component).toBeTruthy();
    expect(component.form.invalid).toBe(true);
  });

  it('should reject a weak password via the pattern validator', () => {
    component.form.patchValue({ ...validValue, password: 'weak' });
    expect(component.form.get('password')!.valid).toBe(false);
  });

  it('should accept a strong password and a valid email', () => {
    component.form.setValue(validValue);
    expect(component.form.valid).toBe(true);
  });

  it('should not call the service when the form is invalid', () => {
    component.onSubmit();
    expect(authService.register).not.toHaveBeenCalled();
  });

  it('should navigate to /feed on successful registration', () => {
    component.form.setValue(validValue);
    authService.register.mockReturnValue(of({ token: 'abc' }));

    component.onSubmit();

    expect(authService.register).toHaveBeenCalledWith(validValue);
    expect(router.navigate).toHaveBeenCalledWith(['/feed']);
  });

  it('should display a fallback error message on failure', () => {
    component.form.setValue(validValue);
    authService.register.mockReturnValue(throwError(() => ({})));

    component.onSubmit();

    expect(component.errorMessage).toBe("Erreur lors de l'inscription.");
    expect(component.isLoading).toBe(false);
  });
});
