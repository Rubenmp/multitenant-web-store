import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NotificationService } from 'src/service/notification/notification.service';
import { Tenant } from 'src/service/tenant/dto/tenant';
import { TenantService } from 'src/service/tenant/tenant.service';

@Component({
  selector: 'app-tenants',
  templateUrl: './tenants.component.html',
  styleUrls: ['./tenants.component.scss']
})
export class TenantsComponent implements OnInit {
  tenants: Tenant[] = []

  displayedColumns: string[] = ['tenantId', 'tenantName', 'active'];
  dataSource!: MatTableDataSource<Tenant>;

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  filterById: string = "";
  filterByProductName: string = "";


  constructor(private tenantService: TenantService,
    private notificationService: NotificationService) { }


  async ngOnInit(): Promise<void> {
    /*
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
    });*/
  }

  updateOrdersInTable(tenant: Tenant[]) {
    this.dataSource = new MatTableDataSource(tenant);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  applyFilterById(event: Event) {
    const filter = this.getFilterValue(event);
    this.filterById = filter;
    this.applyFilterInternal()
  }

  applyFilterByName(event: Event) {
    const filter = this.getFilterValue(event);
    this.filterByProductName = filter;
    this.applyFilterInternal()
  }


  private applyFilterInternal() {
    const newOrders = this.tenants.filter(o => o.name.toLowerCase().includes(this.filterByProductName))
      .filter(o => o.id.toLocaleString().includes(this.filterById));
    this.updateOrdersInTable(newOrders);
  }

  private getFilterValue(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    return filterValue.trim().toLowerCase();
  }
}
