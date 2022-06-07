import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(private http: HttpClient) { }

  public async call() {
    console.log("inside products service")

    const products = await this.http
      .get<any[]>(`${environment.baseUrl}/product/list`).subscribe();

    console.log("products service end async call")
    console.log(products)
  }
}
