import { TableColumn, FormField } from '../interfaces/account.interfacte';

// Configuración de columnas de la tabla para cuentas
export const ACCOUNT_TABLE_COLUMNS: TableColumn[] = [
  { key: 'accountNumber', title: 'Número de Cuenta', type: 'text' },
  { key: 'accountType', title: 'Tipo de Cuenta', type: 'text' },
  { key: 'identification', title: 'Identificación', type: 'text' },
  { key: 'balance', title: 'Saldo', type: 'number' },
  { key: 'status', title: 'Estado', type: 'text' },
  { key: 'createdAt', title: 'Fecha Creación', type: 'text' },
];

// Configuración de campos del formulario para editar cuentas
export const ACCOUNT_FORM_FIELDS: FormField[] = [
  { 
    id: 'accountNumber', 
    label: 'Número de Cuenta', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'accountType', 
    label: 'Tipo de Cuenta', 
    type: 'select', 
    required: true,
    options: [
      { value: 'SAVINGS', label: 'Ahorros' },
      { value: 'CHECKING', label: 'Corriente' },
      { value: 'CREDIT', label: 'Crédito' },
      { value: 'INVESTMENT', label: 'Inversión' }
    ]
  },
  { 
    id: 'identification', 
    label: 'Identificación', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'balance', 
    label: 'Saldo', 
    type: 'number', 
    required: true 
  }
];

// Campos para formulario de creación de cuentas (sin valores pre-llenados)
export const ACCOUNT_CREATE_FORM_FIELDS: FormField[] = [
  { 
    id: 'accountNumber', 
    label: 'Número de Cuenta', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'accountType', 
    label: 'Tipo de Cuenta', 
    type: 'select', 
    required: true,
    options: [
      { value: 'SAVINGS', label: 'Ahorros' },
      { value: 'CHECKING', label: 'Corriente' },
      { value: 'CREDIT', label: 'Crédito' },
      { value: 'INVESTMENT', label: 'Inversión' }
    ]
  },
  { 
    id: 'identification', 
    label: 'Identificación', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'balance', 
    label: 'Saldo Inicial', 
    type: 'number', 
    required: true 
  }
];

// Configuración para el componente de búsqueda
export const ACCOUNT_SEARCH_FIELDS = [
  'accountNumber',
  'accountType', 
  'identification',
  'status'
];

// Mensajes de la aplicación
export const ACCOUNT_MESSAGES = {
  CREATE_SUCCESS: 'Cuenta creada exitosamente',
  UPDATE_SUCCESS: 'Cuenta actualizada exitosamente',
  DELETE_SUCCESS: 'Cuenta eliminada exitosamente',
  CREATE_ERROR: 'Error al crear la cuenta',
  UPDATE_ERROR: 'Error al actualizar la cuenta',
  DELETE_ERROR: 'Error al eliminar la cuenta',
  LOAD_ERROR: 'Error al cargar las cuentas',
  VALIDATION_ERROR: 'Por favor complete todos los campos requeridos',
  DELETE_CONFIRMATION: '¿Está seguro que desea eliminar esta cuenta?',
  DELETE_WARNING: 'Esta acción no se puede deshacer.'
};
