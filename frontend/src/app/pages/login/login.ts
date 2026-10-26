import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router,RouterLink } from '@angular/router';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { Button } from 'primeng/button';
import { Message } from 'primeng/message';
import { Auth } from '../../services/auth';
import { ThemeService } from '../../services/theme-service';


@Component({
  selector: 'app-login',
  imports: [FormsModule, InputText, Password, Button, Message,RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  // values typed in the form (connected with [(ngModel)])
  username = '';
  password = '';

  // values the screen must react to → signals
  errorMessage = signal('');
  loading = signal(false);

  constructor(private auth: Auth, private router: Router, public themeService: ThemeService) {}

  onLogin(): void {
    // 1. simple check before calling the backend
    if (this.username === '' || this.password === '') {
      this.errorMessage.set('Please enter your Admin ID and password');
      return;
    }

    this.errorMessage.set('');
    this.loading.set(true);

    // 2. call the backend
    this.auth.login(this.username, this.password).subscribe({

      // 3a. success: save the token and open the User Management page
      next: (response) => {
        this.loading.set(false);
        this.auth.saveLogin(response);
        this.router.navigate([this.auth.homePage()]);
      },

      // 3b. failure: show a message
      error: (err) => {
        this.loading.set(false);
        if (err.status === 401) {
          this.errorMessage.set('Invalid username or password');
        } else if (err.status === 0) {
          this.errorMessage.set('Cannot reach the server. Is the backend running?');
        } else {
          this.errorMessage.set('Something went wrong. Please try again.');
        }
      }
    });
  }
}