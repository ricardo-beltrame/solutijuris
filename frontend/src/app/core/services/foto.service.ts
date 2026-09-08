import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PerfilUpdate } from '../auth/auth.models';

@Injectable({ providedIn: 'root' })
export class FotoService {
  private readonly http = inject(HttpClient);
  private readonly API = '/api/usuarios';

  uploadFoto(file: File): Observable<{ fotoUrl: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ fotoUrl: string }>(`${this.API}/foto`, formData);
  }

  removerFoto(): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.API}/foto`);
  }

  getFoto(): Observable<{ fotoUrl: string }> {
    return this.http.get<{ fotoUrl: string }>(`${this.API}/foto`);
  }

  getPerfil(): Observable<{ nome: string; email: string; telefone: string; fotoUrl: string }> {
    return this.http.get<{ nome: string; email: string; telefone: string; fotoUrl: string }>(
      `${this.API}/perfil`,
    );
  }

  atualizarPerfil(
    dados: PerfilUpdate,
  ): Observable<{ nome: string; telefone: string; message: string }> {
    return this.http.put<{ nome: string; telefone: string; message: string }>(
      `${this.API}/perfil`,
      dados,
    );
  }
}
