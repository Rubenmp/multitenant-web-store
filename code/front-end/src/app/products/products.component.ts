import { Component, OnInit } from '@angular/core';
import { isOkResponse } from 'src/service/dto/api';
import { ProductsService } from '../../service/products.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {

  constructor(private productsService: ProductsService) { }

  async ngOnInit(): Promise<void> {
    const productsResponse = await this.productsService.list();

    if (isOkResponse(productsResponse)) {
      console.log("isOkResponse yes");
    } else {
      console.log("isOkResponse no");
    }

    console.log(`${JSON.stringify(productsResponse)}`);
  }

}
