// libs/models/src/lib/cliente.ts
export interface Cliente {
  id?: string;
  nombre: string;
  email: string;
  segmento: 'PERSONA' | 'PYME' | 'CORP';
}