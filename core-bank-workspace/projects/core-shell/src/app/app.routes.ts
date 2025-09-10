import { loadRemoteModule } from '@angular-architects/native-federation';
import { Routes } from '@angular/router';
import { GeneralLayoutComponent } from './layouts/general-layout/general-layout.component';

export const routes: Routes = [
  {path: '', component: GeneralLayoutComponent, pathMatch: 'full'},
  {
    path: 'mfe1',
    component: GeneralLayoutComponent,
    loadChildren: () =>
      loadRemoteModule({
        remoteEntry: 'http://localhost:4201/remoteEntry.json',
        remoteName: 'client-mfe',
        exposedModule: './Routes'
      }).then(m => m.routes).catch(err => { console.error('Error loading remote routes', err); return []; })
  },
  {
    path: 'cuentas',
    component: GeneralLayoutComponent,
    loadChildren: () =>
      loadRemoteModule({
        remoteEntry: 'http://localhost:4203/remoteEntry.json',
        remoteName: 'account-mfe',
        exposedModule: './Routes'
      }).then(m => m.routes).catch(err => { console.error('Error loading remote routes', err); return []; })
  
  },
  {
    path: 'movimientos', 
    component: GeneralLayoutComponent,
     loadChildren: () =>
      loadRemoteModule({
        remoteEntry: 'http://localhost:4202/remoteEntry.json',
        remoteName: 'movment-mfe',
        exposedModule: './Routes'
      }).then(m => m.routes).catch(err => { console.error('Error loading remote routes', err); return []; })
  },
  {
    path: 'reportes',
    component: GeneralLayoutComponent
  }
];
