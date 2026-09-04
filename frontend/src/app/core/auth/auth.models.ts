export interface User {
  nome: string;
  email: string;
  role: string;
}

export interface AuthResponse {
  token: string;
  nome: string;
  email: string;
  perfil: string;
}

export interface LoginRequest {
  email: string;
  senha: string;
}
