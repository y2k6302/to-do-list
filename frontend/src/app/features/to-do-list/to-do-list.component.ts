import { Component, OnInit } from '@angular/core'
import { TaskService } from 'src/app/service/task.service'
import { Completed, Priority, Task, TaskRequestBody } from 'src/app/types/tasks'

@Component({
  selector: 'app-to-do-list',
  templateUrl: './to-do-list.component.html',
  styleUrls: ['./to-do-list.component.scss']
})
export class ToDoListComponent implements OnInit {

  readonly ADD = "Add"
  readonly EDIT = "Edit"
  readonly priorities = Object.entries(Priority).map(([key, value]) => ({ key:key, value:value }))
  readonly sortMap = new Map([[Priority.HIGH, 2], [Priority.MEDIUM, 1], [Priority.LOW, 0]])

  toDoTasks: Task[] = []
  completedTasks: Task[] = []
  inputMessage: string = ''
  inputPriority: Priority = Priority.MEDIUM
  inputReminderTime: Date | null = null
  action: string = ''
  displayDialog: boolean = false
  displayErrorDialog: boolean = false
  serverErrorMessage: string = ''
  selectTask: Task = { id: '', message: '', completed: Completed.N, priority: Priority.MEDIUM }
  taskRequestBody: TaskRequestBody = { message: '', completed: Completed.N, priority: Priority.MEDIUM }
  validationFailedMessage = ''

  constructor(private taskService: TaskService) { }

  ngOnInit() {
    this.getTasks()
  }

  getTasks() {
    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.toDoTasks = tasks.filter(it => it.completed === Completed.N)
        this.completedTasks = tasks.filter(it => it.completed === Completed.Y)
        this.sortTasks()
      },
      error: (error) => {
        this.displayErrorDialog = true
        this.serverErrorMessage = error.message
      }
    })
  }

  sortTasks() {
    this.toDoTasks.sort((x, y) => {
      //sort by priority
      const xPriority: any = this.sortMap.get(x.priority)
      const yPriority: any = this.sortMap.get(y.priority)      
      if (xPriority > yPriority) {
        return -1
      } else if (xPriority < yPriority) {
        return 1
      }

      //sort by expired
      if(this.isExpired(x.reminderTime)) {
        return -1
      } else if (this.isExpired(y.reminderTime)) {
        return 1
      }

      //sort by remider
      const xRemider = this.stringToDate(x.reminderTime)?.getTime()
      const yRemider = this.stringToDate(y.reminderTime)?.getTime()      
      if (xRemider !== undefined && yRemider !== undefined) {
        if (xRemider > yRemider) {
          return 1
        } else {
          return -1
        }
      }

      if (xRemider) {
        return -1
      }

      if (yRemider) {
        return 1
      }

      return 0
    })
  }

  addDialoag() {
    this.inputMessage = ''
    this.inputPriority = Priority.MEDIUM
    this.inputReminderTime = null
    this.validationFailedMessage = ''
    this.action = this.ADD
    this.showDialog()
  }

  add() {
    if (this.validation()) {
      this.taskRequestBody.message = this.inputMessage
      this.taskRequestBody.priority = this.inputPriority
      this.taskRequestBody.reminderTime = this.inputReminderTime?.toISOString()

      this.taskService.create(this.taskRequestBody).subscribe({
        next: (task) => {
          this.getTasks()
          this.hideDialog()
        },
        error: (error) => {
          this.serverErrorMessage = error.message
          this.showErrorDialog()
        }
      })
    }
  }

  editDialog(task: Task) {
    this.inputMessage = task.message
    this.inputPriority = task.priority
    this.inputReminderTime = this.stringToDate(task.reminderTime)
    this.selectTask = task
    this.validationFailedMessage = ''
    this.action = this.EDIT
    this.showDialog()
  }

  edit() {
    if (this.validation()) {
      this.selectTask.message = this.inputMessage
      this.selectTask.priority = this.inputPriority
      this.selectTask.reminderTime = this.inputReminderTime?.toISOString()

      this.taskService.update(this.selectTask).subscribe({
        next: (task) => {
          this.getTasks()
          this.hideDialog()
        },
        error: (error) => {
          this.serverErrorMessage = error.message
          this.showErrorDialog()
        }
      })
    }
  }

  validation() {
    if (!(this.inputMessage && this.inputPriority)) {
      this.validationFailedMessage = "Message & priority can't be empty."
      return false
    }

    if (this.inputPriority === Priority.HIGH && (this.inputReminderTime === null || this.inputReminderTime.getTime() < Date.now())) {
      this.validationFailedMessage = 'High priority task need available reminder time.'
      return false
    }
    this.validationFailedMessage = ''
    return true
  }

  done(id: string) {
    this.taskService.complete(id).subscribe({
      next: (task) => {
        this.getTasks()
      },
      error: (error) => {
        this.serverErrorMessage = error.message
        this.showErrorDialog()
      }
    })
  }

  delete(id: string) {
    this.taskService.delete(id).subscribe({
      next: (task) => {
        this.toDoTasks = this.toDoTasks.filter(t => t.id !== id)
      },
      error: (error) => {
        this.serverErrorMessage = error.message
        this.showErrorDialog()
      }
    })
  }

  reopen(id: string) {
    this.taskService.reopen(id).subscribe({
      next: (task) => {
        this.getTasks()
      },
      error: (error) => {
        this.serverErrorMessage = error.message
        this.showErrorDialog()
      }
    })
  }

  postponed(task: Task) {
    let now = new Date();
    now.setMinutes(now.getMinutes() + 30);
    task.reminderTime = now.toISOString()

    this.taskService.update(task).subscribe({
      next: (task) => {
        this.getTasks()
      },
      error: (error) => {
        this.serverErrorMessage = error.message
        this.showErrorDialog()
      }
    })
  }

  stringToDate(dateAsString: string | undefined) {
    if (dateAsString) {
      return new Date(dateAsString)
    }
    return null
  }

  isExpired(reminderTime: string | undefined) {
    const time = this.stringToDate(reminderTime)
    if (time) {
      return time.getTime() <= Date.now()
    }
    return false
  }

  showDialog() {
    this.displayDialog = true
  }

  hideDialog() {
    this.displayDialog = false
  }

  showErrorDialog() {
    this.displayErrorDialog = true
  }

  hideErrorDialog() {
    this.displayErrorDialog = false
    this.serverErrorMessage = ''
  }

  getBackGroupColer(proiory: string, completed: string): string {
    if (completed === Completed.Y) {
      return 'gray'
    } else {
      let key = Priority[proiory as keyof typeof Priority]
      if (key === Priority.HIGH) {
        return '#f2b9b96b'
      } else if (key === Priority.MEDIUM) {
        return 'rgba(186, 169, 14, 0.25)'
      } else {
        return 'rgba(75, 205, 49, 0.35)'
      }
    }
  }

  get completedEnum(): typeof Completed {
    return Completed; 
  }

}
