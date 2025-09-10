import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

interface MenuItem {
  label: string;
  route: string;
  icon?: string;
}

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  
  readonly menuItems: MenuItem[] = [
    {
      label: 'Clientes',
      route: '/mfe1',
      icon: 'fas fa-users'
    },
    {
      label: 'Cuentas',
      route: '/cuentas',
      icon: 'fas fa-credit-card'
    },
    {
      label: 'Movimientos',
      route: '/movimientos',
      icon: 'fas fa-exchange-alt'
    },
    {
      label: 'Reportes',
      route: '/reportes',
      icon: 'fas fa-chart-bar'
    }
  ];

}
