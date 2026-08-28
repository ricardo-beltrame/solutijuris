import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PrazoService } from '../../core/services/prazo.service';
import { Prazo } from '../../core/models/prazo.model';

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
    MatTooltipModule,
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
    } else if (filtro === 'proximos7') {
      this.prazoService.listarProximos(7).subscribe({
        next: (dados) => {
          this.prazos.set(dados);
          this.loading.set(false);
        },
        error: () => {
          this.erro.set('Erro ao carregar prazos próximos.');
          this.loading.set(false);
        },
      });
    } else if (filtro === 'proximos30') {
      this.prazoService.listarProximos(30).subscribe({
        next: (dados) => {
          this.prazos.set(dados);
          this.loading.set(false);
        },
        error: () => {
          this.erro.set('Erro ao carregar prazos próximos.');
          this.loading.set(false);
        },
      });
    } else {
      this.prazoService.listarTodos().subscribe({
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

  formatarData(data: string): string {
    return new Date(data).toLocaleDateString('pt-BR');
  }

  diasRestantes(dataVencimento: string): number {
    const data = new Date(dataVencimento);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const diff = data.getTime() - hoje.getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  diasRestantesAbs(dataVencimento: string): number {
    return Math.abs(this.diasRestantes(dataVencimento));
  }
}
