import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';
import { AuthResponse, User, EmployeeRole } from '../../shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'hr_app_token';
  private userSubject = new BehaviorSubject<User | null>(this.getUserFromToken());

  public user$ = this.userSubject.asObservable();
  private BASE_URL = 'http://localhost:8080/auth';

  private http = inject(HttpClient);

  private saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.userSubject.next(this.getUserFromToken());
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.userSubject.next(null);
  }

  getUserFromToken(): User | null {
    const token = this.getToken();
    if (token) {
      try {
        const payload: any = jwtDecode(token);
        const user: User = {
                id: payload.sub,
                email: payload.email,
                roles: payload.roles as EmployeeRole[],
        };
        return user;
      } catch (e) {
        this.logout();
        return null;
      }
    }
    return null;
  }

  hasRole(requiredRoles: EmployeeRole[]): boolean {
    const user = this.userSubject.value;
    if (!user) return false;
    return requiredRoles.some(role => user.roles.includes(role));
  }

  isCurrentUser(id: string): boolean {
    const user = this.userSubject.value;
    return user?.id === id;
  }

  login(email: string, password: string): Observable<AuthResponse> {
    const dto = { email, password };
    return this.http.post<AuthResponse>(`${this.BASE_URL}/login`, dto).pipe(
            tap(response => this.saveToken(response.token)),
            catchError(error => {
    return of({ token: '' } as AuthResponse);
      })
    );
  }
}