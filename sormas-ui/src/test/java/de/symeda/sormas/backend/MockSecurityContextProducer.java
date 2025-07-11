/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend;

import static org.mockito.ArgumentMatchers.any;

import java.security.Principal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.security.enterprise.SecurityContext;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import de.hilling.junit.cdi.ContextControlWrapper;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.user.CurrentUserService;

@Alternative
@ApplicationScoped
public class MockSecurityContextProducer {

    // Use a ThreadLocal to store the principal for the current test thread.
    // This allows different tests (potentially running in parallel) to set their own user.
    private static final ThreadLocal<Principal> currentTestPrincipal = new ThreadLocal<>();

    // Keep a reference to the mock for resetting if necessary, though the ThreadLocal handles most of it.
    private static SecurityContext cachedMockSecurityContext;

    @Produces
    public SecurityContext createMockSecurityContext() {
        if (cachedMockSecurityContext == null) {
            cachedMockSecurityContext = Mockito.mock(SecurityContext.class);

            Mockito.when(cachedMockSecurityContext.getCallerPrincipal()).thenAnswer((Answer<Principal>) invocation -> {
                // Return the principal set for the current thread, or null if none set.
                return currentTestPrincipal.get();
            });

            // Configure other security methods based on the current principal
            Mockito.when(cachedMockSecurityContext.isCallerInRole(any(String.class))).thenAnswer(invocationOnMock -> {
                String role = invocationOnMock.getArgument(0);
                UserRight userRight = UserRight.valueOf(role);
                final CurrentUserService currentUserService = ContextControlWrapper.getInstance().getContextualReference(CurrentUserService.class);
                return currentUserService.getCurrentUser().getUserRoles().stream().anyMatch(userRole -> userRole.getUserRights().contains(userRight));
            });

        }
        return cachedMockSecurityContext;
    }

    /**
     * Sets the principal for the current test thread.
     * Call this in your @BeforeEach method or directly in a test.
     */
    public static void setTestUser(String username) {
        if (username == null) {
            currentTestPrincipal.set(null); // No user
        } else {
            currentTestPrincipal.set(new Principal() {

                @Override
                public String getName() {
                    return username;
                }
            });
        }
    }

    /**
     * Sets a specific Principal object for the current test thread.
     */
    public static void setTestPrincipal(Principal principal) {
        currentTestPrincipal.set(principal);
    }

    /**
     * Cleans up the ThreadLocal after each test to prevent state leakage.
     * Call this in your @AfterEach method.
     */
    public static void clearTestUser() {
        currentTestPrincipal.remove();
        // If you were resetting the mock directly, you'd do it here too,
        // but with ThreadLocal, it's mostly about clearing the thread-specific state.
    }

}
