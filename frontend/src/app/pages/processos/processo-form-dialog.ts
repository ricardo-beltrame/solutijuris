import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProcessoService } from '../../core/services/processo.service';
import { ProcessoRequest } from '../../core/models/processo.model';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-processo-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
    MatProgressSpinnerModule,
    MatDialogModule,
  ],
  templateUrl: './processo-form-dialog.html',
  styleUrls: ['./processo-form-dialog.css'],
})
export class ProcessoFormDialog {
  private readonly fb = inject(FormBuilder);
  private readonly processoService = inject(ProcessoService);
  private readonly dialogRef = inject(MatDialogRef<ProcessoFormDialog>);

  readonly loading = signal(false);
  readonly erro = signal('');

  // Enums para os selects
  readonly areasDireito = [
    'CIVEL',
    'PENAL',
    'TRABALHISTA',
    'TRIBUTARIO',
    'PREVIDENCIARIO',
    'FAMILIA',
    'EMPRESARIAL',
    'CONSUMIDOR',
    'ADMINISTRATIVO',
    'CONSTITUCIONAL',
    'TRABALHISTA_EMPRESA',
    'IMOBILIARIO',
    'DIGITAL',
    'TRABALHISTA_SINDICAL',
    'SAUDE',
  ];

  readonly tribunais = [
    'STF',
    'STJ',
    'TST',
    'TSE',
    'STM',
    'TJ_AC',
    'TJ_AL',
    'TJ_AP',
    'TJ_AM',
    'TJ_BA',
    'TJ_CE',
    'TJ_DF',
    'TJ_ES',
    'TJ_GO',
    'TJ_MA',
    'TJ_MT',
    'TJ_MS',
    'TJ_MG',
    'TJ_PA',
    'TJ_PB',
    'TJ_PR',
    'TJ_PE',
    'TJ_PI',
    'TJ_RJ',
    'TJ_RN',
    'TJ_RS',
    'TJ_RO',
    'TJ_RR',
    'TJ_SC',
    'TJ_SP',
    'TJ_SE',
    'TJ_TO',
    'TRF_1',
    'TRF_2',
    'TRF_3',
    'TRF_4',
    'TRF_5',
    'TRF_6',
  ];

  readonly form = this.fb.group({
    numeroUnico: [
      '',
      [Validators.required, Validators.pattern(/^\d{7}-\d{2}\.\d{4}\.\d\.\d{2}\.\d{4}$/)],
    ],
    areaDireito: ['', Validators.required],
    tribunal: ['', Validators.required],
    vara: ['', Validators.required],
    assunto: [''],
    dataDistribuicao: [null as any],
    segredoJustica: [false],
    valorCausa: [null as any],
    poloAtivo: [''],
    poloPassivo: [''],
    responsavelId: [{ value: '', disabled: true }],
  });

  constructor() {
    // Recebe o responsavelId injetado via MAT_DIALOG_DATA
    const data = inject(MAT_DIALOG_DATA);
    this.form.patchValue({ responsavelId: data?.responsavelId ?? '' });
  }

  salvar(): void {
    console.log('=== SALVAR CHAMADO ===');
    console.log('Form válido?', this.form.valid);
    console.log('Valores:', this.form.getRawValue());
    Object.keys(this.form.controls).forEach((key) => {
      const c = this.form.get(key);
      if (c?.invalid) {
        console.log(`Campo inválido: ${key}`, c.errors);
      }
    });
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.erro.set('');

    const raw = this.form.getRawValue();
    const request: ProcessoRequest = {
      numeroUnico: raw.numeroUnico!,
      areaDireito: raw.areaDireito!,
      tribunal: raw.tribunal!,
      vara: raw.vara!,
      assunto: raw.assunto || undefined,
      dataDistribuicao: raw.dataDistribuicao
        ? new Date(raw.dataDistribuicao).toISOString().split('T')[0]
        : undefined,
      segredoJustica: raw.segredoJustica ?? false,
      valorCausa: raw.valorCausa ? Number(raw.valorCausa) : undefined,
      poloAtivo: raw.poloAtivo || undefined,
      poloPassivo: raw.poloPassivo || undefined,
      responsavelId: raw.responsavelId!,
    };

    this.processoService.cadastrar(request).subscribe({
      next: (processo) => {
        this.loading.set(false);
        this.dialogRef.close(processo);
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err?.error?.message ?? err?.error ?? 'Erro ao cadastrar processo.';
        this.erro.set(typeof msg === 'string' ? msg : 'Erro ao cadastrar processo.');
      },
    });
  }

  cancelar(): void {
    this.dialogRef.close();
  }
}
