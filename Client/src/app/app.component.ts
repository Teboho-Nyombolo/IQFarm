import { Component, HostListener, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
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

  readonly #platformId = inject(PLATFORM_ID);
  isMobile = true;

  ngOnInit() {
    if (isPlatformBrowser(this.#platformId)) {
      this.checkScreenSize();
    }
  }

  @HostListener('window:resize')
  onResize() {
    if (isPlatformBrowser(this.#platformId)) {
      this.checkScreenSize();
    }
  };

  checkScreenSize() {
    const mobile = window.innerWidth <= 768;
    this.isMobile = mobile;
  }

}
