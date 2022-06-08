import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {
  TOKEN_KEY: string = 'TOKEN';
  USER_FULL_NAME_KEY: string = 'USER_FULL_NAME';

  constructor() { }

  storeToken(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | undefined {
    return localStorage.getItem(this.TOKEN_KEY) || undefined;
  }

  storeUserFirstLastName(userFullName: string) {
    localStorage.setItem(this.USER_FULL_NAME_KEY, userFullName);
  }

  getUserFirstLastName(): string | undefined {
    return localStorage.getItem(this.USER_FULL_NAME_KEY) || undefined;
  }

  clearStorage() {
    localStorage.removeItem(this.TOKEN_KEY);
  }

}
