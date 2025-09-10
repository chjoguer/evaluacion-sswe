import { Component, inject, OnInit, signal } from '@angular/core';
// import { Table } from '../../components/shared/table/table';
import { Subject } from 'rxjs';
import { ClientService } from '../../services/client-service';
import { Table } from '@bank/table'; 
import { Modal } from '@bank/modal'; 
import { Search, SearchTableConfig, SearchResult } from '@bank/search';
import { CLIENT_TABLE_COLUMNS, CLIENT_FORM_FIELDS, CLIENT_CREATE_FORM_FIELDS } from '../../config/constants';
import { Client, UpdateClientDTO, CreateClientDTO } from '../../interfaces/client.interface';
import { ClientStore } from '../../states/client.state';
import { NotificationService } from '../../services/notification-service';
@Component({
  selector: 'app-client',
  imports: [Table, Modal, Search],
  standalone: true,
  templateUrl: './client.html',
  styleUrls: ['./client.css'],
  providers: [ClientStore] 

})
export class ClientComponent implements OnInit {
  private clientService = inject(ClientService);
  private readonly clientStore = inject(ClientStore);
  private readonly notificationService = inject(NotificationService);

  readonly clients = this.clientStore.clients;
  readonly loading = this.clientStore.loading;
  readonly selectedClient = this.clientStore.selectedClient;
  readonly isEditModalOpen = this.clientStore.isEditModalOpen;
  readonly isDeleteModalOpen = this.clientStore.isDeleteModalOpen;

  users = signal<any[]>([]);
  filteredUsers = signal<any[]>([]); // ✅ Nueva señal para usuarios filtrados
  loading2 = signal(false);

  clientes: any[] = [];
  searchTerm$ = new Subject<string>();
  error = '';
  successMessage = '';
  submitted = false;
  errorMessage = '';

 
  protected tableColumns = CLIENT_TABLE_COLUMNS;
  protected customFields = CLIENT_FORM_FIELDS;
  protected createFields = CLIENT_CREATE_FORM_FIELDS; // ✅ Campos para creación

  // ✅ Configuración del componente integrado Search + Table
  protected searchTableConfig: SearchTableConfig = {
    searchConfig: {
      placeholder: 'Buscar clientes por nombre, identificación, teléfono, email...',
      searchFields: ['fullName', 'identification', 'cellphone', 'direction', 'documentType'],
      minLength: 0, // ✅ Cambiado de 2 a 0 para mostrar todos los clientes desde el inicio
      debounceTime: 300,
      caseSensitive: false,
      exactMatch: false
    },
    tableColumns: CLIENT_TABLE_COLUMNS,
    showCreateButton: true,
    createButtonText: 'Nuevo Cliente',
    createButtonClass: 'btn btn-success'
  };

  mappedCustomFields(customFields: any[], selectedRow: any): void {
    // Mapeo actualizado con la estructura exacta del API
    customFields[0]['value'] = selectedRow.fullName;        // fullName
    customFields[1]['value'] = selectedRow.identification;  // identification
    customFields[2]['value'] = selectedRow.cellphone;       // cellphone
    customFields[3]['value'] = selectedRow.direction;       // direction
    customFields[4]['value'] = selectedRow.documentType;    // documentType
    customFields[5]['value'] = selectedRow.password;        // password
    customFields[6]['value'] = selectedRow.status;          // status (boolean)

    this.customFields = customFields;
    console.log('Selected mappedCustomFields with API structure:', customFields);
    console.log('Selected row data:', selectedRow);
  }

    ngOnInit(): void {
    this.loadUsers();
    // ✅ Asegurar que los datos se muestren inmediatamente en el SearchTable
    console.log('Component initialized - clients should be visible immediately');
  }




