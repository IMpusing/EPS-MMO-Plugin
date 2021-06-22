import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { getAllNPCResponse, loginResponse, verifyResponse } from 'src/common/generalTypes';
import { AdminSiteComponent } from '../compontents/admin/admin-site/admin-site.component';

@Injectable({
  providedIn: 'root'
})
export class APIService {
  public static apiUrl = 'http://localhost:10100/'

  constructor(private http:HttpClient) { }

  public login(name : string, password : string) : Observable<loginResponse>{
    return this.http.get<loginResponse>(APIService.apiUrl + "login/" + name +"/" + password)
  }

  public verify(key : string) : Observable<verifyResponse>{
    return this.http.get<verifyResponse>(APIService.apiUrl + "admin/" + key)
  }

  public getAllNPC() : Observable<getAllNPCResponse>{
    return this.http.get<getAllNPCResponse>(APIService.apiUrl + "admin/" + AdminSiteComponent.SessionKey + 
      "/npc/getall")
  }


}
