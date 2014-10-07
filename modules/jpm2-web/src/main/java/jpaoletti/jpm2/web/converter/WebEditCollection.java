package jpaoletti.jpm2.web.converter;

import java.util.Collection;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class WebEditCollection extends WebEditObject {

    public WebEditCollection() {
        super();
    }

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Collection<Object> value = (Collection<Object>) ((object == null) ? null : getValue(object, field));
        final String res = "@page:collection-converter.jsp"
                + "?entityId=" + getEntity().getId()
                + ((getRelated() != null) ? "&related=" + getRelated() : "")
                + ((getFilter() != null) ? "&filter=" + getFilter().getId() : "")
                + "&textField=" + getTextField()
                + "&pageSize=" + getPageSize()
                + "&minSearch=" + getMinSearch();
        if (value == null || value.isEmpty()) {
            return res;
        } else {
            final StringBuilder sb = new StringBuilder();
            for (Object o : value) {
                sb.append(getEntity().getDao().getId(o)).append(",");
            }
            return res + "&value=" + sb.toString().substring(0, sb.toString().length() - 1);
        }
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            try {
                final Collection<Object> c = (Collection<Object>) getValue(object, field);
                c.clear();
                final String[] split = ((String) newValue).split("[,]");
                for (String s : split) {
                    c.add(getEntity().getDao().get(s));
                }
                return c;
            } catch (Exception ex) {
                JPMUtils.getLogger().error(ex);
                return null;
            }
        }
    }
}
