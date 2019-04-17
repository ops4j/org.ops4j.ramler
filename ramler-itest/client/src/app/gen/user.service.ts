import { User } from './user';
import { UserResource } from './user-resource';
import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RestResponse } from './rest-response';
import { CRUD_URL } from '../app.module';

@Injectable()
export class UserService implements UserResource {

    constructor(@Inject(CRUD_URL) private url: string, private httpClient: HttpClient) { }

    findAllUsers(q: string, sort: string): RestResponse<User[]> {
        return this.httpClient.get<User[]>(this.url, { params: { q, sort } }).toPromise();
    }

    createUser(body: User): RestResponse<User> {
        return this.httpClient.post<User>(this.url, body).toPromise();
    }

    findUserById(id: number): RestResponse<User> {
        return this.httpClient.get<User>(`${this.url}/${id}`).toPromise();
    }

    deleteUserById(id: number): RestResponse<void> {
        return this.httpClient.delete<void>(`${this.url}/${id}`).toPromise();
    }

    putUserById(body: User, id: number): RestResponse<User> {
        return this.httpClient.put<User>(`${this.url}/${id}`, body).toPromise();
    }

    patchUserById(body: string, id: number): RestResponse<User> {
        return this.httpClient.patch<User>(`${this.url}/${id}`, body).toPromise();
    }
}
