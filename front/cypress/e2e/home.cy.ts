describe('Home (landing) page', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('displays the landing page with both auth actions', () => {
    cy.get('img[alt="logo"]').should('be.visible');
    cy.contains('a', 'Se connecter').should('be.visible');
    cy.contains('a', "S'inscrire").should('be.visible');
  });

  it('navigates to the login page', () => {
    cy.contains('a', 'Se connecter').click();
    cy.location('pathname').should('eq', '/login');
  });

  it('navigates to the register page', () => {
    cy.contains('a', "S'inscrire").click();
    cy.location('pathname').should('eq', '/register');
  });
});
