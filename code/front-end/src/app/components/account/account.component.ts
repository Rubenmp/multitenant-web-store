import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  hide = true;
  show_login = true;

  constructor() { }

  ngOnInit(): void {
  }


  login(): void {
    console.log("login")
  }

  show_login_form(): void {
    this.show_login = true;
  }

  signup(): void {
    console.log("signup");
  }

  show_signup_form(): void {
    this.show_login = false;
  }
}
