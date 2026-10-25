import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-pied-de-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './pied-de-page.html',
  styleUrl: './pied-de-page.scss'
})
export class PiedDePage {
  protected readonly anneeCourante = new Date().getFullYear();
}
