import { inject, Injectable } from '@angular/core';
import { CustomHttp } from './custom-http';
import { catchError, Observable, of, map } from 'rxjs';
import { Account, AccountApiResponse, CreateAccountDTO, UpdateAccountDTO } from '../interfaces/account.interfacte';
import { NotificationService } from './notification-service';
import { environment } from '../enviroments/enviroments';

@Injectable({
  providedIn: 'root'
})
export class AccountService extends CustomHttp<any> {
  private endpoint = environment.path;
  private notificationService = inject(NotificationService);

  // Función para mapear la respuesta del API al formato interno
  private mapApiResponseToAccount(apiResponse: AccountApiResponse): Account {
    return {
      id: apiResponse.accountId,                // accountId del API -> id interno
      accountNumber: apiResponse.accountNumber, // accountNumber se mantiene igual
      accountType: apiResponse.accountType,     // accountType se mantiene igual
      balance: apiResponse.balance,             // balance se mantiene igual
      identification: apiResponse.identification, // identification se mantiene igual
      status: 'ACTIVE',                         // Agregamos status por defecto ya que no viene del API
      createdAt: apiResponse.createdAt          // createdAt se mantiene igual
    };
  }

  getAccounts(filters?: { clientReference?: number; status?: string }): Observable<Account[]> {
    return this.get(this.endpoint, filters).pipe(
      map((response: AccountApiResponse[]) => 
        response.map(account => this.mapApiResponseToAccount(account))
      ),
      catchError(error => {
        this.notificationService.showError('Error al cargar cuentas');
        console.error('Error loading accounts:', error);
        return of([]);
      })
    );
  }

  getAccountById(id: string): Observable<Account | null> {
    return this.getById(this.endpoint, id).pipe(
      map((response: any) => 
        response ? this.mapApiResponseToAccount(response) : null
      ),
      catchError(error => {
        this.notificationService.showError('Error al cargar cuenta');
        console.error('Error loading account:', error);
        return of(null);
      })
    );
  }

  getAccountByNumber(accountNumber: string): Observable<Account | null> {
    return this.get(`${this.endpoint}/by-number/${accountNumber}`).pipe(
      map((response: any) => 
        response ? this.mapApiResponseToAccount(response) : null
      ),
      catchError(error => {
        this.notificationService.showError('Error al buscar cuenta');
        console.error('Error finding account:', error);
        return of(null);
      })
    );
  }

  getAccountsByClient(clientReference: number): Observable<Account[]> {
    return this.get(`${this.endpoint}/by-client/${clientReference}`).pipe(
      map((response: any[]) => 
        response.map(account => this.mapApiResponseToAccount(account))
      ),
      catchError(error => {
        this.notificationService.showError('Error al cargar cuentas del cliente');
        console.error('Error loading client accounts:', error);
        return of([]);
      })
    );
  }

  createAccount(accountData: CreateAccountDTO): Observable<Account | null> {
    return this.post(this.endpoint, accountData).pipe(
      map((response: any) => 
        response ? this.mapApiResponseToAccount(response) : null
      ),
      catchError(error => {
        this.notificationService.showError('Error al crear cuenta');
        console.error('Error creating account:', error);
        return of(null);
      })
    );
  }

  updateAccount(id: string, accountData: UpdateAccountDTO): Observable<Account | null> {
    return this.put(this.endpoint, id, accountData).pipe(
      map((response: any) => 
        response ? this.mapApiResponseToAccount(response) : null
      ),
      catchError(error => {
        this.notificationService.showError('Error al actualizar cuenta');
        console.error('Error updating account:', error);
        return of(null);
      })
    );
  }

  deleteAccount(id: string): Observable<boolean> {
    return this.delete(this.endpoint, id).pipe(
      map(() => true),
      catchError(error => {
        this.notificationService.showError('Error al eliminar cuenta');
        console.error('Error deleting account:', error);
        return of(false);
      })
    );
  }

  // Métodos adicionales específicos para cuentas
  getAccountsByType(type: string): Observable<Account[]> {
    return this.get(`${this.endpoint}/by-type/${type}`).pipe(
      map((response: any[]) => 
        response.map(account => this.mapApiResponseToAccount(account))
      ),
      catchError(error => {
        this.notificationService.showError('Error al cargar cuentas por tipo');
        console.error('Error loading accounts by type:', error);
        return of([]);
      })
    );
  }

  updateBalance(accountId: string, newBalance: number): Observable<Account | null> {
    return this.put(`${this.endpoint}`, accountId, { currentBalance: newBalance }).pipe(
      map((response: any) => 
        response ? this.mapApiResponseToAccount(response) : null
      ),
      catchError(error => {
        this.notificationService.showError('Error al actualizar saldo');
        console.error('Error updating balance:', error);
        return of(null);
      })
    );
  }

  // Método para validar número de cuenta (verificar si ya existe)
  validateAccountNumber(accountNumber: string): Observable<boolean> {
    return this.get(`${this.endpoint}/validate/${accountNumber}`).pipe(
      map((response: any) => !response?.exists), // true si NO existe (válido)
      catchError(error => {
        console.error('Error validating account number:', error);
        return of(false);
      })
    );
  }
}
