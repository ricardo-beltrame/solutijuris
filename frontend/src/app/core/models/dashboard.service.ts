import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardDTO } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly API = '/api/dashboard';

  constructor(private http: HttpClient) {}

  obterIndicadores(): Observable<DashboardDTO> {
    return this.http.get<DashboardDTO>(this.API);
  }

  obterDistribuicao(): Observable<Record<string, number>> {
    return this.http.get<Record<string, number>>(`${this.API}/distribuicao`);
  }
}
