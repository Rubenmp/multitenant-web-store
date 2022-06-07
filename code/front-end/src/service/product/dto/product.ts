export interface ListProductResponse {
    code: string;
    message: string;
    data: Product[];
}

export class Product {
    id!: number;
    name!: string;
    image!: string;
    descriptioin!: string;
}