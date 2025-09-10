import { Injectable, signal, computed } from '@angular/core';
import { Movement } from '../interfaces/movement.interface';

@Injectable()
export class MovementStore {
  // State
  private readonly _movements = signal<Movement[]>([]);
  private readonly _loading = signal(false);
  private readonly _selectedMovement = signal<Movement | null>(null);
  private readonly _isEditModalOpen = signal(false);
  private readonly _isDeleteModalOpen = signal(false);
  private readonly _isCreateModalOpen = signal(false);

  // Selectors
  readonly movements = this._movements.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly selectedMovement = this._selectedMovement.asReadonly();
  readonly isEditModalOpen = this._isEditModalOpen.asReadonly();
  readonly isDeleteModalOpen = this._isDeleteModalOpen.asReadonly();
  readonly isCreateModalOpen = this._isCreateModalOpen.asReadonly();
  readonly movementCount = computed(() => this._movements().length);
  readonly totalBalance = computed(() => this._movements().reduce((sum, movement) => sum + movement.balance, 0));

  // Actions
  setMovements(movements: Movement[]): void {
    this._movements.set(movements);
  }

  addMovement(movement: Movement): void {
    this._movements.update(movements => [...movements, movement]);
  }

  updateMovement(updatedMovement: Movement): void {
    this._movements.update(movements =>
      movements.map(movement =>
        movement.id === updatedMovement.id ? updatedMovement : movement
      )
    );
  }

  removeMovement(id: number): void {
    this._movements.update(movements =>
      movements.filter(movement => movement.id !== id)
    );
  }

  setLoading(loading: boolean): void {
    this._loading.set(loading);
  }

  selectMovement(movement: Movement | null): void {
    this._selectedMovement.set(movement);
  }

  openEditModal(movement: Movement): void {
    this._selectedMovement.set(movement);
    this._isEditModalOpen.set(true);
  }

  closeEditModal(): void {
    this._selectedMovement.set(null);
    this._isEditModalOpen.set(false);
  }

  openDeleteModal(movement: Movement): void {
    this._selectedMovement.set(movement);
    this._isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    this._selectedMovement.set(null);
    this._isDeleteModalOpen.set(false);
  }

  openCreateModal(): void {
    this._selectedMovement.set(null);
    this._isCreateModalOpen.set(true);
  }

  closeCreateModal(): void {
    this._selectedMovement.set(null);
    this._isCreateModalOpen.set(false);
  }
}
