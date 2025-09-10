import { Component, inject, OnInit, signal } from '@angular/core';
import { Subject } from 'rxjs';
import { AccountService } from '../../services/account-service';
import { SearchTable, SearchTableConfig, SearchResult } from '@bank/search';
import { Modal } from '@bank/modal'; 
import { ACCOUNT_TABLE_COLUMNS, ACCOUNT_FORM_FIELDS, ACCOUNT_CREATE_FORM_FIELDS, ACCOUNT_MESSAGES } from '../../config/constants';
import { Account, UpdateAccountDTO, CreateAccountDTO } from '../../interfaces/account.interfacte';
import { AccountStore } from '../../states/account.state';
import { NotificationService } from '../../services/notification-service';

@Component({
  selector: 'app-account',
  imports: [Modal, SearchTable],
  standalone: true,
  templateUrl: './account.html',
  styleUrls: ['./account.css'],
  providers: [AccountStore]
})
export class AccountComponent implements OnInit {
  private accountService = inject(AccountService);
  private readonly accountStore = inject(AccountStore);
  private readonly notificationService = inject(NotificationService);

  readonly accounts = this.accountStore.accounts;
  readonly loading = this.accountStore.loading;
  readonly selectedAccount = this.accountStore.selectedAccount;
  readonly totalBalance = this.accountStore.totalBalance;
  readonly activeAccounts = this.accountStore.activeAccounts;

  users = signal<Account[]>([]);
  loading2 = signal(false);

  searchTerm$ = new Subject<string>();
  error = '';
  successMessage = '';
  submitted = false;
  errorMessage = '';

  protected tableColumns = ACCOUNT_TABLE_COLUMNS;
  protected customFields = ACCOUNT_FORM_FIELDS;
  protected createFields = ACCOUNT_CREATE_FORM_FIELDS;

  // Configuración del componente integrado Search + Table
  protected searchTableConfig: SearchTableConfig = {
    searchConfig: {
      placeholder: 'Buscar cuentas por número, tipo, identificación...',
      searchFields: ['accountNumber', 'accountType', 'identification', 'status'],
      minLength: 2,
      debounceTime: 300,
      caseSensitive: false,
      exactMatch: false
    },
    tableColumns: ACCOUNT_TABLE_COLUMNS,
    showCreateButton: true,
    createButtonText: 'Nueva Cuenta',
    createButtonClass: 'btn btn-success'
  };

  // Estados de modales
  isModalOpenAlert = false;
  isModalOpen = false;
  isCreateModalOpen = false;
  selectedRow: Account | null = null;

  ngOnInit(): void {
    this.loadAccounts();
  }

  private loadAccounts() {
    this.loading2.set(true);
    this.accountStore.setLoading(true);
    
    this.accountService.getAccounts().subscribe({
      next: (accounts) => {
        this.users.set(accounts);
        this.accountStore.setAccounts(accounts);
        this.accountStore.setLoading(false);
        this.loading2.set(false);
        console.log('Loaded accounts:', accounts);
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
        this.notificationService.showError(ACCOUNT_MESSAGES.LOAD_ERROR);
        this.accountStore.setLoading(false);
        this.loading2.set(false);
      }
    });
  }

  mappedCustomFields(customFields: any[], selectedRow: Account): void {
    // Mapeo con la nueva estructura real del API de cuentas
    customFields[0]['value'] = selectedRow.accountNumber;    // accountNumber
    customFields[1]['value'] = selectedRow.accountType;      // accountType
    customFields[2]['value'] = selectedRow.identification;   // identification
    customFields[3]['value'] = selectedRow.balance;          // balance

    this.customFields = customFields;
    console.log('Selected mappedCustomFields for account:', customFields);
    console.log('Selected account data:', selectedRow);
  }

  // Modal de eliminación
  openModalAlert(row: Account) {
    console.log('Opening delete modal with account data:', row);
    this.selectedRow = row;
    this.isModalOpenAlert = true;
  }

  closeModalAlert() {
    this.isModalOpenAlert = false;
    this.selectedRow = null;
  }

