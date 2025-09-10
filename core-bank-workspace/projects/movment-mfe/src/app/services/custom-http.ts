import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../enviroments/enviroments';

@Injectable({
  providedIn: 'root'
})
export class CustomHttp<T> {
   private http = inject(HttpClient);
  protected baseUrl = environment.apiUrl;

  protected get(endpoint: string, params?: Record<string, any>): Observable<T[]> {
    const httpParams = this.buildParams(params);
    return this.http.get<T[]>(`${this.baseUrl}/${endpoint}`, { params: httpParams });
  }

  protected getById(endpoint: string, id: string | number): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}/${endpoint}/${id}`);
  }

  protected post(endpoint: string, data: Partial<T>): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}/${endpoint}`, data);
  }

  protected put(endpoint: string, id: string | number, data: Partial<T>): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}/${endpoint}/${id}`, data);
  }

  protected delete(endpoint: string, id: string | number): Observable<any> {
    return this.http.delete<void>(`${this.baseUrl}/${endpoint}/${id}`);
  }

  private buildParams(params?: Record<string, any>): HttpParams {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key] !== null && params[key] !== undefined) {
          httpParams = httpParams.set(key, params[key].toString());
        }
      });
    }
    return httpParams;
  }
}
