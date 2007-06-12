package net.stevechaloner.intellijad.config;

import net.stevechaloner.idea.util.properties.DOMable;
import net.stevechaloner.idea.util.properties.DOMableCollectionContentType;
import net.stevechaloner.idea.util.properties.DOMableGeneric;
import net.stevechaloner.idea.util.properties.DOMablePropertyContainer;
import net.stevechaloner.idea.util.properties.DOMableTableModel;
import net.stevechaloner.idea.util.properties.ImmutablePropertyDescriptor;
import net.stevechaloner.idea.util.properties.PropertyContainer;
import net.stevechaloner.idea.util.properties.PropertyDescriptor;
import net.stevechaloner.idea.util.properties.converters.ConverterFactory;
import net.stevechaloner.intellijad.config.rules.RenderRuleFactory;
import static net.stevechaloner.intellijad.config.rules.RenderRuleFactory.BooleanRules;
import static net.stevechaloner.intellijad.config.rules.RenderRuleFactory.IntegerRules;
import static net.stevechaloner.intellijad.config.rules.RenderRuleFactory.StringRules;
import net.stevechaloner.intellijad.config.rules.RuleContext;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Steve Chaloner
 */
public class Config implements DOMable
{
    private static final CommandLinePropertyDescriptor<Boolean> ANNOTATE = createBooleanProperty("a");
    private static final CommandLinePropertyDescriptor<Boolean> ANNOTATE_FULLY = createBooleanProperty("af");
    private static final CommandLinePropertyDescriptor<Boolean> CLEAR_PREFIXES = createBooleanProperty("clear");
    private static final PropertyDescriptor<String> CONFIRM_NAVIGATION_TRIGGERED_DECOMPILE = new ImmutablePropertyDescriptor<String>("confirm-navigation-triggered-decompile");
    private static final PropertyDescriptor<Boolean> CREATE_OUTPUT_DIRECTORY = new ImmutablePropertyDescriptor<Boolean>("create-output-directory");
    private static final PropertyDescriptor<Boolean> ALWAYS_EXCLUDE_RECURSIVELY = new ImmutablePropertyDescriptor<Boolean>("always-exclude-recursively");
    private static final CommandLinePropertyDescriptor<Boolean> DEAD = createBooleanProperty("dead");
    private static final PropertyDescriptor<Boolean> DECOMPILE_TO_MEMORY = new ImmutablePropertyDescriptor<Boolean>("decompile-to-memory");
    private static final CommandLinePropertyDescriptor<Boolean> DEFAULT_INITIALIZERS = createBooleanProperty("i");
    private static final CommandLinePropertyDescriptor<Boolean> DISASSEMBLER_ONLY = createBooleanProperty("dis");
    private static final PropertyDescriptor<ExclusionTableModel> EXCLUSION_TABLE_MODEL = new ImmutablePropertyDescriptor<ExclusionTableModel>("exclusion-table-model");
    private static final CommandLinePropertyDescriptor<Boolean> FIELDS_FIRST = createBooleanProperty("ff");
    private static final CommandLinePropertyDescriptor<String> FILE_EXTENSION = createStringProperty("s", "java");
    private static final CommandLinePropertyDescriptor<Boolean> FULLY_QUALIFIED_NAMES = createBooleanProperty("f");
    private static final PropertyDescriptor<String> JAD_PATH = new ImmutablePropertyDescriptor<String>("jad-path");
    private static final CommandLinePropertyDescriptor<Boolean> LINE_NUMBERS_AS_COMMENTS = createBooleanProperty("lnc");
    private static final CommandLinePropertyDescriptor<Boolean> NOCAST = createBooleanProperty("nocast");
    private static final CommandLinePropertyDescriptor<Boolean> NOCLASS = createBooleanProperty("noclass");
    private static final CommandLinePropertyDescriptor<Boolean> NOCODE = createBooleanProperty("nocode");
    private static final CommandLinePropertyDescriptor<Boolean> NOCONV = createBooleanProperty("noconv");
    private static final CommandLinePropertyDescriptor<Boolean> NOCTOR = createBooleanProperty("noctor");
    private static final CommandLinePropertyDescriptor<Boolean> NODOS = createBooleanProperty("nodos");
    private static final CommandLinePropertyDescriptor<Boolean> NOFD = createBooleanProperty("nofd");
    private static final CommandLinePropertyDescriptor<Boolean> NOINNER = createBooleanProperty("noinner");
    private static final CommandLinePropertyDescriptor<Boolean> NOLVT = createBooleanProperty("nolvt");
    private static final CommandLinePropertyDescriptor<Boolean> NONLB = createBooleanProperty("nonlb");
    private static final CommandLinePropertyDescriptor<String> OUTPUT_DIRECTORY = createStringProperty("d");
    private static final CommandLinePropertyDescriptor<Boolean> OVERWRITE = createBooleanProperty("o");
    private static final PropertyDescriptor<Integer> LIMIT_INDENTATION = new ImmutablePropertyDescriptor<Integer>("indentation", 4);
    private static final CommandLinePropertyDescriptor<Integer> LIMIT_INT_RADIX = createIntegerProperty("radix", 10);
    private static final CommandLinePropertyDescriptor<Integer> LIMIT_LONG_RADIX = createIntegerProperty("lradix", 10);
    private static final CommandLinePropertyDescriptor<Integer> LIMIT_MAX_STRING_LENGTH = createIntegerProperty("l", 64);
    private static final CommandLinePropertyDescriptor<Integer> LIMIT_PACK_FIELDS = createIntegerProperty("pv", 3);
    private static final CommandLinePropertyDescriptor<Boolean> PIPE = createBooleanProperty("p");
    private static final CommandLinePropertyDescriptor<String> PREFIX_NUMERICAL_CLASSES = createStringProperty("pc", "_cls");
    private static final CommandLinePropertyDescriptor<String> PREFIX_NUMERICAL_FIELDS = createStringProperty("pf", "_fld");
    private static final CommandLinePropertyDescriptor<String> PREFIX_NUMERICAL_LOCALS = createStringProperty("pl", "_lcl");
    private static final CommandLinePropertyDescriptor<String> PREFIX_NUMERICAL_METHODS = createStringProperty("pm", "_mth");
    private static final CommandLinePropertyDescriptor<String> PREFIX_NUMERICAL_PARAMETERS = createStringProperty("pp", "_prm");
    private static final CommandLinePropertyDescriptor<String> PREFIX_PACKAGES = createStringProperty("pa");
    private static final CommandLinePropertyDescriptor<String> PREFIX_UNUSED_EXCEPTIONS = createStringProperty("pe", "_ex");
    private static final PropertyDescriptor<Boolean> READ_ONLY = new ImmutablePropertyDescriptor<Boolean>("read-only");
    private static final CommandLinePropertyDescriptor<Boolean> REDUNDANT_BRACES = createBooleanProperty("b");
    private static final CommandLinePropertyDescriptor<Boolean> RESTORE_PACKAGES = createBooleanProperty("r");
    private static final CommandLinePropertyDescriptor<Boolean> SPLIT_STRINGS_AT_NEWLINE = createBooleanProperty("nl");
    private static final CommandLinePropertyDescriptor<Boolean> SAFE = createBooleanProperty("safe");
    private static final PropertyDescriptor<Boolean> SORT = new ImmutablePropertyDescriptor<Boolean>("sort");
    private static final CommandLinePropertyDescriptor<Boolean> SPACE_AFTER_KEYWORD = createBooleanProperty("space");
    private static final CommandLinePropertyDescriptor<Boolean> STATISTICS = createBooleanProperty("stat");
    private static final CommandLinePropertyDescriptor<Boolean> USE_TABS = createBooleanProperty("t");
    private static final CommandLinePropertyDescriptor<Boolean> VERBOSE = createBooleanProperty("v");

