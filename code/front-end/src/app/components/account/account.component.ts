import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { isOkResponse } from 'src/service/dto/api';
import { IdentificationService } from 'src/service/identification/identification.service';
import { NotificationService } from 'src/service/notification/notification.service';
import {Router} from "@angular/router"
import { AuthenticationResponse } from 'src/service/identification/dto/authentication-response';
import { LocalStorageService } from 'src/service/local-storage/local-storage.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  hidePassword = true;
  showLogin = true;
  inputFirstName: string = ''
  inputLastName: string = ''
  inputEmail: string = ''
  inputPassword: string = ''

  constructor(private identificationService: IdentificationService,
    private notificationService: NotificationService,
    private router: Router,
    private localStorageService: LocalStorageService) { }

  ngOnInit(): void {
  }


  async login() {
    await (await this.identificationService.login(this.inputEmail, this.inputPassword)).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.afterLogin(response);
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (e: HttpErrorResponse) => {
        this.notificationService.showErrorWithDefault(e, "Internal error in login.");
      },
    });
  }

  afterLogin(response: AuthenticationResponse) {
    this.router.navigate(['/products'])
    console.log("token: " +  response.data.token);
    this.localStorageService.storeToken(response.data.token);
    console.log("token stored: " +  this.localStorageService.getToken());

  }

  showLoginForm(): void {
    this.showLogin = true;
  }

  async signUp() {
    await (await this.identificationService.signUp(this.inputEmail, this.inputPassword, this.inputFirstName, this.inputLastName)).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.notificationService.showInfoMessage("Successful sign up");
          this.showLoginForm();
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (e: HttpErrorResponse) => {
        this.notificationService.showErrorWithDefault(e, "Internal error in signup.");
      },
    });
  }

  showSignupForm(): void {
    this.showLogin = false;
  }
}
