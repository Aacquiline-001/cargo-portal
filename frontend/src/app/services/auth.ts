import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// The shape of the JSON the backend sends back after login
export interface LoginResponse {
  token: string;
  username: string;
  role: string;
  mustChangePassword: boolean;   // true = signed in with a temporary/default password
}

@Injectable({
  providedIn: 'root',
})
export class Auth {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  // 1. Send username + password to the backend
  login(username: string, password: string): Observable<LoginResponse> {
    const body = { username: username, password: password };
    return this.http.post<LoginResponse>(this.apiUrl + '/login', body);
  }

  // 2. Remember the login in the browser
  saveLogin(response: LoginResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('username', response.username);
    localStorage.setItem('role', response.role);
    localStorage.setItem('mustChangePassword', String(response.mustChangePassword));
  }

  // 3. Read saved values (null if not logged in)
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  isAdmin(): boolean {
    return this.getRole() === 'BUSINESS_ADMIN';
  }

  // true while the user still has a temporary/default password
  mustChangePassword(): boolean {
    return localStorage.getItem('mustChangePassword') === 'true';
  }

  // call after a successful password change
  passwordChanged(): void {
    localStorage.setItem('mustChangePassword', 'false');
  }

  // the first page after login
  homePage(): string {
    if (this.mustChangePassword()) {
      return '/change-password';     // temporary password → choose a new one first
    }
    return '/user-management';
  }

  // 4. Forget everything
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    localStorage.removeItem('mustChangePassword');
  }
}