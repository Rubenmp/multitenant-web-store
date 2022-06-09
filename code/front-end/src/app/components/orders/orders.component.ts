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
          this.dataSource = new MatTableDataSource(response.data);
          this.paginator.pageSize = 10;
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (_) => {
        this.notificationService.showError("Internal error fetching orders.");
      },
    });
  }

  ngAfterViewInit() {

  }
/*
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }*/
}
