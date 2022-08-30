import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { isOkResponse } from 'src/service/dto/api';
import { NotificationService } from 'src/service/notification/notification.service';
import { TenantService } from 'src/service/tenant/tenant.service';
import { UserService } from 'src/service/user/user.service';


type AdminRow = { id: number, tenantId: number, email: string, firstName: string, lastName: string, isBeingUpdated: boolean, isSelected: boolean }
type TenantRow = { tenantId: number, name: string, active: boolean, isBeingUpdated: boolean, isSelected: boolean }
type TenantCreation = { name: string }


@Component({
  selector: 'app-admins',
  templateUrl: './admins.component.html',
  styleUrls: ['./admins.component.scss']
})
export class AdminsComponent implements OnInit {

  tenants: TenantRow[] = []
  admins: AdminRow[] = []

  displayedColumns: string[] = ['id', 'tenantId', 'email', 'firstName', 'lastName'];
  dataSource!: MatTableDataSource<AdminRow>;
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
    private userService: UserService,
    private notificationService: NotificationService) { }


  async ngOnInit(): Promise<void> {
    this.refreshAdmins();
  }

  async refreshAdmins() {
    await (await this.userService.listAdmins()).subscribe({
      next: (response) => {
        if (isOkResponse(response)) {
          this.admins = response.data.map((t) => {
            return { id: t.id, tenantId: t.tenantId, email: t.email, firstName: t.firstName, lastName: t.lastName, isBeingUpdated: false, isSelected: false }
          });
          console.log(this.admins);

          this.paginator.pageSize = 5;
          this.updateAdminsInTable(this.admins);
        } else {
          this.notificationService.showError(response.message);
        }
      },
      error: (_) => {
        this.notificationService.showError("Internal error fetching admins.");
      },
    });

    this.isBeingUpdated = false;
    this.selectedRows = 0;
  }


  updateAdminsInTable(admins: AdminRow[]) {
    this.dataSource = new MatTableDataSource(admins);
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
    /*
    const newTenants = this.admins.filter(o => o.name.toLowerCase().includes(this.filterByProductName))
      .filter(o => o.tenantId.toLocaleString().includes(this.filterById));
    this.updateAdminsInTable(newTenants);*/
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
  async createAdmin() {
    return;
    /*if (this.isCreatingTenant) {
      await (await this.tenantService.createTenant(this.inputTenantName)).subscribe({
        next: async (response) => {
          if (isOkResponse(response)) {
            this.notificationService.showInfoMessage("Tenant created with id " + response.data);
            this.isCreatingTenant = false;
            await this.refreshTenants();
          } else {
            this.notificationService.showError(response.message);
          }
        },
        error: (_) => {
          this.notificationService.showError("Internal error creating tenant.");
        },
      });

    } else {
      this.isCreatingTenant = true;
      this.tenantToCreate = [{ name: '' }]
      this.dataSourceToCreate = new MatTableDataSource(this.tenantToCreate);
    }*/
  }


  cancelAction() {
    this.refreshAdmins();
  }


  async updateAdmin() {
    return;
    /*
    if (this.selectedRows > 0) {
      const rowsToUpdate = this.tenants.filter(row => row.isSelected);
      const isUpdating = (rowsToUpdate.length > 0 && rowsToUpdate[0].isBeingUpdated);
      if (isUpdating) {
        let successResponses = 0;
        for (let rowToUpdate of rowsToUpdate) {
          await (await this.tenantService.updateTenant(rowToUpdate.tenantId, rowToUpdate.name)).subscribe({
            next: (response) => {
              if (isOkResponse(response)) {
                this.notificationService.showInfoMessage("Tenant updated");
              } else {
                this.notificationService.showError(response.message);
              }
            },
            error: (_) => {
              this.notificationService.showError("Internal error updating tenant.");
            },
          });
        }
        await this.refreshTenants();
      
      } else {
        rowsToUpdate.forEach(row => row.isBeingUpdated = true);
        this.isBeingUpdated = true;
      }
    }
    */
  }


  async deleteSelectedAdmin() {
    if (this.selectedRows > 0) {
      const selectedAdminIds = this.admins.filter(row => row.isSelected).map(admin => admin.id);

      for (let selectedAdminId of selectedAdminIds) {
        await (await this.userService.deleteUser(selectedAdminId)).subscribe({
          next: async (response) => {
            if (isOkResponse(response)) {
              this.notificationService.showInfoMessage("Admin deleted");
              await this.refreshAdmins();
            } else {
              this.notificationService.showError(response.message);
            }
          },
          error: (_) => {
            this.notificationService.showError("Internal error deleting admin.");
          },
        });
      }
    }
  }
}
