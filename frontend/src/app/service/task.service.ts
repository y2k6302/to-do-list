import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Task } from 'src/app/types/tasks';
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

  create(task: Task) {
    return this.http.post<Task>(`${this.tasksApiUtl}`, task);
  }

  complete(id: string) {
    return this.http.put<Task>(`${this.tasksApiUtl}/${id}/complete`, {});
  }

  redo(id: string) {
    return this.http.put<Task>(`${this.tasksApiUtl}/${id}/redo`, {});
  }

  update(task: Task) {
    return this.http.put<Task>(`${this.tasksApiUtl}/${task.id}`, task);
  }

  delete(id: string) {
    return this.http.delete(`${this.tasksApiUtl}/${id}`, {});
  }

}
