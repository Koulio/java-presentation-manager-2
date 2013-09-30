package jpaoletti.jpm2.web.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class EditFileConverter extends Converter {

    private String measure = "k";
    private String accept = "'@'"; // /(\.|\/)(gif|jpe?g|png)$/i

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {
        try {
            final byte[] value = (byte[]) getValue(object, field);
            if (value != null && value.length > 0) {
                String len;
                switch (getMeasure().charAt(0)) {
                    case 'b':
                        len = value.length + " bytes";
                        break;
                    case 'm':
                        len = (value.length / 1024 / 1024) + " Mb";
                        break;
                    case 'k':
                        ;
                    default:
                        len = (value.length / 1024) + "Kb";
                }
                return "@page:file-converter.jsp?delete=true&len=" + len + "&accept=" + getAccept();
            } else {
                return "@page:file-converter.jsp?delete=false&accept=" + getAccept();
            }
        } catch (ClassCastException e) {
            throw new ConverterException("jpm.converter.file.error.not.file");
        }
    }

    @Override
    public Object build(Field field, Object object, Object newValue) throws ConverterException {
        if (newValue != null && newValue.equals("@current:")) {
            throw new IgnoreConvertionException();
        }
        if (newValue == null || newValue.equals("")) {
            return null;
        }
        final File file = new File(String.valueOf(newValue)); //Tmp path
        if (!file.exists()) {
            throw new ConverterException("jpm.error.uploading.file");
        }
        byte[] bFile = new byte[(int) file.length()];

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(bFile);
        } catch (IOException ex) {
            throw new ConverterException("jpm.error.uploading.file");
        }
        return bFile;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
