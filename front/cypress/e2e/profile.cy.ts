describe('Profile page', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/api/v1/users/me', { fixture: 'profile.json' }).as(
      'me',
    );
    cy.intercept('GET', '**/api/v1/users/me/subscriptions', {
      fixture: 'subscriptions.json',
    }).as('subs');
  });

  it('loads the profile and pre-fills the form', () => {
    cy.loginAndVisit('/profile');
    cy.wait(['@me', '@subs']);

    cy.get('input[formControlName="username"]').should('have.value', 'john');
    cy.get('input[formControlName="email"]').should(
      'have.value',
      'john@test.com',
    );
    cy.get('.subscription-card').should('have.length', 1);
    cy.contains('.sub-name', 'Angular').should('be.visible');
  });

  it('updates the profile and shows a success message', () => {
    cy.intercept('PUT', '**/api/v1/users/1', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'john@test.com',
        username: 'johnny',
        bio: 'Nouvelle bio.',
      },
    }).as('update');

    cy.loginAndVisit('/profile');
    cy.wait(['@me', '@subs']);

    cy.get('input[formControlName="username"]')
      .clear({ force: true })
      .type('johnny', { force: true });
    cy.get('button[type="submit"]').click();

    cy.wait('@update').its('request.body').should('deep.include', {
      username: 'johnny',
      email: 'john@test.com',
    });
    cy.get('.success-message').should(
      'contain.text',
      'Profil mis à jour avec succès.',
    );
  });

  it('unsubscribes from a subject', () => {
    cy.intercept('DELETE', '**/api/v1/subscriptions/1', {
      statusCode: 200,
      body: [],
    }).as('unsub');

    cy.loginAndVisit('/profile');
    cy.wait(['@me', '@subs']);

    cy.contains('.subscription-card', 'Angular')
      .find('button')
      .contains('Se désabonner')
      .click();

    cy.wait('@unsub');
    cy.contains('Vous n\'êtes abonné à aucun thème.').should('be.visible');
  });

  it('logs out from the navbar and returns to the landing page', () => {
    cy.loginAndVisit('/profile');
    cy.wait(['@me', '@subs']);

    cy.get('.logout-btn').click();
    cy.location('pathname').should('eq', '/');
  });
});
