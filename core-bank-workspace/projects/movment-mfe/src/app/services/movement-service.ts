import { inject, Injectable } from '@angular/core';
import { CustomHttp } from './custom-http';
import { catchError, Observable, of, map } from 'rxjs';
import { Movement, MovementApiResponse, CreateMovementDTO, UpdateMovementDTO } from '../interfaces/movement.interface';
import { NotificationService } from './notification-service';
import { environment } from '../enviroments/enviroments';

@Injectable({
  providedIn: 'root'
})
export class MovementService extends CustomHttp<any> {
   private endpoint = environment.path;
   private notificationService = inject(NotificationService);

  // Función de mapeo para transformar datos del API
  private mapApiResponseToMovement(apiResponse: MovementApiResponse): Movement {
    return {
      id: apiResponse.id,
      uniqueKey: apiResponse.uniqueKey,
      accountId: apiResponse.accountId,
      occurredAt: apiResponse.occurredAt,
      movementType: apiResponse.movementType.toString(),
      amount: apiResponse.amount,
      balance: apiResponse.balance,
      description: apiResponse.description,
      reference: apiResponse.reference,
      createdAt: apiResponse.createdAt,
      updatedAt: apiResponse.updatedAt
    };
  }

  // Obtener todos los movimientos
  getMovements(filters?: { accountId?: number; movementType?: string; dateFrom?: string; dateTo?: string }): Observable<Movement[]> {
    return this.get(this.endpoint, filters).pipe(
      map((apiResponse: MovementApiResponse[]) => 
        apiResponse.map(item => this.mapApiResponseToMovement(item))
      ),
      catchError(error => {
        this.notificationService.showError('Error al cargar movimientos');
        console.error('Error loading movements:', error);
        return of([]);
      })
    );
  }

  // Obtener movimientos por cuenta
  getMovementsByAccount(accountId: number): Observable<Movement[]> {
    return this.getMovements({ accountId });
  }

  // Obtener movimiento por ID
  getMovementById(id: number): Observable<Movement | null> {
    return this.getById(this.endpoint, id).pipe(
      map((apiResponse: MovementApiResponse) => this.mapApiResponseToMovement(apiResponse)),
      catchError(error => {
        this.notificationService.showError('Error al cargar movimiento');
        console.error('Error loading movement:', error);
        return of(null);
      })
    );
  }

  // Crear nuevo movimiento
  createMovement(movementData: CreateMovementDTO): Observable<Movement | null> {
    return this.post(this.endpoint, movementData).pipe(
      map((apiResponse: MovementApiResponse) => this.mapApiResponseToMovement(apiResponse)),
      catchError(error => {
        this.notificationService.showError('Error al crear movimiento');
        console.error('Error creating movement:', error);
        return of(null);
      })
    );
  }

  // Actualizar movimiento
  updateMovement(id: number, movementData: UpdateMovementDTO): Observable<Movement | null> {
    return this.put(this.endpoint, id, movementData).pipe(
      map((apiResponse: MovementApiResponse) => this.mapApiResponseToMovement(apiResponse)),
      catchError(error => {
        this.notificationService.showError('Error al actualizar movimiento');
        console.error('Error updating movement:', error);
        return of(null);
      })
    );
  }

  // Eliminar movimiento
  deleteMovement(id: number): Observable<boolean> {
    return this.delete(this.endpoint, id).pipe(
      map(() => {
        this.notificationService.showSuccess('Movimiento eliminado correctamente');
        return true;
      }),
      catchError(error => {
        this.notificationService.showError('Error al eliminar movimiento');
        console.error('Error deleting movement:', error);
        return of(false);
      })
    );
  }

  // Buscar movimientos
  searchMovements(searchTerm: string): Observable<Movement[]> {
    // Por ahora implementamos una búsqueda simple obteniendo todos los movimientos
    // y filtrando en el frontend. En una implementación real, esto se haría en el backend
    return this.getMovements().pipe(
      map((movements: Movement[]) => 
        movements.filter(movement => 
          movement.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
          movement.reference.toLowerCase().includes(searchTerm.toLowerCase()) ||
          movement.movementType.toLowerCase().includes(searchTerm.toLowerCase())
        )
      )
    );
  }
}
