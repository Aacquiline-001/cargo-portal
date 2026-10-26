import { Component, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { Button } from 'primeng/button';
import { Message } from 'primeng/message';
import { AccountService } from '../../services/account-service';

@Component({
  selector: 'app-forgot-password',
  imports: [FormsModule, RouterLink, InputText, Password, Button, Message],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {

  // which screen is showing: 1 details → 2 otp → 3 done
  step = signal<'details' | 'otp' | 'done'>('details');

  // screen 1
  username = '';
  emailId = '';

  // screen 2
  otp = '';
  newPassword = '';
  confirmPassword = '';

  demoOtp = signal<string | null>(null);   // the OTP shown on screen (demo mode only)
  sending = signal(false);
  saving = signal(false);
  errorMessage = signal('');
  infoMessage = signal('');

  constructor(private accountService: AccountService) {}

  // ---------- SCREEN 1: Send OTP ----------
  sendOtp(form: NgForm): void {
    if (form.invalid) {
      form.form.markAllAsTouched();
      return;
    }
    this.requestOtp();
  }

  // also used by "Resend OTP" on screen 2
  requestOtp(): void {
    this.sending.set(true);
    this.errorMessage.set('');
    this.infoMessage.set('');

    this.accountService.forgotPassword(this.username, this.emailId).subscribe({
      next: (response) => {
        this.sending.set(false);
        this.demoOtp.set(response.demoOtp);
        this.otp = '';
        if (!response.demoOtp) {
          this.infoMessage.set(response.message);
        }
        this.step.set('otp');                // go to screen 2
      },
      error: (err) => {
        this.sending.set(false);
        this.showError(err);                 // e.g. "Please wait a minute..."
      }
    });
  }

  // fills the OTP box with the code shown in the demo box
  useDemoOtp(): void {
    const code = this.demoOtp();
    if (code) {
      this.otp = code;
    }
  }

  // ---------- SCREEN 2: Reset password ----------
  resetPassword(form: NgForm): void {
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
    this.accountService.resetPassword(this.username, this.otp, this.newPassword).subscribe({
      next: () => {
        this.saving.set(false);
        this.step.set('done');               // go to screen 3
      },
      error: (err) => {
        this.saving.set(false);
        this.showError(err);                 // e.g. "Incorrect OTP. 4 tries left."
      }
    });
  }

  // back from screen 2 to screen 1 (to fix the User ID or email)
  changeDetails(): void {
    this.errorMessage.set('');
    this.infoMessage.set('');
    this.demoOtp.set(null);
    this.step.set('details');
  }

  private showError(err: any): void {
    if (err.status === 0) {
      this.errorMessage.set('Cannot reach the server. Is the backend running?');
    } else if (err.error && err.error.fieldErrors) {
      this.errorMessage.set(Object.values(err.error.fieldErrors).join(', '));
    } else if (err.error && err.error.message) {
      this.errorMessage.set(err.error.message);
    } else {
      this.errorMessage.set('Something went wrong. Please try again.');
    }
  }
}