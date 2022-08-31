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
    this.tenantId = this.getPathTenantId();
    
    return this.tenantId;
  }

  getPathTenantId(): number {
    const pathSplitted = this.router.url.split("/");
    const tenantIndex = pathSplitted.findIndex(x => x === "tenant");
    const pathTenantStr = (pathSplitted.length > tenantIndex && tenantIndex > -1) ? pathSplitted[tenantIndex+1] : '';
    const pathTenantInt = parseInt(pathTenantStr);
    
    return (!pathTenantInt) ? this.META_TENANT_ID : pathTenantInt;
  }

  getTenantId(): number {
    return this.tenantId ? this.tenantId : this.META_TENANT_ID;
  }

  private getBaseUrl() {
    const tenantId = this.tenantId ? this.tenantId : this.getPathTenantId();
    return "/tenant/" + tenantId;
  }

  navigate(command: any): Promise<boolean>{
    return this.router.navigate([this.getBaseUrl() + command]);
  }

}
