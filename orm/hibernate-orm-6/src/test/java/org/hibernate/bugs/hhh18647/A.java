package org.hibernate.bugs.hhh18647;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class A {
    @Id
    private Integer id;
}
