import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { isOkResponse } from 'src/service/dto/api';
import { NotificationService } from 'src/service/notification/notification.service';
import { Product } from 'src/service/product/dto/product';
import { ProductsService } from 'src/service/product/products.service';

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
}
