import { ComponentFixture, TestBed } from '@angular/core/testing'
import { HttpClientModule } from '@angular/common/http'

import { TabViewModule } from 'primeng/tabview'
import { OrderListModule } from 'primeng/orderlist'
import { DialogModule } from 'primeng/dialog'
import { FormsModule } from '@angular/forms'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { CalendarModule } from 'primeng/calendar'
import { SwPush } from '@angular/service-worker'
import { DropdownModule } from 'primeng/dropdown'
import { ImageModule } from 'primeng/image'

import { ToDoListComponent } from './to-do-list.component'
import { Task, Tasks } from 'src/app/types/tasks'
import { ToDoService } from 'src/app/service/task.service'
import { Observable } from 'rxjs'
import * as TE from 'fp-ts/TaskEither'

describe('ToDoListComponent', () => {
  let component: ToDoListComponent
  let fixture: ComponentFixture<ToDoListComponent>
  let testTask1: Task
  let testTask2: Task
  let toDoServiceSpy: jasmine.SpyObj<ToDoService>

  beforeEach(async () => {
    const toDoSpy = jasmine.createSpyObj('ToDoService', ['getToDoTasks',
      'getCompletedTasks', 'reorder', 'save', 'completed', 'update', 'delete'])
    const swPush = jasmine.createSpyObj('SwPush', {}, { notificationClicks: Observable })
    await TestBed.configureTestingModule({
      declarations: [ToDoListComponent],
      imports: [
        HttpClientModule,
        TabViewModule,
        OrderListModule,
        DialogModule,
        FormsModule,
        BrowserAnimationsModule,
        CalendarModule,
        DropdownModule,
        ImageModule
      ],
      providers: [
        { provide: ToDoService, useValue: toDoSpy },
        { provide: SwPush, useValue: swPush },
      ]
    })
      .compileComponents();

    toDoServiceSpy = TestBed.inject(ToDoService) as jasmine.SpyObj<ToDoService>

    fixture = TestBed.createComponent(ToDoListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    testTask1 = { id: '1', message: 'msg1', completed: 'N', priority: 'Medium', reminderTime: '2999-01-01 01:00:00' }
    testTask2 = { id: '2', message: 'msg2', completed: 'N', priority: 'Low', reminderTime: '2999-01-01 01:00:00' }
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('component test', () => {

    describe('dialog', () => {
      describe('add', () => {
        it(`should clear inputMessage and display addDialog when 'addDialog()' being called`, () => {
          component.inputMessage = 'for test'
          component.displayAddDialog = false
          component.addDialoag()

          expect(component.inputMessage).toBe('')
          expect(component.displayAddDialog).toBeTrue()
        })

        it(`should hidden add dialog and tasks index increase 1 when 'add()' being called`, async () => {
          toDoServiceSpy.save.and.returnValue(TE.of({ task: testTask2 }))
          component.toDoTasks = [testTask1]
          component.displayAddDialog = true
          await component.add()

          expect(component.toDoTasks.length).toBe(2)
          expect(component.displayAddDialog).toBeFalse()
        })
      })

      describe('edit', () => {
        it(`should selectTask equals task and display edit dialog when 'editDialog()' being called`, () => {
          const task: Task = testTask1
          component.displayEditDialog = false
          component.editDialog(task)

          expect(component.selectTask).toBe(task)
          expect(component.displayEditDialog).toBeTrue()
        })

        it(`should hidden edit dialog and selectTask.message equals inputMessage when 'edit()' being called`, async () => {
          component.toDoTasks = [testTask1, testTask2]
          toDoServiceSpy.update.and.returnValue(
            TE.of({ task: { id: '1', message: 'test message 2', completed: 'N', priority: 'Medium', reminderTime: '2999-01-01 01:00:00' } })
          )
          component.displayEditDialog = true
          component.selectTask = testTask1
          component.inputMessage = 'test message 2'
          component.inputPriority = 'Medium'
          component.inputReminderTime = component.stringToDate('2999-01-01 01:00:00')
          await component.edit()

          expect(component.selectTask.message).toBe(component.inputMessage)
          expect(component.selectTask.priority).toBe(component.inputPriority)
          expect(component.selectTask.reminderTime).toBe(component.dateToString(component.inputReminderTime))
          expect(component.displayEditDialog).toBeFalse()
        })
      })
    })

    describe('other', () => {
      it(`should toDotasks reduce task when that task done`, async () => {
        toDoServiceSpy.completed.and.returnValue(TE.of({ task: testTask1 }))
        component.toDoTasks = [testTask1, testTask2]
        await component.done(testTask1.id)

        expect(component.toDoTasks.filter(t => t.id == testTask1.id)).toEqual([])
      })

      it(`should task delete when 'delete(task.id)' being called`, async () => {
        toDoServiceSpy.delete.and.returnValue(TE.of({}))
        component.toDoTasks = [testTask1, testTask2]
        await component.delete(testTask1.id)

        expect(component.toDoTasks.filter(t => t.id == testTask1.id)).toEqual([])
      })

      it(`should toDoTasks equals 'getToDoTasks()' result when 'onSelectTabChange(0)' being called`, async () => {
        const tasks: Tasks = { tasks: [testTask1, testTask2] }
        component.toDoTasks = []
        toDoServiceSpy.getTasks.and.returnValue(TE.of(tasks))
        await component.onSelectTabChange({ index: 0 })

        expect(component.toDoTasks).toEqual(tasks.tasks)
      })

      it(`should completedTasks equals 'getCompletedTasks()' result when 'onSelectTabChange(1)' being called`, async () => {
        const tasks: Task[] = [testTask1, testTask2]
        component.completedTasks = []
        toDoServiceSpy.getCompletedTasks.and.returnValue(TE.of({ tasks: tasks }))
        await component.onSelectTabChange({ index: 1 })

        expect(component.completedTasks).toBe(tasks)
      })
    })

    describe('API Failed', () => {
      it(`should get error when toDoService.getToDoTasks() failed`, async () => {
        toDoServiceSpy.getTasks.and.returnValue(TE.left(new Error('Get to-do tasks failed.')))

        await component.getToDoTasks()

        expect(component.displayErrorDialog).toBeTrue()
        expect(component.serverErrorMessage).toBe('Get to-do tasks failed.')
      })

      it(`should get error when toDoService.getCompletedTasks() failed`, async () => {
        toDoServiceSpy.getCompletedTasks.and.returnValue(TE.left(new Error('Get Completed tasks failed.')))

        await component.getCompletedTasks()

        expect(component.displayErrorDialog).toBeTrue()
        expect(component.serverErrorMessage).toBe('Get Completed tasks failed.')
      })

      it(`should get error when toDoService.save() failed`, async () => {
        toDoServiceSpy.save.and.returnValue(TE.left(new Error('Save task failed.')))
        component.inputMessage = 'test'
        component.inputPriority = 'Low'

        await component.add()

        expect(component.displayErrorDialog).toBeTrue()
        expect(component.serverErrorMessage).toBe('Save task failed.')
      })

      it(`should get error when toDoService.completed() failed`, async () => {
        toDoServiceSpy.completed.and.returnValue(TE.left(new Error('Completed task failed.')))
        component.inputMessage = 'test'
        component.inputPriority = 'Low'

        await component.done('task id')

        expect(component.displayErrorDialog).toBeTrue()
        expect(component.serverErrorMessage).toBe('Completed task failed.')
      })

      it(`should get error when toDoService.update() failed`, async () => {
        toDoServiceSpy.update.and.returnValue(TE.left(new Error('Update task failed.')))
        component.inputMessage = 'test'
        component.inputPriority = 'Low'

        await component.edit()

        expect(component.displayErrorDialog).toBeTrue()
        expect(component.serverErrorMessage).toBe('Update task failed.')
      })

      it(`should get error when toDoService.delete() failed`, async () => {
        toDoServiceSpy.delete.and.returnValue(TE.left(new Error('Delete task failed.')))

        await component.delete('task id')

        expect(component.displayErrorDialog).toBeTrue()
        expect(component.serverErrorMessage).toBe('Delete task failed.')
      })

      it(`should close ErrorDialog when closeErrorDialog() being called`, async () => {
        component.displayErrorDialog = true
        component.serverErrorMessage = 'some error message'

        component.closeErrorDialog()

        expect(component.displayErrorDialog).toBeFalse()
        expect(component.serverErrorMessage).toBe('')
      })
    })

    describe('tasks sort', () => {
      it(`should sort task by priority 'High -> Medium -> Low' when sortTasks() being called`, async () => {
        const highTask1 = { ...testTask1, priority: 'High', reminderTime: '2022-01-02 01:00:00' }
        const highTask2 = { ...testTask1, priority: 'High', reminderTime: '2022-01-01 01:00:00' }
        const mediumTask1 = { ...testTask1, priority: 'Medium', reminderTime: undefined }
        const mediumTask2 = { ...testTask1, priority: 'Medium', reminderTime: '2022-01-01 01:00:00' }
        const mediumTask3 = { ...testTask1, priority: 'Medium', reminderTime: '2022-01-02 01:00:00' }
        const lowTask1 = { ...testTask1, priority: 'Low', reminderTime: '2022-01-02 01:00:00' }
        const lowTask2 = { ...testTask1, priority: 'Low', reminderTime: undefined }
        const lowTask3 = { ...testTask1, priority: 'Low', reminderTime: undefined, message: 'lowTask3' }
        component.toDoTasks = [mediumTask1, lowTask1, mediumTask3, mediumTask2, highTask2, lowTask2, highTask1, lowTask3]

        component.sortTasks()

        expect(component.toDoTasks[0].priority).toBe('High')
        expect(component.toDoTasks[0].reminderTime).toBe('2022-01-01 01:00:00')
        expect(component.toDoTasks[1].priority).toBe('High')
        expect(component.toDoTasks[1].reminderTime).toBe('2022-01-02 01:00:00')
        expect(component.toDoTasks[2].priority).toBe('Medium')
        expect(component.toDoTasks[2].reminderTime).toBe('2022-01-01 01:00:00')
        expect(component.toDoTasks[3].priority).toBe('Medium')
        expect(component.toDoTasks[3].reminderTime).toBe('2022-01-02 01:00:00')
        expect(component.toDoTasks[4].priority).toBe('Medium')
        expect(component.toDoTasks[5].priority).toBe('Low')
        expect(component.toDoTasks[5].reminderTime).toBe('2022-01-02 01:00:00')
        expect(component.toDoTasks[6].priority).toBe('Low')
        expect(component.toDoTasks[7].message).toBe('lowTask3')
      })
    })

    describe('expired check', () => {
      it(`should get false when time not expired`, async () => {
        expect(component.isExpired('2099-01-01 01:00:00')).toBeFalse()
      })

      it(`should get true when time was expired`, async () => {
        expect(component.isExpired('2001-01-01 01:00:00')).toBeTrue()
      })
    })

    describe('priority and reminderTime validation', () => {
      it(`should get true when 'validation()' being called and inputPriority is 'High' and inputReminderTime is available`, async () => {
        component.inputPriority = 'High'
        component.inputReminderTime = component.stringToDate('2099-01-01 01:00:00')

        expect(component.validation()).toBeTrue()
      })

      it(`should get false when 'validation()' being called and use not available reminder time.`, async () => {
        component.inputPriority = 'High'
        component.inputReminderTime = component.stringToDate('2022-01-01 01:00:00')

        expect(component.validation()).toBeFalse()
      })

      it(`should get false when 'validation()' being called and inputPriority is 'High' and inputReminderTime is null`, async () => {
        component.inputPriority = 'High'
        component.inputReminderTime = null

        expect(component.validation()).toBeFalse()
      })
    })

    describe('date and string convert', () => {
      it(`should get string result when dateToString being called`, () => {
        const date = new Date('2022-01-01 01:01:01')
        const result = component.dateToString(date)

        expect(result).toBe('2022-01-01 01:01:00')
      })

      it(`should get Undefined when dateToString input is null`, () => {
        const result = component.dateToString(null)

        expect(result).toBeUndefined()
      })


      it(`should get date result when stringToDate being called`, () => {
        const dateAsString = '2022-01-01 01:01:01'
        const date = new Date(dateAsString)
        const result = component.stringToDate(dateAsString)

        expect(result).toEqual(date)
      })

      it(`should get null when stringToDate input is undefined`, () => {
        const result = component.stringToDate(undefined)

        expect(result).toBeNull()
      })

      it(`should get one hour ago when addOneOur being called`, () => {
        const result = component.addOnehour('2022-01-01 01:00:00')

        expect(result).toBe('2022-01-01 02:00:00')
      })
    })

    describe('notification', () => {
      it(`should get notification option when buildNotificationOption being called`, () => {
        const result = component.buildNotificationOption(testTask2)
        const expectResult = {
          body: testTask2.message,
          data: {
            id: testTask2.id
          },
          requireInteraction: true,
          actions: [
            {
              action: 'done',
              title: 'Done'
            },
            {
              action: 'wait',
              title: 'Reminder in an hour'
            }
          ]
        }

        expect(result).toEqual(expectResult)
      })

      describe('timer', () => {
        it(`should timerMap add notification timer when initNotifications being called`, async () => {
          component.toDoTasks = [testTask1, testTask2]
          component.initNotifications()

          expect(component.timerMap.get(testTask1.id)).toBeDefined()
          expect(component.timerMap.get(testTask2.id)).toBeDefined()
        })

        it(`should timerMap add notification timer when addNotification being called`, async () => {
          component.addNotification(testTask2)

          expect(component.timerMap.get(testTask2.id)).toBeDefined()
        })

        it(`should timerMap remove notification timer when removeNotification being called`, async () => {
          component.addNotification(testTask2)
          component.removeNotification(testTask2.id)

          expect(component.timerMap.get(testTask2.id)).toBeUndefined()
        })

        it(`should timerMap keep have timer notification timer when rebuildNotification being called`, async () => {
          component.addNotification(testTask2)
          await component.rebuildNotification(testTask2)

          expect(component.timerMap.get(testTask2.id)).toBeDefined()
        })

        describe('click event', () => {
          it(`should completed task when notificationHandle being called with 'done' action`, async () => {
            toDoServiceSpy.completed.and.returnValue(TE.of({ task: testTask1 }))
            component.toDoTasks = [testTask1, testTask2]
            await component.notificationHandle('done', testTask1.id)

            expect(component.toDoTasks.length).toEqual(1)
            expect(component.toDoTasks[0]).toEqual(testTask2)
          })

          it(`should task reminderTime add one hour when notificationHandle being called with 'wait' action`, async () => {
            component.toDoTasks = [testTask1, testTask2]
            toDoServiceSpy.update.and.returnValue(
              TE.of({ task: { id: '1', message: 'msg1', completed: 'N', priority: 'Medium', reminderTime: '2999-01-01 02:00:00' } })
            )
            await component.notificationHandle('wait', testTask1.id)

            expect(component.toDoTasks.length).toEqual(2)
            expect(component.toDoTasks[0].reminderTime).toEqual('2999-01-01 02:00:00')
          })
        })
      })
    })
  })

  describe('templated test', () => {
    let compiledComponent: HTMLElement

    beforeEach(() => {
      fixture.detectChanges()
      compiledComponent = fixture.nativeElement
    });

    describe('add task dialog save button enabled or disabled', () => {
      let saveButton: HTMLElement

      beforeEach(() => {
        const addButton: HTMLElement = compiledComponent.querySelector('#addButton')!
        addButton.click()
        fixture.detectChanges()
        saveButton = compiledComponent.querySelector('#saveButton')!
      });

      it(`should save button disabled when input is blank`, () => {
        expect(saveButton.getAttribute('disabled')).toBe('')
      })

      it(`should save button enabled when input is not blank`, () => {
        component.inputMessage = "test task"
        fixture.detectChanges()

        expect(saveButton.getAttribute('disabled')).toBeNull()
      })
    })

    describe('edit task dialog save button enabled or disabled', () => {
      let editSaveButton: HTMLElement

      beforeEach(() => {
        component.toDoTasks = [testTask1]
        fixture.detectChanges()
        const addButton: HTMLElement = compiledComponent.querySelector('.edit-button')!
        addButton.click()
        fixture.detectChanges()
        editSaveButton = compiledComponent.querySelector('#editSaveButton')!
      });

      it(`should save button enabled when input is not blank`, () => {
        expect(editSaveButton.getAttribute('disabled')).toBeNull()
      })

      it(`should save button disabled when input is blank`, () => {
        component.inputMessage = '  '
        fixture.detectChanges();

        expect(editSaveButton.getAttribute('disabled')).toBe('')
      })
    })

    describe('done and delete button event', () => {

      beforeEach(() => {
        component.toDoTasks = [testTask1]
        fixture.detectChanges()
      })

      it(`should trigger 'done(id)' when done button clicked`, () => {
        const doneButton: HTMLElement = compiledComponent.querySelector('.done-button')!
        spyOn(component, 'done')
        doneButton.click()

        expect(component.done).toHaveBeenCalled()
      })

      it(`should trigger 'delete(id)' when done button clicked`, () => {
        const deleteButton: HTMLElement = compiledComponent.querySelector('.delete-button')!
        spyOn(component, 'delete')
        deleteButton.click()

        expect(component.delete).toHaveBeenCalled()
      })

    })
  })

})


