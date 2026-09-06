import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
}
