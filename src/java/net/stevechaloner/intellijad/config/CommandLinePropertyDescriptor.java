package net.stevechaloner.intellijad.config;

import net.stevechaloner.idea.util.properties.DOMable;
import net.stevechaloner.idea.util.properties.PropertyDescriptor;
import net.stevechaloner.intellijad.config.rules.RenderRule;
import net.stevechaloner.intellijad.config.rules.RuleContext;

/**
 * .
 *
 * @author Steve Chaloner
 */
public interface CommandLinePropertyDescriptor<T> extends PropertyDescriptor<T> {
    /**
     * Gets the option of the property.
     *
     * @param ruleContext the context the rule is evaluating in
     * @param domable     the domable
     * @return the option name
     */
    String getOption(RuleContext ruleContext,
                     DOMable<T> domable);

    /**
     * Gets the render rule of the property.
     *
     * @return the render rule
     */
    RenderRule getRenderRule();
}
