import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Cours } from '../../models/cours.model';

const MOTIFS = ['motif--orange', 'motif--violet', 'motif--jaune', 'motif--sarcelle'];

@Component({
  selector: 'app-carte-cours',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './carte-cours.html',
  styleUrl: './carte-cours.scss'
})
export class CarteCours {
  readonly cours = input.required<Cours>();
  readonly indice = input(0);

  protected get motif(): string {
    return MOTIFS[this.indice() % MOTIFS.length];
  }
}
