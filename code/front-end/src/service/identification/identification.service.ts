import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../dto/api';


@Injectable({
  providedIn: 'root'
})
export class IdentificationService {
  private signUpUrl: string = `${environment.baseUrl}/user/create`;
  private loginUrl: string = `${environment.baseUrl}/user/login`;

  constructor(private http: HttpClient) { }

  async signup(email: string, password: string, firstName: string, lastName: string) {
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
}
