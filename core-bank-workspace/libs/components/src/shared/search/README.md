# Componente Search - Documentación de Uso

## 📋 Descripción

El componente `Search` es una solución genérica y reutilizable para búsqueda en tiempo real con funcionalidades avanzadas como:

- ✅ **Búsqueda en múltiples campos**
- ✅ **Debounce configurable** 
- ✅ **Case sensitive/insensitive**
- ✅ **Búsqueda exacta o parcial**
- ✅ **Estados de carga**
- ✅ **Contadores de resultados**
- ✅ **Accesibilidad completa**
- ✅ **Responsive design**

## 🚀 Componentes Disponibles

### 1. `Search` - Componente de búsqueda básico
### 2. `SearchTable` - Componente integrado (Search + Table)

---

## 📖 Ejemplos de Uso

### 1. Uso Básico del Componente Search

```typescript
// component.ts
import { Component, signal } from '@angular/core';
import { Search, SearchConfig, SearchResult } from '@bank/search';

@Component({
  selector: 'app-example',
  imports: [Search],
  template: `
    <app-ui-search
      [data]="data()"
      [config]="searchConfig"
      [loading]="loading()"
      (searchResults)="onSearchResults($event)">
    </app-ui-search>
    
    <!-- Mostrar resultados -->
    <div *ngFor="let item of filteredData()">
      {{ item.name }} - {{ item.email }}
    </div>
  `
})
export class ExampleComponent {
  data = signal([
    { id: 1, name: 'Juan Pérez', email: 'juan@example.com', department: 'IT' },
    { id: 2, name: 'María García', email: 'maria@example.com', department: 'HR' },
    { id: 3, name: 'Pedro López', email: 'pedro@example.com', department: 'Finance' }
  ]);
  
  filteredData = signal<any[]>([]);
  loading = signal(false);

  searchConfig: SearchConfig = {
    placeholder: 'Buscar empleados...',
    searchFields: ['name', 'email', 'department'],
    minLength: 2,
    debounceTime: 300,
    caseSensitive: false,
    exactMatch: false
  };

  onSearchResults(result: SearchResult): void {
    this.filteredData.set(result.filteredData);
    console.log(`Encontrados ${result.resultCount} resultados para "${result.searchTerm}"`);
  }
}
```

### 2. Uso del Componente Integrado SearchTable

```typescript
// component.ts
import { Component, signal } from '@angular/core';
import { SearchTable, SearchTableConfig, SearchResult } from '@bank/search';

@Component({
  selector: 'app-users',
  imports: [SearchTable],
  template: `
    <app-ui-search-table
      [data]="users()"
      [config]="searchTableConfig"
      [loading]="loading()"
      (deleteRow)="onDelete($event)"
      (editRow)="onEdit($event)"
      (createNew)="onCreate()"
      (searchResults)="onSearchResults($event)">
    </app-ui-search-table>
  `
})
export class UsersComponent {
  users = signal([
    { id: 1, name: 'Juan Pérez', email: 'juan@example.com', role: 'Admin' },
    { id: 2, name: 'María García', email: 'maria@example.com', role: 'User' }
  ]);
  
  loading = signal(false);

  searchTableConfig: SearchTableConfig = {
    searchConfig: {
      placeholder: 'Buscar usuarios por nombre, email o rol...',
      searchFields: ['name', 'email', 'role'],
      minLength: 2,
      debounceTime: 400,
      caseSensitive: false,
      exactMatch: false
    },
    tableColumns: [
      { key: 'name', title: 'Nombre' },
      { key: 'email', title: 'Email' },
      { key: 'role', title: 'Rol' }
    ],
    showCreateButton: true,
    createButtonText: 'Nuevo Usuario',
    createButtonClass: 'btn btn-primary'
  };

  onSearchResults(result: SearchResult): void {
    console.log('Resultados de búsqueda:', result);
  }

  onDelete(user: any): void {
    console.log('Eliminar usuario:', user);
  }

  onEdit(user: any): void {
    console.log('Editar usuario:', user);
  }

  onCreate(): void {
    console.log('Crear nuevo usuario');
  }
}
```

### 3. Búsqueda Avanzada con Campos Anidados

