import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../dto/api';
import { ListProductResponse } from './dto/product';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  private createUrl: string = `${environment.baseUrl}/product/create`;
  private updateUrl: string = `${environment.baseUrl}/product/update`;
  private listProductsUrl: string = `${environment.baseUrl}/product/list`;
  private deleteUrl: string = `${environment.baseUrl}/product/delete`;

  constructor(private http: HttpClient) { }

  async create(name: string) {
    return await this.http
      .post<ApiResponse>(this.createUrl, { name });
  }


  async update(id: number, name: string) {
    return await this.http
      .put<ApiResponse>(this.updateUrl, { id, name });
  }

  async delete(id: number) {
    let params = new HttpParams().set("id", id);

    return await this.http.delete<ApiResponse>(this.deleteUrl, { params });
  }

  async list() {
    let params = new HttpParams().set("active", "true");

    return await this.http
      .get<ListProductResponse>(this.listProductsUrl, { params });
  }
}
