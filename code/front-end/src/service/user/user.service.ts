import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../dto/api';
import { MWSRouterService } from '../router/mwsrouter.service';
import { ListAdminsResponse } from './dto/user-response';
import { UserUpdate } from './dto/user-update';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private signUpUrl: string = `${environment.baseUrl}/user/create`;
  private loginUrl: string = `${environment.baseUrl}/user/login`;
  private listAdminsUrl: string = `${environment.baseUrl}/user/filter`;
  private updateUserUrl: string = `${environment.baseUrl}/user/update`;
  private deleteUserUrl: string = `${environment.baseUrl}/user/delete`;

  constructor(private http: HttpClient, private router: MWSRouterService) { }

  async signUpUser(email: string, password: string, firstName: string, lastName: string) {
    return await this.signUpInternal(this.router.getTenantId(), email, password, firstName, lastName, 'USER');
  }

  async signUpAdmin(tenantId: number, email: string, password: string, firstName: string, lastName: string) {
    return await this.signUpInternal(tenantId, email, password, firstName, lastName, 'ADMIN');
  }

  private async signUpInternal(tenantId: number, email: string, password: string, firstName: string, lastName: string, role: string) {
    const body = {
      tenantId,
      role,
      email,
      password,
      firstName,
      lastName
    }

    return await this.http.post<ApiResponse>(this.signUpUrl, body);
  }

  async updateUser(update: UserUpdate) {
    return await this.http
      .put<ApiResponse>(this.updateUserUrl, update);
  }

  async login(email: string, password: string) {
    const body = {
      tenantId: this.router.getTenantId(),
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

    return await this.http.delete<ApiResponse>(this.deleteUserUrl, { params });
  }
}
