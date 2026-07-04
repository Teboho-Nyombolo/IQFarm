import { HttpClient} from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthServiceService } from '../../../core/auth-service.service';
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

  constructor( private authService: AuthServiceService, private router: Router, private http: HttpClient ) {}

  register() {
        this.authService.register(this.user).subscribe({
          next: (response:any) => {
            
            this.authService.setUserId(response.id);

            alert('Registration successful!');
          },
        error: (error:any) => {
          console.error(error);
          alert('Registration failed!');
        }
      });
  }

  goToFarm(){
    this.router.navigate(['/farmInfo']);
  }

}
