package de.symeda.sormas.backend.audit;

import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.TransactionSynchronizationRegistry;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Singleton
@Startup
@DependsOn("StartupShutdownService")
@AuditIgnore
public class AuditTransactionSchedulerService {

    @Inject
    private AuditRecordService auditRecordService;

    @Resource
    private TransactionSynchronizationRegistry tsr;

    public <T> void schedulePostCommitAuditRecordUpdate(final Class<T> entityClass, final Long entityId, final Long changeUserId) {
        if (entityId == null || changeUserId == null) {
            return;
        }
        tsr.registerInterposedSynchronization(new javax.transaction.Synchronization() {

            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCompletion(int status) {
                if (status == javax.transaction.Status.STATUS_COMMITTED) {
                    auditRecordService.updateAuditRecord(entityClass, entityId, changeUserId);
                }
            }
        });
    }

    public void schedulePostCommitAuditRecordUpdate(final AbstractDomainObject ado, final Long changeUserId) {
        schedulePostCommitAuditRecordUpdate(ado.getClass(), ado.getId(), changeUserId);
    }

}
