import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.scss']
})
export class SideMenuComponent implements OnInit {

  @ViewChild("sideMenu") sideMenu!: ElementRef;
  @Input() set action(value: string) {
    console.log("entra");
    if (value == "OPEN") {
      this.openNav();
    }
  }


  constructor() { }

  ngOnInit(): void {
  }

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
