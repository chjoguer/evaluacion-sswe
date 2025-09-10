import { Component, inject, OnInit, signal } from '@angular/core';
import { Subject } from 'rxjs';
import { MovementService } from '../../services/movement-service';
import { Table } from '@bank/table'; 
import { Modal } from '@bank/modal'; 
import { Search, SearchTable, SearchTableConfig, SearchResult } from '@bank/search';
import { MOVEMENT_TABLE_COLUMNS, MOVEMENT_FORM_FIELDS, MOVEMENT_CREATE_FORM_FIELDS } from '../../config/constants';
import { Movement, UpdateMovementDTO, CreateMovementDTO } from '../../interfaces/movement.interface';
import { MovementStore } from '../../states/movement.state';
import { NotificationService } from '../../services/notification-service';

@Component({
  selector: 'app-movement',
  imports: [Table, Modal, Search, SearchTable],
  standalone: true,
  templateUrl: './movement.html',
  styleUrls: ['./movement.css'],
  providers: [MovementStore] 
})
export class MovementComponent implements OnInit {
  private movementService = inject(MovementService);
  private readonly movementStore = inject(MovementStore);
  private readonly notificationService = inject(NotificationService);

  readonly movements = this.movementStore.movements;
  readonly loading = this.movementStore.loading;
  readonly selectedMovement = this.movementStore.selectedMovement;
  readonly isEditModalOpen = this.movementStore.isEditModalOpen;
  readonly isDeleteModalOpen = this.movementStore.isDeleteModalOpen;

  movementsData = signal<any[]>([]);
  loading2 = signal(false);

  movements_list: any[] = [];
  searchTerm$ = new Subject<string>();
  error = '';
  successMessage = '';
  submitted = false;
  errorMessage = '';

  protected tableColumns = MOVEMENT_TABLE_COLUMNS;
  protected customFields = MOVEMENT_FORM_FIELDS;
  protected createFields = MOVEMENT_CREATE_FORM_FIELDS;

  // Configuración del componente integrado Search + Table
  protected searchTableConfig: SearchTableConfig = {
    searchConfig: {
      placeholder: 'Buscar movimientos por descripción, referencia, tipo...',
      searchFields: ['description', 'reference', 'movementType', 'amount'],
      minLength: 0,
      debounceTime: 300,
      caseSensitive: false,
      exactMatch: false
    },
    tableColumns: MOVEMENT_TABLE_COLUMNS,
    showCreateButton: true,
    createButtonText: 'Nuevo Movimiento',
    createButtonClass: 'btn btn-success'
  };

  mappedCustomFields(customFields: any[], selectedRow: any): void {
    // Mapeo con la estructura de la API de movimientos
    customFields[0]['value'] = selectedRow.accountId;      // accountId
    customFields[1]['value'] = selectedRow.occurredAt;     // occurredAt
    customFields[2]['value'] = selectedRow.movementType;   // movementType
    customFields[3]['value'] = selectedRow.amount;         // amount
    customFields[4]['value'] = selectedRow.description;    // description
    customFields[5]['value'] = selectedRow.reference;      // reference

    this.customFields = customFields;
    console.log('Selected mappedCustomFields with API structure:', customFields);
    console.log('Selected row data:', selectedRow);
  }

  ngOnInit(): void {
    this.loadMovements();
    console.log('Movement component initialized - movements should be visible immediately');
  }

  private loadMovements() {
    this.loading2.set(true);
    this.movementStore.setLoading(true);
    this.movementService.getMovements().subscribe({
      next: (movements) => {
        this.movementsData.set(movements);
        this.movementStore.setMovements(movements);
        this.movementStore.setLoading(false);
        this.loading2.set(false);
      },
      error: (error) => {
        console.error('Error loading movements:', error);
        this.loading2.set(false);
        this.movementStore.setLoading(false);
      },
    });
  }

  // Modal states
  isModalOpenAlert = false;
  isModalOpen = false;
  isCreateModalOpen = false;
  selectedRow: any = null;

