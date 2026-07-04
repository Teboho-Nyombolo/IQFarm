import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, NgClass],
  template: `
    <nav
      class="fixed bottom-0 left-0 w-full flex justify-around items-center h-20 px-6 bg-[#f8f9ff] border-t border-[#dce9ff] shadow-[0px_-4px_12px_rgba(0,109,54,0.03)] z-50"
      aria-label="Main navigation">
      <!-- Home -->
      <a id="nav-home" routerLink="/dashboard"
        [ngClass]="activeTab === 'home' ? 'bg-[#50c878]/20 text-[#006d36]' : 'text-[#3e4a3f] hover:bg-[#eff4ff]'"
        class="flex flex-col items-center justify-center rounded-full px-4 py-1 nav-btn transition-colors"
        [attr.aria-current]="activeTab === 'home' ? 'page' : null">
        <span class="material-symbols-outlined" [style.font-variation-settings]="activeTab === 'home' ? '\\'FILL\\' 1' : 'normal'">home</span>
        <span class="font-['JetBrains_Mono'] text-[11px] font-bold">Home</span>
      </a>

      <!-- Crops -->
      <a id="nav-crops" routerLink="/crops"
        [ngClass]="activeTab === 'crops' ? 'bg-[#50c878]/20 text-[#006d36]' : 'text-[#3e4a3f] hover:bg-[#eff4ff]'"
        class="flex flex-col items-center justify-center rounded-full px-4 py-1 nav-btn transition-colors"
        [attr.aria-current]="activeTab === 'crops' ? 'page' : null">
        <span class="material-symbols-outlined" [style.font-variation-settings]="activeTab === 'crops' ? '\\'FILL\\' 1' : 'normal'">potted_plant</span>
        <span class="font-['JetBrains_Mono'] text-[11px] font-bold">Crops</span>
      </a>

      <!-- Weather -->
      <a id="nav-weather" routerLink="/dashboard/weather"
        [ngClass]="activeTab === 'weather' ? 'bg-[#50c878]/20 text-[#006d36]' : 'text-[#3e4a3f] hover:bg-[#eff4ff]'"
        class="flex flex-col items-center justify-center rounded-full px-4 py-1 nav-btn transition-colors"
        [attr.aria-current]="activeTab === 'weather' ? 'page' : null">
        <span class="material-symbols-outlined" [style.font-variation-settings]="activeTab === 'weather' ? '\\'FILL\\' 1' : 'normal'">cloudy</span>
        <span class="font-['JetBrains_Mono'] text-[11px] font-bold">Weather</span>
      </a>

      <!-- Alerts -->
      <a id="nav-alerts" routerLink="#"
        [ngClass]="activeTab === 'alerts' ? 'bg-[#50c878]/20 text-[#006d36]' : 'text-[#3e4a3f] hover:bg-[#eff4ff]'"
        class="flex flex-col items-center justify-center rounded-full px-4 py-1 nav-btn relative transition-colors"
        [attr.aria-current]="activeTab === 'alerts' ? 'page' : null">
        <span class="material-symbols-outlined" [style.font-variation-settings]="activeTab === 'alerts' ? '\\'FILL\\' 1' : 'normal'">notifications</span>
        <span class="font-['JetBrains_Mono'] text-[11px] font-bold">Alerts</span>
        <!-- Unread dot -->
        <span class="absolute top-1 right-3 w-2 h-2 bg-[#ba1a1a] rounded-full"></span>
      </a>
    </nav>
  `
})
export class NavbarComponent {
  @Input() activeTab: 'home' | 'crops' | 'weather' | 'alerts' = 'home';
}
