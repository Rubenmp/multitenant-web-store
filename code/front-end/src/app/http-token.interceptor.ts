import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { timeout } from "rxjs/operators";
import { LocalStorageService } from "src/service/local-storage/local-storage.service";
import { NotificationService } from "src/service/notification/notification.service";

@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {
  HTTP_CALL_TIMEOUT_IN_MILLISECONDS : number = 10000;

  constructor(
    public localStorageService: LocalStorageService,
    private notificationService: NotificationService,
  ) { }

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = this.localStorageService.getToken();

    if (token) {
      request = request.clone({
        headers: request.headers
          .set('Authorization', `Bearer ${token}`)
          .set("Timezone-Info", `${this.getTimeZoneId()}`)
      });
    }

    return next.handle(request).pipe(timeout(this.HTTP_CALL_TIMEOUT_IN_MILLISECONDS));
  }

  private timeoutException() {
    const response = {
      type: "TIMEOUT",
      message: "Problem connecting with the server, please try later"
    };
    this.handleNotification(response);
  }

  private handleNotification = (response: { type: string; message: string; } | null | undefined) => {
    if (response === undefined || response === null) {
      return false;
    }
    return true;
    /*
        switch (response.type) {
          
          case Const.type.OK_RESPONSE:
            return true;
          case Const.type.ERROR_RESPONSE:
            this.notificationService.showWarning(response.exception.message);
            return false;
          case Const.type.TIMEOUT_EXCEPTION:
            this.notificationService.showError(response.message);
            return false;
        }*/
  }

  getTimeZoneId = () => {
    let timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

    if (timeZone === undefined) {
      timeZone = "Europe/Madrid";
    }

    return timeZone;
  }
}