  openModalAlert(row: any) {
    console.log('Opening delete modal with data:', row);
    this.selectedRow = row;
    this.isModalOpenAlert = true;
    console.log('Selected movement for deletion:', this.selectedRow);
  }
  
  closeModalAlert() {
    this.isModalOpenAlert = false;
    this.selectedRow = null;
  }

  openModal(row: any) {
    this.selectedRow = row;
    this.isModalOpen = true;
    this.mappedCustomFields(this.customFields, this.selectedRow);
    this.movementStore.openEditModal(row);
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedRow = null;
  }

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
    this.openModalAlert(row);
  }

  onModalSubmitAlert(movementData: any) {
    console.log('Delete confirmation received:', movementData);
    
    const id = movementData?.id || this.selectedRow?.id;
    
    if (!id) {
      this.notificationService.showError('No se pudo obtener el ID del movimiento');
      return;
    }

    console.log('Deleting movement with ID:', id);
    
    this.movementStore.setLoading(true);
    
    this.movementService.deleteMovement(id).subscribe({
      next: (success) => {
        if (success) {
          this.movementStore.setLoading(false);
          this.closeModalAlert();
          this.notificationService.showSuccess('Movimiento eliminado exitosamente');
          this.loadMovements();
        }
      },
      error: (err) => {
        console.error('Error deleting movement:', err);
        this.movementStore.setLoading(false);
        this.notificationService.showError('Ocurrió un error al eliminar el movimiento');
      }
    });
  }

  onModalSubmit(movementData: any): void {
    const selectedMovement = this.movementStore.selectedMovement();
    console.log('Submitted Fields:', movementData);
    console.log('Selected Movement:', selectedMovement);
    
    if (!selectedMovement?.id) {
      this.notificationService.showError('No hay movimiento seleccionado');
      return;
    }

    // Transformar los datos al formato del API
    const updatePayload: UpdateMovementDTO = {
      accountId: Number(movementData.accountId),
      occurredAt: movementData.occurredAt,
      movementType: movementData.movementType,
      amount: Number(movementData.amount),
      description: movementData.description,
      reference: movementData.reference
    };

    console.log('Update payload to API:', updatePayload);

    this.movementStore.setLoading(true);
    
    this.movementService.updateMovement(selectedMovement.id, updatePayload)
      .subscribe({
        next: (updatedMovement) => {
          if (updatedMovement) {
            this.movementStore.updateMovement(updatedMovement);
            this.movementStore.closeEditModal();
            this.isModalOpen = false;
            this.notificationService.showSuccess('Movimiento actualizado exitosamente');
            this.loadMovements();
          }
        },
        error: (error) => {
          console.error('Error updating movement:', error);
          this.notificationService.showError('Error al actualizar movimiento');
        },
        complete: () => this.movementStore.setLoading(false)
      });
  }

  onCreateMovement(movementData: any): void {
    console.log('Creating movement with data:', movementData);

    // Generar automáticamente la fecha de ocurrencia (momento actual)
    const currentDate = new Date().toISOString();

    // Transformar los datos al formato del API
    const createPayload: CreateMovementDTO = {
      accountId: Number(movementData.accountId),
      occurredAt: currentDate, // Se genera automáticamente
      movementType: movementData.movementType,
      amount: Number(movementData.amount),
      description: movementData.description,
      reference: movementData.reference
    };

    console.log('Create payload to API:', createPayload);

    this.movementStore.setLoading(true);
    
    this.movementService.createMovement(createPayload)
      .subscribe({
        next: (newMovement) => {
          if (newMovement) {
            this.movementStore.addMovement(newMovement);
            this.closeCreateModal();
            this.notificationService.showSuccess('Movimiento creado exitosamente');
            this.loadMovements();
          }
        },
        error: (error) => {
          console.error('Error creating movement:', error);
          this.notificationService.showError('Error al crear movimiento');
        },
        complete: () => this.movementStore.setLoading(false)
      });
  }

  onSearchResults(result: SearchResult): void {
    console.log('Search results:', result);
  }
}
