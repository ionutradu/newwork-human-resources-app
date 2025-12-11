import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
        EmployeeProfile,
        EmployeeSensitiveProfile,
        AbsenceRequestDTO,
        FeedbackRequestDTO,
        ManagerUpdateEmployeeDTO,
        CreateUserRequestDTO,
        AbsenceActionRequestDTO,
        ManagerUpdateAbsenceRequestDTO,
        ManagerUpdateFeedbackDTO
        } from '../../shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private BASE_URL = 'http://localhost:8080';
  private http = inject(HttpClient);

  listPublicEmployees(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get<any>(`${this.BASE_URL}/employees/public?page=${page}&size=${size}`);
  }

  getPublicProfile(id: string): Observable<EmployeeProfile> {
    return this.http.get<EmployeeProfile>(`${this.BASE_URL}/employees/public/${id}`);
  }

  getSensitiveProfile(id: string): Observable<EmployeeSensitiveProfile> {
    return this.http.get<EmployeeSensitiveProfile>(`${this.BASE_URL}/employees/${id}`);
  }

  requestAbsence(dto: AbsenceRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.BASE_URL}/employees/absence`, dto);
  }

  leaveFeedback(employeeId: string, dto: FeedbackRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.BASE_URL}/employees/${employeeId}/feedback`, dto);
  }

  createEmployee(dto: CreateUserRequestDTO): Observable<any> {
    return this.http.post<any>(`${this.BASE_URL}/employees`, dto);
  }

  updateEmployee(id: string, dto: ManagerUpdateEmployeeDTO): Observable<any> {
    return this.http.patch<any>(`${this.BASE_URL}/manager/employees/${id}`, dto);
  }

  processAbsence(requestId: string, dto: AbsenceActionRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.BASE_URL}/manager/absences/${requestId}/process`, dto);
  }

  updateAbsenceRequest(id: string, dto: ManagerUpdateAbsenceRequestDTO): Observable<any> {
    return this.http.patch<any>(`${this.BASE_URL}/manager/absences/${id}`, dto);
  }

  updateFeedback(id: string, dto: ManagerUpdateFeedbackDTO): Observable<any> {
    return this.http.patch<any>(`${this.BASE_URL}/manager/feedbacks/${id}`, dto);
  }
}