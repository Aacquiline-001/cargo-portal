import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { providePrimeNG } from 'primeng/config';

import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth-interceptor';
import { CargoTheme } from './theme';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    providePrimeNG({
      theme: {
        preset: CargoTheme,
        options: {
          darkModeSelector: '.app-dark',   // dark mode when <html class="app-dark">
          cssLayer: {
            name: 'primeng',               // our own CSS always wins over PrimeNG's
            order: 'primeng'
          }
        }
      }
    })
  ]
};