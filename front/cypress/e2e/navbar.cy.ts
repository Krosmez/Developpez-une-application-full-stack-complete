describe('Navbar navigation (authenticated)', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/api/v1/feed*', { fixture: 'feed.json' });
    cy.intercept('GET', '**/api/v1/subjects', { fixture: 'subjects.json' });
    cy.intercept('GET', '**/api/v1/users/me', { fixture: 'profile.json' });
    cy.intercept('GET', '**/api/v1/users/me/subscriptions', {
      fixture: 'subscriptions.json',
    });
  });

  it('exposes the authenticated navigation links and moves between sections', () => {
    cy.loginAndVisit('/feed');

    cy.get('.desktop-nav').within(() => {
      cy.contains('a', 'Articles').should('be.visible');
      cy.contains('a', 'Thèmes').should('be.visible');
    });

    cy.contains('.desktop-nav a', 'Thèmes').click();
    cy.location('pathname').should('eq', '/topics');

    cy.contains('.desktop-nav a', 'Articles').click();
    cy.location('pathname').should('eq', '/feed');

    cy.get('.profile-btn').click();
    cy.location('pathname').should('eq', '/profile');
  });

  it('hides the authenticated navigation for guests', () => {
    cy.visit('/');
    cy.get('.desktop-nav').should('not.exist');
  });
});
