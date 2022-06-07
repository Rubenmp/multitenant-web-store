import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ListProductResponse, Product } from './dto/product';
import { Observable } from 'rxjs';
import { to } from './dto/api';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  constructor(private http: HttpClient) { }

  list() {
    console.log("inside products service")

    return to(this.http
      .get<ListProductResponse>(`${environment.baseUrl}/product/list`).toPromise());
      /*
      .subscribe(
        {
          next(response) {
            console.log(`${JSON.stringify(response.code)}`);
            console.log(`${JSON.stringify(response.data)}`);
            return undefined; //response.data;
          },
          error(err) { console.log('Received an error: ' + err); }
        }
      );*/

    //console.log("products service end async call")
    //console.log(products)
  }
}
