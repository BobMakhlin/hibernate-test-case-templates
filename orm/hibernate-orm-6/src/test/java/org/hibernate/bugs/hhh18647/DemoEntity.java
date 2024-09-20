package org.hibernate.bugs.hhh18647;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class DemoEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_id")
    private A a;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b_id")
    private B b;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id")
    private C c;
}
