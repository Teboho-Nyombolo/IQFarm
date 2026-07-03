import { Component, HostListener, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MobileOnlyComponent } from './shared/mobile-only/mobile-only.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MobileOnlyComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  
  isMobile = true;

  ngOnInit() {
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  };

  private checkScreenSize(){
    this.isMobile = window.innerWidth <= 768;
  }

}
