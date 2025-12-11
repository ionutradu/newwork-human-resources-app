import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { EmployeeProfile, EmployeeSensitiveProfile, FeedbackRequestDTO, ManagerUpdateEmployeeDTO, EmployeeRole, AbsenceDTO, FeedbackDTO } from '../../shared/models/user.model';
import { switchMap, tap } from 'rxjs/operators';
import { of, Observable } from 'rxjs';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile: EmployeeProfile | EmployeeSensitiveProfile | null = null;
  profileId: string = '';
  private authService = inject(AuthService);
  private apiService = inject(ApiService);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  isManager = false;
  isCoworker = false;
  isEmployee = false;
  isCurrentUser = false;
  isSensitiveAccess = false;
  loading = true;
  feedbackContent = '';
  feedbackMessage = '';
  editMessage = '';
  showEditForm = false;
  editDto: ManagerUpdateEmployeeDTO = {};
  editRolesRaw = '';
  absences: AbsenceDTO[] = [];
  feedbacks: FeedbackDTO[] = [];

  ngOnInit(): void {
    console.log('ProfileComponent init - current user:', this.authService.getUserFromToken());
    this.authService.user$.pipe(
      tap(user => {
        console.log('auth.user$ emitted:', user);
        this.isManager = user?.roles.includes('MANAGER') || false;
        this.isCoworker = user?.roles.includes('COWORKER') || false;
        this.isEmployee = user?.roles.includes('EMPLOYEE') || false;
      }),
      switchMap(() => this.route.paramMap),
      tap(params => {
        this.profileId = params.get('id') || '';
        console.log('route param id=', this.profileId);
        this.isCurrentUser = this.authService.isCurrentUser(this.profileId);
        this.isSensitiveAccess = this.isManager || this.isCurrentUser;
        console.log({ isCurrentUser: this.isCurrentUser, isManager: this.isManager, isCoworker: this.isCoworker, isSensitiveAccess: this.isSensitiveAccess });
      }),
      switchMap(() => this.loadProfile())
    ).subscribe((p: EmployeeProfile | EmployeeSensitiveProfile | null) => {
      console.log('loadProfile result:', p);
      this.profile = p as any;
      this.loading = false;
        this.absences = (this.profile as any).absences || [];
        this.feedbacks = (this.profile as any).feedbacks || [];
        if (this.profile) {
            const p: any = this.profile;
            this.editDto.email = p.email;
            this.editDto.firstName = p.firstName;
            this.editDto.lastName = p.lastName;
            this.editDto.monthlySalary = p.monthlySalary ?? p.salary ?? undefined;
            this.editRolesRaw = (this.profile.roles || []).join(',');
          }
      this.cdr.detectChanges();
    }, (err: any) => {
      console.error('Error loading profile', err);
      this.loading = false;
      this.cdr.detectChanges();
    });
  }

  loadProfile(): Observable<EmployeeProfile | EmployeeSensitiveProfile | null> {
    if (!this.profileId) return of(null);
    if (this.isSensitiveAccess) {
      console.log('loading sensitive profile', this.profileId);
      return this.apiService.getSensitiveProfile(this.profileId) as Observable<EmployeeProfile | EmployeeSensitiveProfile | null>;
    } else if (this.isCoworker) {
      console.log('loading public profile', this.profileId);
      return this.apiService.getPublicProfile(this.profileId) as Observable<EmployeeProfile | EmployeeSensitiveProfile | null>;
    }
    console.log('no access to profile, returning null');
    return of(null);
  }

  submitFeedback() {
    const dto: FeedbackRequestDTO = { text: this.feedbackContent };
    this.apiService.leaveFeedback(this.profileId, dto).subscribe({
      next: () => {
        this.feedbackMessage = 'Feedback submitted successfully!';
        this.feedbackContent = '';
        // refresh profile to show new feedbacks
        this.cdr.detectChanges();
      },
      error: () => {
        this.feedbackMessage = 'Failed to submit feedback.';
        this.cdr.detectChanges();
      }
    });
  }

  updateAbsenceStatus(abs: AbsenceDTO) {
    if (!abs || !abs.id) return;
    const dto = { status: abs.status } as any;
    this.apiService.updateAbsenceRequest(abs.id, dto).subscribe({
      next: () => {
        // keep UI in sync; optionally show a message
        console.log('Absence status updated', abs.id, abs.status);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to update absence', err);
        this.cdr.detectChanges();
      }
    });
  }

    updateEmployee() {
      this.editMessage = '';
      this.apiService.updateEmployee(this.profileId, this.editDto).subscribe({
        next: (updatedProfile) => {
          this.profile = updatedProfile;
          this.editMessage = 'Profile updated successfully!';
          this.showEditForm = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.editMessage = 'Failed to update profile.';
          this.cdr.detectChanges();
        }
      });
    }

    saveManagerEdit() {
    // parse roles from comma separated input
    const roles = this.editRolesRaw.split(',').map(r => r.trim()).filter(r => r) as EmployeeRole[];
    if (roles.length) this.editDto.roles = roles;
    this.updateEmployee();
    }

  getSalary(): number | null {
    if (!this.profile) return null;
    const p: any = this.profile;
    return p.monthlySalary ?? p.salary ?? null;
  }

  // Format a Java LocalDate represented as [year, month, day] or {year,month,day} or ISO date string
  formatLocalDate(value: any): string {
    if (!value) return '';
    const pad = (n: number) => String(n).padStart(2, '0');
    let year: number | undefined;
    let month: number | undefined;
    let day: number | undefined;
    if (Array.isArray(value) && value.length >= 3) {
      year = Number(value[0]); month = Number(value[1]); day = Number(value[2]);
    } else if (typeof value === 'object' && value !== null && ('year' in value)) {
      year = Number((value as any).year); month = Number((value as any).month); day = Number((value as any).day);
    } else if (typeof value === 'string' || typeof value === 'number') {
      const d = new Date(value);
      if (!isNaN(d.getTime())) {
        year = d.getFullYear(); month = d.getMonth() + 1; day = d.getDate();
      }
    }
    if (year && month && day) {
      return `${pad(day)}.${pad(month)}.${year}`;
    }
    return '';
  }

  // Format a Java Instant (ISO string or epoch) to `dd.MM.yyyy HH:mm` local time
  formatInstant(value: any): string {
    if (!value) return '';
    let d: Date;
    if (typeof value === 'number') {
      d = new Date(value);
    } else if (typeof value === 'string') {
      // ensure ISO strings with zone are parsed correctly
      d = new Date(value);
    } else if (value instanceof Date) {
      d = value;
    } else {
      // unknown shape
      return '';
    }
    if (isNaN(d.getTime())) return '';
    const pad = (n: number) => String(n).padStart(2, '0');
    const day = pad(d.getDate());
    const month = pad(d.getMonth() + 1);
    const year = d.getFullYear();
    const hours = pad(d.getHours());
    const mins = pad(d.getMinutes());
    return `${day}.${month}.${year} ${hours}:${mins}`;
  }
}