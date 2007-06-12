package net.stevechaloner.intellijad.config.rules;

import net.stevechaloner.idea.util.properties.DOMable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Steve Chaloner
 */
public interface RenderRule<T> {
    /**
     * @param ruleContext
     * @param domable
     * @return
     */
    boolean evaluate(@NotNull RuleContext ruleContext,
                     DOMable<T> domable);
}
