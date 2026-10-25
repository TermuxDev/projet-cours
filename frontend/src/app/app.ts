import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Entete } from './components/entete/entete';
import { PiedDePage } from './components/pied-de-page/pied-de-page';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Entete, PiedDePage],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
}
