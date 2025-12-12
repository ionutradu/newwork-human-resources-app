import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { EmployeeRole, CreateUserRequestDTO, AbsenceActionRequestDTO, ManagerUpdateAbsenceRequestDTO, ManagerUpdateFeedbackDTO } from '../../shared/models/user.model';

@Component({
  selector: 'app-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
    templateUrl: './manager.component.html',
})
export class ManagerComponent {
  private apiService = inject(ApiService);
  message = '';
  newUser: CreateUserRequestDTO = { email: '', password: '', roles: ['EMPLOYEE'] };

  absenceRequestId = '';
  patchAbsenceId = '';
  patchAbsenceStatus: 'APPROVED' | 'REJECTED' = 'APPROVED';
  patchFeedbackId = '';
  patchFeedbackContent = '';

  createEmployee() {
    this.message = '';
    this.apiService.createEmployee(this.newUser).subscribe({
      next: (res) => {
        this.message = `Employee ${res.email} created successfully!`;
        this.newUser = { email: '', password: '', roles: ['EMPLOYEE'] };
      },
      error: () => {
        this.message = 'Failed to create employee.';
      }
    });
  }

  processAbsence(action: 'ACCEPT' | 'REJECT') {
    if (!this.absenceRequestId) return;
    const dto: AbsenceActionRequestDTO = { action };
    this.apiService.processAbsence(this.absenceRequestId, dto).subscribe({
      next: () => {
        this.message = `Absence request ${this.absenceRequestId} ${action}ED successfully.`;
      },
      error: () => {
        this.message = `Failed to ${action} absence request.`;
      }
    });
  }

  patchAbsenceRequest() {
      if (!this.patchAbsenceId) return;
      const dto: ManagerUpdateAbsenceRequestDTO = { status: this.patchAbsenceStatus };
      this.apiService.updateAbsenceRequest(this.patchAbsenceId, dto).subscribe({
          next: () => {
              this.message = `Absence request ${this.patchAbsenceId} status updated to ${this.patchAbsenceStatus}.`;
          },
          error: () => {
              this.message = 'Failed to update absence request status.';
          }
      });
  }

  patchFeedback() {
      if (!this.patchFeedbackId) return;
      const dto: ManagerUpdateFeedbackDTO = { polishedText: this.patchFeedbackContent };
      this.apiService.updateFeedback(this.patchFeedbackId, dto).subscribe({
          next: () => {
              this.message = `Feedback ${this.patchFeedbackId} updated successfully.`;
              this.patchFeedbackContent = '';
          },
          error: () => {
              this.message = 'Failed to update feedback.';
          }
      });
  }
}