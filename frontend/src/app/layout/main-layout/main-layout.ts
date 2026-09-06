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

  readonly navItems = [
    { path: '/dashboard', label: 'Dashboard', icon: 'dashboard' },
    { path: '/processos', label: 'Processos', icon: 'gavel' },
    { path: '/prazos', label: 'Prazos', icon: 'event' },
  ];

  constructor() {
    this.breakpoints.observe([Breakpoints.Handset]).subscribe({
      next: (result) => {
        this.isMobile.set(result.matches);
      },
    });

    // Carrega foto do backend ao iniciar
    this.fotoService.getFoto().subscribe({
      next: (res) => {
        if (res.fotoUrl) this.userFoto.set(res.fotoUrl);
      },
      error: () => {
        // Tenta pegar do User do AuthService
        const fotoFromUser = this.auth.user()?.fotoUrl;
        if (fotoFromUser) this.userFoto.set(fotoFromUser);
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
    setTimeout(() => {
      this.showModalFoto.set(true);
    });
  }

  fecharModalFoto(): void {
    this.showModalFoto.set(false);
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
      next: () => {
        this.userFoto.set(null);
      },
      error: (err) => {
        console.error('Erro ao remover foto:', err);
      },
    });
  }
}
