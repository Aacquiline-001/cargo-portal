// Simple { "message": "..." } answer from the backend
export interface MessageResponse {
  message: string;
}

// Answer of "Send OTP". demoOtp is filled only when app.otp.delivery=screen
export interface ForgotPasswordResponse {
  message: string;
  demoOtp: string | null;
}