package org.acme.SQL;


import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.acme.Util.CollectionsUtil;
import org.acme.Util.PrimitiveUtil.BooleanUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SQLCreator {

    @Inject
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
        }



        Query query = null;
        if (BooleanUtils.isTrue(consultaNativa) && tClass != null) {
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
        if (CollectionsUtil.isValid(result)) {
            return (List<T>) result;
        }
        return null;
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