    /**
     * The persistence model.
     */
    private final DOMable domable;

//    private final DOMableTableModel exclusionTableModel = new DOMableTableModel("exclusions",
//                                                                                new ExclusionTableModel());

    /**
     * The properties.
     */
    private final PropertyContainer<PropertyDescriptor, DOMable> propertyContainer;

    private final List<CommandLinePropertyDescriptor> commandLinePropertyDescriptors = new ArrayList<CommandLinePropertyDescriptor>();

    private final RuleContext ruleContext;

    public Config(RuleContext ruleContext)
    {
        this.ruleContext = ruleContext;
        ruleContext.setConfig(this);

        DOMablePropertyContainer dpc = new DOMablePropertyContainer("config");
        registerBooleanProperty(ANNOTATE, dpc);
        registerBooleanProperty(ANNOTATE_FULLY, dpc);
        registerBooleanProperty(CLEAR_PREFIXES, dpc);
        registerBooleanProperty(CREATE_OUTPUT_DIRECTORY, dpc);
        registerStringProperty(CONFIRM_NAVIGATION_TRIGGERED_DECOMPILE, dpc);
        registerBooleanProperty(ALWAYS_EXCLUDE_RECURSIVELY, dpc);
        registerBooleanProperty(DEAD, dpc);
        registerBooleanProperty(DECOMPILE_TO_MEMORY, dpc);
        registerBooleanProperty(DEFAULT_INITIALIZERS, dpc);
        registerBooleanProperty(DISASSEMBLER_ONLY, dpc);
        registerStringProperty(FILE_EXTENSION, dpc);
        registerBooleanProperty(FIELDS_FIRST, dpc);
        registerBooleanProperty(FULLY_QUALIFIED_NAMES, dpc);
        registerIntegerProperty(LIMIT_INDENTATION, dpc);
        registerIntegerProperty(LIMIT_INT_RADIX, dpc);
        registerIntegerProperty(LIMIT_LONG_RADIX, dpc);
        registerIntegerProperty(LIMIT_MAX_STRING_LENGTH, dpc);
        registerIntegerProperty(LIMIT_PACK_FIELDS, dpc);
        registerBooleanProperty(LINE_NUMBERS_AS_COMMENTS, dpc);
        registerBooleanProperty(NOCONV, dpc);
        registerBooleanProperty(NOCAST, dpc);
        registerBooleanProperty(NOCLASS, dpc);
        registerBooleanProperty(NOCODE, dpc);
        registerBooleanProperty(NODOS, dpc);
        registerBooleanProperty(NOCTOR, dpc);
        registerBooleanProperty(NOFD, dpc);
        registerBooleanProperty(NOINNER, dpc);
        registerBooleanProperty(NOLVT, dpc);
        registerBooleanProperty(NONLB, dpc);
        registerStringProperty(OUTPUT_DIRECTORY, dpc);
        registerBooleanProperty(OVERWRITE, dpc);
        registerBooleanProperty(PIPE, dpc);
        registerStringProperty(PREFIX_NUMERICAL_CLASSES, dpc);
        registerStringProperty(PREFIX_NUMERICAL_FIELDS, dpc);
        registerStringProperty(PREFIX_NUMERICAL_LOCALS, dpc);
        registerStringProperty(PREFIX_NUMERICAL_METHODS, dpc);
        registerStringProperty(PREFIX_NUMERICAL_PARAMETERS, dpc);
        registerStringProperty(PREFIX_PACKAGES, dpc);
        registerStringProperty(PREFIX_UNUSED_EXCEPTIONS, dpc);
        registerBooleanProperty(READ_ONLY, dpc);
        registerBooleanProperty(REDUNDANT_BRACES, dpc);
        registerBooleanProperty(RESTORE_PACKAGES, dpc);
        registerBooleanProperty(SAFE, dpc);
        registerBooleanProperty(SORT, dpc);
        registerBooleanProperty(SPACE_AFTER_KEYWORD, dpc);
        registerBooleanProperty(SPLIT_STRINGS_AT_NEWLINE, dpc);
        registerBooleanProperty(STATISTICS, dpc);
        registerBooleanProperty(USE_TABS, dpc);
        registerBooleanProperty(VERBOSE, dpc);
        registerStringProperty(JAD_PATH, dpc);

        dpc.put(EXCLUSION_TABLE_MODEL,
                new DOMableTableModel(EXCLUSION_TABLE_MODEL.getName(),
                                      new ExclusionTableModel()));

        this.domable = dpc;
        this.propertyContainer = dpc;

    }

