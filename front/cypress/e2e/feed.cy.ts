describe('Feed page', () => {
  it('lists the feed articles returned by the API', () => {
    cy.intercept('GET', '**/api/v1/feed*', { fixture: 'feed.json' }).as('feed');
    cy.loginAndVisit('/feed');

    cy.wait('@feed');
    cy.get('.feed-card').should('have.length', 2);
    cy.contains('.card-title', 'Premier article Angular').should('be.visible');
    cy.contains('.card-meta', 'alice').should('be.visible');
  });

  it('toggles the sort order and re-queries the feed', () => {
    cy.intercept('GET', '**/api/v1/feed?sort=desc', { fixture: 'feed.json' }).as(
      'feedDesc',
    );
    cy.intercept('GET', '**/api/v1/feed?sort=asc', { fixture: 'feed.json' }).as(
      'feedAsc',
    );
    cy.loginAndVisit('/feed');
    cy.wait('@feedDesc');

    cy.get('.sort-btn').click();
    cy.wait('@feedAsc');
  });

  it('shows an empty state when there are no articles', () => {
    cy.intercept('GET', '**/api/v1/feed*', { body: [] }).as('feed');
    cy.loginAndVisit('/feed');

    cy.wait('@feed');
    cy.contains('Votre fil est vide.').should('be.visible');
  });

  it('shows an error message when the feed fails to load', () => {
    cy.intercept('GET', '**/api/v1/feed*', { statusCode: 500, body: {} }).as(
      'feed',
    );
    cy.loginAndVisit('/feed');

    cy.wait('@feed');
    cy.get('.error-message').should(
      'contain.text',
      "Impossible de charger le fil d'actualité.",
    );
  });

  it('navigates to a post detail when a card is clicked', () => {
    cy.intercept('GET', '**/api/v1/feed*', { fixture: 'feed.json' }).as('feed');
    cy.intercept('GET', '**/api/v1/posts/1', { fixture: 'post.json' }).as('post');
    cy.loginAndVisit('/feed');
    cy.wait('@feed');

    cy.get('.feed-card').first().click();
    cy.location('pathname').should('eq', '/posts/1');
    cy.wait('@post');
  });
});
