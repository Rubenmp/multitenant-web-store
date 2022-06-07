import { Component, OnInit } from '@angular/core';
import { ProductsService } from '../products.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {

  constructor(public productsService: ProductsService) { }

  ngOnInit(): void {
    console.log("products init")
    this.productsService.call();
  }

}
