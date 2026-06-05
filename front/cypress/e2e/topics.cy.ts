describe('Topics page', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/api/v1/subjects', { fixture: 'subjects.json' }).as(
      'subjects',
    );
    cy.intercept('GET', '**/api/v1/users/me/subscriptions', {
      fixture: 'subscriptions.json',
    }).as('subs');
  });

  it('lists all subjects and marks subscribed ones as already followed', () => {
    cy.loginAndVisit('/topics');
    cy.wait(['@subjects', '@subs']);

    cy.get('.topic-card').should('have.length', 3);
    // Angular (id 1) is already subscribed -> button disabled & labelled.
    cy.contains('.topic-card', 'Angular')
      .find('button')
      .should('be.disabled')
      .and('contain.text', 'Déjà abonné');
    // Java (id 2) is not subscribed -> subscribe button enabled.
    cy.contains('.topic-card', 'Java')
      .find('button')
      .should('be.enabled')
      .and('contain.text', "S'abonner");
  });

  it('subscribes to a new subject and updates the button state', () => {
    cy.intercept('POST', '**/api/v1/subscriptions/2', {
      statusCode: 200,
      body: [
        { id: 1, name: 'Angular', description: 'desc' },
        { id: 2, name: 'Java', description: 'desc' },
      ],
    }).as('subscribe');

    cy.loginAndVisit('/topics');
    cy.wait(['@subjects', '@subs']);

    cy.contains('.topic-card', 'Java').find('button').click();
    cy.wait('@subscribe');

    cy.contains('.topic-card', 'Java')
      .find('button')
      .should('be.disabled')
      .and('contain.text', 'Déjà abonné');
  });

  it('shows an error message when subjects fail to load', () => {
    cy.intercept('GET', '**/api/v1/subjects', { statusCode: 500, body: {} }).as(
      'subjectsErr',
    );
    cy.loginAndVisit('/topics');
    cy.wait('@subjectsErr');

    cy.get('.error-message').should(
      'contain.text',
      'Impossible de charger les thèmes.',
    );
  });
});
