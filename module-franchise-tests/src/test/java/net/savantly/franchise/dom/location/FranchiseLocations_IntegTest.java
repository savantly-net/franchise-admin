package net.savantly.franchise.dom.location;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.RollbackException;

import org.apache.causeway.applib.services.iactnlayer.InteractionService;
import org.apache.causeway.commons.functional.Try;
import org.apache.causeway.testing.unittestsupport.applib.matchers.ThrowableMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.val;
import net.savantly.franchise.dom.group.FranchiseGroups;
import net.savantly.franchise.fixture.location.FranchiseLocation_persona;
import net.savantly.franchise.integtests.FranchiseModuleIntegTestAbstract;

@Transactional
public class FranchiseLocations_IntegTest extends FranchiseModuleIntegTestAbstract {

    @Inject
    FranchiseLocations menu;
    @Inject
    FranchiseGroups groups;

    @Nested
    public static class listAll extends FranchiseLocations_IntegTest {

        @Test
        public void happyCase() {

            // given
            fixtureScripts.run(new FranchiseLocation_persona.PersistAll());
            transactionService.flushTransaction();

            // when
            final List<FranchiseLocation> all = wrap(menu).listAll();

            // then
            assertThat(all).hasSize(FranchiseLocation_persona.values().length);
        }

        @Test
        public void whenNone() {

            // when
            final List<FranchiseLocation> all = wrap(menu).listAll();

            // then
            assertThat(all).hasSize(0);
        }
    }

    @Nested
    public static class create extends FranchiseLocations_IntegTest {

        @Test
        public void happyCase() {

            wrap(menu).create(null, "Faz");

            // then
            final List<FranchiseLocation> all = wrap(menu).listAll();
            assertThat(all).hasSize(1);
        }

        @Test
        public void whenAlreadyExists() {

            // given
            fixtureScripts.runPersona(FranchiseLocation_persona.FIZZ);
            interactionService.nextInteraction();

            // we execute this in its own transaction so that it can be discarded
            Try<Void> attempt = transactionService.runTransactional(Propagation.REQUIRES_NEW, () -> {

                // expect
                Throwable cause = assertThrows(Throwable.class, () -> {
                    // when
                    wrap(menu).create(null, "Fizz");
                    transactionService.flushTransaction();
                });

                MatcherAssert.assertThat(cause,
                        ThrowableMatchers.causedBy(DuplicateKeyException.class));
                // also expect
                //MatcherAssert.assertThat(cause,
                //        ThrowableMatchers.causedBy(DuplicateKeyException.class));
            });


            // then
            assertThat(attempt.isFailure()).isTrue();
            val failureIfAny = attempt.getFailure();
            assertThat(failureIfAny).isPresent();
            assertThat(failureIfAny.get()).isInstanceOf(TransactionSystemException.class);
            assertThat(failureIfAny.get().getCause()).isInstanceOf(RollbackException.class);

        }

    }

    @Inject protected InteractionService interactionService;
}
