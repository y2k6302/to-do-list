import { HttpClient, HttpParams, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { Tasks, Task } from 'src/app/types/tasks';
import { environment } from 'src/environments/environment';
import * as TE from 'fp-ts/TaskEither'

@Injectable({
  providedIn: 'root'
})
export class ToDoService {

  private tasksApiUtl = `${environment.toDoListBackendUrl}/v1/tasks`

  constructor(private http: HttpClient) { }
  
  getTasksByCompleted(completed: string) {
    const params = new HttpParams().set('completed', completed)
    return TE.tryCatch(
      () => lastValueFrom(this.http.get<Task[]>(`${this.tasksApiUtl}`, {params: params})),
      (error: any) => new Error(`Get to-do tasks failed. ${error.message}`)
    )
  }

  save(task: Task) {
    return TE.tryCatch(
      () => lastValueFrom(this.http.post<Task>(`${this.tasksApiUtl}`, task)),
      (error: any) => new Error(`Save task failed. ${error.message}`)
    )
  }

  completed(id: string) {
    return TE.tryCatch(
      () => lastValueFrom(this.http.put<Task>(`${this.tasksApiUtl}/${id}/completed`, {})),
      (error: any) => new Error(`Completed task failed. ${error.message}`)
    )
  }

  update(task: Task) {
    return TE.tryCatch(
      () => lastValueFrom(this.http.put<Task>(`${this.tasksApiUtl}/${task.id}`, task)),
      (error: any) => new Error(`Update task failed. ${error.message}`)
    )
  }

  delete(id: string) {
    return TE.tryCatch(
      () => lastValueFrom(this.http.delete(`${this.tasksApiUtl}/${id}`, {})),
      (error: any) => new Error(`Delete task failed. ${error.message}`)
    )
  }

}
