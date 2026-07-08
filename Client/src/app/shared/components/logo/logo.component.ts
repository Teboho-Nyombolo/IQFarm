import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-logo',
  standalone: true,
  imports: [],
  template: `
    <div class="flex items-center gap-2">
      <div
        [style.width]="size + 'px'"
        [style.height]="size + 'px'"
        class="rounded-xl bg-[var(--color-primary)] flex items-center justify-center"
      >
        <span
          [style.fontSize]="(size / 2.5) + 'px'"
          class="text-white font-bold"
        >
          IQ
        </span>
      </div>
      @if (showText) {
        <span
          [style.fontSize]="textSize + 'px'"
          class="font-bold text-[var(--color-tertiary)]"
        >
          Farm
        </span>
      }
    </div>
  `
})
export class LogoComponent {
  @Input() size = 48;
  @Input() showText = true;
  @Input() textSize = 20;
}
