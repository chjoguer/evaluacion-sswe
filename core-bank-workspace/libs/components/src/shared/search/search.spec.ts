import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Search, SearchConfig, SearchResult } from './search';

describe('SearchComponent', () => {
  let component: Search;
  let fixture: ComponentFixture<Search>;

  const mockData = [
    { id: 1, name: 'Juan Pérez', email: 'juan@example.com', city: 'Madrid' },
    { id: 2, name: 'María García', email: 'maria@example.com', city: 'Barcelona' },
    { id: 3, name: 'Pedro Rodríguez', email: 'pedro@example.com', city: 'Valencia' }
  ];

  const mockConfig: SearchConfig = {
    placeholder: 'Buscar usuarios...',
    searchFields: ['name', 'email', 'city'],
    minLength: 2,
    debounceTime: 100,
    caseSensitive: false,
    exactMatch: false
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Search, CommonModule, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(Search);
    component = fixture.componentInstance;
    component.data = mockData;
    component.config = mockConfig;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with all data', () => {
    expect(component.filteredData()).toEqual(mockData);
    expect(component.resultCount()).toBe(3);
  });

  it('should filter data when searching', (done) => {
    component.searchResults.subscribe((result: SearchResult) => {
      if (result.searchTerm === 'juan') {
        expect(result.filteredData.length).toBe(1);
        expect(result.filteredData[0].name).toBe('Juan Pérez');
        done();
      }
    });

    component.onSearchChange('juan');
  });

  it('should clear search results', () => {
    component.onSearchChange('juan');
    component.clearSearch();
    
    expect(component.searchTerm()).toBe('');
    expect(component.filteredData()).toEqual(mockData);
  });

  it('should handle case insensitive search', (done) => {
    component.searchResults.subscribe((result: SearchResult) => {
      if (result.searchTerm === 'JUAN') {
        expect(result.filteredData.length).toBe(1);
        done();
      }
    });

    component.onSearchChange('JUAN');
  });

  it('should search in multiple fields', (done) => {
    component.searchResults.subscribe((result: SearchResult) => {
      if (result.searchTerm === 'barcelona') {
        expect(result.filteredData.length).toBe(1);
        expect(result.filteredData[0].city).toBe('Barcelona');
        done();
      }
    });

    component.onSearchChange('barcelona');
  });

  it('should respect minimum length', () => {
    component.onSearchChange('a'); // Less than minLength (2)
    expect(component.filteredData()).toEqual(mockData);
  });

  it('should update data correctly', () => {
    const newData = [{ id: 4, name: 'Ana López', email: 'ana@example.com', city: 'Sevilla' }];
    component.updateData(newData);
    
    expect(component.filteredData()).toEqual(newData);
  });
});
