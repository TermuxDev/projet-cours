import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Mascotte } from '../mascotte/mascotte';

@Component({
  selector: 'app-entete',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, Mascotte],
  templateUrl: './entete.html',
  styleUrl: './entete.scss'
})
export class Entete {
  protected readonly auth = inject(AuthService);
}
