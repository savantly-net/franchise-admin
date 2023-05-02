package net.savantly.franchise.dom.web.blockType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.BookmarkPolicy;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.MinLength;
import org.apache.causeway.applib.annotation.NatureOfService;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.Programmatic;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.persistence.jpa.applib.services.JpaSupportService;

import net.savantly.franchise.FranchiseModule;
import net.savantly.franchise.types.Name;

@Named(FranchiseModule.NAMESPACE + ".BlockTypes")
@DomainService(
        nature = NatureOfService.VIEW
)
@javax.annotation.Priority(PriorityPrecedence.EARLY)
@lombok.RequiredArgsConstructor(onConstructor_ = {@Inject} )
public class BlockTypes {
	
    final RepositoryService repositoryService;
    final JpaSupportService jpaSupportService;
    final BlockTypeRepository repository;


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public BlockType create(
            @Name final String name) {
        return repositoryService.persist(BlockType.withRequiredFields(name));
    }


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<BlockType> listAll() {
        return repository.findAll();
    }
    
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public BlockType findByName(final BlockType item) {
        return item;
    }

    public Collection<BlockType> autoComplete0FindByName(@MinLength(1) final String search) {
        if (Objects.isNull(search) || "".equals(search)) {
            return Collections.emptyList();
        }
        return repository.findByNameContainingIgnoreCase(search);
    }

    @Programmatic
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public BlockType getById(final Long id) {
        return repository.getReferenceById(id);
    }


    @Programmatic
    public void ping() {
        jpaSupportService.getEntityManager(BlockType.class)
            .ifSuccess(entityManager -> {
                final TypedQuery<BlockType> q = entityManager.get().createQuery(
                        "SELECT p FROM BlockType p ORDER BY p.name",
                        BlockType.class)
                    .setMaxResults(1);
                q.getResultList();
            });
    }

}
