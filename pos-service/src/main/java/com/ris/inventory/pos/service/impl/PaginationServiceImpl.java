package com.ris.inventory.pos.service.impl;

import com.ris.inventory.pos.service.PaginationService;
import com.ris.inventory.pos.util.exception.InvalidPaginationException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PaginationServiceImpl implements PaginationService {

    private int count;

    private int offset;

    private int limit;

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public int getOffset() {
        if (offset > count)
            return count - 1;
        return offset - 1;
    }

    @Override
    public int getLimit() {
        if (limit > count)
            return count;
        return limit;
    }

    @Override
    public void setPaginationParams(int limit, int offset, int count) {
        this.count = count;
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public void verify() {
        if (this.limit <= 0 || this.offset <= 0 || this.offset == this.limit)
            throw new InvalidPaginationException("Invalid pagination params");
    }
}
