import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-farm',
  imports: [FormsModule],
  templateUrl: './farm.component.html',
  styleUrl: './farm.component.css'
})
export class FarmComponent {

    farm = {
    ownerId: 0,  // set this from logged-in user
    farmName: '',
    streetAddress: '',
    suburb: '',
    city: '',
    province: ''
  };

  createFarm() {
    console.log("first")
  }

}
