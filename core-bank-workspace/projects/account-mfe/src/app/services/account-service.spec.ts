import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AccountService } from './account-service';
import { NotificationService } from './notification-service';
import { Account, AccountApiResponse, CreateAccountDTO, UpdateAccountDTO } from '../interfaces/account.interfacte';
import { environment } from '../enviroments/enviroments';

describe('AccountService', () => {
  let service: AccountService;
  let httpMock: HttpTestingController;
  let notificationService: jasmine.SpyObj<NotificationService>;

  const mockApiResponse: AccountApiResponse = {
    accountId: 1,
    accountNumber: '1234567890',
    identification: '12345678',
    accountType: 'SAVINGS',
    balance: 1500.0,
    createdAt: '2023-01-01T00:00:00Z',
    updatedAt: '2023-01-01T00:00:00Z'
  };

  const mockAccount: Account = {
    id: 1,
    accountNumber: '1234567890',
    accountType: 'SAVINGS',
    balance: 1500.0,
    identification: '12345678',
    status: 'ACTIVE',
    createdAt: '2023-01-01T00:00:00Z'
  };

  beforeEach(() => {
    const notificationSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AccountService,
        { provide: NotificationService, useValue: notificationSpy }
      ]
    });

    service = TestBed.inject(AccountService);
    httpMock = TestBed.inject(HttpTestingController);
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAccounts', () => {
    it('should return accounts when API call is successful', () => {
      const mockResponse: AccountApiResponse[] = [mockApiResponse];

      service.getAccounts().subscribe(accounts => {
        expect(accounts).toEqual([mockAccount]);
        expect(accounts.length).toBe(1);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should return empty array and show error when API call fails', () => {
      service.getAccounts().subscribe(accounts => {
        expect(accounts).toEqual([]);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al cargar cuentas');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.error(new ProgressEvent('Network error'));
    });

    it('should pass filters as query parameters', () => {
      const filters = { clientReference: 1, status: 'ACTIVE' };

      service.getAccounts(filters).subscribe();

      const req = httpMock.expectOne(request => 
        request.url === `${environment.apiUrl}/${environment.path}` &&
        request.params.get('clientReference') === '1' &&
        request.params.get('status') === 'ACTIVE'
      );
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });
  });

  describe('getAccountById', () => {
    it('should return account when API call is successful', () => {
      const accountId = '1';

      service.getAccountById(accountId).subscribe(account => {
        expect(account).toEqual(mockAccount);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${accountId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const accountId = '1';

      service.getAccountById(accountId).subscribe(account => {
        expect(account).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al cargar cuenta');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${accountId}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('getAccountByNumber', () => {
    it('should return account when API call is successful', () => {
      const accountNumber = '1234567890';

      service.getAccountByNumber(accountNumber).subscribe(account => {
        expect(account).toEqual(mockAccount);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/by-number/${accountNumber}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const accountNumber = '1234567890';

      service.getAccountByNumber(accountNumber).subscribe(account => {
        expect(account).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al buscar cuenta');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/by-number/${accountNumber}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('getAccountsByClient', () => {
    it('should return client accounts when API call is successful', () => {
      const clientReference = 1;
      const mockResponse: AccountApiResponse[] = [mockApiResponse];

      service.getAccountsByClient(clientReference).subscribe(accounts => {
        expect(accounts).toEqual([mockAccount]);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/by-client/${clientReference}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should return empty array and show error when API call fails', () => {
      const clientReference = 1;

      service.getAccountsByClient(clientReference).subscribe(accounts => {
        expect(accounts).toEqual([]);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al cargar cuentas del cliente');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/by-client/${clientReference}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('createAccount', () => {
    it('should create account when API call is successful', () => {
      const createData: CreateAccountDTO = {
        accountNumber: '1234567890',
        identification: '12345678',
        accountType: 'SAVINGS',
        balance: 1500.0
      };

      service.createAccount(createData).subscribe(account => {
        expect(account).toEqual(mockAccount);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createData);
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const createData: CreateAccountDTO = {
        accountNumber: '1234567890',
        identification: '12345678',
        accountType: 'SAVINGS',
        balance: 1500.0
      };

      service.createAccount(createData).subscribe(account => {
        expect(account).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al crear cuenta');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('updateAccount', () => {
    it('should update account when API call is successful', () => {
      const accountId = '1';
      const updateData: UpdateAccountDTO = {
        balance: 2000.0
      };

      service.updateAccount(accountId, updateData).subscribe(account => {
        expect(account).toEqual(mockAccount);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${accountId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const accountId = '1';
      const updateData: UpdateAccountDTO = {
        balance: 2000.0
      };

      service.updateAccount(accountId, updateData).subscribe(account => {
        expect(account).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al actualizar cuenta');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${accountId}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('deleteAccount', () => {
    it('should return true when API call is successful', () => {
      const accountId = '1';

      service.deleteAccount(accountId).subscribe(result => {
        expect(result).toBe(true);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${accountId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should return false and show error when API call fails', () => {
      const accountId = '1';

      service.deleteAccount(accountId).subscribe(result => {
        expect(result).toBe(false);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al eliminar cuenta');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${accountId}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('getAccountsByType', () => {
    it('should return accounts by type when API call is successful', () => {
      const accountType = 'SAVINGS';
      const mockResponse: AccountApiResponse[] = [mockApiResponse];

      service.getAccountsByType(accountType).subscribe(accounts => {
        expect(accounts).toEqual([mockAccount]);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/by-type/${accountType}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should return empty array and show error when API call fails', () => {
      const accountType = 'SAVINGS';

      service.getAccountsByType(accountType).subscribe(accounts => {
        expect(accounts).toEqual([]);
        expect(notificationService.showError).toHaveBeenCalledWith('Error al cargar cuentas por tipo');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/by-type/${accountType}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('updateBalance', () => {
    it('should update balance when API call is successful', () => {
      const accountId = '1';
      const newBalance = 2500.0;

      service.updateBalance(accountId, newBalance).subscribe(account => {
        expect(account).toEqual(mockAccount);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${accountId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual({ currentBalance: newBalance });
      req.flush(mockApiResponse);
    });

    it('should return null and show error when API call fails', () => {
      const accountId = '1';
      const newBalance = 2500.0;

      service.updateBalance(accountId, newBalance).subscribe(account => {
        expect(account).toBeNull();
        expect(notificationService.showError).toHaveBeenCalledWith('Error al actualizar saldo');
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/${accountId}`);
      req.error(new ProgressEvent('Network error'));
    });
  });

  describe('validateAccountNumber', () => {
    it('should return true when account number does not exist (valid)', () => {
      const accountNumber = '1234567890';

      service.validateAccountNumber(accountNumber).subscribe(isValid => {
        expect(isValid).toBe(true);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/validate/${accountNumber}`);
      expect(req.request.method).toBe('GET');
      req.flush({ exists: false });
    });

    it('should return false when account number exists (invalid)', () => {
      const accountNumber = '1234567890';

      service.validateAccountNumber(accountNumber).subscribe(isValid => {
        expect(isValid).toBe(false);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/validate/${accountNumber}`);
      expect(req.request.method).toBe('GET');
      req.flush({ exists: true });
    });

    it('should return false when API call fails', () => {
      const accountNumber = '1234567890';

      service.validateAccountNumber(accountNumber).subscribe(isValid => {
        expect(isValid).toBe(false);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/${environment.path}/validate/${accountNumber}`);
      req.error(new ProgressEvent('Network error'));
    });
  });
});
