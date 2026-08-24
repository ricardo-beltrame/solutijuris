import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { PrazoService } from '../../core/services/prazo.service';
import { Prazo } from '../../core/models/prazo.model';
import { MatCardModule } from '@angular/material/card';

type Filtro = 'todos' | 'vencidos' | 'proximos7' | 'proximos30';

@Component({
  selector: 'app-prazos',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatMenuModule,
    MatCardModule,
  ],
  templateUrl: './prazos.html',
  styleUrls: ['./prazos.css'],
})
export class Prazos {
  private readonly prazoService = inject(PrazoService);

  readonly prazos = signal<Prazo[]>([]);
  readonly loading = signal<boolean>(true);
  readonly erro = signal<string>('');
  readonly filtroAtual = signal<Filtro>('todos');

  readonly colunas = ['descricao', 'processo', 'responsavel', 'vencimento', 'status', 'acoes'];

  readonly filtros = [
    { key: 'todos' as Filtro, label: 'Todos', icone: 'list' },
    { key: 'vencidos' as Filtro, label: 'Vencidos', icone: 'warning' },
    { key: 'proximos7' as Filtro, label: 'Próximos 7 dias', icone: 'event' },
    { key: 'proximos30' as Filtro, label: 'Próximos 30 dias', icone: 'date_range' },
  ];

  // Contagem por status para badges nos filtros
  readonly countVencidos = computed(
    () => this.prazos().filter((p) => p.status === 'VENCIDO').length,
  );
  readonly countProximos7 = computed(() => {
    const hoje = new Date();
    const limite = new Date();
    limite.setDate(limite.getDate() + 7);
    return this.prazos().filter((p) => {
      const data = new Date(p.dataVencimento);
      return data >= hoje && data <= limite && p.status === 'ABERTO';
    }).length;
  });

  constructor() {
    this.carregar('todos');
  }

  carregar(filtro: Filtro): void {
    this.filtroAtual.set(filtro);
    this.loading.set(true);
    this.erro.set('');

    if (filtro === 'vencidos') {
      this.prazoService.listarVencidos().subscribe({
        next: (dados) => {
          this.prazos.set(dados);
          this.loading.set(false);
        },
        error: () => {
          this.erro.set('Erro ao carregar prazos vencidos.');
          this.loading.set(false);
        },
      });
    } else {
      // Para "todos", "proximos7" e "proximos30", usa listarPorPeriodo
      const hoje = new Date().toISOString().split('T')[0];
      let fim: string;

      if (filtro === 'proximos7') {
        const d = new Date();
        d.setDate(d.getDate() + 7);
        fim = d.toISOString().split('T')[0];
      } else if (filtro === 'proximos30') {
        const d = new Date();
        d.setDate(d.getDate() + 30);
        fim = d.toISOString().split('T')[0];
      } else {
        // "todos" — busca um período amplo (início do ano até +90 dias)
        const inicio = new Date(new Date().getFullYear(), 0, 1).toISOString().split('T')[0];
        const d = new Date();
        d.setDate(d.getDate() + 90);
        fim = d.toISOString().split('T')[0];
        this.buscarPorPeriodo(inicio, fim);
        return;
      }

      this.buscarPorPeriodo(hoje, fim);
    }
  }

  private buscarPorPeriodo(inicio: string, fim: string): void {
    this.prazoService.listarPorPeriodo(inicio, fim).subscribe({
      next: (dados) => {
        this.prazos.set(dados);
        this.loading.set(false);
      },
      error: () => {
        this.erro.set('Erro ao carregar prazos.');
        this.loading.set(false);
      },
    });
  }

  cumprirPrazo(prazo: Prazo, event: Event): void {
    event.stopPropagation();
    this.prazoService.cumprir(prazo.id).subscribe({
      next: (atualizado) => {
        const lista = this.prazos().map((p) => (p.id === atualizado.id ? atualizado : p));
        this.prazos.set(lista);
      },
      error: () => {
        this.erro.set('Erro ao cumprir prazo.');
      },
    });
  }

  formatarData(data?: string): string {
    if (!data) return '-';
    return new Date(data).toLocaleDateString('pt-BR');
  }

  diasRestantes(dataVencimento: string): number {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const data = new Date(dataVencimento);
    data.setHours(0, 0, 0, 0);
    const diff = data.getTime() - hoje.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  diasRestantesAbs(dataVencimento: string): number {
    return Math.abs(this.diasRestantes(dataVencimento));
  }
}
