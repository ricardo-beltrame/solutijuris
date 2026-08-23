import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatCardModule, MatIconModule, MatButtonModule, MatTableModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
})
export class Dashboard {
  // Placeholder — depois conectamos ao DashboardService do backend
  indicadores = [
    { label: 'Processos Ativos', valor: 12, icone: 'gavel', cor: 'primary' },
    { label: 'Prazos Vencidos', valor: 3, icone: 'warning', cor: 'warn' },
    { label: 'Prazos da Semana', valor: 7, icone: 'event', cor: 'accent' },
    { label: 'Advogados', valor: 4, icone: 'people', cor: 'primary' },
  ];

  prazosProximos = [
    {
      descricao: 'Contestar',
      processo: '1234567-89.2024.8.26.0100',
      vencimento: '25/08/2026',
      status: 'ABERTO',
    },
    {
      descricao: 'Apresentar réplica',
      processo: '9876543-21.2024.8.26.0100',
      vencimento: '27/08/2026',
      status: 'ABERTO',
    },
    {
      descricao: 'Recurso',
      processo: '1122334-45.2024.8.26.0100',
      vencimento: '30/08/2026',
      status: 'PENDENTE',
    },
  ];
}
