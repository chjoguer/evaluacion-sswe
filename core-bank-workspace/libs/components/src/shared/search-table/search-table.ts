import { Component, Input, Output, EventEmitter, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Search, SearchConfig, SearchResult } from '../search/search';
import { Table } from '../table/table';

export interface SearchTableConfig {
  searchConfig: SearchConfig;
  tableColumns: { key: string, title: string }[];
  showCreateButton?: boolean;
  createButtonText?: string;
  createButtonClass?: string;
}

@Component({
  selector: 'app-ui-search-table',
  templateUrl: './search-table.html',
  styleUrls: ['./search-table.css'],
  imports: [CommonModule, Search, Table],
  standalone: true
})
export class SearchTable implements OnInit {
  @Input() data: any[] = [];
  @Input() config: SearchTableConfig = {
    searchConfig: {
      placeholder: 'Buscar...',
      searchFields: [],
      minLength: 1,
      debounceTime: 300,
      caseSensitive: false,
      exactMatch: false
    },
    tableColumns: [],
    showCreateButton: false,
    createButtonText: 'Crear Nuevo',
    createButtonClass: 'btn btn-primary'
  };
  @Input() loading: boolean = false;

  @Output() deleteRow = new EventEmitter<any>();
  @Output() editRow = new EventEmitter<any>();
  @Output() createNew = new EventEmitter<void>();
  @Output() searchResults = new EventEmitter<SearchResult>();

  // Signal para los datos filtrados
  filteredData = signal<any[]>([]);

  ngOnInit(): void {
    this.filteredData.set([...this.data]);
  }

  onSearchResults(result: SearchResult): void {
    this.filteredData.set(result.filteredData);
    this.searchResults.emit(result);
  }

  onDeleteRow(row: any): void {
    this.deleteRow.emit(row);
  }

  onEditRow(row: any): void {
    this.editRow.emit(row);
  }

  onCreateNew(): void {
    this.createNew.emit();
  }

  // Método para actualizar datos desde el componente padre
  updateData(newData: any[]): void {
    this.data = newData;
    // Si no hay búsqueda activa, mostrar todos los datos
    if (!this.filteredData() || this.filteredData().length === 0) {
      this.filteredData.set([...newData]);
    }
  }
}
