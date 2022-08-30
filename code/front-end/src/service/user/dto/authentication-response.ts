export interface AuthenticationResponse {
    code: string;
    message: string;
    data: AuthenticationData;
}

export class AuthenticationData {
    token!: string;
    firstName!: string;
    lastName!: string;
    role!: string;
}