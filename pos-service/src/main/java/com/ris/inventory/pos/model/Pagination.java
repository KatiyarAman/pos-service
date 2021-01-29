package com.ris.inventory.pos.model;

import com.ris.inventory.pos.util.exception.InvalidPaginationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pagination {

    private static final Logger logger = LoggerFactory.getLogger(Pagination.class);

    private int count;

    private int offset;

    private int limit;

    private boolean isIgnorable;

    public Pagination() {
        this.limit = 0;
        this.offset = 0;
        this.isIgnorable = true;
    }

    public Pagination(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
        this.isIgnorable = false;
    }

    public boolean isIgnorable() {
        return isIgnorable;
    }

    public int getCount() {
        return count;
    }

    public int getOffset() {
        if (offset > count)
            return count == 0 ? 0 : count - 1;
        return offset - 1;
    }

    public int getLimit() {
        if (limit > count)
            return count;
        return limit;
    }

    public void verify(int count) {
        logger.info("Count '{}' is added to the pagination.", count);
        this.count = count;
        if (this.limit <= 0 || this.offset <= 0 || this.offset == this.limit)
            throw new InvalidPaginationException("Invalid pagination params");
    }
}
