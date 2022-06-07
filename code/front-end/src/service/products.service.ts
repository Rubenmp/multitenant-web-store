import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ListProductResponse, Product } from './dto/product';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(private http: HttpClient) { }

  async list() {
    return await this.http
      .get<ListProductResponse>(`${environment.baseUrl}/product/list`);
  }
}
