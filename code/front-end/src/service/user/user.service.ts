import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../dto/api';
import { ListAdminsResponse } from './dto/user-response';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private signUpUrl: string = `${environment.baseUrl}/user/create`;
  private loginUrl: string = `${environment.baseUrl}/user/login`;
  private listAdminsUrl: string = `${environment.baseUrl}/user/filter`;
  private deleteAdminUrl: string = `${environment.baseUrl}/user/delete`;

  constructor(private http: HttpClient) { }

  async signUp(email: string, password: string, firstName: string, lastName: string) {
    const body = {
      tenantId: 1, // TODO: set this parameter dynamically
      role: "USER",
      email,
      password,
      firstName,
      lastName
    }

    return await this.http.post<ApiResponse>(this.signUpUrl, body);
  }

  async login(email: string, password: string) {
    const body = {
      email,
      password
    }

    return await this.http.post<ApiResponse>(this.loginUrl, body);
  }

  async listAdmins() {
    return await this.http.get<ListAdminsResponse>(this.listAdminsUrl);
  }

  async deleteUser(id: number) {
    let params = new HttpParams().set("id", id);

    return await this.http.delete<ApiResponse>(this.deleteAdminUrl, { params });
  }
}
