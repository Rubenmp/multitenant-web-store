import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountComponent } from './components/account/account.component';
import { ProductsComponent } from './components/products/products.component';
import { OrdersComponent } from './orders/orders.component';

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
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
