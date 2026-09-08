import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AuthService } from '../../core/auth/auth.service';
import { FotoService } from '../../core/services/foto.service';
import { PerfilUpdate } from '../../core/auth/auth.models';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatDividerModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './main-layout.html',
  styleUrls: ['./main-layout.css'],
})
export class MainLayoutComponent {
  private readonly auth = inject(AuthService);
  private readonly breakpoints = inject(BreakpointObserver);
  private readonly fotoService = inject(FotoService);

  readonly isDark = signal(false);
  readonly isMobile = signal(false);
  readonly userFoto = signal<string | null>(null);
  readonly showModalFoto = signal(false);
  readonly uploading = signal(false);
  readonly saving = signal(false);
  readonly showSenhaFields = signal(false);
  private pendingAction: string | null = null;

  // Form signals
  readonly formNome = signal('');
  readonly formTelefone = signal('');
  readonly formSenhaAtual = signal('');
  readonly formNovaSenha = signal('');

  readonly navItems = [
    { path: '/dashboard', label: 'Dashboard', icon: 'dashboard' },
    { path: '/processos', label: 'Processos', icon: 'gavel' },
    { path: '/prazos', label: 'Prazos', icon: 'event' },
  ];

  constructor() {
    this.breakpoints.observe([Breakpoints.Handset]).subscribe({
      next: (result) => this.isMobile.set(result.matches),
    });

    this.carregarPerfil();
  }

  get userEmail(): string {
    return this.auth.user()?.email ?? '';
  }

  private carregarPerfil(): void {
    this.fotoService.getPerfil().subscribe({
      next: (res) => {
        this.formNome.set(res.nome);
        this.formTelefone.set(res.telefone || '');
        if (res.fotoUrl) this.userFoto.set(res.fotoUrl);
      },
      error: () => {
        const user = this.auth.user();
        if (user) {
          this.formNome.set(user.nome);
          this.formTelefone.set(user.telefone || '');
          if (user.fotoUrl) this.userFoto.set(user.fotoUrl);
        }
      },
    });
  }

  get userNome(): string {
    return this.auth.user()?.nome ?? 'Usuário';
  }
  get userRole(): string {
    return this.auth.user()?.role ?? '';
  }
  get iniciais(): string {
    const nome = this.userNome.trim();
    if (!nome) return '?';
    const partes = nome.split(' ');
    if (partes.length === 1) return partes[0].substring(0, 2).toUpperCase();
    return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
  }

  toggleTheme(): void {
    const dark = !this.isDark();
    this.isDark.set(dark);
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light');
  }

  logout(): void {
    this.auth.logout();
  }

  abrirModalFoto(): void {
    this.pendingAction = 'abrirModal';
  }

  onMenuClosed(): void {
    if (this.pendingAction === 'abrirModal') {
      this.showModalFoto.set(true);
    }
    this.pendingAction = null;
  }

  fecharModalFoto(): void {
    this.showModalFoto.set(false);
    this.showSenhaFields.set(false);
    this.formSenhaAtual.set('');
    this.formNovaSenha.set('');
  }

  toggleSenhaFields(): void {
    this.showSenhaFields.update((v) => !v);
    if (!this.showSenhaFields()) {
      this.formSenhaAtual.set('');
      this.formNovaSenha.set('');
    }
  }

  onFotoSelecionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.uploading.set(true);
      this.fotoService.uploadFoto(file).subscribe({
        next: (res) => {
          this.userFoto.set(res.fotoUrl);
          this.uploading.set(false);
        },
        error: (err) => {
          console.error('Erro ao enviar foto:', err);
          this.uploading.set(false);
        },
      });
    }
  }

  removerFoto(): void {
    this.fotoService.removerFoto().subscribe({
      next: () => this.userFoto.set(null),
      error: (err) => console.error('Erro ao remover foto:', err),
    });
  }

  salvarPerfil(): void {
    this.saving.set(true);

    const dados: PerfilUpdate = {
      nomeCompleto: this.formNome(),
      telefone: this.formTelefone(),
    };

    if (this.showSenhaFields() && this.formSenhaAtual() && this.formNovaSenha()) {
      dados.senhaAtual = this.formSenhaAtual();
      dados.novaSenha = this.formNovaSenha();
    }

    this.fotoService.atualizarPerfil(dados).subscribe({
      next: (res) => {
        this.saving.set(false);
        this.showSenhaFields.set(false);
        this.formSenhaAtual.set('');
        this.formNovaSenha.set('');
        // Atualiza o nome exibido no header
        // TODO: atualizar o AuthService.user() se necessário
      },
      error: (err) => {
        console.error('Erro ao salvar perfil:', err);
        this.saving.set(false);
      },
    });
  }
}
