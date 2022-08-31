import { Component, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { BreakpointObserver } from '@angular/cdk/layout';
import { delay } from 'rxjs/operators';
import { LocalStorageService } from 'src/service/local-storage/local-storage.service';
import { MWSRouterService } from 'src/service/router/mwsrouter.service';

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
    private localStorageService: LocalStorageService,
    private router: MWSRouterService) { }

  ngAfterViewInit() {
    this.configureAutomaticSideNavHide();
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

  shouldShowAccountSection(): boolean {
    return !this.isUserLogged();
  }

  isUserLogged(): boolean {
    return !(!this.localStorageService.getToken());
  }

  getUserFullName(): string {
    return this.localStorageService.getUserFirstLastName() || '';
  }

  getUserRole(): string {
    const role = this.localStorageService.getUserRole();
    return role || "Unknown";
  }

  isSuperUser(): boolean {
    return this.getUserRole() === 'SUPER';
  }

  logout(): void {
    this.localStorageService.clearStorage();
    this.router.navigate('/account')
  }


  // Redirections
  redirectToProductsSection() {
    this.router.navigate('/products')
  }

  redirectToAdminsSection() {
    this.router.navigate('/admins')
  }

  redirectToOrdersSection() {
    this.router.navigate('/orders')
  }

  redirectToAccountSection() {
    this.router.navigate('/account')
  }

  redirectToTenantsSection() {
    this.router.navigate('/tenants')
  }
}
