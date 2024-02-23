package org.hibernate.bugs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
public class OrderEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @Column
    private String consumerId;
    @Column
    private String supplierId;

    public OrderEntity() {
    }

    public OrderEntity(UUID id, String consumerId, String supplierId) {
        this.id = id;
        this.consumerId = consumerId;
        this.supplierId = supplierId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}
