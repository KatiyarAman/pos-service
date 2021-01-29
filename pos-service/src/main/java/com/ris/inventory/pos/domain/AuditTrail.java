package com.ris.inventory.pos.domain;

import com.ris.inventory.pos.domain.converter.OperationConverter;
import com.ris.inventory.pos.util.enumeration.Operation;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "audit_trail")
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(updatable = false, nullable = false)
    private String authority;

    @Column(updatable = false, nullable = false)
    private String userId;

    @Column(name = "tableId", updatable = false, nullable = false)
    private Long entityId;

    @Column(updatable = false, nullable = false)
    @Convert(converter = OperationConverter.class)
    private Operation operation;

    @Column(updatable = false, nullable = false)
    private Class tableName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Date dateCreated;

    public AuditTrail() {
    }

    public AuditTrail(String authority, String userId, Long entityId, Operation operation, Class tableName) {
        this.authority = authority;
        this.userId = userId;
        this.entityId = entityId;
        this.operation = operation;
        this.tableName = tableName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Operation getOperation() {
        return operation;
    }

    public Class getTableName() {
        return tableName;
    }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

    public String getUserId() {
        return userId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }
}
