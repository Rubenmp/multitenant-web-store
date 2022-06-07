import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private errorTitle: string = "Error";

  constructor() { }

  showError() {
    this.showMessage(this.errorTitle, "There was an internal error");
  }

  showMessage(title: string, msg: string) {
    console.log(title + msg)
  }
}
