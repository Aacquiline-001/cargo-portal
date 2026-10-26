import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

// Protects a page: only logged-in users may open it.
export const authGuard: CanActivateFn = (route, state) => {

  const router = inject(Router);
  const token = localStorage.getItem('token');

  // 1. not logged in → login page
  if (!token) {
    return router.createUrlTree(['/login']);
  }

  // 2. signed in with a temporary password → must choose a new one first
  const mustChange = localStorage.getItem('mustChangePassword') === 'true';
  if (mustChange && !state.url.startsWith('/change-password')) {
    return router.createUrlTree(['/change-password']);
  }

  // 3. everything fine → open the page
  return true;
};