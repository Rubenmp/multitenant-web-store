import { Component, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { BreakpointObserver } from '@angular/cdk/layout';
import { delay } from 'rxjs/operators';
import { LocalStorageService } from 'src/service/local-storage/local-storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'front-end';

  @ViewChild(MatSidenav)
  sidenav!: MatSidenav;

  constructor(private observer: BreakpointObserver,
    private localStorageService: LocalStorageService) { }

  ngAfterViewInit() {
    this.configureAutomaticSideNavHide();
    this.localStorageService.clearStorage();
  }

  shouldShowAccountSection(): boolean {
    return !this.isUserLogged();
  }

  isUserLogged(): boolean {
    return !(!this.localStorageService.getToken());
  }

  getUserFullName(): string {
    return this.localStorageService.getUserFirstLastName() || '';
  }

  private configureAutomaticSideNavHide() {
    this.observer
      .observe(['(max-width: 800px)'])
      .pipe(delay(1))
      .subscribe((res) => {
        if (res.matches) {
          this.sidenav.mode = 'over';
          this.sidenav.close();
        } else {
          this.sidenav.mode = 'side';
          this.sidenav.open();
        }
      });
  }
}
