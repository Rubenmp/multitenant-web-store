export interface ListAdminsResponse {
    code: string;
    message: string;
    data: User[];
}

export class User {
    id!: number;
    tenantId!: number;
    email!: string;
    firstName!: string;
    lastName!: string;
}