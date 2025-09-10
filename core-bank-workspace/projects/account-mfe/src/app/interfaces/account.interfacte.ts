export enum AccountType {
  SAVINGS = 'SAVINGS',
  CHECKING = 'CHECKING',
  CREDIT = 'CREDIT',
  INVESTMENT = 'INVESTMENT'
}

export enum AccountStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  CLOSED = 'CLOSED'
}

// Interface para los datos que vienen del API (estructura real del microservicio)
export interface AccountApiResponse {
  accountId: number;
  accountNumber: string;
  identification: string;
  accountType: string;
  balance: number;
  createdAt: string;
  updatedAt: string;
}

// Interface para usar en la aplicación
export interface Account {
  id: number;
  accountNumber: string;
  accountType: string;
  balance: number;
  identification: string;
  status: string;
  createdAt: string;
}

// Interface para crear nuevas cuentas (formato que espera el API)
export interface CreateAccountDTO {
  accountNumber: string;
  identification: string;
  accountType: string;
  balance: number;
}

// Interface para actualizar cuentas (formato que espera el API)
export interface UpdateAccountDTO {
  accountNumber?: string;
  identification?: string;
  accountType?: string;
  balance?: number;
}

// Interfaces para la tabla y formularios
export interface TableColumn {
  key: keyof Account;
  title: string;
  type?: 'text' | 'number' | 'boolean';
}

export interface FormField {
  id: keyof Account;
  label: string;
  type: 'text' | 'number' | 'select' | 'password';
  required?: boolean;
  options?: { value: any; label: string }[];
}