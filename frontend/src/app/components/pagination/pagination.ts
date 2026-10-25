import { Component, computed, input, output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  templateUrl: './pagination.html',
  styleUrl: './pagination.scss'
})
export class Pagination {
  readonly pageActuelle = input.required<number>();
  readonly totalElements = input.required<number>();
  readonly taillePage = input(9);
  readonly changerPage = output<number>();

  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.totalElements() / this.taillePage())));

  protected aller(page: number): void {
    const cible = Math.min(Math.max(1, page), this.totalPages());
    if (cible !== this.pageActuelle()) {
      this.changerPage.emit(cible);
    }
  }
}
