import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { OrderProductResponse } from './dto/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private orderProductsUrl: string = `${environment.baseUrl}/order/create`;

  constructor(private http: HttpClient) { }

  async orderProduct(userId: number, productId: number) {
    console.log("orderProduct: " + productId);
    const body = {
      userId,
      productId
    }

    return await this.http
      .post<OrderProductResponse>(this.orderProductsUrl, body);
  }
}
