import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private errorTitle: string = "Error";

  constructor(private _snackBar: MatSnackBar) { }

  showError(msg: string) {
    this.showMessage(this.errorTitle, msg);
  }

  showMessage(title: string, msg: string) {
    this._snackBar.open(msg, 'Splash', {
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
  }
}
