import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  @ViewChild("sideMenu") sideMenu!: ElementRef;

  title = 'front-end';
  sideMenuAction!: string;

  openNav() {
    console.log("openNav")
    console.log(this.sideMenu);

    this.sideMenu.nativeElement.style.width = "250px";
    this.sideMenu.nativeElement.style.marginLeft = "0px";
  }

  closeNav() {
    console.log("closeNav")

    this.sideMenu.nativeElement.style.width = "0";
    this.sideMenu.nativeElement.style.marginLeft= "0";
  }
}
