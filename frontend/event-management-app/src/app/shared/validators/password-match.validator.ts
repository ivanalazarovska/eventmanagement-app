import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from "@angular/forms";

export function passwordMatchValidator(controlName: string, matchingControlName: string): ValidatorFn {
  return (formGroup: AbstractControl): ValidationErrors | null => {
    const control = formGroup.get(controlName);
    const matchingControl = formGroup.get(matchingControlName);

    if (!control || !matchingControl) {
      return null;
    }

    const password = control.value;

    
    const uppercaseRegex = /[A-Z]/;
    const specialCharRegex = /[!@#$%^&*(),.?":{}|<>]/;

    if (!uppercaseRegex.test(password) || !specialCharRegex.test(password)) {
      control.setErrors({ invalidPassword: true });
      return { invalidPassword: true };
    } else {
      control.setErrors(null);
    }

    
    if (password !== matchingControl.value) {
      matchingControl.setErrors({ mismatch: true });
      return { passwordsMismatch: true };
    } else {
      matchingControl.setErrors(null);
    }

    return null;
  };
}

