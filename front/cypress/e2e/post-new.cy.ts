describe('Create new post page', () => {
  beforeEach(() => {
    cy.intercept('GET', '**/api/v1/users/me/subscriptions', {
      fixture: 'subjects.json',
    }).as('subs');
  });

  it('loads the subscribed subjects into the select', () => {
    cy.loginAndVisit('/posts/new');
    cy.wait('@subs');

    cy.get('mat-select[formControlName="subjectId"]').click();
    cy.get('mat-option').should('have.length', 3);
    cy.contains('mat-option', 'Angular').click();
  });

  it('keeps the submit button disabled until the form is valid', () => {
    cy.loginAndVisit('/posts/new');
    cy.wait('@subs');
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('creates an article and redirects to its detail page', () => {
    cy.intercept('POST', '**/api/v1/posts', {
      statusCode: 201,
      body: { id: 42 },
    }).as('create');
    cy.intercept('GET', '**/api/v1/posts/42', { fixture: 'post.json' }).as(
      'post',
    );

    cy.loginAndVisit('/posts/new');
    cy.wait('@subs');

    cy.get('mat-select[formControlName="subjectId"]').click();
    cy.contains('mat-option', 'Angular').click();
    cy.get('input[formControlName="title"]').type('Mon nouvel article', {
      force: true,
    });
    cy.get('textarea[formControlName="content"]').type('Un contenu intéressant.', {
      force: true,
    });
    cy.get('button[type="submit"]').click();

    cy.wait('@create').its('request.body').should('deep.equal', {
      subjectId: 1,
      title: 'Mon nouvel article',
      content: 'Un contenu intéressant.',
    });
    cy.location('pathname').should('eq', '/posts/42');
  });

  it('shows an error message when creation fails', () => {
    cy.intercept('POST', '**/api/v1/posts', {
      statusCode: 500,
      body: { message: "Erreur lors de la création de l'article." },
    }).as('create');

    cy.loginAndVisit('/posts/new');
    cy.wait('@subs');

    cy.get('mat-select[formControlName="subjectId"]').click();
    cy.contains('mat-option', 'Angular').click();
    cy.get('input[formControlName="title"]').type('Titre', { force: true });
    cy.get('textarea[formControlName="content"]').type('Contenu', { force: true });
    cy.get('button[type="submit"]').click();

    cy.wait('@create');
    cy.get('.error-message').should(
      'contain.text',
      "Erreur lors de la création de l'article.",
    );
  });
});
