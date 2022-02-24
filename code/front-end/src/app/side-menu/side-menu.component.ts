import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.scss']
})
export class SideMenuComponent implements OnInit {

  @ViewChild("myNameElem")
  myNameElem!: ElementRef;

  constructor() { }

  ngOnInit(): void {
  }

  openNav() {
  console.log("openNav")
  console.log(this.myNameElem);

  this.myNameElem.nativeElement.style.width = "250px";
  this.myNameElem.nativeElement.style.marginLeft = "0px";
  }

  closeNav() {
    console.log("closeNav")

    this.myNameElem.nativeElement.style.width = "0";
    this.myNameElem.nativeElement.style.marginLeft= "0";
  }

}
