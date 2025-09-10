import { Component, signal } from '@angular/core';
import { HeaderComponent } from '../../components/header/header.component';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { FooterComponent } from '../../components/footer.component/footer.component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-general-layout',
  imports: [HeaderComponent,SidebarComponent,RouterOutlet],
  templateUrl: './general-layout.component.html',
  styleUrl: './general-layout.component.css'
})
export class GeneralLayoutComponent {
  protected readonly title = signal('core-shell');

}
