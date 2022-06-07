import { Component, OnInit } from '@angular/core';
import { isOkResponse } from 'src/service/dto/api';
import { Product } from 'src/service/dto/product';
import { ProductsService } from '../../service/products.service';
import { NotificationService } from '../notification.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {
  private products: Product[] | undefined;

  constructor(private productsService: ProductsService, private notificationService: NotificationService) { }

  async ngOnInit(): Promise<void> {
    const productsResponse = await (await this.productsService.list()).subscribe(
      response => {
        if (isOkResponse(response)) {
          this.products = response.data;
          console.log("isokresponse true")
          console.log(this.products);
          this.notificationService.showError();
        } else {
          console.log("isOkResponse no (error)");
          this.notificationService.showError();
          console.log(response);
        }
      }
    );
  }
}
