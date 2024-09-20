package org.hibernate.bugs.hhh18647;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class B {
    @Id
    private Integer id;
}
