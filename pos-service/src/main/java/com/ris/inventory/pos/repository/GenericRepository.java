package com.ris.inventory.pos.repository;

import com.ris.inventory.pos.domain.Auditable;
import com.ris.inventory.pos.model.Pagination;
import org.hibernate.Interceptor;

import java.util.List;
import java.util.Map;

public interface GenericRepository<T extends Auditable> {

    public void setEntity(Class<T> clazz);

    public T get(Long id);

    public List<T> findAllByColumn(String column, Object value);

    public List<T> findAllByColumn(Pagination pagination, String column, Object value);

    public T findByColumn(String column, Object value);

    public T save(T object, Interceptor interceptor);

    public void setDeleted(Long id);

    public void update(Map<String, Object> object, Long id);

    public List<T> list();

    public void sort(List<T> data, String column);

    public List<T> list(int offset, int limit);

    public int count();
}