  private loadUsers() {
    this.loading2.set(true);
    this.clientStore.setLoading(true);
    this.clientService.getUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.filteredUsers.set(users); // ✅ Inicializar datos filtrados con todos los usuarios
        this.clientStore.setClients(users);
        this.clientStore.setLoading(false);

        this.loading2.set(false);
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.loading2.set(false);
      },
    });
    this.clientService.getClients().subscribe((data) => {
        this.users.set(data);
        this.filteredUsers.set(data); // ✅ También aquí
      console.log('Loaded clients:', data);
    });
  }

  openModalAlert(row: any){
    console.log('Opening delete modal with data:', row);
    this.selectedRow = row;
    this.isModalOpenAlert = true;
    // No necesitamos mapear campos para el modal de eliminación, solo pasar los datos
    console.log('Selected client for deletion:', this.selectedRow);
  }
  
  closeModalAlert() {
    this.isModalOpenAlert = false;
    this.selectedRow = null;
  }



  isModalOpenAlert = false;
  isModalOpen = false;
  isCreateModalOpen = false; // ✅ Nueva propiedad para modal de creación
  selectedRow: any = null;

 

  openModal(row: any) {
    this.selectedRow = row;
    this.isModalOpen = true;

    this.mappedCustomFields(this.customFields,this.selectedRow);
        this.clientStore.openEditModal(row); // ✅ Esto setea el selectedClient

  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedRow = null;
  }

  // ✅ Métodos para modal de creación
  openCreateModal(): void {
    this.isCreateModalOpen = true;
    console.log('Opening create modal');
  }

  closeCreateModal(): void {
    this.isCreateModalOpen = false;
    console.log('Closing create modal');
  }

  deleteRow(row: any) {
    console.log('Deleting row:', row);
    this.openModalAlert(row); // Reutilizar el método openModalAlert
  }

  onModalSubmitAlert(clientData: any) {
    console.log('Delete confirmation received:', clientData);
    
    // El modal pasa directamente los datos del cliente seleccionado
    const identification = clientData?.identification || this.selectedRow?.identification;
    
    if (!identification) {
      this.notificationService.showError('No se pudo obtener la identificación del cliente');
      return;
    }

    console.log('Deleting client with identification:', identification);
    
    this.clientStore.setLoading(true);
    
    this.clientService.deleteUser(identification).subscribe({
      next: () => {
        this.clientStore.setLoading(false);
        this.closeModalAlert();
        this.notificationService.showSuccess('Cliente eliminado exitosamente');
        this.loadUsers(); // Recargar la lista
      },
      error: (err) => {
        console.error('Error deleting client:', err);
        this.clientStore.setLoading(false);
        this.notificationService.showError('Ocurrió un error al eliminar el cliente');
      }
    });
  }


  // onModalSubmit(fields: any) {
  //   console.log('Submitted Fields:', fields);
  //   this.clientService.updateUser(fields.identification,fields).subscribe({
  //     next: (response: any) => {
  //       this.successMessage = 'Client updated successfully!';
  //       console.log('Client updated:', response);
  //       this.loadUsers();
  //     },
  //     error: (error: Error) => {
  //       this.error = error.message;
  //       console.error('Error creating client:', error);
  //       this.loading.set(false);
  //     }
  //   });
  //   this.isModalOpen = false;
  // }
    onModalSubmit(clientData: any): void {
    const selectedClient = this.clientStore.selectedClient();
    console.log('Submitted Fields:', clientData);
    console.log('Selected Client:', selectedClient);
    
    if (!selectedClient?.identification) {
      this.notificationService.showError('No hay cliente seleccionado');
      return;
    }

    // Transformar los datos al formato exacto del API
    const updatePayload: UpdateClientDTO = {
      fullName: clientData.fullName,
      direction: clientData.direction,
      cellphone: clientData.cellphone,
      password: clientData.password,
      status: clientData.status === 'true' || clientData.status === true, // Asegurar boolean
      documentType: clientData.documentType,
      identification: clientData.identification
    };

    console.log('Update payload to API:', updatePayload);

    this.clientStore.setLoading(true);
    
    this.clientService.updateUser(selectedClient.identification, updatePayload)
      .subscribe({
        next: (updatedClient) => {
          if (updatedClient) {
            this.clientStore.updateClient(updatedClient);
            this.clientStore.closeEditModal();
            this.isModalOpen = false; // Cerrar modal local también
            this.notificationService.showSuccess('Cliente actualizado exitosamente');
            this.loadUsers(); // Recargar datos
          }
        },
        error: (error) => {
          console.error('Error updating client:', error);
          this.notificationService.showError('Error al actualizar cliente');
        },
        complete: () => this.clientStore.setLoading(false)
      });
  }

  // ✅ Método para crear nuevos clientes
  onCreateClient(clientData: any): void {
    console.log('Creating client with data:', clientData);

    // Transformar los datos al formato exacto del API
    const createPayload: CreateClientDTO = {
      fullName: clientData.fullName,
      direction: clientData.direction,
      cellphone: clientData.cellphone,
      password: clientData.password,
      status: clientData.status === 'true' || clientData.status === true, // Asegurar boolean
      documentType: clientData.documentType,
      identification: clientData.identification
    };

    console.log('Create payload to API:', createPayload);

    this.clientStore.setLoading(true);
    
    this.clientService.createUser(createPayload)
      .subscribe({
        next: (newClient) => {
          if (newClient) {
            this.clientStore.addClient(newClient);
            this.closeCreateModal(); // ✅ Cerrar modal de creación
            this.notificationService.showSuccess('Cliente creado exitosamente');
            this.loadUsers(); // Recargar datos
          }
        },
        error: (error) => {
          console.error('Error creating client:', error);
          this.notificationService.showError('Error al crear cliente');
        },
        complete: () => this.clientStore.setLoading(false)
      });
  }

  // ✅ Método para manejar resultados de búsqueda
  onSearchResults(result: SearchResult): void {
    console.log('Search results:', result);
    // ✅ Actualizar los datos filtrados según la búsqueda
    this.filteredUsers.set(result.filteredData || []);
  }
}
