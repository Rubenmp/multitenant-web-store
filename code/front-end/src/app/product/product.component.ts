import { Component, Input, OnInit } from '@angular/core';
import { Product } from 'src/service/product/dto/product';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.scss']
})
export class ProductComponent implements OnInit {
  @Input()
  product!: Product;

  constructor() { }

  ngOnInit(): void {
    console.log("product init");
    console.log(this.product);
  }

}
