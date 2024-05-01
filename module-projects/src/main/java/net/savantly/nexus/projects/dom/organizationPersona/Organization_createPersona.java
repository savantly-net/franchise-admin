package net.savantly.nexus.projects.dom.organizationPersona;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.Transient;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.ParameterLayout;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.services.repository.RepositoryService;

import net.savantly.nexus.organizations.dom.organization.Organization;
import net.savantly.nexus.projects.dom.generator.PersonaGenerator;
import net.savantly.nexus.projects.dom.persona.Persona;
import net.savantly.nexus.projects.dom.persona.PersonaDTO;

@Action
@javax.annotation.Priority(PriorityPrecedence.EARLY)
@lombok.RequiredArgsConstructor(onConstructor_ = { @Inject })
public class Organization_createPersona {

    final Organization organization;

    public static class ActionEvent
            extends org.apache.causeway.applib.CausewayModuleApplib.ActionDomainEvent<Organization_createPersona> {
    }

    @Inject
    @Transient
    PersonaGenerator personaGenerator;
    @Inject
    @Transient
    RepositoryService repositoryService;

    @ActionLayout(named = "Create Persona", associateWith = "personas", promptStyle = PromptStyle.DIALOG)
    public Persona act(
            @ParameterLayout(named = "Description") final String description) {
        final PersonaDTO personaDto = personaGenerator.generatePersona(formatContext(description));
        personaDto.setId(UUID.randomUUID().toString());
        final Persona persona = Persona.fromDto(personaDto);
        OrganizationPersona organizationPersona = OrganizationPersona.withRequiredFields(persona, organization);
        repositoryService.persist(organizationPersona);
        return persona;
    }

    private String formatContext(String description) {
        return "Organization: " + organization.getName() + ", Description: " + description;
    }
}
