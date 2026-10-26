import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Admin, AdminRequest } from '../models/admin';

@Injectable({
  providedIn: 'root',
})
export class AdminService {

  private apiUrl = 'http://localhost:8080/api/admins';

  constructor(private http: HttpClient) {}

  // GET /api/admins?adminId=..&region=..&employeeNumber=..
  search(adminId: string, region: string, employeeNumber: string): Observable<Admin[]> {
    const params = new HttpParams()
      .set('adminId', adminId)
      .set('region', region)
      .set('employeeNumber', employeeNumber);
    return this.http.get<Admin[]>(this.apiUrl, { params: params });
  }

  // POST /api/admins
  create(request: AdminRequest): Observable<Admin> {
    return this.http.post<Admin>(this.apiUrl, request);
  }

  // PUT /api/admins/5
  update(adminPk: number, request: AdminRequest): Observable<Admin> {
    return this.http.put<Admin>(this.apiUrl + '/' + adminPk, request);
  }

  // DELETE /api/admins/5  (backend answers with plain text)
  delete(adminPk: number): Observable<string> {
    return this.http.delete(this.apiUrl + '/' + adminPk, { responseType: 'text' });
  }
}