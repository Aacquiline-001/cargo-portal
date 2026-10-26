import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ForgotPasswordResponse, MessageResponse } from '../models/account';

@Injectable({
  providedIn: 'root',
})
export class AccountService {

  private authUrl = 'http://localhost:8080/api/auth';
  private accountUrl = 'http://localhost:8080/api/account';

  constructor(private http: HttpClient) {}

  // ---------- no login needed ----------

  // SCREEN 1 — POST /api/auth/forgot-password  → makes and "sends" an OTP
  forgotPassword(username: string, emailId: string): Observable<ForgotPasswordResponse> {
    return this.http.post<ForgotPasswordResponse>(this.authUrl + '/forgot-password',
      { username: username, emailId: emailId });
  }

  // SCREEN 2 — POST /api/auth/reset-password  → OTP + new password
  resetPassword(username: string, otp: string, newPassword: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(this.authUrl + '/reset-password',
      { username: username, otp: otp, newPassword: newPassword });
  }

  // ---------- login needed (the interceptor adds the token) ----------

  // POST /api/account/change-password
  changePassword(currentPassword: string, newPassword: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(this.accountUrl + '/change-password',
      { currentPassword: currentPassword, newPassword: newPassword });
  }
}