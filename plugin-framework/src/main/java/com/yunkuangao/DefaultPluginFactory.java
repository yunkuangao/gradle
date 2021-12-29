package com.yunkuangao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 *
 */
public class DefaultPluginFactory implements PluginFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPluginFactory.class);

    @Override
    public Plugin create(final PluginWrapper pluginWrapper) {
        String pluginClassName = pluginWrapper.getDescriptor().getPluginClass();
        logger.debug("Create instance for plugin '{}'", pluginClassName);

        Class<?> pluginClass;
        try {
            pluginClass = pluginWrapper.getPluginClassLoader().loadClass(pluginClassName);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        // once we have the class, we can do some checks on it to ensure
        // that it is a valid implementation of a plugin.
        int modifiers = pluginClass.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)
                || !isSubClass(Plugin.class, pluginClass)
             //   || (!Plugin.class.isAssignableFrom(pluginClass))
        ) {
            logger.error("The plugin class '{}' is not valid", pluginClassName);
            return null;
        }

        // create the plugin instance
        try {
            var a = pluginClass.getConstructors();
            var c = a[0].getParameterTypes();
            var d = c[0].isAssignableFrom(PluginWrapper.class);
            var b = a[0].newInstance(pluginWrapper);
            Constructor<?> constructor = pluginClass.getConstructor(PluginWrapper.class);
            return (Plugin) constructor.newInstance(pluginWrapper);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private Boolean isSubClass(Class<?> parent, Class<?> sub) {
        while (sub != null) {
            if (sub.getName().equals(parent.getName())) return true;
            if (sub.getName().equals("java.lang.Object")) return false;
            sub = sub.getSuperclass();
        }
        return false;
    }

}
