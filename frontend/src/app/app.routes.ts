import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { UserManagement } from './pages/user-management/user-management';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { ChangePassword } from './pages/change-password/change-password';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  // open to everyone
  { path: 'login', component: Login },
  { path: 'forgot-password', component: ForgotPassword },

  // must be signed in
  { path: 'user-management', component: UserManagement, canActivate: [authGuard] },
  { path: 'change-password', component: ChangePassword, canActivate: [authGuard] },

  // these two must ALWAYS be last
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];