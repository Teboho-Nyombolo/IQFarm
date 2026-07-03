import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [],
  templateUrl: './loader.component.html',
  styleUrl: './loader.component.css'
})
export class LoaderComponent implements OnInit {

  loading = true;

  ngOnInit() {
    setTimeout(() => {
      this.loading = false;
    }, 2000)
  }
}
