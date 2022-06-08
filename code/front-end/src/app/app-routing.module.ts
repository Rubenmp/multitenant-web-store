import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountComponent } from './components/account/account.component';
import { ProductsComponent } from './components/products/products.component';

const routes: Routes = [
  {
    path: 'products',
    component: ProductsComponent,
  },
  {
    path: 'account',
    component: AccountComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
