import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-ui-table',
  imports: [],
  templateUrl: './table.html',
  styleUrl: './table.css'
})
export class Table {
  @Input() columns: { key: string, title: string }[] = [];
  @Input() data: any[] = [];
  @Input() loading: boolean = false;

  @Output() deleteRow = new EventEmitter<any>();
  @Output() openModal = new EventEmitter<any>();
  @Output() openModalAlert = new EventEmitter<any>();

  onDelete(row: any) {
    console.log('Delete row:', row);
    this.deleteRow.emit(row);
    this.openModalAlert.emit(row);

  }

  onEdit(row: any) {
    this.openModal.emit(row);
    console.log('Edit row:', this.openModal);
    console.log('Edit row:', row);
  }
}