  deleteRow(row: Account) {
    console.log('Deleting account:', row);
    this.openModalAlert(row);
  }

  onModalSubmitAlert(accountData: any) {
    console.log('Delete confirmation received:', accountData);
    
    const accountId = accountData?.id || this.selectedRow?.id;
    
    if (!accountId) {
      this.notificationService.showError('No se pudo obtener el ID de la cuenta');
      return;
    }

    console.log('Deleting account with ID:', accountId);
    
    this.accountStore.setLoading(true);
    
    this.accountService.deleteAccount(accountId).subscribe({
      next: (success) => {
        if (success) {
          this.accountStore.removeAccount(accountId);
          this.closeModalAlert();
          this.notificationService.showSuccess(ACCOUNT_MESSAGES.DELETE_SUCCESS);
          this.loadAccounts();
        }
      },
      error: (err) => {
        console.error('Error deleting account:', err);
        this.notificationService.showError(ACCOUNT_MESSAGES.DELETE_ERROR);
      },
      complete: () => this.accountStore.setLoading(false)
    });
  }

  // Modal de edición
  openModal(row: Account) {
    this.selectedRow = row;
    this.isModalOpen = true;
    this.mappedCustomFields(this.customFields, this.selectedRow);
    this.accountStore.openEditModal(row);
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedRow = null;
    this.accountStore.closeEditModal();
  }

  onModalSubmit(accountData: any): void {
    const selectedAccount = this.accountStore.selectedAccount();
    console.log('Submitted Fields:', accountData);
    console.log('Selected Account:', selectedAccount);
    
    if (!selectedAccount?.id) {
      this.notificationService.showError('No hay cuenta seleccionada');
      return;
    }

    // Transformar los datos al formato que espera el API real
    const updatePayload: UpdateAccountDTO = {
      accountNumber: accountData.accountNumber,
      accountType: accountData.accountType,
      identification: accountData.identification,
      balance: Number(accountData.balance)
    };

    console.log('Update payload to API:', updatePayload);

    this.accountStore.setLoading(true);
    
    this.accountService.updateAccount(selectedAccount.id.toString(), updatePayload)
      .subscribe({
        next: (updatedAccount) => {
          if (updatedAccount) {
            this.accountStore.updateAccount(updatedAccount);
            this.accountStore.closeEditModal();
            this.isModalOpen = false;
            this.notificationService.showSuccess(ACCOUNT_MESSAGES.UPDATE_SUCCESS);
            this.loadAccounts();
          }
        },
        error: (error) => {
          console.error('Error updating account:', error);
          this.notificationService.showError(ACCOUNT_MESSAGES.UPDATE_ERROR);
        },
        complete: () => this.accountStore.setLoading(false)
      });
  }

  // Modal de creación
  openCreateModal(): void {
    this.isCreateModalOpen = true;
    console.log('Opening create account modal');
  }

  closeCreateModal(): void {
    this.isCreateModalOpen = false;
    console.log('Closing create account modal');
  }

  onCreateAccount(accountData: any): void {
    console.log('Creating account with data:', accountData);

    // Transformar los datos al formato que espera el API real
    const createPayload: CreateAccountDTO = {
      accountNumber: accountData.accountNumber,
      accountType: accountData.accountType,
      identification: accountData.identification,
      balance: Number(accountData.balance)
    };

    console.log('Create payload to API:', createPayload);

    this.accountStore.setLoading(true);
    
    this.accountService.createAccount(createPayload)
      .subscribe({
        next: (newAccount) => {
          if (newAccount) {
            this.accountStore.addAccount(newAccount);
            this.closeCreateModal();
            this.notificationService.showSuccess(ACCOUNT_MESSAGES.CREATE_SUCCESS);
            this.loadAccounts();
          }
        },
        error: (error) => {
          console.error('Error creating account:', error);
          this.notificationService.showError(ACCOUNT_MESSAGES.CREATE_ERROR);
        },
        complete: () => this.accountStore.setLoading(false)
      });
  }

  // Método para manejar resultados de búsqueda
  onSearchResults(result: SearchResult): void {
    console.log('Account search results:', result);
  }
}
