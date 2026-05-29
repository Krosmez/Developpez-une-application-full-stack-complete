import { buildToken } from '../support/commands';

describe('Login page', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('keeps the submit button disabled while the form is empty', () => {
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('shows a validation error when a required field is left empty', () => {
    cy.get('input[formControlName="identifier"]').focus().blur();
    cy.contains('mat-error', 'Ce champ est requis').should('be.visible');
  });

  it('displays an error message when credentials are rejected', () => {
    cy.intercept('POST', '**/api/v1/auth/login', {
      statusCode: 401,
      body: { message: 'Identifiants incorrects.' },
    }).as('login');

    cy.get('input[formControlName="identifier"]').type('john', { force: true });
    cy.get('input[formControlName="password"]').type('wrongpass', { force: true });
    cy.get('button[type="submit"]').click();

    cy.wait('@login');
    cy.get('.error-message').should('contain.text', 'Identifiants incorrects.');
    cy.location('pathname').should('eq', '/login');
  });

  it('logs in successfully and redirects to the feed', () => {
    cy.intercept('POST', '**/api/v1/auth/login', {
      statusCode: 200,
      body: { token: buildToken() },
    }).as('login');
    cy.intercept('GET', '**/api/v1/feed*', { fixture: 'feed.json' }).as('feed');

    cy.get('input[formControlName="identifier"]').type('john', { force: true });
    cy.get('input[formControlName="password"]').type('Abcdef1!', { force: true });
    cy.get('button[type="submit"]').click();

    cy.wait('@login');
    cy.location('pathname').should('eq', '/feed');
    cy.wait('@feed');
  });

  it('redirects already-authenticated users away from /login (guest guard)', () => {
    cy.intercept('GET', '**/api/v1/feed*', { fixture: 'feed.json' });
    cy.loginAndVisit('/login');
    cy.location('pathname').should('eq', '/feed');
  });
});
