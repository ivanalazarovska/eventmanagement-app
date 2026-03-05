import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { passwordValidator } from '../../../shared/validators/password-validator';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../../core/services/auth/auth.service';
import { Router } from '@angular/router'; // Add Router import
import { User } from '../../../core/models/user';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
  ]
})
export class LoginComponent implements OnInit {
  public loginForm: FormGroup | undefined;
  showPassword: boolean = false;
  errorMessage: string | null = null; // For storing error messages

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router // Inject Router for navigation
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          passwordValidator
        ]
      ]
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  public getPasswordErrorMessage(): string {
    const passwordControl = this.loginForm?.get('password');

    if (passwordControl?.hasError('required')) {
      return 'Ве молиме внесете лозинка';
    }
    if (passwordControl?.hasError('minlength')) {
      return 'Лозинката мора да има најмалку 6 карактери';
    }
    if (passwordControl?.hasError('passwordUppercase')) {
      return 'Лозинката мора да содржи барем една голема буква';
    }
    if (passwordControl?.hasError('passwordSpecialChar')) {
      return 'Лозинката мора да содржи барем еден специјален карактер';
    }
    return '';
  }

  public getEmailErrorMessage(): string {
    if (this.loginForm?.get('email')?.hasError('required')) {
      return 'Ве молиме внесете e-mail адреса';
    }
    if (this.loginForm?.get('email')?.hasError('email')) {
      return 'Ве молиме внесете валидна e-mail адреса';
    }
    return '';
  }

  onSubmit(): void {
    if (this.loginForm?.valid) {
      const email = this.loginForm?.get('email')?.value;
      const password = this.loginForm?.get('password')?.value;

      // Call login API from AuthService
      this.authService.login(email, password).subscribe({
        next: (response: any) => {
          localStorage.setItem('token', response.token);
          localStorage.setItem('email', response.email);
          localStorage.setItem('role', response.role);
          this.authService.setAuthenticated(true);
          this.router.navigate(['/home'])
        },
        error: (error) => {
          console.error(error)

          this.errorMessage = 'Invalid credentials';
        },
        complete: () => {
          console.log("Login successfull!")
        }
      })
    } else {
      console.log('Login Form is not valid!');
    }
  }
}
