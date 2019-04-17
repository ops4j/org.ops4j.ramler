import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RestResponse } from './rest-response';
import { {{baseUrlToken}} } from '../app.module';

@Injectable()
export class {{serviceName}} implements {{resourceName}} {

    constructor(@Inject({{baseUrlToken}}) private baseUrl: string, private httpClient: HttpClient) { }

