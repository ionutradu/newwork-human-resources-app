import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { AbsenceRequestDTO } from '../../shared/models/user.model';

@Component({
  selector: 'app-absence',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './absence.component.html',
  styleUrls: ['./absence.component.css'],
})
export class AbsenceComponent {
  absence: AbsenceRequestDTO = { startDate: '', endDate: '', reason: '' };
  message: string = '';
  private apiService = inject(ApiService);

  requestAbsence() {
    this.message = '';
    this.apiService.requestAbsence(this.absence).subscribe({
      next: () => {
        this.message = 'Absence request submitted successfully!';
      },
      error: (err) => {
        this.message = 'Failed to submit absence request.';
      }
    });
  }
}