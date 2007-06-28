package net.stevechaloner.intellijad.config.rules;

import net.stevechaloner.idea.util.properties.DOMable;
import org.jetbrains.annotations.NotNull;

/**
 * A render rule is used to contol property usage based on the context and bound
 * property at the point of evaluation.
 * @author Steve Chaloner
 */
public interface RenderRule<T> {

    /**
     * Evaluates based on the context and property to ascertain if the property
     * should be considered valid for use at the point of invokation.
     *
     * @param ruleContext the context the rule is evaluated in
     * @param domable the property this rule is bound to
     * @return true if the property should be considered valid for use
     */
    boolean evaluate(@NotNull RuleContext ruleContext,
                     DOMable<T> domable);
}
