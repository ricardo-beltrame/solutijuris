import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-redefinir-senha',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './redefinir-senha.html',
  styleUrl: './redefinir-senha.css',
})
export class RedefinirSenha implements OnInit {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  token = signal<string | null>(null);
  loading = signal(false);
  sucesso = signal(false);
  erro = signal<string | null>(null);
  hideSenha = true;

  form = this.fb.group({
    novaSenha: ['', [Validators.required, Validators.minLength(6)]],
    confirmarSenha: ['', [Validators.required]],
  });

  ngOnInit() {
    this.token.set(this.route.snapshot.queryParamMap.get('token'));

    if (!this.token()) {
      this.erro.set('Token não encontrado. Solicite uma nova recuperação de senha.');
    }
  }

  redefinir() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const nova = this.form.value.novaSenha!;
    const confirmar = this.form.value.confirmarSenha!;

    if (nova !== confirmar) {
      this.erro.set('As senhas não coincidem.');
      return;
    }

    if (!this.token()) {
      this.erro.set('Token inválido. Solicite uma nova recuperação.');
      return;
    }

    this.loading.set(true);
    this.erro.set(null);

    this.auth.redefinirSenha(this.token()!, nova).subscribe({
      next: () => {
        this.loading.set(false);
        this.sucesso.set(true);
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.loading.set(false);
        this.erro.set(err.error?.message || 'Erro ao redefinir senha. Tente novamente.');
      },
    });
  }
}
