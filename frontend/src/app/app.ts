import { Component, signal, inject } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent {
  private readonly TOKEN_KEY = 'hr_app_token';
  protected readonly title = signal('hr-frontend');
  private auth = inject(AuthService);
  private router = inject(Router);
  
  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  getFirstName(): string | null {
    return localStorage.getItem('hr_app_first_name');
  }

  getLastName(): string | null {
    return localStorage.getItem('hr_app_last_name');
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
