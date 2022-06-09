import { Component, OnInit } from '@angular/core';
import { isOkResponse } from 'src/service/dto/api';
import { NotificationService } from 'src/service/notification/notification.service';
import { Order } from 'src/service/order/dto/orders';
import { OrderService } from 'src/service/order/order.service';


@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  displayedColumns: string[] = ['orderId', 'productName', 'productImage', 'date'];

  constructor(private orderService: OrderService,
    private notificationService: NotificationService) { }

  async ngOnInit(): Promise<void> {

    await (await this.orderService.listOrders()).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.orders = response.data;
          console.log(this.orders);
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (_) => {
        this.notificationService.showError("Internal error fetching orders.");
      },
    });
  }
}
