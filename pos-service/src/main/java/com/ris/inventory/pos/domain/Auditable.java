package com.ris.inventory.pos.domain;

import java.util.Date;

public interface Auditable {

    public Long getId();

    public Date getDateCreated();
}
