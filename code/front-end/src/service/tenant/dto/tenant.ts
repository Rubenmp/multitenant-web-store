export interface ListTenantsResponse {
    code: string;
    message: string;
    data: Tenant[];
}

export class Tenant {
    tenantId!: number;
    name!: string;
    active!: boolean;
}