    /**
     * @param pd
     * @param dpc
     */
    private void registerBooleanProperty(PropertyDescriptor<Boolean> pd,
                                         DOMablePropertyContainer dpc)
    {
        dpc.put(pd,
                new DOMableGeneric<Boolean>(pd.getName(),
                                            ConverterFactory.getBooleanConverter(),
                                            DOMableCollectionContentType.BOOLEAN));
    }

    /**
     * @param clpd
     * @param dpc
     */
    private void registerBooleanProperty(CommandLinePropertyDescriptor<Boolean> clpd,
                                         DOMablePropertyContainer dpc)
    {
        dpc.put(clpd,
                new DOMableGeneric<Boolean>(clpd.getName(),
                                            ConverterFactory.getBooleanConverter(),
                                            DOMableCollectionContentType.BOOLEAN));
        ruleContext.addProperty(clpd);
        commandLinePropertyDescriptors.add(clpd);
    }

    /**
     * @param pd
     * @param dpc
     */
    private void registerIntegerProperty(PropertyDescriptor<Integer> pd,
                                         DOMablePropertyContainer dpc)
    {
        dpc.put(pd,
                new DOMableGeneric<Integer>(pd.getName(),
                                            ConverterFactory.getIntegerConverter(),
                                            DOMableCollectionContentType.INTEGER));
    }

