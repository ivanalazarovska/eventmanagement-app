import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { passwordMatchValidator } from '../../../shared/validators/password-match.validator';
import { AuthService } from '../../../core/services/auth/auth.service';
import { Router } from '@angular/router';
import { UserRole } from '../../../core/models/user';
import { passwordValidator } from '../../../shared/validators/password-validator';

@Component({
  standalone: false,
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  public registerForm: FormGroup | undefined;
  public errorMessage: string = '';
  public successMessage: string = '';
  userRoles: UserRole[] = Object.values(UserRole);

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6), passwordValidator]],
      confirmPassword: ['', [Validators.required]],
      role: [UserRole.STUDENT, [Validators.required]]
    }, {
      validator: passwordMatchValidator('password', 'confirmPassword')
    });
  }

  // Password error messages
  public getPasswordErrorMessage(): string {
    const passwordControl = this.registerForm?.get('password');
    if (passwordControl?.hasError('required')) {
      return 'Password is required';
    }
    if (passwordControl?.hasError('minlength')) {
      return 'Password must be at least 6 characters';
    }
    if (passwordControl?.hasError('passwordUppercase')) {
      return 'Password must contain at least one uppercase letter';
    }
    if (passwordControl?.hasError('passwordSpecialChar')) {
      return 'Password must contain at least one special character';
    }
    return '';
  }

  // Confirm password error messages
  public getConfirmPasswordErrorMessage(): string {
    const confirmPasswordControl = this.registerForm?.get('confirmPassword');
    if (confirmPasswordControl?.hasError('required')) {
      return 'Confirm Password is required';
    }
    if (confirmPasswordControl?.hasError('mismatch')) {
      return 'Passwords do not match';
    }
    return '';
  }

    // Getters for FormControls to simplify template binding
  public getPasswordControl(): FormControl | null {
    return this.registerForm?.get('password') as FormControl;
  }

  public getConfirmPasswordControl(): FormControl | null {
    return this.registerForm?.get('confirmPassword') as FormControl;
  }

  onSubmit(): void {
    if (this.registerForm!.valid) {
      const { name, surname, username, email, password, confirmPassword, role } = this.registerForm!.value;
      console.log(role); // to check the role value

      this.authService.register(username, password, confirmPassword, name, surname, email, role).subscribe({
        next: (response: any) => {
          this.successMessage = 'Registration successful! Please log in.';
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.errorMessage = 'Registration failed. Please check your data.';
        },
        complete: () => {
          console.log('Registration process completed.');
        }
      });

    } else {
      console.log('Form is not valid!', this.registerForm!.errors);
    }
  }

}
