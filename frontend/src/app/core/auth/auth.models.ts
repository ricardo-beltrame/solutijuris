export interface User {
  nome: string;
  email: string;
  role: string;
  telefone: string;
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

export interface PerfilUpdate {
  nomeCompleto: string;
  telefone: string;
  senhaAtual?: string;
  novaSenha?: string;
}
