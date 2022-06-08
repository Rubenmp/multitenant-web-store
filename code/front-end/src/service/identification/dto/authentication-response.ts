export interface AuthenticationResponse {
    code: string;
    message: string;
    data: AuthenticationData;
}

export class AuthenticationData {
    firstName!: string;
    lastName!: string;
    token!: string;
}