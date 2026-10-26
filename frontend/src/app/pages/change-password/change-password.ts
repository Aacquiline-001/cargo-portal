import { Component, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Password } from 'primeng/password';
import { Button } from 'primeng/button';
import { Message } from 'primeng/message';
import { AccountService } from '../../services/account-service';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-change-password',
  imports: [FormsModule, Password, Button, Message],
  templateUrl: './change-password.html',
  styleUrl: './change-password.css',
})
export class ChangePassword {

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';

  // true when the user signed in with a temporary/default password
  forced = false;

  saving = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  constructor(private accountService: AccountService, private auth: Auth, private router: Router) {
    this.forced = this.auth.mustChangePassword();
  }

  save(form: NgForm): void {
    this.successMessage.set('');
    this.errorMessage.set('');

    if (form.invalid) {
      form.form.markAllAsTouched();
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage.set('The two new passwords do not match');
      return;
    }

    this.saving.set(true);
    this.accountService.changePassword(this.currentPassword, this.newPassword).subscribe({
      next: () => {
        this.saving.set(false);
        this.auth.passwordChanged();          // no longer "must change"

        if (this.forced) {
          // first real password chosen → go to the normal home page
          this.router.navigate([this.auth.homePage()]);
          return;
        }
        this.successMessage.set('Password changed. Use the new password next time you sign in.');
        form.resetForm();                     // empty the three boxes
      },
      error: (err) => {
        this.saving.set(false);
        if (err.error && err.error.fieldErrors) {
          this.errorMessage.set(Object.values(err.error.fieldErrors).join(', '));
        } else if (err.error && err.error.message) {
          this.errorMessage.set(err.error.message);      // e.g. "Current password is incorrect"
        } else {
          this.errorMessage.set('Something went wrong. Please try again.');
        }
      }
    });
  }

  goBack(): void {
    this.router.navigate([this.auth.homePage()]);
  }

  signOut(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}