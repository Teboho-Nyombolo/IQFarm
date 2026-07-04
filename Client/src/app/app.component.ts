import { Component, HostListener, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MobileOnlyComponent } from './shared/mobile-only/mobile-only.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MobileOnlyComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
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

  checkScreenSize() {
    const mobile = window.innerWidth <= 768;
    this.isMobile = mobile;
  }

}
