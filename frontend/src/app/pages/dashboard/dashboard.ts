import { Component, inject, signal, computed } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { DashboardService } from '../../core/services/dashboard.service';
import { PrazoService } from '../../core/services/prazo.service';
import { DashboardDTO } from '../../core/models/dashboard.model';
import { Prazo } from '../../core/models/prazo.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatCardModule,
    MatIconModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    DatePipe,
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
})
export class Dashboard {
  private readonly dashboardService = inject(DashboardService);
  private readonly prazoService = inject(PrazoService);

  readonly loading = signal(true);
  readonly erro = signal('');
  readonly dados = signal<DashboardDTO | null>(null);
  readonly prazosProximos = signal<Prazo[]>([]);
  readonly colunas = ['descricao', 'vencimento', 'status'];

  readonly cards = computed(() => {
    const d = this.dados();
    if (!d) return [];
    return [
      {
        label: 'Processos Ativos',
        valor: d.processosAtivos ?? 0,
        icone: 'gavel',
        corClass: 'blue',
        trend: undefined as string | undefined,
        trendDir: undefined as string | undefined,
      },
      {
        label: 'Prazos Abertos',
        valor: d.prazosAbertos ?? 0,
        icone: 'event',
        corClass: 'amber',
        trend: undefined as string | undefined,
        trendDir: undefined as string | undefined,
      },
      {
        label: 'Prazos Vencidos',
        valor: d.prazosVencidos ?? 0,
        icone: 'warning',
        corClass: 'red',
        trend: undefined as string | undefined,
        trendDir: undefined as string | undefined,
      },
      {
        label: 'Prazos Cumpridos',
        valor: d.prazosCumpridos ?? 0,
        icone: 'check_circle',
        corClass: 'green',
        trend: undefined as string | undefined,
        trendDir: undefined as string | undefined,
      },
    ];
  });

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.loading.set(true);
    this.erro.set('');

    this.dashboardService.obterIndicadores().subscribe({
      next: (data) => {
        this.dados.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.erro.set('Erro ao carregar dados do dashboard.');
        this.loading.set(false);
      },
    });

    this.prazoService.listarProximos(7).subscribe({
      next: (prazos) => this.prazosProximos.set(prazos),
      error: () => {},
    });
  }

  diasRestantes(dataVencimento: string): number {
    const data = new Date(dataVencimento);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const diff = data.getTime() - hoje.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }
}