```typescript
// Para datos con propiedades anidadas
const complexData = [
  {
    id: 1,
    user: {
      profile: {
        firstName: 'Juan',
        lastName: 'Pérez'
      },
      contact: {
        email: 'juan@example.com',
        phone: '123-456-7890'
      }
    },
    department: {
      name: 'IT',
      location: 'Madrid'
    }
  }
];

const searchConfig: SearchConfig = {
  placeholder: 'Buscar en datos complejos...',
  searchFields: [
    'user.profile.firstName',
    'user.profile.lastName', 
    'user.contact.email',
    'department.name',
    'department.location'
  ],
  minLength: 3,
  debounceTime: 500
};
```

### 4. Configuración para Diferentes Casos de Uso

```typescript
// Búsqueda exacta (para códigos, IDs)
const exactSearchConfig: SearchConfig = {
  placeholder: 'Buscar por código exacto...',
  searchFields: ['code', 'id'],
  minLength: 1,
  debounceTime: 100,
  exactMatch: true,
  caseSensitive: true
};

// Búsqueda instantánea (sin debounce)
const instantSearchConfig: SearchConfig = {
  placeholder: 'Búsqueda instantánea...',
  searchFields: ['name', 'title'],
  minLength: 1,
  debounceTime: 0,
  caseSensitive: false
};

// Búsqueda case sensitive
const sensitiveSearchConfig: SearchConfig = {
  placeholder: 'Búsqueda sensible a mayúsculas...',
  searchFields: ['code', 'identifier'],
  minLength: 2,
  debounceTime: 300,
  caseSensitive: true,
  exactMatch: false
};
```

---

## 🎛️ Configuraciones Disponibles

### SearchConfig Interface

```typescript
interface SearchConfig {
  placeholder?: string;        // Texto del placeholder (default: "Buscar...")
  searchFields: string[];      // Campos donde buscar (REQUERIDO)
  minLength?: number;          // Mínimo de caracteres (default: 1)
  debounceTime?: number;       // Tiempo de espera en ms (default: 300)
  caseSensitive?: boolean;     // Sensible a mayúsculas (default: false)
  exactMatch?: boolean;        // Búsqueda exacta (default: false)
}
```

### SearchTableConfig Interface

```typescript
interface SearchTableConfig {
  searchConfig: SearchConfig;           // Configuración de búsqueda
  tableColumns: TableColumn[];          // Columnas de la tabla
  showCreateButton?: boolean;           // Mostrar botón crear (default: false)
  createButtonText?: string;            // Texto del botón (default: "Crear Nuevo")
  createButtonClass?: string;           // Clases CSS del botón (default: "btn btn-primary")
}
```

### SearchResult Interface

```typescript
interface SearchResult<T = any> {
  originalData: T[];    // Datos originales
  filteredData: T[];    // Datos filtrados
  searchTerm: string;   // Término de búsqueda
  resultCount: number;  // Número de resultados
}
```

---

## 🔧 Métodos Públicos

### Search Component

```typescript
// Limpiar búsqueda
clearSearch(): void

// Actualizar datos
updateData(newData: any[]): void

// Obtener configuración actual
get currentConfig(): SearchConfig
```

---

## 🎨 Personalización CSS

```css
/* Personalizar el input de búsqueda */
.search-input {
  border-radius: 1rem !important;
  border-color: #your-color !important;
}

/* Personalizar los resultados */
.result-count {
  color: #your-color !important;
  font-weight: bold !important;
}

/* Personalizar el botón de limpiar */
.search-clear-btn:hover {
  background-color: #your-hover-color !important;
}
```

---

## 📱 Responsive Design

El componente está optimizado para dispositivos móviles con:

- ✅ Font-size 16px en móvil (evita zoom en iOS)
- ✅ Layout flexible
- ✅ Touch-friendly buttons
- ✅ Optimized spacing

---

## ♿ Accesibilidad

- ✅ ARIA labels completos
- ✅ Navegación por teclado
- ✅ Screen reader friendly
- ✅ Focus management
- ✅ High contrast support
- ✅ Reduced motion support

---

## 🚀 Rendimiento

- ✅ **Signals** para reactividad óptima
- ✅ **TrackBy functions** en loops
- ✅ **Debounce** configurable
- ✅ **OnPush** change detection
- ✅ **Lazy loading** compatible

---

## 📦 Instalación en Otros Proyectos

1. Copiar la carpeta `search/` a tu librería de componentes
2. Agregar al `public-api.ts`:
   ```typescript
   export * from './search/search';
   ```
3. Configurar alias en `tsconfig.json`:
   ```json
   {
     "paths": {
       "@your-lib/search": ["libs/components/src/shared/public-api.ts"]
     }
   }
   ```

¡Listo para usar! 🎉
