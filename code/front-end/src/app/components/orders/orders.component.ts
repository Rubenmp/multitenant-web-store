import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
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
  orders: Order[] = []

  displayedColumns: string[] = ['orderId', 'productName', 'productImage', 'date'];
  dataSource!: MatTableDataSource<Order>;

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;


  constructor(private orderService: OrderService,
    private notificationService: NotificationService) { }


  async ngOnInit(): Promise<void> {
    await (await this.orderService.listOrders()).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.orders = response.data;
          this.paginator.pageSize = 5;
          this.updateOrdersInTable(this.orders);
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (_) => {
        this.notificationService.showError("Internal error fetching orders.");
      },
    });
  }

  updateOrdersInTable(order: Order[]) {
    this.dataSource = new MatTableDataSource(order);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    const filter = filterValue.trim().toLowerCase();
    const newOrders = this.orders.filter(o => o.product.name.toLowerCase().includes(filter));
    this.updateOrdersInTable(newOrders);
  }
}
