export enum DocumentType {
  CEDULA = 'CEDULA',
  PASAPORTE = 'PASAPORTE',
  RUC = 'RUC'
}

export enum ClientStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

// Interface para los datos que vienen del API
export interface ClientApiResponse {
  id: string;
  fullName: string;
  identification: string;
  cellphone: string;
  direction: string;
  documentType: DocumentType | string;
  password: string;
  status: boolean;
  createdAt: string;
  updatedAt: string;
}

// Interface para usar en la aplicación
export interface Client {
  id: string;
  fullName: string;
  identification: string;
  cellphone: string;
  direction: string;
  documentType: string;
  password?: string;
  status: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// Interface para crear/actualizar clientes (estructura exacta para API)
export interface CreateClientDTO {
  fullName: string;
  direction: string;
  cellphone: string;
  password: string;
  status: boolean;
  documentType: string;
  identification: string;
}

export interface UpdateClientDTO {
  fullName?: string;
  direction?: string;
  cellphone?: string;
  password?: string;
  status?: boolean;
  documentType?: string;
  identification?: string;
}

// Interfaces para la tabla y formularios
export interface TableColumn {
  key: keyof Client;
  title: string;
  type?: 'text' | 'number' | 'boolean';
}

export interface FormField {
  id: keyof Client;
  label: string;
  type: 'text' | 'number' | 'select' | 'password';
  required?: boolean;
  options?: { value: any; label: string }[];
}