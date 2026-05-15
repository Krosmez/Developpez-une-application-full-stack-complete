import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [guestGuard]
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/auth/login/login.component').then(m => m.LoginComponent),
    canActivate: [guestGuard]
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/auth/register/register.component').then(m => m.RegisterComponent),
    canActivate: [guestGuard]
  },
  {
    path: 'feed',
    loadComponent: () => import('./pages/feed/feed.component').then(m => m.FeedComponent),
    canActivate: [authGuard]
  },
  {
    path: 'posts/new',
    loadComponent: () => import('./pages/posts/post-new/post-new.component').then(m => m.PostNewComponent),
    canActivate: [authGuard]
  },
  {
    path: 'posts/:id',
    loadComponent: () => import('./pages/posts/post-detail/post-detail.component').then(m => m.PostDetailComponent),
    canActivate: [authGuard]
  },
  {
    path: 'topics',
    loadComponent: () => import('./pages/topics/topics.component').then(m => m.TopicsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '' }
];
