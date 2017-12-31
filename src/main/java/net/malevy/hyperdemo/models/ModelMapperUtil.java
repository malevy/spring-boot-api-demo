package net.malevy.hyperdemo.models;

import net.malevy.hyperdemo.models.domain.Task;
import org.modelmapper.AbstractConverter;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;

public class ModelMapperUtil {

    public static ModelMapper build() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new AbstractConverter<String, Task.Importance>() {
            @Override
            protected Task.Importance convert(String s) {
                return Task.Importance.lookup(s).orElse(Task.Importance.NORMAL);
            }
        });
        return modelMapper;
    }

    public static Throwable findRootException(MappingException me) {
        Throwable cause = me.getCause();
        while (null != cause.getCause()) cause = cause.getCause();
        return cause;
    }

}
