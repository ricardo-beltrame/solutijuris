import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Prazo, PrazoRequest } from '../models/prazo.model';

@Injectable({ providedIn: 'root' })
export class PrazoService {
  private readonly API = '/api/prazos';

  constructor(private http: HttpClient) {}

  listarPorProcesso(processoId: string): Observable<Prazo[]> {
    return this.http.get<Prazo[]>(`${this.API}/processo/${processoId}`);
  }

  listarPorResponsavel(responsavelId: string): Observable<Prazo[]> {
    return this.http.get<Prazo[]>(`${this.API}/responsavel/${responsavelId}`);
  }

  listarVencidos(): Observable<Prazo[]> {
    return this.http.get<Prazo[]>(`${this.API}/vencidos`);
  }

  listarPorPeriodo(inicio: string, fim: string): Observable<Prazo[]> {
    return this.http.get<Prazo[]>(`${this.API}/periodo`, {
      params: { inicio, fim },
    });
  }

  cadastrar(request: PrazoRequest): Observable<Prazo> {
    return this.http.post<Prazo>(this.API, request);
  }

  cumprir(id: string): Observable<Prazo> {
    return this.http.patch<Prazo>(`${this.API}/${id}/cumprir`, {});
  }
}
