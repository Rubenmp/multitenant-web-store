import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class MWSRouterService {
  private tenantId: number | undefined;
  private META_TENANT_ID: number = 1;

  constructor(private router: Router) { }

  storeTenantId(): number {
    const pathSplitted = this.router.url.split("/");
    const tenantIndex = pathSplitted.findIndex(x => x === "tenant");
    const pathTenantStr = (pathSplitted.length > tenantIndex && tenantIndex > -1) ? pathSplitted[tenantIndex+1] : '';
    const pathTenantInt = parseInt(pathTenantStr);
    this.tenantId = (!pathTenantInt) ? this.META_TENANT_ID : pathTenantInt;
    
    return this.tenantId;
  }

  getTenantId(): number {
    return this.tenantId ? this.tenantId : this.META_TENANT_ID;
  }

  private getBaseUrl() {
    return "/tenant/" + this.tenantId;
  }

  navigate(command: any): Promise<boolean>{
    return this.router.navigate([this.getBaseUrl() + command]);
  }

}
