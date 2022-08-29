export interface ListTenantsResponse {
    code: string;
    message: string;
    data: Tenant[];
}

export class Tenant {
    id!: number;
    name!: string;
    active!: boolean;
}