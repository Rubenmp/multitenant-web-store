import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'front-end';
  sideMenuAction!: string;

  openSideMenu() {
    console.log("openSideMenu");
    this.sideMenuAction = "OPEN";
  }
}
