import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { isOkResponse } from 'src/service/dto/api';
import { NotificationService } from 'src/service/notification/notification.service';
import { Product } from 'src/service/product/dto/product';
import { ProductsService } from 'src/service/product/products.service';


type TableRow = { id: number, name: string, image: string, description: string, isBeingUpdated: boolean, isSelected: boolean }
type RowCreation = { name: string, imageUrl: string, description: string }

@Component({
  selector: 'app-products-admin',
  templateUrl: './products-admin.component.html',
  styleUrls: ['./products-admin.component.scss']
})
export class ProductsAdminComponent implements OnInit {
  products: Product[] = []

  displayedColumns: string[] = ['id', 'name', 'image', 'description'];
  dataSource!: MatTableDataSource<Product>;

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  filterById: string = "";
  filterByProductName: string = "";

  // Creation
  toCreate: RowCreation[] = [];
  inputName: string = '';
  inputImageUrl: string = '';
  inputDescription: string = '';
  isCreating: boolean = false;
  displayedColumnsToCreate: string[] = ['name', 'imageUrl', 'description'];
  dataSourceToCreate!: MatTableDataSource<RowCreation>;

  // Update/delete
  selectedRows: number = 0;
  isBeingUpdated: boolean = false;



  constructor(private productsService: ProductsService,
    private notificationService: NotificationService) {
  }


  async ngOnInit(): Promise<void> {
    await this.fetchProducts();
  }

  async fetchProducts() {
    await (await this.productsService.list()).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.products = response.data;
          this.updateProductsInTable(this.products);
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (e) => {
        this.notificationService.showError(e.error.message);
      },
    });

    this.isCreating = false;
    this.isBeingUpdated = false;
    this.selectedRows = 0;
  }


  updateProductsInTable(product: Product[]) {
    this.dataSource = new MatTableDataSource(product);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  applyFilterById(event: Event) {
    const filter = this.getFilterValue(event);
    this.filterById = filter;
    this.applyFilterInternal()
  }

  applyFilterByProductName(event: Event) {
    const filter = this.getFilterValue(event);
    this.filterByProductName = filter;
    this.applyFilterInternal()
  }


  private applyFilterInternal() {
    /*
    const newOrders = this.products.filter(o => o.product.name.toLowerCase().includes(this.filterByProductName))
      .filter(o => o.id.toLocaleString().includes(this.filterById));
    this.updateProductsInTable(newOrders);*/
  }

  private getFilterValue(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    return filterValue.trim().toLowerCase();
  }










  // TODO
  clickRow(row: TableRow) {
    if (this.isBeingUpdated) {
      return;
    }
    if (row.isSelected) {
      row.isSelected = false;
      this.selectedRows -= 1;
    } else {
      row.isSelected = true;
      this.selectedRows += 1;
    }
  }

  isRowSelected() {
    return this.selectedRows > 0;
  }

  // Tenants operations
  async create() {
    if (this.isCreating) {
      const row = this.toCreate[0];
      await (await this.productsService.create(row.name, row.imageUrl, row.description)).subscribe({
        next: async (response) => {
          if (isOkResponse(response)) {
            this.notificationService.showInfoMessage("Tenant created with id " + response.data);
            
            this.fetchProducts();
          } else {
            this.notificationService.showError(response.message);
          }
        },
        error: (e) => {
          this.notificationService.showError(e.error.message);
        },
      });

    } else {
      this.isCreating = true;
      this.toCreate = [{ name: '', imageUrl: '', description: '' }]
      this.dataSourceToCreate = new MatTableDataSource(this.toCreate);
    }
  }


  async cancelAction() {
    await this.fetchProducts();
    if (this.paginator && !this.paginator.pageSize) {
      this.paginator.pageSize = 5;
    }
  }


  async update() {
    /*
    if (this.selectedRows > 0) {
      const rowsToUpdate = this.products.filter(row => row.isSelected);
      const isUpdating = (rowsToUpdate.length > 0 && rowsToUpdate[0].isBeingUpdated);
      if (isUpdating) {
        for (let rowToUpdate of rowsToUpdate) {
          await (await this.productsService.updateTenant(rowToUpdate.tenantId, rowToUpdate.name)).subscribe({
            next: (response) => {
              if (isOkResponse(response)) {
                this.notificationService.showInfoMessage("Tenant updated");
              } else {
                this.notificationService.showError(response.message);
              }
            },
            error: (e) => {
              this.notificationService.showError(e.error.message);
            },
          });
        }
        await this.fetchProducts();
      
      } else {
        rowsToUpdate.forEach(row => row.isBeingUpdated = true);
        this.isBeingUpdated = true;
      }
    }*/
  }


  async deleteSelected() {/*
    if (this.selectedRows > 0) {
      const selectedTenantIds = this.products.filter(row => row.isSelected).map(tenant => tenant.tenantId);

      for (let selectedTenantId of selectedTenantIds) {
        await (await this.productsService.delete(selectedTenantId)).subscribe({
          next: async (response) => {
            if (isOkResponse(response)) {
              this.notificationService.showInfoMessage("Tenant deleted");
              await this.fetchProducts();
            } else {
              this.notificationService.showError(response.message);
            }
          },
          error: (e) => {
            this.notificationService.showError(e.error.message);
          },
        });
      }
    }*/
  }

}
