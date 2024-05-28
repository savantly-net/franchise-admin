package net.savantly.nexus.forms.dom.form;

import java.util.List;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.services.repository.RepositoryService;

import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import net.savantly.nexus.forms.dom.connections.jdbcConnection.JdbcConnectionRepository;
import net.savantly.nexus.forms.dom.connections.webhook.WebhookRepository;
import net.savantly.nexus.forms.dom.destinations.Destination;
import net.savantly.nexus.forms.dom.destinations.DestinationType;

@Log4j2
@jakarta.annotation.Priority(PriorityPrecedence.EARLY)
@Action(semantics = SemanticsOf.NON_IDEMPOTENT, commandPublishing = Publishing.ENABLED, executionPublishing = Publishing.ENABLED)
@ActionLayout(associateWith = "destinations", promptStyle = PromptStyle.DIALOG)
@lombok.RequiredArgsConstructor(onConstructor_ = { @Inject })
public class Form_addDestination {

    final Form form;

    public static class ActionEvent
            extends org.apache.causeway.applib.CausewayModuleApplib.ActionDomainEvent<Form_addDestination> {
    }

    @Inject
    WebhookRepository webhookRepository;
    @Inject
    JdbcConnectionRepository jdbcConnectionRepository;
    @Inject
    RepositoryService repositoryService;

    @MemberSupport
    public Form act(
            final String destination,
            final String name,
            final String collectionName) {

        final DestinationValue destinationValue = DestinationValue.fromString(destination);

        String destinationId = destinationValue.getDestinationId();
        DestinationType destinationType = destinationValue.getDestinationType();
        var persisted = repositoryService
                .persist(Destination.withName(form, destinationType, destinationId, name, collectionName));
        form.addDestination(persisted);
        return form;
    }

    @MemberSupport
    public List<String> choices0Act() {

        log.info("choices0Act");
        var org = form.getOrganization();

        var allJdbcConnections = jdbcConnectionRepository.findByOrganizationId(org.getId()).stream()
                .map(d -> new DestinationValue(d.getName(), DestinationType.JDBC, d.getId()).toString())
                .toList();
        log.info("jdbc connections: {}", allJdbcConnections.size());

        var allWebhooks = webhookRepository.findByOrganizationId(org.getId()).stream()
                .map(d -> new DestinationValue(d.getName(), DestinationType.WEBHOOK, d.getId()).toString())
                .toList();

        log.info("webhooks: {}", allWebhooks.size());

        var allInstances = List.of(allJdbcConnections, allWebhooks).stream()
                .flatMap(List::stream)
                .toList();
        return allInstances;
    }

    private static class DestinationValue {
        private final String name;
        private final DestinationType destinationType;
        private final String destinationId;

        DestinationValue(String name, DestinationType destinationType, String destinationId) {
            this.name = name;
            this.destinationType = destinationType;
            this.destinationId = destinationId;
        }

        static DestinationValue fromString(String value) {
            var parts = value.split("\\|");
            return new DestinationValue(parts[0], DestinationType.valueOf(parts[1]), parts[2]);
        }

        public DestinationType getDestinationType() {
            return destinationType;
        }

        public String getDestinationId() {
            return destinationId;
        }

        @Override
        public String toString() {
            return formatDestinationValue(name, destinationType, destinationId);
        }

        protected String formatDestinationValue(String name, DestinationType destinationType, String destinationId) {
            var cleanName = name.replaceAll("\\|", " ");
            return cleanName + "|" + destinationType.name() + "|" + destinationId;
        }
    }

}
