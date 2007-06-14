package net.stevechaloner.intellijad.decompilers;

/**
 * @author Steve Chaloner
 */
public class DecompilationException extends Exception
{
    /**
     * Initialises a new instance of this class.
     *
     * @param string the message
     */
    public DecompilationException(String string)
    {
        super(string);
    }

    /**
     * Initialises a new instance of this class.
     *
     * @param string    the message
     * @param throwable the cause
     */
    public DecompilationException(String string,
                                  Throwable throwable)
    {
        super(string,
              throwable);
    }

    /**
     * Initialises a new instance of this class.
     *
     * @param throwable the cause
     */
    public DecompilationException(Throwable throwable)
    {
        super(throwable);
    }
}
