import { initFederation } from '@angular-architects/native-federation';

initFederation({
  'client-mfe': 'http://localhost:4201/remoteEntry.json',
  'account-mfe': 'http://localhost:4203/remoteEntry.json',
  'movment-mfe': 'http://localhost:4202/remoteEntry.json'

})
  .catch(err => console.error(err))
  .then(_ => import('./bootstrap'))
  .catch(err => console.error(err));
