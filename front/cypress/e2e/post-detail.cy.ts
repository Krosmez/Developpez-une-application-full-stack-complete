describe('Post detail page', () => {
  it('displays the post, its meta and its comments', () => {
    cy.intercept('GET', '**/api/v1/posts/1', { fixture: 'post.json' }).as('post');
    cy.loginAndVisit('/posts/1');
    cy.wait('@post');

    cy.get('.post-title').should('contain.text', 'Premier article Angular');
    cy.get('.post-content').should('contain.text', 'Contenu détaillé');
    cy.get('.comment-row').should('have.length', 1);
    cy.contains('.comment-author', 'bob').should('be.visible');
  });

  it('shows an error message when the post is not found', () => {
    cy.intercept('GET', '**/api/v1/posts/999', { statusCode: 404, body: {} }).as(
      'post',
    );
    cy.loginAndVisit('/posts/999');
    cy.wait('@post');

    cy.get('.error-message').should('contain.text', 'Article introuvable.');
  });

  it('adds a comment and reloads the post', () => {
    cy.intercept('GET', '**/api/v1/posts/1', { fixture: 'post.json' }).as('post');
    cy.intercept('POST', '**/api/v1/posts/1/comments', {
      statusCode: 201,
      body: { id: 2, content: 'Merci !', author: { id: 1, username: 'john' } },
    }).as('addComment');
    cy.loginAndVisit('/posts/1');
    cy.wait('@post');

    cy.get('textarea[formControlName="content"]').type('Merci !');
    cy.get('button.send-btn').click();

    cy.wait('@addComment').its('request.body').should('deep.equal', {
      content: 'Merci !',
    });
    // The component reloads the post after a successful comment.
    cy.wait('@post');
    cy.get('textarea[formControlName="content"]').should('have.value', '');
  });

  it('goes back to the feed', () => {
    cy.intercept('GET', '**/api/v1/posts/1', { fixture: 'post.json' }).as('post');
    cy.intercept('GET', '**/api/v1/feed*', { fixture: 'feed.json' });
    cy.loginAndVisit('/posts/1');
    cy.wait('@post');

    cy.get('.back-btn').click();
    cy.location('pathname').should('eq', '/feed');
  });
});
