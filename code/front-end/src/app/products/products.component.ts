import { Component, OnInit } from '@angular/core';
import { isOkResponse } from 'src/service/dto/api';
import { NotificationService } from 'src/service/notification/notification.service';
import { Product } from 'src/service/product/dto/product';
import { ProductsService } from '../../service/product/products.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {
  private products: Product[] = [];

  constructor(private productsService: ProductsService,
    private notificationService: NotificationService) { }

  async ngOnInit(): Promise<void> {
    await (await this.productsService.list()).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.products = response.data;
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (_) => {
        this.notificationService.showError("Internal error fetching products.");
      },
    });
  }

  getProducts() : Product[]{ 
    return this.products;
  }
}
