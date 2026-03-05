import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    const token = localStorage.getItem('token');

    if (!token) {
      console.warn('AuthGuard: No token found.');
      return this.router.parseUrl('/login');
    }

    try {
      const decodedToken: any = jwtDecode(token);
      const isExpired = Number(decodedToken.exp) * 1000 < Date.now();

      if (isExpired) {
        console.warn('AuthGuard: Token expired.');
        localStorage.removeItem('token'); // cleanup
        return this.router.parseUrl('/login');
      }

      return true;
    } catch (err) {
      console.error('AuthGuard: Invalid token.', err);
      localStorage.removeItem('token');
      return this.router.parseUrl('/login');
    }
  }
}
