import { Routes } from '@angular/router';
import { roleGuard } from './core/guards/role.guard';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { AbsenceComponent } from './pages/absence/absence.component';
import { ManagerComponent } from './pages/manager/manager.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: DashboardComponent,
    canActivate: [roleGuard],
    data: { roles: ['COWORKER', 'MANAGER'] }
  },
  {
    path: 'profile/:id',
    component: ProfileComponent,
    canActivate: [roleGuard],
    data: { roles: ['EMPLOYEE', 'COWORKER', 'MANAGER'] }
  },
  {
    path: 'absence',
    component: AbsenceComponent,
    canActivate: [roleGuard],
    data: { roles: ['EMPLOYEE'] }
  },
  {
    path: 'manager',
    component: ManagerComponent,
    canActivate: [roleGuard],
    data: { roles: ['MANAGER'] }
  },
  { path: '**', redirectTo: '' }
];