    /**
     * @param clpd
     * @param dpc
     */
    private void registerIntegerProperty(CommandLinePropertyDescriptor<Integer> clpd,
                                         DOMablePropertyContainer dpc)
    {
        dpc.put(clpd,
                new DOMableGeneric<Integer>(clpd.getName(),
                                            ConverterFactory.getIntegerConverter(),
                                            DOMableCollectionContentType.INTEGER));
        ruleContext.addProperty(clpd);
        commandLinePropertyDescriptors.add(clpd);
    }

    /**
     * @param pd
     * @param dpc
     */
    private void registerStringProperty(PropertyDescriptor<String> pd,
                                        DOMablePropertyContainer dpc)
    {
        dpc.put(pd,
                new DOMableGeneric<String>(pd.getName(),
                                           ConverterFactory.getStringConverter(),
                                           DOMableCollectionContentType.STRING));
    }

    /**
     * @param clpd
     * @param dpc
     */
    private void registerStringProperty(CommandLinePropertyDescriptor<String> clpd,
                                        DOMablePropertyContainer dpc)
    {
        dpc.put(clpd,
                new DOMableGeneric<String>(clpd.getName(),
                                           ConverterFactory.getStringConverter(),
                                           DOMableCollectionContentType.STRING));
        ruleContext.addProperty(clpd);
        commandLinePropertyDescriptors.add(clpd);
    }

    public String getConfirmNavigationTriggeredDecompile()
    {
        return CONFIRM_NAVIGATION_TRIGGERED_DECOMPILE.getValue(propertyContainer.get(CONFIRM_NAVIGATION_TRIGGERED_DECOMPILE));
    }

