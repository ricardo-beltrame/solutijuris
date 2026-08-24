import { Component, inject, signal, computed } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DashboardService } from '../../core/services/dashboard.service';
import { PrazoService } from '../../core/services/prazo.service';
import { DashboardDTO } from '../../core/models/dashboard.model';
import { Prazo } from '../../core/models/prazo.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatCardModule, MatIconModule, MatTableModule, MatProgressSpinnerModule, DatePipe],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
})
export class Dashboard {
  private readonly dashboardService = inject(DashboardService);
  private readonly prazoService = inject(PrazoService);

  // Estado reativo com signals
  readonly indicadores = signal<DashboardDTO | null>(null);
  readonly prazosProximos = signal<Prazo[]>([]);
  readonly loading = signal<boolean>(true);
  readonly erro = signal<string>('');

  // Colunas da tabela
  readonly colunas = ['descricao', 'processo', 'vencimento', 'status'];

  // Cards derivados dos indicadores
  readonly cards = computed(() => {
    const dados = this.indicadores();
    if (!dados) return [];
    return [
      { label: 'Processos Ativos', valor: dados.processosAtivos, icone: 'gavel', cor: 'primary' },
      { label: 'Prazos Vencidos', valor: dados.prazosVencidos, icone: 'warning', cor: 'warn' },
      { label: 'Prazos Abertos', valor: dados.prazosAbertos, icone: 'event', cor: 'accent' },
      { label: 'Advogados', valor: dados.totalUsuarios, icone: 'people', cor: 'primary' },
    ];
  });

  constructor() {
    this.carregarDados();
  }

  private carregarDados(): void {
    this.loading.set(true);
    this.erro.set('');

    // Calcula período: hoje + 7 dias
    const hoje = new Date().toISOString().split('T')[0];
    const semana = new Date();
    semana.setDate(semana.getDate() + 7);
    const fimSemana = semana.toISOString().split('T')[0];

    // Carrega indicadores e prazos em paralelo
    this.dashboardService.obterIndicadores().subscribe({
      next: (dados) => {
        this.indicadores.set(dados);
      },
      error: () => {
        this.erro.set('Erro ao carregar indicadores.');
        this.loading.set(false);
      },
    });

    this.prazoService.listarPorPeriodo(hoje, fimSemana).subscribe({
      next: (prazos) => {
        this.prazosProximos.set(prazos);
        this.loading.set(false);
      },
      error: () => {
        // Se falhar prazos, ainda mostra indicadores
        this.loading.set(false);
      },
    });
  }

  recarregar(): void {
    this.carregarDados();
  }
}
