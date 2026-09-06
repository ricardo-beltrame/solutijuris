export interface User {
  nome: string;
  email: string;
  role: string;
  fotoUrl: string | null;
}

export interface AuthResponse {
  token: string;
  nome: string;
  email: string;
  perfil: string;
  fotoUrl: string | null;
}

export interface LoginRequest {
  email: string;
  senha: string;
}
