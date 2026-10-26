// What the backend SENDS us (matches AdminResponse.java)
export interface Admin {
  adminPk: number;
  adminId: string;
  adminName: string;
  region: string;
  branch: string;
  language: string;
  employeeNumber: string;
  contactNumber: string;
  emailId: string;
  active: boolean;
  createdAt: string;
}

// What WE SEND to the backend for Add / Update (matches AdminRequest.java)
export interface AdminRequest {
  adminId: string;
  adminName: string;
  region: string;
  branch: string;
  language: string;
  employeeNumber: string;
  contactNumber: string;
  emailId: string;
  active: boolean;
}