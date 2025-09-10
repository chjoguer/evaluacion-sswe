import { inject, Injectable } from '@angular/core';
import { CustomHttp } from './custom-http';
import { catchError, Observable, of, map } from 'rxjs';
import { Client, ClientApiResponse, CreateClientDTO, UpdateClientDTO } from '../interfaces/client.interface';
import { NotificationService } from './notification-service';
import { environment } from '../enviroments/enviroments';

@Injectable({
  providedIn: 'root'
})
export class ClientService extends CustomHttp<any> {
   private endpoint = environment.path;
   private notificationService = inject(NotificationService);

  // ✅ Función de mapeo para transformar datos del API
  private mapApiResponseToClient(apiResponse: ClientApiResponse): Client {
    return {
      id: apiResponse.id,
      fullName: apiResponse.fullName,
      identification: apiResponse.identification,
      cellphone: apiResponse.cellphone,
      direction: apiResponse.direction,
      documentType: apiResponse.documentType.toString(),
      password: apiResponse.password,
      status: apiResponse.status,
      createdAt: apiResponse.createdAt,
      updatedAt: apiResponse.updatedAt
    };
  }

  // ✅ Método actualizado con mapeo
  getUsers(filters?: { name?: string; active?: boolean }): Observable<Client[]> {
    return this.get(this.endpoint, filters).pipe(
      map((apiResponse: ClientApiResponse[]) => 
        apiResponse.map(item => this.mapApiResponseToClient(item))
      ),
      catchError(error => {
        this.notificationService.showError('Error al cargar clientes');
        console.error('Error loading clients:', error);
        return of([]);
      })
    );
  }

  getClients(filters?: { name?: string; active?: boolean }): Observable<Client[]> {
    return this.getUsers(filters); // ✅ Reutilizar método principal
  }

  getUserById(id: string): Observable<Client | null> {
    return this.getById(this.endpoint, id).pipe(
      map((apiResponse: ClientApiResponse) => this.mapApiResponseToClient(apiResponse)),
      catchError(error => {
        this.notificationService.showError('Error al obtener cliente');
        console.error('Error getting client:', error);
        return of(null);
      })
    );
  }

  createUser(clientData: CreateClientDTO): Observable<Client | null> {
    return this.post(this.endpoint, clientData).pipe(
      map((apiResponse: ClientApiResponse) => this.mapApiResponseToClient(apiResponse)),
      catchError(error => {
        this.notificationService.showError('Error al crear cliente');
        console.error('Error creating client:', error);
        return of(null);
      })
    );
  }

  updateUser(identification: string, clientData: Partial<UpdateClientDTO>): Observable<Client | null> {
    return this.put(this.endpoint, identification, clientData).pipe(
      map((apiResponse: ClientApiResponse) => this.mapApiResponseToClient(apiResponse)),
      catchError(error => {
        this.notificationService.showError('Error al actualizar cliente');
        console.error('Error updating client:', error);
        return of(null);
      })
    );
  }

  deleteUser(identification: string): Observable<boolean> {
    return this.delete(this.endpoint, identification).pipe(
      map(() => true),
      catchError(error => {
        this.notificationService.showError('Error al eliminar cliente');
        console.error('Error deleting client:', error);
        return of(false);
      })
    );
  }

  // Métodos específicos si los necesitas
  getUsersByRole(role: string): Observable<Client[]> {
    return this.get(`${this.endpoint}/by-role`, { role }).pipe(
      map((apiResponse: ClientApiResponse[]) => 
        apiResponse.map(item => this.mapApiResponseToClient(item))
      ),
      catchError(error => {
        this.notificationService.showError('Error al obtener clientes por rol');
        console.error('Error getting clients by role:', error);
        return of([]);
      })
    );
  }
}
