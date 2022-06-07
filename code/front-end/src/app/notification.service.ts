import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor() { }

  showError() {
    this.showErrorMessage("There was an internal error");
  }

  showErrorMessage(msg: string) {
    console.log(msg)
  }
}
