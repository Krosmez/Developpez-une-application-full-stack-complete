/// <reference types="cypress" />

// localStorage key used by the app to store the JWT (see jwt.interceptor.ts).
export const TOKEN_KEY = 'mdd_token';

/** Builds a fake JWT whose `exp` payload is `secondsFromNow` in the future. */
export function buildToken(secondsFromNow = 3600): string {
  const payload = {
    sub: '1',
    exp: Math.floor(Date.now() / 1000) + secondsFromNow,
  };
  return `header.${btoa(JSON.stringify(payload))}.signature`;
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      /**
       * Visits a URL with a valid JWT already in localStorage, so the route
       * guards treat the session as authenticated.
       */
      loginAndVisit(url: string): Chainable<void>;
    }
  }
}

Cypress.Commands.add('loginAndVisit', (url: string) => {
  cy.visit(url, {
    onBeforeLoad(win) {
      win.localStorage.setItem(TOKEN_KEY, buildToken());
    },
  });
});
