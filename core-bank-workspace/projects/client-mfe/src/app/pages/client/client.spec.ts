import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ClientComponent } from './client';
import { ClientService } from '../../services/client-service';
import { NotificationService } from '../../services/notification-service';

describe('ClientComponent', () => {
  let component: ClientComponent;
  let fixture: ComponentFixture<ClientComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientComponent, HttpClientTestingModule],
      providers: [
        ClientService,
        NotificationService
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
