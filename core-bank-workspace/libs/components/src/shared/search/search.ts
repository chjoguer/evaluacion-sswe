import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';

export interface SearchConfig {
  placeholder?: string;
  searchFields: string[]; // Campos en los que buscar
  minLength?: number; // Mínimo de caracteres para buscar
  debounceTime?: number; // Tiempo de espera antes de buscar
  caseSensitive?: boolean; // Si la búsqueda es sensible a mayúsculas
  exactMatch?: boolean; // Si busca coincidencia exacta
}

export interface SearchResult<T = any> {
  originalData: T[];
  filteredData: T[];
  searchTerm: string;
  resultCount: number;
}

@Component({
  selector: 'app-ui-search',
  templateUrl: './search.html',
  styleUrls: ['./search.css'],
  imports: [CommonModule, FormsModule],
  standalone: true
})
export class Search implements OnInit, OnDestroy {
  @Input() data: any[] = [];
  @Input() config: SearchConfig = {
    placeholder: 'Buscar...',
    searchFields: [],
    minLength: 1,
    debounceTime: 300,
    caseSensitive: false,
    exactMatch: false
  };
  @Input() loading: boolean = false;
  @Input() showResults: boolean = true; // Controlar si mostrar o no el contador de resultados

  @Output() searchResults = new EventEmitter<SearchResult>();
  @Output() searchTermChange = new EventEmitter<string>();

  // Signals para reactividad
  searchTerm = signal<string>('');
  filteredData = signal<any[]>([]);
  isSearching = signal<boolean>(false);

  // Computed para el contador de resultados
  resultCount = computed(() => this.filteredData().length);
  hasResults = computed(() => this.filteredData().length > 0);
  showNoResults = computed(() => 
    this.searchTerm().length >= (this.config.minLength || 1) && 
    !this.isSearching() && 
    !this.hasResults()
  );

  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.setupSearch();
    // Inicializar con todos los datos
    this.filteredData.set([...this.data]);
    this.emitSearchResults();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupSearch(): void {
    this.searchSubject
      .pipe(
        debounceTime(this.config.debounceTime || 300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(term => {
        this.performSearch(term);
      });
  }

  onSearchChange(term: string): void {
    this.searchTerm.set(term);
    this.searchTermChange.emit(term);
    
    if (term.length < (this.config.minLength || 1)) {
      this.filteredData.set([...this.data]);
      this.isSearching.set(false);
      this.emitSearchResults();
      return;
    }

    this.isSearching.set(true);
    this.searchSubject.next(term);
  }

  private performSearch(term: string): void {
    if (!term || term.length < (this.config.minLength || 1)) {
      this.filteredData.set([...this.data]);
      this.isSearching.set(false);
      this.emitSearchResults();
      return;
    }

    const searchTerm = this.config.caseSensitive ? term : term.toLowerCase();
    
    const filtered = this.data.filter(item => {
      return this.config.searchFields.some(field => {
        const fieldValue = this.getNestedProperty(item, field);
        
        if (fieldValue === null || fieldValue === undefined) {
          return false;
        }

        const strValue = this.config.caseSensitive 
          ? String(fieldValue) 
          : String(fieldValue).toLowerCase();

        return this.config.exactMatch 
          ? strValue === searchTerm
          : strValue.includes(searchTerm);
      });
    });

    this.filteredData.set(filtered);
    this.isSearching.set(false);
    this.emitSearchResults();
  }

  private getNestedProperty(obj: any, path: string): any {
    return path.split('.').reduce((current, key) => current?.[key], obj);
  }

  private emitSearchResults(): void {
    const result: SearchResult = {
      originalData: this.data,
      filteredData: this.filteredData(),
      searchTerm: this.searchTerm(),
      resultCount: this.filteredData().length
    };
    
    this.searchResults.emit(result);
  }

  clearSearch(): void {
    this.searchTerm.set('');
    this.filteredData.set([...this.data]);
    this.isSearching.set(false);
    this.emitSearchResults();
  }

  // Método público para actualizar los datos desde el componente padre
  updateData(newData: any[]): void {
    this.data = newData;
    
    // Si hay un término de búsqueda activo, re-aplicar la búsqueda
    if (this.searchTerm()) {
      this.performSearch(this.searchTerm());
    } else {
      this.filteredData.set([...newData]);
      this.emitSearchResults();
    }
  }

  // Getters para uso en el template
  get currentConfig(): SearchConfig {
    const defaults: SearchConfig = {
      placeholder: 'Buscar...',
      minLength: 1,
      debounceTime: 300,
      caseSensitive: false,
      exactMatch: false,
      searchFields: []
    };
    
    return { ...defaults, ...this.config };
  }
}
