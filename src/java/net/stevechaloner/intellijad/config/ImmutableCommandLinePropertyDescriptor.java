package net.stevechaloner.intellijad.config;

import net.stevechaloner.idea.util.properties.DOMable;
import net.stevechaloner.idea.util.properties.ImmutablePropertyDescriptor;
import net.stevechaloner.intellijad.config.rules.RenderRule;
import net.stevechaloner.intellijad.config.rules.RenderRuleFactory;
import net.stevechaloner.intellijad.config.rules.RuleContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author Steve Chaloner
 */
public class ImmutableCommandLinePropertyDescriptor<T> extends ImmutablePropertyDescriptor<T> implements CommandLinePropertyDescriptor<T> {
    /**
     * Render type.
     */
    private final RenderType renderType;

    /**
     * The rendering rule.
     */
    private final RenderRule renderRule;

    /**
     * Initialises a new instance of this class with the given arguments.
     *
     * @param name the name of the property
     */
    public ImmutableCommandLinePropertyDescriptor(String name) {
        this(name,
                null,
                RenderRuleFactory.getDefaultRenderRule(),
                RenderType.NAME_ONLY);
    }

    /**
     * Initialises a new instance of this class with the given arguments.
     *
     * @param name       the name of the property
     * @param renderRule the rule to control rendering of this property
     */
    public ImmutableCommandLinePropertyDescriptor(String name,
                                                  RenderRule renderRule) {
        this(name,
                null,
                renderRule,
                RenderType.NAME_ONLY);
    }

    /**
     * Initialises a new instance of this class with the given arguments.
     *
     * @param name         the name of the property
     * @param defaultValue the default value of the property
     * @param renderRule   the rule to control rendering of this property
     * @param renderType   the render type
     */
    public ImmutableCommandLinePropertyDescriptor(String name,
                                                  T defaultValue,
                                                  RenderRule renderRule,
                                                  RenderType renderType) {
        super(name,
                defaultValue);
        this.renderType = renderType;
        this.renderRule = renderRule;
    }

    // javadoc inherited
    public RenderRule getRenderRule() {
        return renderRule;
    }

    // javadoc inherited
    public String getOption(@NotNull RuleContext ruleContext,
                            DOMable<T> domable) {
        String s = "";
        if (renderRule.evaluate(ruleContext,
                domable)) {
            StringBuilder sb = new StringBuilder();
            switch (renderType) {
                case NAME_ONLY:
                    sb.append('-').append(getName());
                    break;
                case VALUE:
                    sb.append(' ');
                    // fall through...
                case VALUE_NO_SPACE:
                    sb.append(getValue(domable));
                    break;
            }
            s = sb.toString();
        }
        return s;
    }
}
