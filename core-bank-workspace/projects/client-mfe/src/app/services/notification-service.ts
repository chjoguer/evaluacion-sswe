import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  showSuccess(message: string): void {
    // Implementar toast/snackbar
    console.log('Success:', message);
  }

  showError(message: string): void {
    // Implementar toast/snackbar
    console.error('Error:', message);
  }

  showInfo(message: string): void {
    console.info('Info:', message);
  }
}