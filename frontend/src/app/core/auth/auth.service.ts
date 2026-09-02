import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, User } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = '/api/auth';
  private readonly TOKEN_KEY = 'solutijuris_token';
  private readonly USER_KEY = 'solutijuris_user';

  // Estado reativo do usuário (signal do Angular 21)
  readonly user = signal<User | null>(this.getStoredUser());
  readonly isAuthenticated = signal<boolean>(!!this.getStoredToken());

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API}/login`, credentials).pipe(
      tap((res) => {
        this.setSession(res);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.user.set(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private setSession(res: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, res.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(res.user));
    this.user.set(res.user);
    this.isAuthenticated.set(true);
  }

  private getStoredUser(): User | null {
    const raw = localStorage.getItem(this.USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as User;
    } catch {
      localStorage.removeItem(this.USER_KEY);
      return null;
    }
  }

  private getStoredToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  solicitarRecuperacao(email: string): Observable<any> {
    return this.http.post('/api/auth/recuperar-senha', { email });
  }

  redefinirSenha(token: string, novaSenha: string): Observable<any> {
    return this.http.post('/api/auth/redefinir-senha', { token, novaSenha });
  }
}
