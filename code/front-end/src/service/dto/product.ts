export interface ListProductResponse {
    type: string;
    exception: {};
    data: Product[];
  }

export class Product {
    id!: number;
    name!: string;
    image!: string;
    descriptioin!: string;
}