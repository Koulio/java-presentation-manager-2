package jpaoletti.jpm2.web.converter;

import java.util.Date;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.model.Field;
import org.joda.time.DateTime;

/**
 *
 * @author jpaoletti
 */
public class WebShowDateTime extends WebToString {

    @Override
    public Object visualize(Field field, Object object) throws ConverterException {
        final Date value = (Date) getValue(object, field);
        final DateTime dt = new DateTime(value);
        return wrap(process(dt.toString(getConfig("format", "yyyy-MM-dd"))));
    }
}
