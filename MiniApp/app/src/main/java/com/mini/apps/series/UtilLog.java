package com.mini.apps.series;

import android.util.Log;

public class UtilLog {
    public static final String DefaultTag = "kjm0909";

    public static boolean enable = true;
    public static boolean showClassName = false;
    private static int MaxTraceLevel = 2;
    private static String tag = "kjm0909";
    private static String className = null;

    public static enum LogType
    {
        d(0),
        i(1),
        e(2),
        w(3),
        v(4),

        DEBUG(0),
        INFO(1),
        ERROR(2),
        WARNING(3),
        VERVOSE(4);

        private final int value;

        private LogType( int _Value )
        {
            this.value = _Value;
        }

        public int getValue()
        {
            return value;
        }
    };

    public static void setTag( String _Tag )
    {
        tag = _Tag;
    }

    public static String getTag( )
    {
        if( tag == null || tag.length() == 0 )
        {
            return DefaultTag;
        }
        return tag;
    }

    /**
     * @param maxTraceLevel
     */
    public static void setMaxTraceLevel( int maxTraceLevel )
    {
        if( maxTraceLevel > 0 )
        {
            maxTraceLevel = 0;
        }

        MaxTraceLevel = maxTraceLevel;
    }

    public static int getMaxTraceLevel()
    {
        if( MaxTraceLevel < 0 )
        {
            return 0;
        }

        return MaxTraceLevel;
    }

    public static void d( String _Msg )
    {
        show( LogType.d, getTag(), _Msg, true );
    }

    public static void e( String _Msg )
    {
        show( LogType.e, getTag(), _Msg, true );
    }

    public static void i( String _Msg )
    {
        show( LogType.i, getTag(), _Msg, true );
    }

    public static void w( String _Msg )
    {
        show( LogType.w, getTag(), _Msg, true );
    }

    public static void v( String _Msg )
    {
        show( LogType.v, getTag(), _Msg, true );
    }

    public static void d( String _Tag, String _Msg )
    {
        show( LogType.d, _Tag, _Msg, true );
    }

    public static void e( String _Tag, String _Msg )
    {
        show( LogType.e, _Tag, _Msg, true );
    }

    public static void i( String _Tag, String _Msg )
    {
        show( LogType.i, _Tag, _Msg, true );
    }

    public static void w( String _Tag, String _Msg )
    {
        show( LogType.w, _Tag, _Msg, true );
    }
    public static void v( String _Tag, String _Msg )
    {
        show( LogType.v, _Tag, _Msg, true );

    }

    public static String getClassName()
    {
        if( className == null )
        {
            className = new UtilLog().getClass().getName();
        }
        return className;
    }

    private static void show( UtilLog.LogType _Type, String _Tag, String _Msg, boolean _ShowMethodName )
    {
        if( !enable)
            return;

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        if( _ShowMethodName )
        {
            String methodTrace = "";
            String fileName = stackTraceElements[4].getFileName();
            String methodName = stackTraceElements[4].getMethodName();
            int Line = stackTraceElements[4].getLineNumber();
            int minLevel = 0; // zero-based, 2 is show(..);
            int maxLevel = getMaxTraceLevel();

            for ( int i = 0; i < stackTraceElements.length; i++ )
            {
                if( stackTraceElements[ i ].getClassName().equals( getClassName() ) )
                {
                    minLevel = i + 1;
                }
            }

            if( stackTraceElements.length - minLevel < getMaxTraceLevel() )
            {
                maxLevel = stackTraceElements.length - minLevel;
            }

            for ( int i = minLevel; i < minLevel + maxLevel; i++ )
            {
                if( i == minLevel )
                    methodTrace = stackTraceElements[ i ].getMethodName();
                else
                    methodTrace = stackTraceElements[ i ].getMethodName() + " -> " + methodTrace;


                if( showClassName )
                    methodTrace = stackTraceElements[ i ].getClassName() + ":" + methodTrace;
            }
            if( methodTrace.length() > 0 )
                _Msg =  fileName +", " + methodName + "(), " + Line +", " + _Msg;

            //_Msg =  fileName +", " + methodName + "(), " + Line +", " + "(" +  + ") " + _Msg;
        }
        // Output
        switch ( _Type )
        {
            case d:
            case DEBUG:
                Log.d( _Tag, _Msg );
                break;
            case e:
            case ERROR:
                Log.e( _Tag, _Msg );
                break;
            case i:
            case INFO:
                Log.i( _Tag, _Msg );
                break;
            case w:
            case WARNING:
                Log.w( _Tag, _Msg );
                break;
            case v:
            case VERVOSE:
                Log.v( _Tag, _Msg );
                break;

            default:
                break;
        }
    }
}
