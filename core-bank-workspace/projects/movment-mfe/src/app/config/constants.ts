import { TableColumn, FormField } from '../interfaces/movement.interface';

// Configuración de columnas de la tabla
export const MOVEMENT_TABLE_COLUMNS: TableColumn[] = [
  { key: 'occurredAt', title: 'Fecha', type: 'date' },
  { key: 'movementType', title: 'Tipo', type: 'badge' },
  { key: 'description', title: 'Descripción', type: 'text' },
  { key: 'reference', title: 'Referencia', type: 'text' },
  { key: 'amount', title: 'Monto', type: 'currency' },
  { key: 'balance', title: 'Saldo', type: 'currency' },
  { key: 'accountId', title: 'Cuenta ID', type: 'number' },
];

// Configuración de campos del formulario para edición
export const MOVEMENT_FORM_FIELDS: FormField[] = [
  { 
    id: 'accountId', 
    label: 'ID de Cuenta', 
    type: 'number', 
    required: true 
  },
  { 
    id: 'occurredAt', 
    label: 'Fecha de Ocurrencia', 
    type: 'datetime-local', 
    required: true,
    readonly: true  // Solo lectura en edición
  },
  { 
    id: 'movementType', 
    label: 'Tipo de Movimiento', 
    type: 'select', 
    required: true,
    options: [
      { value: 'CREDIT', label: 'Crédito' },
      { value: 'DEBIT', label: 'Débito' }
    ]
  },
  { 
    id: 'amount', 
    label: 'Monto', 
    type: 'number', 
    required: true 
  },
  { 
    id: 'description', 
    label: 'Descripción', 
    type: 'textarea', 
    required: true 
  },
  { 
    id: 'reference', 
    label: 'Referencia', 
    type: 'text', 
    required: true 
  }
];

// Campos para formulario de creación (sin occurredAt - se genera automáticamente)
export const MOVEMENT_CREATE_FORM_FIELDS: FormField[] = [
  { 
    id: 'accountId', 
    label: 'ID de Cuenta', 
    type: 'number', 
    required: true 
  },
  { 
    id: 'movementType', 
    label: 'Tipo de Movimiento', 
    type: 'select', 
    required: true,
    options: [
      { value: 'CREDIT', label: 'Crédito' },
      { value: 'DEBIT', label: 'Débito' }
    ]
  },
  { 
    id: 'amount', 
    label: 'Monto', 
    type: 'number', 
    required: true 
  },
  { 
    id: 'description', 
    label: 'Descripción', 
    type: 'textarea', 
    required: true 
  },
  { 
    id: 'reference', 
    label: 'Referencia', 
    type: 'text', 
    required: true 
  }
];
