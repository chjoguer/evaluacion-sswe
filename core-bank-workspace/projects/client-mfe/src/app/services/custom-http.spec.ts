import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { CustomHttp } from './custom-http';

describe('CustomHttp', () => {
  let service: CustomHttp<any>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CustomHttp]
    });
    service = TestBed.inject(CustomHttp);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
