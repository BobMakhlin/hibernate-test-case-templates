package org.hibernate.bugs;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class ItemEntity {
    @Id
    private UUID id;
}