    public void setConfirmNavigationTriggeredDecompile(String confirmNavigationTriggeredDecompile)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(CONFIRM_NAVIGATION_TRIGGERED_DECOMPILE);
        value.setValue(confirmNavigationTriggeredDecompile);
    }

    public boolean isReadOnly()
    {
        return READ_ONLY.getValue(propertyContainer.get(READ_ONLY));
    }

    public void setReadOnly(boolean readOnly)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(READ_ONLY);
        value.setValue(readOnly);
    }

    public boolean isAnnotate()
    {
        return ANNOTATE.getValue(propertyContainer.get(ANNOTATE));
    }

    public void setAnnotate(boolean annotate)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(ANNOTATE);
        value.setValue(annotate);
    }

    public boolean isAnnotateFully()
    {
        return ANNOTATE_FULLY.getValue(propertyContainer.get(ANNOTATE_FULLY));
    }

    public void setAnnotateFully(boolean annotateFully)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(ANNOTATE_FULLY);
        value.setValue(annotateFully);
    }

    public boolean isRedundantBraces()
    {
        return REDUNDANT_BRACES.getValue(propertyContainer.get(REDUNDANT_BRACES));
    }

    public void setRedundantBraces(boolean redundantBraces)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(REDUNDANT_BRACES);
        value.setValue(redundantBraces);
    }

    public boolean isClearPrefixes()
    {
        return CLEAR_PREFIXES.getValue(propertyContainer.get(CLEAR_PREFIXES));
    }

    public void setClearPrefixes(boolean clearPrefixes)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(CLEAR_PREFIXES);
        value.setValue(clearPrefixes);
    }

    public String getOutputDirectory()
    {
        return OUTPUT_DIRECTORY.getValue(propertyContainer.get(OUTPUT_DIRECTORY));
    }

    public void setOutputDirectory(String outputDirectory)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(OUTPUT_DIRECTORY);
        value.setValue(outputDirectory);
    }

    public boolean isDead()
    {
        return DEAD.getValue(propertyContainer.get(DEAD));
    }

    public void setDead(boolean dead)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(DEAD);
        value.setValue(dead);
    }

    public boolean isDissassemblerOnly()
    {
        return DISASSEMBLER_ONLY.getValue(propertyContainer.get(DISASSEMBLER_ONLY));
    }

    public void setDissassemblerOnly(boolean dissassemblerOnly)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(DISASSEMBLER_ONLY);
        value.setValue(dissassemblerOnly);
    }

    public boolean isFullyQualifiedNames()
    {
        return FULLY_QUALIFIED_NAMES.getValue(propertyContainer.get(FULLY_QUALIFIED_NAMES));
    }

    public void setFullyQualifiedNames(boolean fullyQualifiedNames)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(FULLY_QUALIFIED_NAMES);
        value.setValue(fullyQualifiedNames);
    }

    public boolean isFieldsFirst()
    {
        return FIELDS_FIRST.getValue(propertyContainer.get(FIELDS_FIRST));
    }

    public void setFieldsFirst(boolean fieldsFirst)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(FIELDS_FIRST);
        value.setValue(fieldsFirst);
    }

    public boolean isDefaultInitializers()
    {
        return DEFAULT_INITIALIZERS.getValue(propertyContainer.get(DEFAULT_INITIALIZERS));
    }

    public void setDefaultInitializers(boolean defaultInitializers)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(DEFAULT_INITIALIZERS);
        value.setValue(defaultInitializers);
    }

    public Integer getMaxStringLength()
    {
        return LIMIT_MAX_STRING_LENGTH.getValue(propertyContainer.get(LIMIT_MAX_STRING_LENGTH));
    }

    public void setMaxStringLength(Integer maxStringLength)
    {
        DOMableGeneric<Integer> value = (DOMableGeneric<Integer>) propertyContainer.get(LIMIT_MAX_STRING_LENGTH);
        value.setValue(maxStringLength);
    }

    public boolean isLineNumbersAsComments()
    {
        return LINE_NUMBERS_AS_COMMENTS.getValue(propertyContainer.get(LINE_NUMBERS_AS_COMMENTS));
    }

    public void setLineNumbersAsComments(boolean lineNumbersAsComments)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(LINE_NUMBERS_AS_COMMENTS);
        value.setValue(lineNumbersAsComments);
    }

    public Integer getLongRadix()
    {
        return LIMIT_LONG_RADIX.getValue(propertyContainer.get(LIMIT_LONG_RADIX));
    }

    public void setLongRadix(Integer longRadix)
    {
        DOMableGeneric<Integer> value = (DOMableGeneric<Integer>) propertyContainer.get(LIMIT_LONG_RADIX);
        value.setValue(longRadix);
    }

    public boolean isSplitStringsAtNewline()
    {
        return SPLIT_STRINGS_AT_NEWLINE.getValue(propertyContainer.get(SPLIT_STRINGS_AT_NEWLINE));
    }

    public void setSplitStringsAtNewline(boolean splitStringsAtNewline)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(SPLIT_STRINGS_AT_NEWLINE);
        value.setValue(splitStringsAtNewline);
    }

    public boolean isNoconv()
    {
        return NOCONV.getValue(propertyContainer.get(NOCONV));
    }

    public void setNoconv(boolean noconv)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NOCONV);
        value.setValue(noconv);
    }

    public boolean isNocast()
    {
        return NOCAST.getValue(propertyContainer.get(NOCAST));
    }

    public void setNocast(boolean nocast)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NOCAST);
        value.setValue(nocast);
    }

    public boolean isNoclass()
    {
        return NOCLASS.getValue(propertyContainer.get(NOCLASS));
    }

    public void setNoclass(boolean noclass)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NOCLASS);
        value.setValue(noclass);
    }

    public boolean isNocode()
    {
        return NOCODE.getValue(propertyContainer.get(NOCODE));
    }

    public void setNocode(boolean nocode)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NOCODE);
        value.setValue(nocode);
    }

    public boolean isNoctor()
    {
        return NOCTOR.getValue(propertyContainer.get(NOCTOR));
    }

    public void setNoctor(boolean noctor)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NOCTOR);
        value.setValue(noctor);
    }

    public boolean isNodos()
    {
        return NODOS.getValue(propertyContainer.get(NODOS));
    }

    public void setNodos(boolean nodos)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NODOS);
        value.setValue(nodos);
    }

    public boolean isNofd()
    {
        return NOFD.getValue(propertyContainer.get(NOFD));
    }

    public void setNofd(boolean nofd)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NOFD);
        value.setValue(nofd);
    }

    public boolean isNoinner()
    {
        return NOINNER.getValue(propertyContainer.get(NOINNER));
    }

    public void setNoinner(boolean noinner)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NOINNER);
        value.setValue(noinner);
    }

    public boolean isNolvt()
    {
        return NOLVT.getValue(propertyContainer.get(NOLVT));
    }

    public void setNolvt(boolean nolvt)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NOLVT);
        value.setValue(nolvt);
    }

    public boolean isNonlb()
    {
        return NONLB.getValue(propertyContainer.get(NONLB));
    }

    public void setNonlb(boolean nonlb)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(NONLB);
        value.setValue(nonlb);

    }

    public Integer getIntRadix()
    {
        return LIMIT_INT_RADIX.getValue(propertyContainer.get(LIMIT_INT_RADIX));
    }

    public void setIntRadix(Integer intRadix)
    {
        DOMableGeneric<Integer> value = (DOMableGeneric<Integer>) propertyContainer.get(LIMIT_INT_RADIX);
        value.setValue(intRadix);
    }

    public String getFileExtension()
    {
        return FILE_EXTENSION.getValue(propertyContainer.get(FILE_EXTENSION));
    }

    public void setFileExtension(String fileExtension)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(FILE_EXTENSION);
        value.setValue(fileExtension == null ? FILE_EXTENSION.getDefault() : fileExtension);
    }

    public boolean isSafe()
    {
        return SAFE.getValue(propertyContainer.get(SAFE));
    }

    public void setSafe(boolean safe)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(SAFE);
        value.setValue(safe);
    }

    public boolean isSpaceAfterKeyword()
    {
        return SPACE_AFTER_KEYWORD.getValue(propertyContainer.get(SPACE_AFTER_KEYWORD));
    }

    public void setSpaceAfterKeyword(boolean spaceAfterKeyword)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(SPACE_AFTER_KEYWORD);
        value.setValue(spaceAfterKeyword);
    }

    public Integer getIndentation()
    {
        return LIMIT_INDENTATION.getValue(propertyContainer.get(LIMIT_INDENTATION));
    }

    public void setIndentation(Integer indentation)
    {
        DOMableGeneric<Integer> value = (DOMableGeneric<Integer>) propertyContainer.get(LIMIT_INDENTATION);
        value.setValue(indentation);
    }

    public boolean isUseTabs()
    {
        return USE_TABS.getValue(propertyContainer.get(USE_TABS));
    }

    public void setUseTabs(boolean useTabs)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(SAFE);
        value.setValue(useTabs);
    }

    public String getPrefixPackages()
    {
        return PREFIX_PACKAGES.getValue(propertyContainer.get(PREFIX_PACKAGES));
    }

    public void setPrefixPackages(String prefixPackages)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(PREFIX_PACKAGES);
        value.setValue(prefixPackages);
    }

    public String getPrefixNumericalClasses()
    {
        return PREFIX_NUMERICAL_CLASSES.getValue(propertyContainer.get(PREFIX_NUMERICAL_CLASSES));
    }

    public void setPrefixNumericalClasses(String prefixNumericalClasses)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(PREFIX_NUMERICAL_CLASSES);
        value.setValue(prefixNumericalClasses);
    }

    public String getPrefixUnusedExceptions()
    {
        return PREFIX_UNUSED_EXCEPTIONS.getValue(propertyContainer.get(PREFIX_UNUSED_EXCEPTIONS));
    }

    public void setPrefixUnusedExceptions(String prefixUnusedExceptions)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(PREFIX_UNUSED_EXCEPTIONS);
        value.setValue(prefixUnusedExceptions);
    }

    public String getPrefixNumericalFields()
    {
        return PREFIX_NUMERICAL_FIELDS.getValue(propertyContainer.get(PREFIX_NUMERICAL_FIELDS));
    }

    public void setPrefixNumericalFields(String prefixNumericalFields)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(PREFIX_NUMERICAL_FIELDS);
        value.setValue(prefixNumericalFields);
    }

    public String getPrefixNumericalLocals()
    {
        return PREFIX_NUMERICAL_LOCALS.getValue(propertyContainer.get(PREFIX_NUMERICAL_LOCALS));
    }

    public void setPrefixNumericalLocals(String prefixNumericalLocals)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(PREFIX_NUMERICAL_LOCALS);
        value.setValue(prefixNumericalLocals);
    }

    public String getPrefixNumericalMethods()
    {
        return PREFIX_NUMERICAL_METHODS.getValue(propertyContainer.get(PREFIX_NUMERICAL_METHODS));
    }

    public void setPrefixNumericalMethods(String prefixNumericalMethods)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(PREFIX_NUMERICAL_METHODS);
        value.setValue(prefixNumericalMethods);
    }

    public String getPrefixNumericalParameters()
    {
        return PREFIX_NUMERICAL_PARAMETERS.getValue(propertyContainer.get(PREFIX_NUMERICAL_PARAMETERS));
    }

    public void setPrefixNumericalParameters(String prefixNumericalParameters)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(PREFIX_NUMERICAL_PARAMETERS);
        value.setValue(prefixNumericalParameters);
    }

    public Integer getPackFields()
    {
        return LIMIT_PACK_FIELDS.getValue(propertyContainer.get(LIMIT_PACK_FIELDS));
    }

    public void setPackFields(Integer packFields)
    {
        DOMableGeneric<Integer> value = (DOMableGeneric<Integer>) propertyContainer.get(LIMIT_PACK_FIELDS);
        value.setValue(packFields);

    }

    public boolean isSort()
    {
        return SORT.getValue(propertyContainer.get(SORT));
    }

    public void setSort(boolean sort)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(SORT);
        value.setValue(sort);
    }

    public boolean isVerbose()
    {
        return VERBOSE.getValue(propertyContainer.get(VERBOSE));
    }

    public void setVerbose(boolean verbose)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(VERBOSE);
        value.setValue(verbose);
    }

    public boolean isOverwrite()
    {
        return OVERWRITE.getValue(propertyContainer.get(OVERWRITE));
    }

    public void setOverwrite(boolean overwrite)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(OVERWRITE);
        value.setValue(overwrite);
    }

    public boolean isStatistics()
    {
        return STATISTICS.getValue(propertyContainer.get(STATISTICS));
    }

    public void setStatistics(boolean statistics)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(STATISTICS);
        value.setValue(statistics);
    }

    public boolean isRestorePackages()
    {
        return RESTORE_PACKAGES.getValue(propertyContainer.get(RESTORE_PACKAGES));
    }

    public void setRestorePackages(boolean restorePackages)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(RESTORE_PACKAGES);
        value.setValue(restorePackages);
    }

    public boolean isPipe()
    {
        return PIPE.getValue(propertyContainer.get(PIPE));
    }

    public void setPipe(boolean pipe)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(PIPE);
        value.setValue(pipe);
    }

    public String getJadPath()
    {
        return JAD_PATH.getValue(propertyContainer.get(JAD_PATH));
    }

    public void setJadPath(String jadPath)
    {
        DOMableGeneric<String> value = (DOMableGeneric<String>) propertyContainer.get(JAD_PATH);
        value.setValue(jadPath);
    }

    public boolean isDecompileToMemory()
    {
        return DECOMPILE_TO_MEMORY.getValue(propertyContainer.get(DECOMPILE_TO_MEMORY));
    }

    public void setDecompileToMemory(boolean decompileToMemory)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(DECOMPILE_TO_MEMORY);
        value.setValue(decompileToMemory);
    }

    public boolean isCreateOutputDirectory()
    {
        return CREATE_OUTPUT_DIRECTORY.getValue(propertyContainer.get(CREATE_OUTPUT_DIRECTORY));
    }

    public void setCreateOutputDirectory(boolean create)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(CREATE_OUTPUT_DIRECTORY);
        value.setValue(create);
    }

    public boolean isAlwaysExcludeRecursively()
    {
        return ALWAYS_EXCLUDE_RECURSIVELY.getValue(propertyContainer.get(ALWAYS_EXCLUDE_RECURSIVELY));
    }

    public void setAlwaysExcludeRecursively(boolean alwaysExcludeRecursively)
    {
        DOMableGeneric<Boolean> value = (DOMableGeneric<Boolean>) propertyContainer.get(ALWAYS_EXCLUDE_RECURSIVELY);
        value.setValue(alwaysExcludeRecursively);
    }

    @NotNull
    public String getName()
    {
        return domable.getName();
    }

    @NotNull
    public Element write()
    {
        return domable.write();
    }

    // javadoc inherited
    public void read(@NotNull Element element)
    {
        domable.read(element);
    }

    // javadoc inherited
    public Object getValue()
    {
        return null;
    }

    /**
     * Creates a standard boolean property that will be rendered when its value is not empty.
     *
     * @param name the name of the property
     * @return the property
     */
    private static CommandLinePropertyDescriptor<String> createStringProperty(@NotNull String name)
    {
        return createStringProperty(name,
                                    null);
    }

    /**
     * Creates a standard boolean property that will be rendered when its value is not empty.
     *
     * @param name         the name of the property
     * @param defaultValue the default value of the property
     * @return the property
     */
    private static CommandLinePropertyDescriptor<String> createStringProperty(@NotNull String name,
                                                                              @Nullable String defaultValue)
    {
        return new ImmutableCommandLinePropertyDescriptor<String>(name,
                                                                  defaultValue,
                                                                  RenderRuleFactory.getRenderRule(StringRules.NOT_EMPTY),
                                                                  RenderType.VALUE);
    }

    /**
     * Creates a standard boolean property that will be rendered when its value is true.
     *
     * @param name the name of the property
     * @return the property
     */
    private static CommandLinePropertyDescriptor<Boolean> createBooleanProperty(@NotNull String name)
    {
        return new ImmutableCommandLinePropertyDescriptor<Boolean>(name,
                                                                   RenderRuleFactory.getRenderRule(BooleanRules.TRUE));
    }

    /**
     * Creates a standard boolean property that will be rendered when its value is not negative.
     *
     * @param name         the name of the property
     * @param defaultValue the default value the property
     * @return the property
     */
    private static CommandLinePropertyDescriptor<Integer> createIntegerProperty(@NotNull String name,
                                                                                @Nullable Integer defaultValue)
    {
        return new ImmutableCommandLinePropertyDescriptor<Integer>(name,
                                                                   defaultValue,
                                                                   RenderRuleFactory.getRenderRule(IntegerRules.NON_NEGATIVE),
                                                                   RenderType.VALUE_NO_SPACE);
    }

    public List<CommandLinePropertyDescriptor> getCommandLinePropertyDescriptors()
    {
        return commandLinePropertyDescriptors;
    }

    public ExclusionTableModel getExclusionTableModel()
    {
        return EXCLUSION_TABLE_MODEL.getValue(propertyContainer.get(EXCLUSION_TABLE_MODEL));
    }
}