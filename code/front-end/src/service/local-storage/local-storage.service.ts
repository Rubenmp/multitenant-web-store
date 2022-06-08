import { Injectable } from '@angular/core';
import { AuthenticationData } from '../identification/dto/authentication-response';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {
  TOKEN_KEY: string = 'TOKEN';
  USER_FULL_NAME_KEY: string = 'USER_FULL_NAME';
  USER_ROLE_KEY: string = 'USER_ROLE';

  constructor() { }

  saveUserInfo(info: AuthenticationData) {
    this.saveToken(info.token);
    this.saveUserFirstLastName(info.firstName + " " + info.lastName);
    this.saveUserRole(info.role)
  }

  private saveToken(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | undefined {
    return localStorage.getItem(this.TOKEN_KEY) || undefined;
  }

  private saveUserFirstLastName(userFullName: string) {
    localStorage.setItem(this.USER_FULL_NAME_KEY, userFullName);
  }

  getUserFirstLastName(): string | undefined {
    return localStorage.getItem(this.USER_FULL_NAME_KEY) || undefined;
  }

  private saveUserRole(role: string) {
    localStorage.setItem(this.USER_ROLE_KEY, role);
  }

  getUserRole(): string | undefined {
    return localStorage.getItem(this.USER_ROLE_KEY) || undefined;
  }

  clearStorage() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_FULL_NAME_KEY);
    localStorage.removeItem(this.USER_ROLE_KEY);
  }

}
