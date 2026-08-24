import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { ProcessoService } from '../../core/services/processo.service';
import { Processo } from '../../core/models/processo.model';

@Component({
  selector: 'app-processos',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
  ],
  templateUrl: './processos.html',
  styleUrls: ['./processos.css'],
})
export class Processos {
  private readonly processoService = inject(ProcessoService);

  readonly processos = signal<Processo[]>([]);
  readonly loading = signal<boolean>(true);
  readonly erro = signal<string>('');
  readonly termoBusca = signal<string>('');

  readonly colunas = [
    'numeroUnico',
    'areaDireito',
    'tribunal',
    'vara',
    'status',
    'responsavel',
    'valorCausa',
  ];

  // Lista filtrada por busca
  readonly processosFiltrados = computed(() => {
    const termo = this.termoBusca().toLowerCase().trim();
    const lista = this.processos();

    if (!termo) return lista;

    return lista.filter(
      (p) =>
        p.numeroUnico.toLowerCase().includes(termo) ||
        p.areaDireito.toLowerCase().includes(termo) ||
        p.responsavel?.nomeCompleto.toLowerCase().includes(termo) ||
        p.poloAtivo?.toLowerCase().includes(termo) ||
        p.poloPassivo?.toLowerCase().includes(termo),
    );
  });

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.loading.set(true);
    this.erro.set('');

    this.processoService.listarTodos().subscribe({
      next: (dados) => {
        this.processos.set(dados);
        this.loading.set(false);
      },
      error: () => {
        this.erro.set('Erro ao carregar processos.');
        this.loading.set(false);
      },
    });
  }

  buscarPorNumero(): void {
    const termo = this.termoBusca().trim();

    if (!termo) {
      this.carregar();
      return;
    }

    // Se parece com CNJ (tem padrão NNNNNNN-NN.AAAA.J.NN.NNNN), busca exata
    if (termo.match(/^\d{7}-\d{2}\.\d{4}\.\d\.\d{2}\.\d{4}$/)) {
      this.loading.set(true);
      this.erro.set('');

      this.processoService.buscarPorNumero(termo).subscribe({
        next: (processo) => {
          this.processos.set([processo]);
          this.loading.set(false);
        },
        error: () => {
          this.erro.set(`Processo ${termo} não encontrado.`);
          this.processos.set([]);
          this.loading.set(false);
        },
      });
    }
    // Caso contrário, a lista filtrada pelo computed já cuida
  }

  limparBusca(): void {
    this.termoBusca.set('');
    this.carregar();
  }

  formatarValor(valor?: number): string {
    if (valor == null) return '-';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(valor);
  }

  formatarData(data?: string): string {
    if (!data) return '-';
    return new Date(data).toLocaleDateString('pt-BR');
  }
}
