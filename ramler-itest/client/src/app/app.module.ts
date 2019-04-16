import { BrowserModule } from '@angular/platform-browser';
import { NgModule, InjectionToken } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';


import { AppComponent } from './app.component';
import { UserService } from './user.service';

export const CRUD_URL = new InjectionToken<string>('crud.url');

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [
    UserService,
    { provide: CRUD_URL, useValue: 'http://localhost:8081' }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
