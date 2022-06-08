import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { isOkResponse } from 'src/service/dto/api';
import { IdentificationService } from 'src/service/identification/identification.service';
import { NotificationService } from 'src/service/notification/notification.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  hide_password = true;
  show_login = true;

  constructor(private identificationService: IdentificationService,
    private notificationService: NotificationService) { }

  ngOnInit(): void {
  }


  async login() {
    const email = "";
    const password = ""

    await (await this.identificationService.login(email, password)).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.notificationService.showInfoMessage("Successful login");
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (e: HttpErrorResponse) => {
        this.notificationService.showErrorWithDefault(e, "Internal error in signup.");
      },
    });
  }

  show_login_form(): void {
    this.show_login = true;
  }

  async signUp() {
    const email = "";
    const password = ""
    const firstName = "";
    const lastName = "";

    await (await this.identificationService.signUp(email, password, firstName, lastName)).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.notificationService.showInfoMessage("Successful sign up");
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (e: HttpErrorResponse) => {
        this.notificationService.showErrorWithDefault(e, "Internal error in signup.");
      },
    });
  }

  show_signup_form(): void {
    this.show_login = false;
  }
}
