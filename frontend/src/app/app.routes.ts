import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login/login').then((m) => m.Login) },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'processos',
        loadComponent: () => import('./pages/processos/processos').then((m) => m.Processos),
      },
      {
        path: 'prazos',
        loadComponent: () => import('./pages/prazos/prazos').then((m) => m.Prazos),
      },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
