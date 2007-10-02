package net.stevechaloner.intellijad.config;

import com.intellij.openapi.options.Configurable;

/**
 * @author Steve
 */
public interface ConfigAccessor extends Configurable
{
    Config getConfig();
}
