package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A Field represents a property of the represented entity.
 *
 * <h2>Simple entity configuration file</h2>
 * <pre>
 * {@code
 * <field id="id" display="all | some_operations" align="right | left | center"
 * width="xxxx" />
 * ....
 * </field> }
 * <
 * /pre>
 *
 * @author jpaoletti
 *
 */
public class Field extends PMCoreObject {

    private String id;
    @Autowired
    @Qualifier("default-converter")
    private Converter defaultConverter;
    /**
     * The property to be accesed on the entity instance objects. There must be
     * a getter and a setter for this name on the represented entity. When null,
     * default is the field id
     */
    private String property;
    private String width;
    private String display;
//    private ArrayList<Validator> validators;
    private String defaultValue;
    private String align; //left right center
    private List<FieldConfig> configs;

    /**
     * Default constructor
     */
    public Field() {
        super();
        align = "left";
        defaultValue = "";
    }

    /**
     * Return the default converter if none is defined
     *
     * @return The converter
     */
    public Converter getDefaultConverter() {
        return defaultConverter;
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    public List<FieldConfig> getConfigs() {
        if (configs == null) {
            configs = new ArrayList<>();
            configs.add(new FieldConfig(FieldConfig.ALL, FieldConfig.ALL, getDefaultConverter()));
        }
        return configs;
    }

    public void setConfigs(List<FieldConfig> configs) {
        this.configs = configs;
    }

    /**
     * A (separated by blanks) list of operation ids where this field will be
     * displayed
     *
     * @return The list
     */
    public String getDisplay() {
        if (display == null || display.trim().equals("")) {
            return "all";
        }
        return display;
    }

    /**
     * Indicates if the field is shown in the given operation id
     *
     * @param operationId The Operation id
     * @return true if field is displayed on the operation
     */
    public boolean shouldDisplay(String operationId) {
        if (operationId == null) {
            return false;
        }
        //First we check permissions
        for (FieldConfig config : getConfigs()) {
            if (config.includes(operationId)) {
                if (config.getPerm() != null && !userHasRole(config.getPerm())) {
                    return false;
                }
            }
        }
        if (getDisplay().equalsIgnoreCase("all")) {
            return true;
        }
        final String[] split = getDisplay().split("[ ]");
        for (String string : split) {
            if (string.equalsIgnoreCase(operationId)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     *
     * @return
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     *
     * @param align
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     *
     * @return
     */
    public String getAlign() {
        return align;
    }

    /**
     *
     * @param width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     *
     * @return
     */
    public String getWidth() {
        if (width == null) {
            return "";
        }
        return width;
    }

    /**
     * String representation of the field
     *
     * @return
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * @return the property of the entity instance object that can be accesed by
     * getter and setter. Default value is the field id
     */
    public String getProperty() {
        String r = property;
        if (r == null || r.trim().equals("")) {
            r = id;
        }
        return r;
    }

    /**
     * Setter for property
     *
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    public void setDefaultConverter(Converter defaultConverter) {
        this.defaultConverter = defaultConverter;
    }

    public Converter getConverter(Operation operation) {
        for (FieldConfig fieldConfig : getConfigs()) {
            if (fieldConfig.match(operation)) {
                return fieldConfig.getConverter();
            }
        }
        return null;
    }

//    /**
//     * Returns the internationalized field tooltip
//     */
//    public String getTooltip() {
//        final String key = getTitle() + ".tooltip";
//        if (key == null) {
//            return null;
//        }
//        final String message = getPm().message(key);
//        if (key.equals(message)) {
//            return null;
//        }
//        return message;
//    }
}
