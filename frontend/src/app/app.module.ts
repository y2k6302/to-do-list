import { NgModule } from '@angular/core'
import { BrowserModule } from '@angular/platform-browser'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'

import { AppRoutingModule } from './app-routing.module'
import { AppComponent } from './app.component'

import { ToDoListComponent } from './features/to-do-list/to-do-list.component'
import { HttpClientModule } from '@angular/common/http'
import { TabViewModule } from 'primeng/tabview'
import { ButtonModule } from 'primeng/button'
import { DialogModule } from 'primeng/dialog'
import { InputTextModule } from 'primeng/inputtext'
import { FormsModule } from '@angular/forms'
import { CalendarModule } from 'primeng/calendar'
import { DropdownModule } from 'primeng/dropdown'
import { ImageModule } from 'primeng/image'
import { CardModule } from 'primeng/card'
import { TagModule } from 'primeng/tag'
import { TooltipModule } from 'primeng/tooltip'


@NgModule({
  declarations: [
    AppComponent,
    ToDoListComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    TabViewModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    FormsModule,
    CalendarModule,
    DropdownModule,
    ImageModule,
    CardModule,
    TagModule,
    TooltipModule    
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
