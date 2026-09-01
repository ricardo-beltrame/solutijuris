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
import { MatDialogModule } from '@angular/material/dialog';
import { ProcessoService } from '../../core/services/processo.service';
import { ProcessoRequest } from '../../core/models/processo.model';
import { AuthService } from '../../core/auth/auth.service';

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
  private readonly auth = inject(AuthService);

  readonly loading = signal(false);
  readonly erro = signal('');

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
    const data = inject(MAT_DIALOG_DATA);
    const responsavelId = data?.responsavelId ?? this.auth.user()?.['id'] ?? '';
    this.form.patchValue({ responsavelId });
  }

  salvar(): void {
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
      next: () => {
        this.loading.set(false);
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err?.error?.message ?? err?.message ?? 'Erro ao cadastrar processo.';
        this.erro.set(msg);
      },
    });
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
}
