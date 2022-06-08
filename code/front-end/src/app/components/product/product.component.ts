import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { isOkResponse } from 'src/service/dto/api';
import { NotificationService } from 'src/service/notification/notification.service';
import { OrderService } from 'src/service/order/order.service';
import { Product } from 'src/service/product/dto/product';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.scss']
})
export class ProductComponent implements OnInit {
  @Input()
  product!: Product;

  constructor(private orderService: OrderService,
    private notificationService: NotificationService) { }

  ngOnInit(): void { }

  async order(): Promise<void> {
    if (!this.product) {
      this.notificationService.showError("Not a product.")
      return;
    }

    await (await this.orderService.orderProduct(this.product.id)).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.notificationService.showInfoMessage("Successful order");
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (e: HttpErrorResponse) => {
        this.notificationService.showErrorWithDefault(e, "Internal error ordering product.");
      },
    });
  }
}
