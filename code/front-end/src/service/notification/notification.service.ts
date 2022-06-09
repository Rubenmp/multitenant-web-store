import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private _snackBar: MatSnackBar) { }

  showError(msg: string) {
    this.showMessage(String.fromCharCode(0x274C) + " " + msg);
  }

  showWarning(msg: string) {
    this.showMessage(String.fromCharCode(0x26A0) + " " + msg);
  }

  showErrorWithDefault(e: HttpErrorResponse, defaultMsg: string) {
    if (e && e.error && e.error.message) {
      this.showError(e.error.message);
    } else {
      this.showError(defaultMsg);
    }
  }

  showInfoMessage(msg: string) {
    this.showMessage(String.fromCharCode(0x2713) + " " + msg)
  }

  private showMessage(msg: string) {
    this._snackBar.open(msg, undefined, {
      horizontalPosition: 'right',
      verticalPosition: 'top',
      duration: 5000,
      panelClass: ['notification-style']
    });
  }
}
