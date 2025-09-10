import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter, OnInit, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ui-modal',
  templateUrl: './modal.html',
  styleUrls: ['./modal.css'],
  imports: [CommonModule, FormsModule],
  standalone: true
})
export class Modal implements OnInit, OnChanges {
  @Input() title: string = 'Modal Title';
  @Input() isOpen: boolean = false;
  @Input() data: any = null;
  @Input() loading: boolean = false;
  @Input() showForm: boolean = true; // Controlar si mostrar formulario o contenido personalizado
  
  // Configuración de botones
  @Input() submitButtonText: string = 'Guardar';
  @Input() cancelButtonText: string = 'Cancelar';
  @Input() submitButtonClass: string = 'btn btn-primary';
  @Input() cancelButtonClass: string = 'btn btn-secondary';
  @Input() showFooter: boolean = true;

  @Output() closeModal = new EventEmitter<void>();
  @Output() submitForm = new EventEmitter<any>();

  @Input() fields: any[] = []; // Array to hold custom fields

  ngOnInit(): void {
    // Si hay data, mapear a los campos
    if (this.data && this.fields.length > 0) {
      this.mapDataToFields();
    }
  }

  ngOnChanges(): void {
    // Re-mapear cuando cambien los datos
    if (this.data && this.fields.length > 0) {
      this.mapDataToFields();
    }
  }

  private mapDataToFields(): void {
    this.fields.forEach(field => {
      if (this.data && this.data[field.id] !== undefined) {
        field.value = this.data[field.id];
      }
    });
  }

  close(): void {
    this.closeModal.emit();
  }

  mapperFields(fields: any[]): any {
    return fields.reduce((acc, field) => {
      acc[field.id] = field.value;
      return acc;
    }, {});
  }

  submit(): void {
    if (this.loading) return; // Prevenir submit múltiple
    
    if (this.showForm) {
      // Emit the form data when submitted
      this.submitForm.emit(this.mapperFields(this.fields));
    } else {
      // Para modales sin formulario (como confirmación de delete)
      // Pasar los datos originales del cliente seleccionado
      this.submitForm.emit(this.data);
    }
  }

  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget) {
      this.close();
    }
  }

  getFieldType(field: any): string {
    return field.type || 'text';
  }

  isFieldRequired(field: any): boolean {
    return field.required === true;
  }

  // TrackBy functions para mejor performance
  trackByFieldId(index: number, field: any): any {
    return field.id;
  }

  trackByOptionValue(index: number, option: any): any {
    return option.value;
  }
}
