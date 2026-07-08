import {
  AfterViewInit,
  Component,
  ElementRef,
  NgZone,
  OnDestroy,
  PLATFORM_ID,
  ViewChild,
  inject,
  input,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

/** A single star in the field. Positions are normalised (0..1) so the
 *  field stays resolution-independent and cheap to resize. */
interface Star {
  x: number; // normalised 0..1
  y: number; // normalised 0..1
  z: number; // depth 0..1 (1 = closest / largest)
  radius: number;
  baseAlpha: number;
  twinkleSpeed: number;
  twinklePhase: number;
  hue: number; // colour hue for a soft, calming tint
}

/**
 * StarfieldComponent
 *
 * A calming, interactive starfield rendered on a <canvas>.
 * - Stars drift slowly upward for a peaceful, meditative feel.
 * - Mouse / touch gestures create a gentle parallax: closer stars follow
 *   the pointer more, producing a soft "looking through a window" effect.
 * - The parallax target is eased (lerped) so motion stays smooth, never jerky.
 * - Honours `prefers-reduced-motion` (no drift / twinkle, instant parallax).
 * - SSR-safe: all browser APIs are guarded behind isPlatformBrowser and the
 *   animation runs outside Angular's change detection zone.
 */
@Component({
  selector: 'app-starfield',
  standalone: true,
  imports: [],
  templateUrl: './starfield.component.html',
  styleUrl: './starfield.component.css',
  host: {
    '[class.starfield--with-bg]': 'background()',
  },
})
export class StarfieldComponent implements AfterViewInit, OnDestroy {
  /** Number of stars. Lower = lighter payload for low-end devices. */
  readonly starCount = input<number>(150);

  /** Maximum star radius in px (closest stars). */
  readonly maxRadius = input<number>(1.8);

  /** Vertical drift per frame, normalised (very small = calm). */
  readonly driftSpeed = input<number>(0.0004);

  /** How far the closest stars shift (px) at full pointer deflection. */
  readonly parallaxStrength = input<number>(32);

  /** Base hue for the stars (default: soft green, fits the IQFarm palette). */
  readonly hue = input<number>(150);

  /** Paint a calming dark night-sky gradient behind the stars. */
  readonly background = input<boolean>(true);

  @ViewChild('canvas', { static: true })
  private canvasRef!: ElementRef<HTMLCanvasElement>;

  private readonly platformId = inject(PLATFORM_ID);
  private readonly ngZone = inject(NgZone);
  private readonly host = inject(ElementRef<HTMLElement>);

  private ctx: CanvasRenderingContext2D | null = null;
  private stars: Star[] = [];
  private rafId = 0;
  private width = 0;
  private height = 0;
  private dpr = 1;
  private resizeObserver?: ResizeObserver;

  // Pointer target (normalised -1..1 from screen centre) and eased value.
  private targetX = 0;
  private targetY = 0;
  private smoothX = 0;
  private smoothY = 0;

  private time = 0;
  private reducedMotion = false;

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return; // SSR: nothing to render on the server
    }

    const canvas = this.canvasRef.nativeElement;
    this.ctx = canvas.getContext('2d');
    this.reducedMotion = window.matchMedia(
      '(prefers-reduced-motion: reduce)',
    ).matches;

    this.initStars();
    this.setupResize();
    this.bindEvents();

    // Run the render loop outside Angular to avoid change-detection churn.
    this.ngZone.runOutsideAngular(() => this.loop());
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.rafId);
    this.resizeObserver?.disconnect();
    if (isPlatformBrowser(this.platformId)) {
      window.removeEventListener('mousemove', this.onPointerMove);
      window.removeEventListener('touchmove', this.onPointerMove);
    }
  }

  private setupResize(): void {
    this.resizeObserver = new ResizeObserver(() => this.resize());
    this.resizeObserver.observe(this.host.nativeElement);
    this.resize();
  }

  private resize(): void {
    const el = this.host.nativeElement;
    this.dpr = Math.min(window.devicePixelRatio || 1, 2);
    this.width = el.clientWidth;
    this.height = el.clientHeight;

    const canvas = this.canvasRef.nativeElement;
    canvas.width = Math.max(1, Math.floor(this.width * this.dpr));
    canvas.height = Math.max(1, Math.floor(this.height * this.dpr));
    canvas.style.width = `${this.width}px`;
    canvas.style.height = `${this.height}px`;
    this.ctx?.setTransform(this.dpr, 0, 0, this.dpr, 0, 0);
  }

  private initStars(): void {
    const count = this.starCount();
    this.stars = Array.from({ length: count }, () => this.makeStar());
  }

  private makeStar(): Star {
    const z = Math.random();
    return {
      x: Math.random(),
      y: Math.random(),
      z,
      radius: 0.4 + z * this.maxRadius(),
      baseAlpha: 0.25 + Math.random() * 0.55,
      twinkleSpeed: 0.4 + Math.random() * 1.2,
      twinklePhase: Math.random() * Math.PI * 2,
      hue: this.hue() + (Math.random() * 30 - 15),
    };
  }

  private onPointerMove = (event: MouseEvent | TouchEvent): void => {
    const point =
      event instanceof TouchEvent ? event.touches[0] : event;
    if (!point) {
      return;
    }
    this.targetX = (point.clientX / window.innerWidth) * 2 - 1;
    this.targetY = (point.clientY / window.innerHeight) * 2 - 1;
  };

  private bindEvents(): void {
    window.addEventListener('mousemove', this.onPointerMove, {
      passive: true,
    });
    window.addEventListener('touchmove', this.onPointerMove, {
      passive: true,
    });
  }

  private loop = (): void => {
    this.rafId = requestAnimationFrame(this.loop);
    this.render();
  };

  private render(): void {
    const ctx = this.ctx;
    if (!ctx || this.width === 0 || this.height === 0) {
      return;
    }

    // Ease the pointer target for a gentle, calming follow.
    const ease = this.reducedMotion ? 1 : 0.05;
    this.smoothX += (this.targetX - this.smoothX) * ease;
    this.smoothY += (this.targetY - this.smoothY) * ease;

    if (!this.reducedMotion) {
      this.time += 0.016;
    }

    ctx.clearRect(0, 0, this.width, this.height);

    const strength = this.parallaxStrength();
    const drift = this.driftSpeed();

    for (const s of this.stars) {
      // Slow upward drift with wrap-around.
      s.y -= drift * (0.3 + s.z);
      if (s.y < 0) {
        s.y += 1;
      }

      const px = s.x * this.width - this.smoothX * strength * s.z;
      const py = s.y * this.height - this.smoothY * strength * s.z;

      // Soft twinkle.
      const twinkle = this.reducedMotion
        ? 1
        : 0.65 + 0.35 * Math.sin(this.time * s.twinkleSpeed + s.twinklePhase);
      const alpha = Math.min(1, s.baseAlpha * twinkle);

      ctx.beginPath();
      ctx.arc(px, py, s.radius, 0, Math.PI * 2);
      ctx.fillStyle = `hsla(${s.hue}, 70%, 82%, ${alpha})`;
      ctx.fill();

      // Gentle glow halo for the closest, brightest stars.
      if (s.z > 0.7) {
        ctx.beginPath();
        ctx.arc(px, py, s.radius * 2.8, 0, Math.PI * 2);
        ctx.fillStyle = `hsla(${s.hue}, 70%, 82%, ${alpha * 0.1})`;
        ctx.fill();
      }
    }
  }
}
