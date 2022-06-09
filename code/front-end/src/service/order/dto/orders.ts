import { Product } from "src/service/product/dto/product";

export interface ListOrdersResponse {
    code: string;
    message: string;
    data: Order[];
}

export class Order {
    id!: number;
    userId!: string;
    product!: Product;
}