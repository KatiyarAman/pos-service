package com.ris.inventory.pos.service;

public interface PaginationService {

    public int getCount();

    public int getOffset();

    public int getLimit();

    public void setPaginationParams(int limit, int offset, int count);

    public void verify();
}
