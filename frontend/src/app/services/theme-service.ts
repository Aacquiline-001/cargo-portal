import { Injectable, signal } from '@angular/core';

// Remembers light/dark mode and switches it.
// Dark mode = the class "app-dark" on the <html> element.
@Injectable({
  providedIn: 'root',
})
export class ThemeService {

  isDark = signal(true);   // dark is the default look

  constructor() {
    // use the choice saved last time (if any)
    const saved = localStorage.getItem('theme');
    if (saved === 'light') {
      this.isDark.set(false);
    }
    this.apply();
  }

  toggle(): void {
    this.isDark.set(!this.isDark());
    localStorage.setItem('theme', this.isDark() ? 'dark' : 'light');
    this.apply();
  }

  private apply(): void {
    const html = document.documentElement;   // the <html> tag
    if (this.isDark()) {
      html.classList.add('app-dark');
    } else {
      html.classList.remove('app-dark');
    }
  }
}