import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClientService } from './client-service';
import { NotificationService } from './notification-service';
import { Client, ClientApiResponse, CreateClientDTO, UpdateClientDTO } from '../interfaces/client.interface';
import { environment } from '../enviroments/enviroments';

describe('ClientService', () => {
  let service: ClientService;
  let httpMock: HttpTestingController;
  let notificationService: jasmine.SpyObj<NotificationService>;

  const mockApiResponse: ClientApiResponse = {
    id: '1',
    fullName: 'Juan Pérez',
    identification: '12345678',
    cellphone: '0987654321',
    direction: 'Calle 123',
    documentType: 'CEDULA',
    password: 'password123',
    status: true,
    createdAt: '2023-01-01T00:00:00Z',
    updatedAt: '2023-01-01T00:00:00Z'
  };

  const mockClient: Client = {
    id: '1',
    fullName: 'Juan Pérez',
    identification: '12345678',
    cellphone: '0987654321',
    direction: 'Calle 123',
    documentType: 'CEDULA',
    password: 'password123',
    status: true,
    createdAt: '2023-01-01T00:00:00Z',
    updatedAt: '2023-01-01T00:00:00Z'
  };

  beforeEach(() => {
    const notificationSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ClientService,
        { provide: NotificationService, useValue: notificationSpy }
      ]
    });

    service = TestBed.inject(ClientService);
    httpMock = TestBed.inject(HttpTestingController);
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getUsers', () => {
    it('should return clients when API call is successful', () => {
      const mockResponse: ClientApiResponse[] = [mockApiResponse];

      service.getUsers().subscribe(clients => {
        expect(clients).toEqual([mockClient]);
        expect(clients.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should return empty array and show error when API call fails', () => {
      service.getUsers().subscribe(clients => {
        expect(clients).toEqual([]);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al cargar clientes');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.error(new ProgressEvent('Network error'));
    });

    it('should pass filters as query parameters', () => {
      const filters = { name: 'Juan', active: true };

      service.getUsers(filters).subscribe();

      const req = httpMock.expectOne(request => 
        request.url === `${environment.apiUrl}/${environment.path}` &&
        request.params.get('name') === 'Juan' &&
        request.params.get('active') === 'true'
      );
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });
  });

  describe('getUserById', () => {
    it('should return client when API call is successful', () => {
      const clientId = '1';

      service.getUserById(clientId).subscribe(client => {
        expect(client).toEqual(mockClient);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${clientId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const clientId = '1';

      service.getUserById(clientId).subscribe(client => {
        expect(client).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al obtener cliente');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${clientId}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('createUser', () => {
    it('should create client when API call is successful', () => {
      const createData: CreateClientDTO = {
        fullName: 'Juan Pérez',
        direction: 'Calle 123',
        cellphone: '0987654321',
        password: 'password123',
        status: true,
        documentType: 'CEDULA',
        identification: '12345678'
      };

      service.createUser(createData).subscribe(client => {
        expect(client).toEqual(mockClient);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createData);
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const createData: CreateClientDTO = {
        fullName: 'Juan Pérez',
        direction: 'Calle 123',
        cellphone: '0987654321',
        password: 'password123',
        status: true,
        documentType: 'CEDULA',
        identification: '12345678'
      };

      service.createUser(createData).subscribe(client => {
        expect(client).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al crear cliente');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('updateUser', () => {
    it('should update client when API call is successful', () => {
      const identification = '12345678';
      const updateData: Partial<UpdateClientDTO> = {
        fullName: 'Juan Carlos Pérez'
      };

      service.updateUser(identification, updateData).subscribe(client => {
        expect(client).toEqual(mockClient);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${identification}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const identification = '12345678';
      const updateData: Partial<UpdateClientDTO> = {
        fullName: 'Juan Carlos Pérez'
      };

      service.updateUser(identification, updateData).subscribe(client => {
        expect(client).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al actualizar cliente');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${identification}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('deleteUser', () => {
    it('should return true when API call is successful', () => {
      const identification = '12345678';

      service.deleteUser(identification).subscribe(result => {
        expect(result).toBe(true);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${identification}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should return false and show error when API call fails', () => {
      const identification = '12345678';

      service.deleteUser(identification).subscribe(result => {
        expect(result).toBe(false);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al eliminar cliente');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${identification}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('getClients', () => {
    it('should call getUsers method', () => {
      spyOn(service, 'getUsers').and.returnValue(service.getUsers());
      
      service.getClients();
      
      expect(service.getUsers).toHaveBeenCalled();
    });
  });

  describe('getUsersByRole', () => {
    it('should return clients by role when API call is successful', () => {
      const role = 'admin';
      const mockResponse: ClientApiResponse[] = [mockApiResponse];

      service.getUsersByRole(role).subscribe(clients => {
        expect(clients).toEqual([mockClient]);
      });

      const req = httpMock.expectOne(request => 
        request.url === `${environment.apiUrl}/${environment.path}/by-role` &&
        request.params.get('role') === role
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should return empty array and show error when API call fails', () => {
      const role = 'admin';

      service.getUsersByRole(role).subscribe(clients => {
        expect(clients).toEqual([]);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al obtener clientes por rol');
      });

      const req = httpMock.expectOne(request => 
        request.url === `${environment.apiUrl}/${environment.path}/by-role`
      );
      req.error(new ProgressEvent('Network error'));
    });
  });
});
