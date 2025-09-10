import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MovementService } from './movement-service';
import { NotificationService } from './notification-service';
import { Movement, MovementApiResponse, CreateMovementDTO, UpdateMovementDTO } from '../interfaces/movement.interface';
import { environment } from '../enviroments/enviroments';

describe('MovementService', () => {
  let service: MovementService;
  let httpMock: HttpTestingController;
  let notificationService: jasmine.SpyObj<NotificationService>;

  const mockApiResponse: MovementApiResponse = {
    id: 1,
    uniqueKey: '3b1f1f9e-7d6c-4c0e-9a4b-5c5a3e2b9c10',
    accountId: 1,
    occurredAt: '2025-09-08T10:00:00Z',
    movementType: 'CREDIT',
    amount: 500.0,
    balance: 1500.0,
    description: 'Depósito inicial',
    reference: 'DEP-001',
    createdAt: '2025-09-09T00:14:32.229831Z',
    updatedAt: '2025-09-09T00:14:32.229831Z'
  };

  const mockMovement: Movement = {
    id: 1,
    uniqueKey: '3b1f1f9e-7d6c-4c0e-9a4b-5c5a3e2b9c10',
    accountId: 1,
    occurredAt: '2025-09-08T10:00:00Z',
    movementType: 'CREDIT',
    amount: 500.0,
    balance: 1500.0,
    description: 'Depósito inicial',
    reference: 'DEP-001',
    createdAt: '2025-09-09T00:14:32.229831Z',
    updatedAt: '2025-09-09T00:14:32.229831Z'
  };

  beforeEach(() => {
    const notificationSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        MovementService,
        { provide: NotificationService, useValue: notificationSpy }
      ]
    });

    service = TestBed.inject(MovementService);
    httpMock = TestBed.inject(HttpTestingController);
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getMovements', () => {
    it('should return movements when API call is successful', () => {
      const mockResponse: MovementApiResponse[] = [mockApiResponse];

      service.getMovements().subscribe(movements => {
        expect(movements).toEqual([mockMovement]);
        expect(movements.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should return empty array and show error when API call fails', () => {
      service.getMovements().subscribe(movements => {
        expect(movements).toEqual([]);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al cargar movimientos');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.error(new ProgressEvent('Network error'));
    });

    it('should pass filters as query parameters', () => {
      const filters = { accountId: 1, movementType: 'CREDIT' };

      service.getMovements(filters).subscribe();

      const req = httpMock.expectOne(request => 
        request.url === `${environment.apiUrl}/${environment.path}` &&
        request.params.get('accountId') === '1' &&
        request.params.get('movementType') === 'CREDIT'
      );
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });
  });

  describe('getMovementsByAccount', () => {
    it('should call getMovements with accountId filter', () => {
      const accountId = 1;
      spyOn(service, 'getMovements').and.returnValue(service.getMovements({ accountId }));
      
      service.getMovementsByAccount(accountId);
      
      expect(service.getMovements).toHaveBeenCalledWith({ accountId });
    });
  });

  describe('getMovementById', () => {
    it('should return movement when API call is successful', () => {
      const movementId = 1;

      service.getMovementById(movementId).subscribe(movement => {
        expect(movement).toEqual(mockMovement);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${movementId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const movementId = 1;

      service.getMovementById(movementId).subscribe(movement => {
        expect(movement).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al cargar movimiento');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${movementId}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('createMovement', () => {
    it('should create movement when API call is successful', () => {
      const createData: CreateMovementDTO = {
        accountId: 1,
        occurredAt: '2025-09-08T10:00:00Z',
        movementType: 'CREDIT',
        amount: 500.0,
        description: 'Depósito inicial',
        reference: 'DEP-001'
      };

      service.createMovement(createData).subscribe(movement => {
        expect(movement).toEqual(mockMovement);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createData);
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const createData: CreateMovementDTO = {
        accountId: 1,
        occurredAt: '2025-09-08T10:00:00Z',
        movementType: 'CREDIT',
        amount: 500.0,
        description: 'Depósito inicial',
        reference: 'DEP-001'
      };

      service.createMovement(createData).subscribe(movement => {
        expect(movement).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al crear movimiento');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('updateMovement', () => {
    it('should update movement when API call is successful', () => {
      const movementId = 1;
      const updateData: UpdateMovementDTO = {
        description: 'Descripción actualizada'
      };

      service.updateMovement(movementId, updateData).subscribe(movement => {
        expect(movement).toEqual(mockMovement);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${movementId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const movementId = 1;
      const updateData: UpdateMovementDTO = {
        description: 'Descripción actualizada'
      };

      service.updateMovement(movementId, updateData).subscribe(movement => {
        expect(movement).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al actualizar movimiento');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${movementId}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('deleteMovement', () => {
    it('should return true when API call is successful', () => {
      const movementId = 1;

      service.deleteMovement(movementId).subscribe(result => {
        expect(result).toBe(true);
        expect(notificationService.showSuccess).toHaveBeenCalledWith('Movimiento eliminado correctamente');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${movementId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should return false and show error when API call fails', () => {
      const movementId = 1;

      service.deleteMovement(movementId).subscribe(result => {
        expect(result).toBe(false);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al eliminar movimiento');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${movementId}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('searchMovements', () => {
    it('should filter movements based on search term', () => {
      const searchTerm = 'depósito';
      const mockResponse: MovementApiResponse[] = [
        mockApiResponse,
        {
          ...mockApiResponse,
          id: 2,
          description: 'Retiro en cajero',
          movementType: 'DEBIT'
        }
      ];

      service.searchMovements(searchTerm).subscribe(movements => {
        expect(movements.length).toBe(1);
        expect(movements[0].description.toLowerCase()).toContain(searchTerm.toLowerCase());
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.flush(mockResponse);
    });

    it('should filter movements by reference', () => {
      const searchTerm = 'DEP-001';
      const mockResponse: MovementApiResponse[] = [mockApiResponse];

      service.searchMovements(searchTerm).subscribe(movements => {
        expect(movements.length).toBe(1);
        expect(movements[0].reference).toContain(searchTerm);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.flush(mockResponse);
    });

    it('should filter movements by movement type', () => {
      const searchTerm = 'CREDIT';
      const mockResponse: MovementApiResponse[] = [mockApiResponse];

      service.searchMovements(searchTerm).subscribe(movements => {
        expect(movements.length).toBe(1);
        expect(movements[0].movementType).toContain(searchTerm);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.flush(mockResponse);
    });

    it('should return empty array when no movements match search term', () => {
      const searchTerm = 'noexiste';
      const mockResponse: MovementApiResponse[] = [mockApiResponse];

      service.searchMovements(searchTerm).subscribe(movements => {
        expect(movements.length).toBe(0);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.flush(mockResponse);
    });
  });
});
