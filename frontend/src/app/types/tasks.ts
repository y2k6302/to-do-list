export interface Task {
  id: string,
  message: string,
  completed: Completed,
  priority: Priority,
  reminderTime?: string
}

export enum Priority {
  HIGH = "HIGH", MEDIUM = "MEDIUM", LOW = "LOW"
}

export enum Completed {
  Y = "Y", N = "N"
}