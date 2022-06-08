import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../dto/api';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private orderProductsUrl: string = `${environment.baseUrl}/order/create`;

  constructor(private http: HttpClient) { }

  async orderProduct(productId: number) {
    return await this.http
      .post<ApiResponse>(this.orderProductsUrl, { productId });
  }
}
