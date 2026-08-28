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
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProcessoService } from '../../core/services/processo.service';
import { Processo } from '../../core/models/processo.model';
import { ProcessoFormDialog } from './processo-form-dialog';
import { AuthService } from '../../core/auth/auth.service';

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
    MatCardModule,
    MatTooltipModule,
    MatDialogModule,
  ],
  templateUrl: './processos.html',
  styleUrls: ['./processos.css'],
})
export class Processos {
  private readonly dialog = inject(MatDialog);
  private readonly auth = inject(AuthService);
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

  readonly processosFiltrados = computed(() => {
    const termo = this.termoBusca().toLowerCase().trim();
    const lista = this.processos();
    if (!termo) return lista;

    return lista.filter(
      (p) =>
        p.numeroUnico.toLowerCase().includes(termo) ||
        p.areaDireito.toLowerCase().includes(termo) ||
        p.responsavel?.nomeCompleto?.toLowerCase().includes(termo) ||
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
    this.carregar();
  }

  limparBusca(): void {
    this.termoBusca.set('');
  }

  abrirNovoProcesso(): void {
    const dialogRef = this.dialog.open(ProcessoFormDialog, {
      width: '600px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.carregar();
      }
    });
  }

  formatarValor(valor?: number): string {
    if (!valor) return '-';
    return valor.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  }
}
