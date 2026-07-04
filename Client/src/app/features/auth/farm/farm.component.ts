import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FarmServiceService } from '../../../core/farm-service.service';
import { AuthServiceService } from '../../../core/auth-service.service';

@Component({
  selector: 'app-farm',
  imports: [FormsModule],
  templateUrl: './farm.component.html',
  styleUrl: './farm.component.css'
})
export class FarmComponent {


  farm={
    ownerId:0,
    farmName:'',
    streetAddress:'',
    suburb:'',
    city:'',
    zipCode:''
  }

  farmRequest = {
    ownerId: 0,
    address:''
  }

  constructor( private farmService: FarmServiceService, private authService: AuthServiceService) {}
  

  createFarm() {

    this.farmRequest.ownerId = this.authService.getUserId();
    this.farmRequest.address =  this.farm.streetAddress + ', ' +
    this.farm.suburb + ', ' +
    this.farm.city + ', ' +
    this.farm.zipCode;

    this.farmService.createFarm(this.farmRequest).subscribe({
      next: (response) => {
        console.log('Farm created successfully:', response);
      },
      error: (error) => {
        console.error('Error creating farm:', error);
      }
    });
  }

}
