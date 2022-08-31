import { TestBed } from '@angular/core/testing';

import { MWSRouterService } from './mwsrouter.service';

describe('MWSRouterService', () => {
  let service: MWSRouterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MWSRouterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
