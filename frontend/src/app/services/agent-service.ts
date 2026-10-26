import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Agent, AgentRequest } from '../models/agent';

@Injectable({
  providedIn: 'root',
})
export class AgentService {

  private apiUrl = 'http://localhost:8080/api/agents';

  constructor(private http: HttpClient) {}

  // GET /api/agents?loginId=..&privilege=..&agentCode=..
  search(loginId: string, privilege: string, agentCode: string): Observable<Agent[]> {
    const params = new HttpParams()
      .set('loginId', loginId)
      .set('privilege', privilege)
      .set('agentCode', agentCode);
    return this.http.get<Agent[]>(this.apiUrl, { params: params });
  }

  // POST /api/agents
  create(request: AgentRequest): Observable<Agent> {
    return this.http.post<Agent>(this.apiUrl, request);
  }

  // PUT /api/agents/3
  update(agentPk: number, request: AgentRequest): Observable<Agent> {
    return this.http.put<Agent>(this.apiUrl + '/' + agentPk, request);
  }

  // DELETE /api/agents/3  (backend answers with plain text)
  delete(agentPk: number): Observable<string> {
    return this.http.delete(this.apiUrl + '/' + agentPk, { responseType: 'text' });
  }
}