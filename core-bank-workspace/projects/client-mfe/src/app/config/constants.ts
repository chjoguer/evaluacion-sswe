import { TableColumn, FormField } from '../interfaces/client.interface';

// Configuración de columnas de la tabla mapeadas a la nueva estructura
export const CLIENT_TABLE_COLUMNS: TableColumn[] = [
  { key: 'fullName', title: 'Nombre Completo', type: 'text' },
  { key: 'identification', title: 'Identificación', type: 'text' },
  { key: 'cellphone', title: 'Teléfono', type: 'text' },
  { key: 'documentType', title: 'Tipo Documento', type: 'text' },
  { key: 'direction', title: 'Dirección', type: 'text' },
  { key: 'status', title: 'Estado', type: 'boolean' },
];

// Configuración de campos del formulario mapeados a la nueva estructura
export const CLIENT_FORM_FIELDS: FormField[] = [
  { 
    id: 'fullName', 
    label: 'Nombre Completo', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'identification', 
    label: 'Identificación', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'cellphone', 
    label: 'Teléfono Celular', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'direction', 
    label: 'Dirección', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'documentType', 
    label: 'Tipo de Documento', 
    type: 'select', 
    required: true,
    options: [
      { value: 'CEDULA', label: 'Cédula' },
      { value: 'PASAPORTE', label: 'Pasaporte' },
      { value: 'RUC', label: 'RUC' }
    ]
  },
  { 
    id: 'password', 
    label: 'Contraseña', 
    type: 'password', 
    required: true 
  },
  { 
    id: 'status', 
    label: 'Estado', 
    type: 'select', 
    required: true,
    options: [
      { value: true, label: 'Activo' },
      { value: false, label: 'Inactivo' }
    ]
  }
];

// ✅ Campos para formulario de creación (sin valores pre-llenados)
export const CLIENT_CREATE_FORM_FIELDS: FormField[] = [
  { 
    id: 'fullName', 
    label: 'Nombre Completo', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'identification', 
    label: 'Identificación', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'cellphone', 
    label: 'Teléfono Celular', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'direction', 
    label: 'Dirección', 
    type: 'text', 
    required: true 
  },
  { 
    id: 'documentType', 
    label: 'Tipo de Documento', 
    type: 'select', 
    required: true,
    options: [
      { value: 'CEDULA', label: 'Cédula' },
      { value: 'PASAPORTE', label: 'Pasaporte' },
      { value: 'RUC', label: 'RUC' }
    ]
  },
  { 
    id: 'password', 
    label: 'Contraseña', 
    type: 'password', 
    required: true 
  },
  { 
    id: 'status', 
    label: 'Estado', 
    type: 'select', 
    required: true,
    options: [
      { value: true, label: 'Activo' },
      { value: false, label: 'Inactivo' }
    ]
  }
];
