import { buildToken } from '../support/commands';

describe('Register page', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('keeps the submit button disabled while the form is empty', () => {
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('rejects an invalid email and a weak password', () => {
    cy.get('input[formControlName="username"]').type('john', { force: true });
    cy.get('input[formControlName="email"]')
      .type('not-an-email', { force: true })
      .blur();
    cy.contains('mat-error', 'Adresse email invalide').should('be.visible');

    cy.get('input[formControlName="password"]')
      .type('weak', { force: true })
      .blur();
    cy.contains('mat-error', '1 caractère spécial').should('be.visible');
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('registers successfully and redirects to the feed', () => {
    cy.intercept('POST', '**/api/v1/auth/register', {
      statusCode: 200,
      body: { token: buildToken() },
    }).as('register');
    cy.intercept('GET', '**/api/v1/feed*', { fixture: 'feed.json' });

    cy.get('input[formControlName="username"]').type('john', { force: true });
    cy.get('input[formControlName="email"]').type('john@test.com', { force: true });
    cy.get('input[formControlName="password"]').type('Abcdef1!', { force: true });
    cy.get('button[type="submit"]').click();

    cy.wait('@register').its('request.body').should('deep.include', {
      username: 'john',
      email: 'john@test.com',
    });
    cy.location('pathname').should('eq', '/feed');
  });

  it('shows a server error message when registration fails', () => {
    cy.intercept('POST', '**/api/v1/auth/register', {
      statusCode: 400,
      body: { message: 'Cet e-mail est déjà utilisé.' },
    }).as('register');

    cy.get('input[formControlName="username"]').type('john', { force: true });
    cy.get('input[formControlName="email"]').type('john@test.com', { force: true });
    cy.get('input[formControlName="password"]').type('Abcdef1!', { force: true });
    cy.get('button[type="submit"]').click();

    cy.wait('@register');
    cy.get('.error-message').should('contain.text', 'Cet e-mail est déjà utilisé.');
  });
});
