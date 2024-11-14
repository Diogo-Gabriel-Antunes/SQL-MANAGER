package org.acme.SQL;



import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SQLCreator {

    EntityManager em;
    private StringBuilder select = new StringBuilder();
    private HashMap campos = new HashMap();
    private StringBuilder from = new StringBuilder();
    private StringBuilder where = new StringBuilder();
    private HashMap<String, Object> param = new HashMap<>();
    private HashMap<Object, Object> invokers = new HashMap<>();

    private Integer limit;
    private Integer offset;
    private String orderBy;

    private Boolean consultaNativa;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public SQLCreator select(String str, String campo) {
        if (!select.isEmpty()) {
            select.append(", ").append(str);
        } else {
            select.append(str);
        }
        campos.put(str, campo);
        return this;
    }

    public SQLCreator from(String str) {
        if (from.isEmpty()) {
            from.append(str);
        } else {
            from.append(" ").append(str);
        }
        return this;
    }

    public SQLCreator where(String str) {
        if (where.isEmpty()) {
            where.append(" WHERE ").append(str);

        } else {
            where.append(" AND ").append(str);
        }
        return this;
    }

    public SQLCreator param(String paramStr, Object paramObj) {
        param.put(paramStr, paramObj);
        return this;
    }

    public SQLCreator paramLike(String paramStr, String paramString) {
        param.put(paramStr, "%" + paramString + "%");
        return this;
    }

    public SQLCreator consultaNativa(Boolean consultaNativa) {
        this.consultaNativa = consultaNativa;
        return this;
    }

    private Query createQuery(Class tClass) {
        String select = "SELECT " + this.select.toString();
        String from = " FROM " + this.from.toString();
        String where = this.where.toString();
        String sql = null;
        if (orderBy != null && offset != null && limit != null) {
            sql = select + from + where + " ORDER BY " + orderBy + " LIMIT " + limit + " OFFSET " + offset;
        } else if (orderBy != null && offset != null && limit != null) {
            sql = select + from + where + " ORDER BY " + orderBy + " OFFSET " + offset;
        } else if (orderBy != null) {
            sql = select + from + where + " ORDER BY " + orderBy;
        }else{
            sql = select + from + where;
        }



        Query query = null;
        if (consultaNativa != null && consultaNativa && tClass != null) {
            query = em.createNativeQuery(sql);
        } else if (tClass != null) {
            query = em.createQuery(sql);
        } else {
            em.createQuery(sql);
        }


        if (param != null && !param.isEmpty()) {
            param.forEach(query::setParameter);
        }
        return query;
    }

    private Query createQuery() {
        return createQuery(null);
    }

    public <T> List<T> listResult(Class<T> tClass) {
        Query query;
        if (tClass != null) {
            query = createQuery(tClass);
        } else {
            query = createQuery();
        }


        Collection result = query.getResultList();
        if (result != null && !result.isEmpty()) {
            return (List<T>) result;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLCreator that = (SQLCreator) o;
        return Objects.equals(em, that.em) && Objects.equals(select, that.select) && Objects.equals(campos, that.campos) && Objects.equals(from, that.from) && Objects.equals(where, that.where) && Objects.equals(param, that.param) && Objects.equals(invokers, that.invokers) && Objects.equals(limit, that.limit) && Objects.equals(offset, that.offset) && Objects.equals(orderBy, that.orderBy) && Objects.equals(consultaNativa, that.consultaNativa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(em, select, campos, from, where, param, invokers, limit, offset, orderBy, consultaNativa);
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
