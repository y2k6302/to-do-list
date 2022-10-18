import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Task, TaskRequestBody } from 'src/app/types/tasks';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private tasksApiUtl = `${environment.toDoListBackendUrl}/v1/tasks`

  constructor(private http: HttpClient) { }

  getTasks() {
    return this.http.get<Task[]>(`${this.tasksApiUtl}`);
  }

  create(task: TaskRequestBody) {
    return this.http.post<Task>(`${this.tasksApiUtl}`, task);
  }

  complete(id: string) {
    return this.http.put<Task>(`${this.tasksApiUtl}/${id}/complete`, {});
  }

  reopen(id: string) {
    return this.http.put<Task>(`${this.tasksApiUtl}/${id}/reopen`, {});
  }

  update(id: string, task: TaskRequestBody) {
    return this.http.put<Task>(`${this.tasksApiUtl}/${id}`, task);
  }

  delete(id: string) {
    return this.http.delete(`${this.tasksApiUtl}/${id}`, {});
  }

}
