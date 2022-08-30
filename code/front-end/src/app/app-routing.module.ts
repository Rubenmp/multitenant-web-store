import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountComponent } from './components/account/account.component';
import { AdminsComponent } from './components/admins/admins.component';
import { OrdersComponent } from './components/orders/orders.component';
import { ProductsComponent } from './components/products/products.component';
import { TenantsComponent } from './components/tenants/tenants.component';

const routes: Routes = [
  { path: "", redirectTo: "/account", pathMatch: "full" },
  {
    path: 'products',
    component: ProductsComponent,
  },
  {
    path: 'account',
    component: AccountComponent,
  },
  {
    path: 'orders',
    component: OrdersComponent,
  },
  {
    path: 'tenants',
    component: TenantsComponent,
  },
  {
    path: 'admins',
    component: AdminsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
