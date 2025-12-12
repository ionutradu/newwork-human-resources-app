import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  private authService = inject(AuthService);
  private router = inject(Router);

  login() {
    this.error = '';
    this.authService.login(this.email, this.password).subscribe({
      next: (res) => {
        if (res.token) {
          // determine destination based on roles
          const user = this.authService.getUserFromToken();
          if (user) {
            const roles = user.roles || [];
            if (roles.includes('MANAGER') || roles.includes('COWORKER')) {
              this.router.navigate(['/employees']);
            } else {
              // default for EMPLOYEE: go to own profile
              this.router.navigate([`/profile/${user.id}`]);
            }
          } else {
            this.router.navigate(['/']);
          }
        } else {
          this.error = 'Invalid credentials or login failed.';
        }
      },
      error: (err) => {
        this.error = 'Login failed. Check server connection.';
      }
    });
  }
}