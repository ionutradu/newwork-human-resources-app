import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { EmployeeRole } from '../../shared/models/user.model';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const requiredRoles = route.data['roles'] as EmployeeRole[];

  const isAuthenticated = !!authService.getToken();

  if (!isAuthenticated) {
    return router.createUrlTree(['/login']);
  }

  if (!requiredRoles || authService.hasRole(requiredRoles)) {
    return true;
  }

  return router.createUrlTree(['/']);
};