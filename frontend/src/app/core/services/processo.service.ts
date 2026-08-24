import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Processo, ProcessoRequest } from '../models/processo.model';

@Injectable({ providedIn: 'root' })
export class ProcessoService {
  private readonly API = '/api/processos';

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<Processo[]> {
    return this.http.get<Processo[]>(this.API);
  }

  buscarPorId(id: string): Observable<Processo> {
    return this.http.get<Processo>(`${this.API}/${id}`);
  }

  buscarPorNumero(numeroUnico: string): Observable<Processo> {
    return this.http.get<Processo>(`${this.API}/numero/${numeroUnico}`);
  }

  listarPorResponsavel(responsavelId: string): Observable<Processo[]> {
    return this.http.get<Processo[]>(`${this.API}/responsavel/${responsavelId}`);
  }

  cadastrar(request: ProcessoRequest): Observable<Processo> {
    return this.http.post<Processo>(this.API, request);
  }
}
