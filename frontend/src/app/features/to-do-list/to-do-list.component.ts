import { Component, OnInit } from '@angular/core'
import { TaskService } from 'src/app/service/task.service'
import { Task } from 'src/app/types/tasks'

@Component({
  selector: 'app-to-do-list',
  templateUrl: './to-do-list.component.html',
  styleUrls: ['./to-do-list.component.scss']
})
export class ToDoListComponent implements OnInit {

  readonly ADD = "Add"
  readonly EDIT = "Edit"
  readonly priorities = ['Low', 'Medium', 'High']
  readonly sortMap = new Map([['High', 2], ['Medium', 1], ['Low', 0]])

  toDoTasks: Task[] = []
  inputMessage: string = ''
  inputPriority: string = 'Medium'
  inputReminderTime: Date | null = null
  action: string = ''
  displayDialog: boolean = false
  displayErrorDialog: boolean = false
  serverErrorMessage: string = ''
  selectTask: Task = { id: '', message: '', completed: 'N', priority: 'Medium' }
  validationFailedMessage = ''

  constructor(private taskService: TaskService) { }

  ngOnInit() {
    this.getTasks()
  }

  getTasks() {
    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.toDoTasks = tasks
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

      //sort by completed
      if (x.completed === 'Y') {
        return 1
      } else if (y.completed === 'Y') {
        return -1
      }

      //sort by priority
      const xPriority: any = this.sortMap.get(x.priority)
      const yPriority: any = this.sortMap.get(y.priority)      
      if (xPriority > yPriority) {
        return -1
      } else if (xPriority < yPriority) {
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
    this.inputPriority = 'Medium'
    this.inputReminderTime = null
    this.validationFailedMessage = ''
    this.action = this.ADD
    this.showDialog()
  }

  add() {
    if (this.validation()) {
      this.selectTask.message = this.inputMessage
      this.selectTask.priority = this.inputPriority
      this.selectTask.reminderTime = this.inputReminderTime?.toISOString()

      this.taskService.create(this.selectTask).subscribe({
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

    if (this.inputPriority === 'High' && (this.inputReminderTime === null || this.inputReminderTime.getTime() < Date.now())) {
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

  redo(id: string) {
    this.taskService.redo(id).subscribe({
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
    if (completed === 'Y') {
      return 'gray'
    } else {
      if (proiory === "High") {
        return '#f2b9b96b'
      } else if (proiory === "Medium") {
        return 'rgba(186, 169, 14, 0.25)'
      } else {
        return 'rgba(75, 205, 49, 0.35)'
      }
    }
  }

}
