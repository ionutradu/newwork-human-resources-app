import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { AbsenceRequestDTO } from '../../shared/models/user.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-absence',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './absence.component.html',
  styleUrls: ['./absence.component.css'],
})
export class AbsenceComponent {
  absence: AbsenceRequestDTO = { startDate: '', endDate: '', reason: '' };
  // validation errors returned by backend (field -> message)
  errors: Record<string,string> = {};
  private apiService = inject(ApiService);
  private authService = inject(AuthService);
  private toastr = inject(ToastrService);
  currentUserId: string = this.authService.getUserFromToken()?.id || '';

  requestAbsence() {
    this.errors = {};
    this.apiService.requestAbsence(this.absence).subscribe({
      next: () => {
        this.toastr.success('Absence request submitted successfully!');
        this.absence = { startDate: '', endDate: '', reason: '' };
      },
      error: (err: any) => {
        // If backend returns validation errors in body as { field: message }
        if (err && err.status === 400 && err.error && typeof err.error === 'object') {
          this.errors = err.error;
          const details = Object.entries(this.errors)
            .map(([field, message]) => `${field}: ${message}`)
            .join('<br>');
          this.toastr.error(details || 'Please fix the highlighted fields.', 'Validation errors', {
            enableHtml: true
          });
        } else {
          this.toastr.error('Failed to submit absence request.');
        }
      }
    });
  }
}