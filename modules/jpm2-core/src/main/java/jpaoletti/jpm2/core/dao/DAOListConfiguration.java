package jpaoletti.jpm2.core.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.sql.JoinType;

/**
 * Options for dao.list().
 *
 * @author jpaoletti
 */
public class DAOListConfiguration {

    private Integer from;
    private Integer max;
    private List<Criterion> restrictions;
    private List<Order> orders;
    private Set<DAOListConfigurationAlias> aliases;

    public DAOListConfiguration() {
        this.orders = new ArrayList<>();
    }

    public DAOListConfiguration(Integer from, Integer max) {
        this.from = from;
        this.max = max;
        this.orders = new ArrayList<>();
    }

    public DAOListConfiguration(Criterion... restrictions) {
        this.orders = new ArrayList<>();
        this.restrictions = new ArrayList<>();
        this.restrictions.addAll(Arrays.asList(restrictions));
    }

    public DAOListConfiguration(Integer from, Integer max, Criterion... restrictions) {
        this.from = from;
        this.max = max;
        this.orders = new ArrayList<>();
        this.restrictions = Arrays.asList(restrictions);
    }

    public DAOListConfiguration(Order order, Criterion... restrictions) {
        this.orders = new ArrayList<>();
        this.withOrder(order);
        this.restrictions = Arrays.asList(restrictions);
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public List<Criterion> getRestrictions() {
        if (restrictions == null) {
            restrictions = new ArrayList<>();
        }
        return restrictions;
    }

    public void setRestrictions(List<Criterion> restrictions) {
        this.restrictions = restrictions;
    }

    public Set<DAOListConfigurationAlias> getAliases() {
        if (aliases == null) {
            aliases = new LinkedHashSet<>();
        }
        return aliases;
    }

    public DAOListConfiguration withMax(Integer max) {
        setMax(max);
        return this;
    }

    public DAOListConfiguration withFrom(Integer from) {
        setFrom(from);
        return this;
    }

    public DAOListConfiguration withAlias(String key, String value) {
        if (!containsAlias(key)) {
            getAliases().add(new DAOListConfigurationAlias(key, value));
        }
        return this;
    }

    public DAOListConfiguration withAlias(String key, String value, JoinType joinType) {
        getAliases().add(new DAOListConfigurationAlias(key, value, joinType));
        return this;
    }

    public DAOListConfiguration withRestrictions(List<Criterion> restrictions) {
        setRestrictions(restrictions);
        return this;
    }

    public DAOListConfiguration withRestriction(Criterion restriction) {
        getRestrictions().add(restriction);
        return this;
    }

    public final DAOListConfiguration withOrder(Order order) {
        getOrders().add(order);
        return this;
    }

    public DAOListConfiguration clearOrders() {
        getOrders().clear();
        return this;
    }

    public boolean containsAlias(String alias) {
        for (DAOListConfigurationAlias a : getAliases()) {
            if (a.getAlias().equals(alias)) {
                return true;
            }
        }
        return false;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public DAOListConfiguration clone() {
        final DAOListConfiguration c = new DAOListConfiguration();
        c.setFrom(getFrom());
        c.setMax(getMax());
        for (Criterion restriction : restrictions) {
            c.withRestriction(restriction);
        }
        for (DAOListConfigurationAlias alias : aliases) {
            c.withAlias(alias.getProperty(), alias.getAlias(), alias.getJoinType());
        }
        for (Order o : getOrders()) {
            c.withOrder(o);
        }
        return c;
    }

    public static class DAOListConfigurationAlias {

        private String property;
        private String alias;
        private JoinType joinType;

        public DAOListConfigurationAlias(String property, String alias) {
            this.property = property;
            this.alias = alias;
        }

        public DAOListConfigurationAlias(String property, String alias, JoinType joinType) {
            this.property = property;
            this.alias = alias;
            this.joinType = joinType;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 71 * hash + Objects.hashCode(this.property);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DAOListConfigurationAlias other = (DAOListConfigurationAlias) obj;
            if (!Objects.equals(this.property, other.property)) {
                return false;
            }
            return true;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public JoinType getJoinType() {
            return joinType;
        }

        public void setJoinType(JoinType joinType) {
            this.joinType = joinType;
        }

    }
}
