export enum MovementType {
  CREDIT = 'CREDIT',
  DEBIT = 'DEBIT'
}

// Interface para los datos que vienen del API
export interface MovementApiResponse {
  id: number;
  uniqueKey: string;
  accountId: number;
  occurredAt: string;
  movementType: MovementType | string;
  amount: number;
  balance: number;
  description: string;
  reference: string;
  createdAt: string;
  updatedAt: string;
}

// Interface para usar en la aplicación
export interface Movement {
  id: number;
  uniqueKey: string;
  accountId: number;
  occurredAt: string;
  movementType: string;
  amount: number;
  balance: number;
  description: string;
  reference: string;
  createdAt?: string;
  updatedAt?: string;
}

// Interface para crear/actualizar movimientos
export interface CreateMovementDTO {
  accountId: number;
  occurredAt: string;
  movementType: string;
  amount: number;
  description: string;
  reference: string;
}

export interface UpdateMovementDTO {
  accountId?: number;
  occurredAt?: string;
  movementType?: string;
  amount?: number;
  description?: string;
  reference?: string;
}

// Interface para columnas de tabla
export interface TableColumn {
  key: string;
  title: string;
  type: 'text' | 'number' | 'date' | 'currency' | 'badge';
}

// Interface para campos de formulario
export interface FormField {
  id: string;
  label: string;
  type: 'text' | 'number' | 'date' | 'datetime-local' | 'select' | 'textarea';
  required: boolean;
  readonly?: boolean;
  options?: { value: any; label: string }[];
}
