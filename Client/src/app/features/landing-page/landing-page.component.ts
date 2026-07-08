import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LogoComponent } from '../../shared/components/logo/logo.component';
import { StarfieldComponent } from '../../shared/components/starfield/starfield.component';

@Component({
  selector: 'app-landing-page',
  imports: [LogoComponent, StarfieldComponent],
  standalone: true,
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css'
})
export class LandingPageComponent {

  constructor( private router: Router) {}

  register() {
    this.router.navigate(['/register']);
  }

}
