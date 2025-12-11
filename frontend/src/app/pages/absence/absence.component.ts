import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { AbsenceRequestDTO } from '../../shared/models/user.model';

@Component({
  selector: 'app-absence',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './absence.component.html',
  styleUrls: ['./absence.component.css'],
})
export class AbsenceComponent {
  absence: AbsenceRequestDTO = { startDate: '', endDate: '', reason: '' };
  message: string = '';
  // validation errors returned by backend (field -> message)
  errors: Record<string,string> = {};
  private cdr = inject(ChangeDetectorRef);
  private apiService = inject(ApiService);
  private authService = inject(AuthService);
  currentUserId: string = this.authService.getUserFromToken()?.id || '';

  requestAbsence() {
    this.message = '';
    this.errors = {};
    this.apiService.requestAbsence(this.absence).subscribe({
      next: () => {
        this.message = 'Absence request submitted successfully!';
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        // If backend returns validation errors in body as { field: message }
        if (err && err.status === 400 && err.error && typeof err.error === 'object') {
          this.errors = err.error;
          this.message = '';
        } else {
          this.message = 'Failed to submit absence request.';
        }
        this.cdr.detectChanges();
      }
    });
  }
}