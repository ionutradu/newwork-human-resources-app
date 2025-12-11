export type EmployeeRole = 'MANAGER' | 'COWORKER' | 'EMPLOYEE';

export interface AuthResponse {
  token: string;
}

export interface User {
  id: string;
  email: string;
  roles: EmployeeRole[];
}

export interface EmployeeProfile {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: EmployeeRole[];
}

export interface EmployeeSensitiveProfile extends EmployeeProfile {
  monthlySalary: number;
}

export interface EmployeeSensitiveProfile extends EmployeeProfile {
  monthlySalary: number;
  absences?: AbsenceDTO[];
  feedbacks?: FeedbackDTO[];
}

export interface FeedbackDTO {
  id: string;
  reviewerName: string;
  polishedText: string;
  // serialized LocalDateTime from backend - use ISO string
  createdAt: string;
}

export interface AbsenceDTO {
  id: string;
  // serialized LocalDate from backend - typically ISO date string or array handled by client
  startDate: string;
  endDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}

export interface AbsenceRequestDTO {
  startDate: string;
  endDate: string;
  reason: string;
}

export interface FeedbackRequestDTO {
  content: string;
}

export interface ManagerUpdateEmployeeDTO {
  monthlySalary?: number;
  roles?: EmployeeRole[];
  firstName?: string;
  lastName?: string;
  email?: string;
}

export interface CreateUserRequestDTO {
  email: string;
  password: string;
  roles: EmployeeRole[];
}

export interface AbsenceActionRequestDTO {
  action: 'ACCEPT' | 'REJECT';
}

export interface ManagerUpdateAbsenceRequestDTO {
  status: 'APPROVED' | 'REJECTED';
}

export interface ManagerUpdateFeedbackDTO {
  content: string;
}