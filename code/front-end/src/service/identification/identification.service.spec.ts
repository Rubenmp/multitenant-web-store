import { TestBed } from '@angular/core/testing';

import { IdentificationService } from './identification.service';

describe('IdentificationService', () => {
  let service: IdentificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IdentificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
