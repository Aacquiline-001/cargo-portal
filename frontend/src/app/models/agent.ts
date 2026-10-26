// What the backend SENDS us (matches AgentResponse.java)
export interface Agent {
  agentPk: number;
  loginId: string;       // "User ID" — the login name, e.g. Agent50
  userName: string;      // display name, e.g. Jissy S Sabu
  language: string;
  emailId: string;
  userType: string;
  privilege: string;
  agentCode: string;
  companyName: string;
  address: string;
  telephone: string;
  active: boolean;
  createdAt: string;
}

// What WE SEND for Add / Update (matches AgentRequest.java)
export interface AgentRequest {
  loginId: string;
  userName: string;
  language: string;
  emailId: string;
  userType: string;
  privilege: string;
  agentCode: string;
  companyName: string;
  address: string;
  telephone: string;
  active: boolean;
}