import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-regiter',
  standalone: true,
  imports: [ FormsModule ],
  templateUrl: './register.component.html',
  styleUrl: './regiter.component.css'
})
export class RegisterComponent {

  user = {
    name: '',
    surname: '',
    email: '',
    password: ''
  };

    // Validation state
  errors: { [key: string]: string } = {};
  isSubmitting = false;



  constructor( private authService: AuthService, private router: Router ) {}

  register() {
    if (!this.validateForm()) {
      return;
    }

    this.isSubmitting = true;

    this.authService.register(this.user).subscribe({
      next: (response: any) => {
        if (response.success && response.data) {
          this.authService.setUserId(response.data.userId);
          this.isSubmitting = false;
          alert('Registration successful!');
          this.goToFarm();
        }
      },
      error: (error: any) => {
        this.isSubmitting = false;
        console.error(error);
        alert('Registration failed!');
      }
    });
  }
  goToFarm(){
    this.router.navigate(['/farmInfo']);
  }

  validateForm(): boolean {
    this.errors = {};
    let isValid = true;

    // Name validation
    if (!this.user.name || this.user.name.trim().length === 0) {
      this.errors['name'] = 'Name is required';
      isValid = false;
    } else if (this.user.name.trim().length < 2) {
      this.errors['name'] = 'Name must be at least 2 characters';
      isValid = false;
    }

    // Surname validation
    if (!this.user.surname || this.user.surname.trim().length === 0) {
      this.errors['surname'] = 'Surname is required';
      isValid = false;
    } else if (this.user.surname.trim().length < 2) {
      this.errors['surname'] = 'Surname must be at least 2 characters';
      isValid = false;
    }

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!this.user.email || this.user.email.trim().length === 0) {
      this.errors['email'] = 'Email is required';
      isValid = false;
    } else if (!emailRegex.test(this.user.email)) {
      this.errors['email'] = 'Please enter a valid email address';
      isValid = false;
    }

    // Password validation
    if (!this.user.password || this.user.password.length === 0) {
      this.errors['password'] = 'Password is required';
      isValid = false;
    } else if (this.user.password.length < 8) {
      this.errors['password'] = 'Password must be at least 8 characters';
      isValid = false;
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(this.user.password)) {
      this.errors['password'] = 'Password must contain uppercase, lowercase, and a number';
      isValid = false;
    }

    return isValid;
  }


}
