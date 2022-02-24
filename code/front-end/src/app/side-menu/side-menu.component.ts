import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.scss']
})
export class SideMenuComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  openNav() {
  console.log("openNav")
  //document.getElementById("mySidebar").style.width = "250px";
  //document.getElementById("main").style.marginLeft = "250px";
  }

  closeNav() {
  //document.getElementById("mySidebar").style.width = "0";
  //document.getElementById("main").style.marginLeft= "0";
  }

}
