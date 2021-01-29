package com.ris.inventory.pos.domain;

import com.ris.inventory.pos.util.enumeration.Operation;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AuditInterceptor extends EmptyInterceptor {

    private final Logger logger = LoggerFactory.getLogger(AuditInterceptor.class);

    private Session session;

    private String userId;

    private String authority;

    private String location;

    private Set<Auditable> inserts = new HashSet<>();

    private Set<Auditable> updates = new HashSet<>();

    public AuditInterceptor() {
    }

    public AuditInterceptor(String userId, String authority, String location) {
        this.userId = userId;
        this.authority = authority;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        if (entity instanceof Auditable)
            inserts.add((Auditable) entity);
        return false;
    }

    @Override
    public void postFlush(Iterator entities) {
        try (Session tempSession = session.sessionWithOptions().connection().autoJoinTransactions().openSession()) {
            for (Auditable audit : inserts) {
                tempSession.persist(new AuditTrail(authority, userId, audit.getId(), Operation.INSERT, audit.getClass()));
            }

            for (Auditable audit : updates) {
                tempSession.persist(new AuditTrail(authority, userId, audit.getId(), Operation.UPDATE, audit.getClass()));
            }
            tempSession.flush();
            logger.info("Audit log for this pos transaction is created successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error accurred while persisting audit log");
        }
        inserts.clear();
        updates.clear();
    }
}
