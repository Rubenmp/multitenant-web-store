import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(private http: HttpClient) { }

  public async call() {
    console.log("inside products service")

    const products = await this.http
      .get<any[]>("http://localhost:8080/product/list").subscribe();


    console.log("products service end async call")
    console.log(products)
  }
}
