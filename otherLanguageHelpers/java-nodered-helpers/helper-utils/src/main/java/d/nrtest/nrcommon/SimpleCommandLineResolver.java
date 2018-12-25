package d.nrtest.nrcommon;


//import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Domen Mori 19. 10. 2018.
 */
public class SimpleCommandLineResolver {

    private String[] args;

    public SimpleCommandLineResolver(String[] args) {
        this.args = args;
    }

    public String getString(final String... cmdAliases) {
        List<String> cmdList = Arrays.asList(cmdAliases);
        try {
            for (int i = 0; i < args.length; ++i) {
                if (cmdList.contains(args[i])) {
                    return args[i + 1];
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return null;
    }

    public String getStringOrDefault(String defaultValue, final String... cmdAliases) {
        String itm = getString(cmdAliases);
        if(itm == null){
            return defaultValue;
        }
        return itm;
    }

    public Integer getInt(String... cmdAliases) {
        try {
            return Integer.parseInt(getString(cmdAliases));
        } catch (NumberFormatException x) {
            return null;
        }
    }

    public Integer getInt(int defaultValue, String... cmdAliases) {
        try {
            return Integer.parseInt(getString(cmdAliases));
        } catch (NumberFormatException x) {
            return defaultValue;
        }
    }

    public boolean hasAny(String... item) {
        for (String arg : args) {
            for (String s : item) {
                if (arg.equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
}
