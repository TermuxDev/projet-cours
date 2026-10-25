import { Component, input } from '@angular/core';

@Component({
  selector: 'app-mascotte',
  standalone: true,
  templateUrl: './mascotte.html',
  styleUrl: './mascotte.scss'
})
export class Mascotte {
  readonly taille = input<'petite' | 'moyenne' | 'grande'>('moyenne');
}
