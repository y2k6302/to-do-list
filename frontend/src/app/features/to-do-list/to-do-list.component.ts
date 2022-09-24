import { DatePipe } from '@angular/common'
import { Component, OnInit } from '@angular/core'
import { Subscription, timer } from 'rxjs'
import { ToDoService } from 'src/app/service/task.service'
import { Task } from 'src/app/types/tasks'
import { SwPush } from '@angular/service-worker'
import { pipe } from 'fp-ts/lib/function'
import * as TE from 'fp-ts/TaskEither'

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

  toDoTasks!: Task[]
  completedTasks!: Task[]
  inputMessage: string = ''
  inputPriority: string = 'Medium'
  inputReminderTime: Date | null = null
  action: string = ''
  displayDialog: boolean = false;
  displayErrorDialog: boolean = false
  serverErrorMessage: string = ''
  selectTask: Task = { id: '', message: '', completed: 'N', priority: 'Medium' }
  sw: ServiceWorkerRegistration | undefined = undefined
  datePipe: DatePipe = new DatePipe('en-US')
  timerMap = new Map<string, Subscription>()
  validationFailedMessage = ''

  constructor(private toDoService: ToDoService, private swPush: SwPush) { }
  
  async ngOnInit(): Promise<void> {

    this.askNotificationPermission()
    await this.getToDoTasks()
  }

  private async askNotificationPermission() {
    if ('Notification' in window) {
      const permission = await Notification.requestPermission()
      if (permission === 'granted') {
        this.sw = await navigator.serviceWorker.getRegistration()
      }
    }
  }

  async getToDoTasks() {
    await pipe(
      this.toDoService.getTasks(),
      TE.match(
        error => {
          this.displayErrorDialog = true
          this.serverErrorMessage = error.message
        },
        tasks => {
          this.toDoTasks = tasks
          this.sortTasks()
        }
      )
    )()
  }

  sortTasks() {
    this.toDoTasks.sort((x, y) => {

      //sort by completed
      if (x.completed === 'Y') {
        return 1
      } else if(y.completed === 'Y') {
        return -1
      }

      const xPriority: any = this.sortMap.get(x.priority)
      const yPriority: any = this.sortMap.get(y.priority)

      //sort by priority
      if (xPriority > yPriority) {
        return -1
      } else if (xPriority < yPriority) {
        return 1
      }

      const xRemider = this.stringToDate(x.reminderTime)?.getTime()
      const yRemider = this.stringToDate(y.reminderTime)?.getTime()

      //sort by remider
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

      return 0;
    })
  }

  addDialoag() {
    this.inputMessage = ''
    this.inputPriority = 'Medium'
    this.inputReminderTime = null
    this.validationFailedMessage = ''
    this.action = this.ADD
    this.showDialog();
  }

  async add() {
    if (this.validation()) {
      this.selectTask.message = this.inputMessage
      this.selectTask.priority = this.inputPriority
      this.selectTask.reminderTime = this.inputReminderTime?.toISOString()

      await pipe(
        this.toDoService.save(this.selectTask),
        TE.match(
          error => {
            this.displayErrorDialog = true
            this.serverErrorMessage = error.message
          },
          task => {
            const saveTask = task
            this.toDoTasks.push(saveTask)
            this.sortTasks()
            this.hideDialog();
          }
        )
      )()
    }
  }

  validation() {
    if (this.inputPriority === 'High' && (this.inputReminderTime === null || this.inputReminderTime.getTime() < Date.now())) {
      this.validationFailedMessage = 'High priority task need available reminder time.'
      return false
    }
    this.validationFailedMessage = ''
    return true
  }

  editDialog(task: Task) {
    this.inputMessage = task.message
    this.inputPriority = task.priority
    this.inputReminderTime = this.stringToDate(task.reminderTime)
    this.selectTask = task
    this.validationFailedMessage = ''
    this.action = this.EDIT
    this.showDialog();
  }

  async edit() {
    if (this.validation()) {
      this.selectTask.message = this.inputMessage
      this.selectTask.priority = this.inputPriority
      this.selectTask.reminderTime = this.inputReminderTime?.toISOString()

      await pipe(
        this.toDoService.update(this.selectTask),
        TE.match(
          error => {
            this.displayErrorDialog = true
            this.serverErrorMessage = error.message
          },
          task => {
            const updateTask = task
            this.getToDoTasks();
            this.hideDialog();
          }
        )
      )()
    }
  }

  async done(id: string) {
    await pipe(
      this.toDoService.complete(id),
      TE.match(
        error => {
          this.displayErrorDialog = true
          this.serverErrorMessage = error.message
        },
        _ => {
          this.getToDoTasks();
        }
      )
    )()
  }

  async delete(id: string) {
    await pipe(
      this.toDoService.delete(id),
      TE.match(
        error => {
          this.displayErrorDialog = true
          this.serverErrorMessage = error.message
        },
        _ => {
          this.toDoTasks = this.toDoTasks.filter(t => t.id !== id)
        }
      )
    )()
  }

  async redo(id: string) {
    await pipe(
      this.toDoService.redo(id),
      TE.match(
        error => {
          this.displayErrorDialog = true
          this.serverErrorMessage = error.message
        },
        _ => {
          this.getToDoTasks();
        }
      )
    )()
  }

  stringToDate(dateAsString: string | undefined) {
    if (dateAsString) {
      return new Date(dateAsString)
    }
    return null
  }

  isExpired(reminderTime: string) {
    const time = this.stringToDate(reminderTime)
    if (time) {
      return time.getTime() < Date.now()
    }
    return false
  }

  closeErrorDialog() {
    this.displayErrorDialog = false
    this.serverErrorMessage = ''
  }

  showDialog() {
    this.displayDialog = true;
  }

  hideDialog() {
    this.displayDialog = false;
  }

  getBackGroupColer(proiory: string, completed: string):string {
    if (completed === 'Y') {
      return 'gray'
    } else {
      if(proiory === "High") {
        return '#f2b9b96b'
      } else if (proiory === "Medium") {
        return 'rgba(186, 169, 14, 0.25)'
      } else {
        return 'rgba(75, 205, 49, 0.35)'
      }
    }
  }

}
