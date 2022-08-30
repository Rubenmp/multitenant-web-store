import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../dto/api';
import { ListTenantsResponse } from './dto/tenant';

@Injectable({
  providedIn: 'root'
})
export class TenantService {
  private createTenantUrl: string = `${environment.baseUrl}/tenant/create`;
  private updateTenantUrl: string = `${environment.baseUrl}/tenant/update`;
  private listTenantsUrl: string = `${environment.baseUrl}/tenant/list`;
  private deleteTenantUrl: string = `${environment.baseUrl}/tenant/delete`;

  constructor(private http: HttpClient) { }

  async createTenant(name: string) {
    return await this.http
      .post<ApiResponse>(this.createTenantUrl, { name });
  }


  async updateTenant(id: number, name: string) {
    return await this.http
      .put<ApiResponse>(this.updateTenantUrl, { id, name });
  }


  async listTenants() {
    return await this.http.get<ListTenantsResponse>(this.listTenantsUrl);
  }

  async delete(id: number) {
    let params = new HttpParams().set("id", id);

    return await this.http.delete<ApiResponse>(this.deleteTenantUrl, { params });
  }
}
