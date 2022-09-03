import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductsAdminComponent } from './products-admin.component';

describe('ProductsAdminComponent', () => {
  let component: ProductsAdminComponent;
  let fixture: ComponentFixture<ProductsAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductsAdminComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductsAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
