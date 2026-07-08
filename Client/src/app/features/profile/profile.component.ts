import { Component, inject ,OnInit} from '@angular/core';
import { LogoComponent } from '../../shared/components/logo/logo.component';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  imports: [LogoComponent,NavbarComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent  implements OnInit {


  readonly #authservice = inject(AuthService);

  user = this.#authservice.getUser();

  ngOnInit(): void {
  }

  logOut (): void {}

}
