package net.savantly.nexus.products.dom.product;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.DomainServiceLayout;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.Programmatic;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.persistence.jpa.applib.services.JpaSupportService;

import net.savantly.nexus.common.types.Description;
import net.savantly.nexus.common.types.Name;
import net.savantly.nexus.products.ProductsModule;
import net.savantly.nexus.products.dom.billing.BillingIntervalType;

@Named(ProductsModule.NAMESPACE + ".Products")
@DomainService
@DomainServiceLayout(
    menuBar = DomainServiceLayout.MenuBar.PRIMARY
)
@javax.annotation.Priority(PriorityPrecedence.EARLY)
@lombok.RequiredArgsConstructor(onConstructor_ = { @Inject })
public class Products {
    final RepositoryService repositoryService;
    final JpaSupportService jpaSupportService;
    final ProductRepository repository;

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public Product create(
            final String id,
            @Name final String name,
            @Description final String description) {
        return repositoryService.persist(Product.withRequiredFields(id, name, description, BillingIntervalType.MONTHLY));
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout()
    public List<Product> listAll() {
        return repository.findAll();
    }

    @Programmatic
    public Product getById(String projectId) {
        return repository.getReferenceById(projectId);
    }

    @Programmatic
    public void ping() {
        jpaSupportService.getEntityManager(Product.class)
                .ifSuccess(entityManager -> {
                    final TypedQuery<Product> q = entityManager.get().createQuery(
                            "SELECT p FROM Products p ORDER BY p.name",
                            Product.class)
                            .setMaxResults(1);
                    q.getResultList();
                });
    }

}
