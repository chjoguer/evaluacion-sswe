import { Injectable, signal, computed } from '@angular/core';
import { Account } from '../interfaces/account.interfacte';

@Injectable()
export class AccountStore {
  // State
  private readonly _accounts = signal<Account[]>([]);
  private readonly _loading = signal(false);
  private readonly _selectedAccount = signal<Account | null>(null);
  private readonly _isEditModalOpen = signal(false);
  private readonly _isDeleteModalOpen = signal(false);

  // Selectors
  readonly accounts = this._accounts.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly selectedAccount = this._selectedAccount.asReadonly();
  readonly isEditModalOpen = this._isEditModalOpen.asReadonly();
  readonly isDeleteModalOpen = this._isDeleteModalOpen.asReadonly();
  readonly accountCount = computed(() => this._accounts().length);

  // Computed properties for account statistics
  readonly totalBalance = computed(() => 
    this._accounts().reduce((sum, account) => sum + account.balance, 0)
  );

  readonly activeAccounts = computed(() => 
    this._accounts().filter(account => account.status === 'ACTIVE')
  );

  readonly inactiveAccounts = computed(() => 
    this._accounts().filter(account => account.status === 'INACTIVE')
  );

  // Actions
  setAccounts(accounts: Account[]): void {
    this._accounts.set(accounts);
  }

  addAccount(account: Account): void {
    this._accounts.update(accounts => [...accounts, account]);
  }

  updateAccount(updatedAccount: Account): void {
    this._accounts.update(accounts =>
      accounts.map(account =>
        account.id === updatedAccount.id ? updatedAccount : account
      )
    );
  }

  removeAccount(accountId: string): void {
    this._accounts.update(accounts =>
      accounts.filter(account => account.id.toString() !== accountId)
    );
  }

  setLoading(loading: boolean): void {
    this._loading.set(loading);
  }

  selectAccount(account: Account | null): void {
    this._selectedAccount.set(account);
  }

  openEditModal(account: Account): void {
    this._selectedAccount.set(account);
    this._isEditModalOpen.set(true);
  }

  closeEditModal(): void {
    this._selectedAccount.set(null);
    this._isEditModalOpen.set(false);
  }

  openDeleteModal(account: Account): void {
    this._selectedAccount.set(account);
    this._isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    this._selectedAccount.set(null);
    this._isDeleteModalOpen.set(false);
  }

  // Additional utility methods
  getAccountByNumber(accountNumber: string): Account | undefined {
    return this._accounts().find(account => account.accountNumber === accountNumber);
  }

  getAccountsByClient(identification: string): Account[] {
    return this._accounts().filter(account => account.identification === identification);
  }

  getAccountsByType(type: string): Account[] {
    return this._accounts().filter(account => account.accountType === type);
  }
}
