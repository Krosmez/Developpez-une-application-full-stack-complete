describe('Route guards', () => {
  const protectedRoutes = ['/feed', '/topics', '/profile', '/posts/new', '/posts/1'];

  protectedRoutes.forEach((route) => {
    it(`redirects unauthenticated users from ${route} to /login`, () => {
      cy.visit(route);
      cy.location('pathname').should('eq', '/login');
    });
  });
});
