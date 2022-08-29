import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../dto/api';
import { ListTenantsResponse } from './dto/tenant';

@Injectable({
  providedIn: 'root'
})
export class TenantService {
  private createTenantUrl: string = `${environment.baseUrl}/order/create`;
  private listTenantsUrl: string = `${environment.baseUrl}/tenant/list`;

  constructor(private http: HttpClient) { }

  async createTenant(tenantName: string) {
    return await this.http
      .post<ApiResponse>(this.createTenantUrl, { tenantName });
  }

  async listOrders() {
    return await this.http.get<ListTenantsResponse>(this.listTenantsUrl);
  }
}
