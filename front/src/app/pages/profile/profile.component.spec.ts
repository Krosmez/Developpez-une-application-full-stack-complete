import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { ProfileComponent } from './profile.component';
import { UserService } from '../../core/services/user.service';
import { SubscriptionService } from '../../core/services/subscription.service';
import { AuthService } from '../../core/services/auth.service';
import { Subject, UserProfile } from '../../core/models';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let userService: { getMe: jest.Mock; updateProfile: jest.Mock };
  let subscriptionService: {
    getSubscriptions: jest.Mock;
    unsubscribe: jest.Mock;
  };
  let authService: { logout: jest.Mock };
  let router: { navigate: jest.Mock };

  const profile: UserProfile = {
    id: 1,
    email: 'john@test.com',
    username: 'john',
    bio: 'hi',
  };
  const subscriptions: Subject[] = [{ id: 2, name: 'Java', description: 'd' }];

  beforeEach(async () => {
    userService = {
      getMe: jest.fn().mockReturnValue(of(profile)),
      updateProfile: jest.fn(),
    };
    subscriptionService = {
      getSubscriptions: jest.fn().mockReturnValue(of(subscriptions)),
      unsubscribe: jest.fn(),
    };
    authService = { logout: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: UserService, useValue: userService },
        { provide: SubscriptionService, useValue: subscriptionService },
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(ProfileComponent, { set: { template: '' } })
      .compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
  });

  it('should load profile and subscriptions on init and patch the form', () => {
    component.ngOnInit();
    expect(component.profile).toBe(profile);
    expect(component.subscriptions).toBe(subscriptions);
    expect(component.form.get('email')!.value).toBe('john@test.com');
    expect(component.form.get('username')!.value).toBe('john');
    expect(component.isLoading).toBe(false);
  });

  it('should set an error message when loading fails', () => {
    userService.getMe.mockReturnValue(throwError(() => new Error()));
    component.ngOnInit();
    expect(component.errorMessage).toBe('Impossible de charger le profil.');
    expect(component.isLoading).toBe(false);
  });

  it('should not submit when the form is invalid', () => {
    component.ngOnInit();
    component.form.get('email')!.setValue('');
    component.onSubmit();
    expect(userService.updateProfile).not.toHaveBeenCalled();
  });

  it('should update the profile with only the filled fields', () => {
    component.ngOnInit();
    const updated = { ...profile, username: 'johnny' };
    userService.updateProfile.mockReturnValue(of(updated));

    component.form.patchValue({
      email: 'john@test.com',
      username: 'johnny',
      bio: '',
      password: '',
    });
    component.onSubmit();

    expect(userService.updateProfile).toHaveBeenCalledWith(1, {
      email: 'john@test.com',
      username: 'johnny',
    });
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { reason: 'profile-updated' },
    });
    expect(component.isSaving).toBe(false);
  });

  it('should include the bio and password when provided', () => {
    component.ngOnInit();
    userService.updateProfile.mockReturnValue(of(profile));

    component.form.patchValue({
      email: 'john@test.com',
      username: 'john',
      bio: 'new bio',
      password: 'Abcdef1!',
    });
    component.onSubmit();

    expect(userService.updateProfile).toHaveBeenCalledWith(1, {
      email: 'john@test.com',
      username: 'john',
      bio: 'new bio',
      password: 'Abcdef1!',
    });
  });

  it('should set an error message when the update fails', () => {
    component.ngOnInit();
    userService.updateProfile.mockReturnValue(throwError(() => ({})));
    component.onSubmit();
    expect(component.errorMessage).toBe('Erreur lors de la mise à jour.');
    expect(component.isSaving).toBe(false);
  });

  it('should unsubscribe from a subject and refresh the list', () => {
    component.ngOnInit();
    subscriptionService.unsubscribe.mockReturnValue(of([]));

    component.unsubscribe(subscriptions[0]);

    expect(subscriptionService.unsubscribe).toHaveBeenCalledWith(2);
    expect(component.subscriptions).toEqual([]);
    expect(component.unsubscribingIds.has(2)).toBe(false);
  });

  it('should ignore an unsubscribe already in progress', () => {
    component.unsubscribingIds.add(2);
    component.unsubscribe(subscriptions[0]);
    expect(subscriptionService.unsubscribe).not.toHaveBeenCalled();
  });

  it('should clear the pending id when unsubscribe fails', () => {
    subscriptionService.unsubscribe.mockReturnValue(throwError(() => ({})));
    component.unsubscribe(subscriptions[0]);
    expect(component.unsubscribingIds.has(2)).toBe(false);
  });

  it('logout should clear auth and navigate home', () => {
    component.logout();
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
