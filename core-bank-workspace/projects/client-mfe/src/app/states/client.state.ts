import { Injectable, signal, computed } from '@angular/core';
import { Client } from '../interfaces/client.interface';

@Injectable()
export class ClientStore {
  // State
  private readonly _clients = signal<Client[]>([]);
  private readonly _loading = signal(false);
  private readonly _selectedClient = signal<Client | null>(null);
  private readonly _isEditModalOpen = signal(false);
  private readonly _isDeleteModalOpen = signal(false);

  // Selectors
  readonly clients = this._clients.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly selectedClient = this._selectedClient.asReadonly();
  readonly isEditModalOpen = this._isEditModalOpen.asReadonly();
  readonly isDeleteModalOpen = this._isDeleteModalOpen.asReadonly();
  readonly clientCount = computed(() => this._clients().length);

  // Actions
  setClients(clients: Client[]): void {
    this._clients.set(clients);
  }

  addClient(client: Client): void {
    this._clients.update(clients => [...clients, client]);
  }

  updateClient(updatedClient: Client): void {
    this._clients.update(clients =>
      clients.map(client =>
        client.identification === updatedClient.identification ? updatedClient : client
      )
    );
  }

  removeClient(identification: string): void {
    this._clients.update(clients =>
      clients.filter(client => client.identification !== identification)
    );
  }

  setLoading(loading: boolean): void {
    this._loading.set(loading);
  }

  selectClient(client: Client | null): void {
    this._selectedClient.set(client);
  }

  openEditModal(client: Client): void {
    this._selectedClient.set(client);
    this._isEditModalOpen.set(true);
  }

  closeEditModal(): void {
    this._selectedClient.set(null);
    this._isEditModalOpen.set(false);
  }

  openDeleteModal(client: Client): void {
    this._selectedClient.set(client);
    this._isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    this._selectedClient.set(null);
    this._isDeleteModalOpen.set(false);
  }
}