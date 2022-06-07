import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ListProductResponse } from './dto/product';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  private listProductsUrl: string = `${environment.baseUrl}/product/list`;

  constructor(private http: HttpClient) { }

  async list() {
    let params = new HttpParams().set("active", "true");

    return await this.http
      .get<ListProductResponse>(this.listProductsUrl, { params });
  }
}
