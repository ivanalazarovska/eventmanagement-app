import { AbstractControl, ValidationErrors } from '@angular/forms';

export function passwordValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.value;


  const uppercaseRegex = /[A-Z]/;

  const specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/;


  if (!uppercaseRegex.test(password)) {
    return { passwordUppercase: true };
  }


  if (!specialCharRegex.test(password)) {
    return { passwordSpecialChar: true };
  }

  return null;
}
