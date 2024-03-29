import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { isOkResponse } from 'src/service/dto/api';
import { NotificationService } from 'src/service/notification/notification.service';
import { TenantService } from 'src/service/tenant/tenant.service';

type TenantRow = { tenantId: number, name: string, active: boolean, isBeingUpdated: boolean, isSelected: boolean }
type TenantCreation = { name: string }

@Component({
  selector: 'app-tenants',
  templateUrl: './tenants.component.html',
  styleUrls: ['./tenants.component.scss']
})
export class TenantsComponent implements OnInit {
  tenants: TenantRow[] = []

  displayedColumns: string[] = ['tenantId', 'name', 'active'];
  dataSource!: MatTableDataSource<TenantRow>;
  filterById: string = "";
  filterByProductName: string = "";

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  @ViewChild(MatSort)
  sort!: MatSort;

  // Creation
  tenantToCreate: TenantCreation[] = [];
  inputTenantName: string = '';
  isCreatingTenant: boolean = false;
  displayedColumnsToCreate: string[] = ['name'];
  dataSourceToCreate!: MatTableDataSource<TenantCreation>;

  // Update/delete
  selectedRows: number = 0;
  isBeingUpdated: boolean = false;

  constructor(private tenantService: TenantService,
    private notificationService: NotificationService) { }


  async ngOnInit(): Promise<void> {

    await this.refreshTenants();
  }

  async refreshTenants() {
    await (await this.tenantService.listTenants()).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.tenants = response.data.map((t) => {
            return { tenantId: t.tenantId, name: t.name, active: t.active, isBeingUpdated: false, isSelected: false }
          });

          this.updateTenantsInTable(this.tenants);
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (e) => {
        this.notificationService.showError(e.error.message);
      },
    });

    this.isCreatingTenant = false;
    this.isBeingUpdated = false;
    this.selectedRows = 0;
  }

  updateTenantsInTable(tenants: TenantRow[]) {
    this.dataSource = new MatTableDataSource(tenants);
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
    const newTenants = this.tenants.filter(o => o.name.toLowerCase().includes(this.filterByProductName))
      .filter(o => o.tenantId.toLocaleString().includes(this.filterById));
    this.updateTenantsInTable(newTenants);
  }

  private getFilterValue(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    return filterValue.trim().toLowerCase();
  }

  clickRow(row: TenantRow) {
    if (this.isBeingUpdated) {
      return;
    }
    if (row.isSelected) {
      row.isSelected = false;
      this.selectedRows -= 1;
    } else {
      row.isSelected = true;
      this.selectedRows += 1;
    }
  }

  isRowSelected() {
    return this.selectedRows > 0;
  }

  // Tenants operations
  async createTenant() {
    if (this.isCreatingTenant) {
      await (await this.tenantService.createTenant(this.inputTenantName)).subscribe({
        next: async (response) => {
          if (isOkResponse(response)) {
            this.notificationService.showInfoMessage("Tenant created with id " + response.data);
            
            this.refreshTenants();
          } else {
            this.notificationService.showError(response.message);
          }
        },
        error: (e) => {
          this.notificationService.showError(e.error.message);
        },
      });

    } else {
      this.isCreatingTenant = true;
      this.tenantToCreate = [{ name: '' }]
      this.dataSourceToCreate = new MatTableDataSource(this.tenantToCreate);
    }
  }


  async cancelAction() {
    await this.refreshTenants();
    if (this.paginator && !this.paginator.pageSize) {
      this.paginator.pageSize = 5;
    }
  }


  async updateTenant() {
    if (this.selectedRows > 0) {
      const rowsToUpdate = this.tenants.filter(row => row.isSelected);
      const isUpdating = (rowsToUpdate.length > 0 && rowsToUpdate[0].isBeingUpdated);
      if (isUpdating) {
        for (let rowToUpdate of rowsToUpdate) {
          await (await this.tenantService.updateTenant(rowToUpdate.tenantId, rowToUpdate.name)).subscribe({
            next: (response) => {
              if (isOkResponse(response)) {
                this.notificationService.showInfoMessage("Tenant updated");
              } else {
                this.notificationService.showError(response.message);
              }
            },
            error: (e) => {
              this.notificationService.showError(e.error.message);
            },
          });
        }
        await this.refreshTenants();
      
      } else {
        rowsToUpdate.forEach(row => row.isBeingUpdated = true);
        this.isBeingUpdated = true;
      }
    }
  }


  async deleteSelectedTenant() {
    if (this.selectedRows > 0) {
      const selectedTenantIds = this.tenants.filter(row => row.isSelected).map(tenant => tenant.tenantId);

      for (let selectedTenantId of selectedTenantIds) {
        await (await this.tenantService.delete(selectedTenantId)).subscribe({
          next: async (response) => {
            if (isOkResponse(response)) {
              this.notificationService.showInfoMessage("Tenant deleted");
              await this.refreshTenants();
            } else {
              this.notificationService.showError(response.message);
            }
          },
          error: (e) => {
            this.notificationService.showError(e.error.message);
          },
        });
      }
    }
  }
}
