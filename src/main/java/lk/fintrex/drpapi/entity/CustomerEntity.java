package lk.fintrex.drpapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer", catalog = "drp")
public class CustomerEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "nic")
    private String nic;

    protected CustomerEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getNic() {
        return nic;
    }
}
