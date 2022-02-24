import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'front-end';

  @ViewChild("sideMenu") sideMenu!: ElementRef;

  openSideMenu() {
    this.sideMenu.nativeElement.style.width = "250px";
    this.sideMenu.nativeElement.style.marginLeft = "0px";
  }

  closeSideMenu() {
    this.sideMenu.nativeElement.style.width = "0";
    this.sideMenu.nativeElement.style.marginLeft= "0";
  }
}
