import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {
  TOKEN_KEY: string = 'TOKEN';

  constructor() { }

  storeToken(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | undefined {
    return localStorage.getItem(this.TOKEN_KEY) || undefined;
  }
}
