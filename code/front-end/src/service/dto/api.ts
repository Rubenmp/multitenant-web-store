import { Observable } from "rxjs";

export interface ApiResponse {
  code: string;
  message: string;
  data: any;

  new (): ApiResponse;

}

export function isOkResponse(response: any) {
  if (response && response.error && response.status !== 200) {
    return false;
  } else if (response && response.type !== "SUCCESS") {
    if (response.body && response.body.type === "SUCCESS") {
      return false;
    }
    return true;
  }
  return false;
}


export async function to(promise: Promise<any>) {
  try {
    const data = await promise;
    return data;
  } catch (err) {
    return err;
  }
}
