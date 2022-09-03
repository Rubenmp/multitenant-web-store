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

  displayedColumns: string[] = ['orderId', 'productName', 'productImage', 'date', 'userId'];
  dataSource!: MatTableDataSource<Order>;

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  filterById: string = "";
  filterByProductName: string = "";


  constructor(private orderService: OrderService,
    private notificationService: NotificationService) {
  }


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
      error: (e) => {
        this.notificationService.showError(e.error.message);
      },
    });
  }


  updateOrdersInTable(order: Order[]) {
    this.dataSource = new MatTableDataSource(order);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  applyFilterById(event: Event) {
    const filter = this.getFilterValue(event);
    this.filterById = filter;
    this.applyFilterInternal()
  }

  applyFilterByProductName(event: Event) {
    const filter = this.getFilterValue(event);
    this.filterByProductName = filter;
    this.applyFilterInternal()
  }


  private applyFilterInternal() {
    const newOrders = this.orders.filter(o => o.product.name.toLowerCase().includes(this.filterByProductName))
      .filter(o => o.id.toLocaleString().includes(this.filterById));
    this.updateOrdersInTable(newOrders);
  }

  private getFilterValue(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    return filterValue.trim().toLowerCase();
  }
}
