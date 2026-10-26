import { HttpInterceptorFn } from '@angular/common/http';

// Runs before EVERY request Angular sends to the backend.
// If we have a token, it adds:  Authorization: Bearer <token>
export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const token = localStorage.getItem('token');

  if (token) {
    const requestWithToken = req.clone({
      setHeaders: { Authorization: 'Bearer ' + token }
    });
    return next(requestWithToken);
  }

  return next(req);
};