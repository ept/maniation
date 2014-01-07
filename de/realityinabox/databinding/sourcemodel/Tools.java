package de.realityinabox.databinding.sourcemodel;

import java.util.StringTokenizer;

public class Tools {

    public static String toJavaName(String basis, boolean startUpperCase) {
        boolean allUpperCase = true;
        for (int i=0; i < basis.length(); i++) {
            if (Character.isLowerCase(basis.charAt(i))) allUpperCase = false;
        }
        if (allUpperCase) basis = basis.toLowerCase();
        boolean nextUpper = startUpperCase;
        String adjusted = "";
        for (int i=0; i < basis.length(); i++) {
            if (Character.isLetterOrDigit(basis.charAt(i))) {
                if ((i == 0) && (!startUpperCase)) adjusted += Character.toLowerCase(basis.charAt(i)); else
                if (nextUpper) adjusted += Character.toUpperCase(basis.charAt(i)); else
                    adjusted += basis.charAt(i);
                nextUpper = Character.isDigit(basis.charAt(i));
            } else nextUpper = true;
        }
        return adjusted;
    }

    public static String toJavaConstant(String basis) {
        String result = "";
        boolean previous_lower = false;
        boolean previous_letter = true;
        for (int i = 0; i < basis.length(); i++) {
            char c = basis.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                if (Character.isUpperCase(c)) {
                    if (previous_lower && previous_letter) result += "_";
                    result += c;
                    previous_lower = false;
                    previous_letter = true;
                } else {
                    result += Character.toUpperCase(c);
                    previous_lower = true;
                    previous_letter = true;
                }
            } else {
                if (previous_letter) result += "_";
                previous_letter = false;
            }
        }
        return result;
    }

    public static String URLtoPackage(java.net.URL url) {
        String result = "";
        StringTokenizer st = new StringTokenizer(url.getHost(), ".", true);
        while (st.hasMoreTokens()) result = st.nextToken() + result;
        return result + url.getPath().replace('/', '.');
    }

    /**
     * Turn an english noun from its singular into its plural form.
     * Handles quite a lot of cases, but obviously not everything.
     */
    public static String singularToPlural(String noun) {
        // Several rules are borrowed from the Lingua::EN::Inflect perl
        // module by Damian Conway. I suppose that's ok under the Perl
        // Artistic License.
        String n = noun.toLowerCase();
        
        if (n.endsWith("y") && !n.endsWith("ay") && !n.endsWith("ey") &&
                !n.endsWith("iy") && !n.endsWith("oy") && !n.endsWith("uy"))
            return noun.substring(0, noun.length() - 1) + "ies";
        if (n.endsWith("man"))
            return noun.substring(0, noun.length() - 2) + "en";
        if (n.endsWith("child"))
            return noun + "ren";
        if (n.endsWith("mouse") || n.endsWith("louse"))
            return noun.substring(0, noun.length() - 4) + "ice";
        if (n.endsWith("goose"))
            return noun.substring(0, noun.length() - 4) + "eese";
        if (n.endsWith("tooth"))
            return noun.substring(0, noun.length() - 4) + "eeth";
        if (n.endsWith("foot"))
            return noun.substring(0, noun.length() - 3) + "eet";
        if (n.endsWith("index") || n.endsWith("simplex") || n.endsWith("codex") ||
                n.endsWith("vortex") || n.endsWith("vertex") || n.endsWith("cortex") ||
                n.endsWith("radix") || n.endsWith("helix") || n.endsWith("appendix"))
            return noun.substring(0, noun.length() - 2) + "ices";
        if (n.endsWith("bacterium") || n.endsWith("erratum") || n.endsWith("extremum") ||
                n.endsWith("maximum") || n.endsWith("minimum") || n.endsWith("momentum") ||
                n.endsWith("optimum") || n.endsWith("quantum") || n.endsWith("cranium") ||
                n.endsWith("curriculum") || n.endsWith("dictum") || n.endsWith("aquarium") ||
                n.endsWith("compendium") || n.endsWith("gymnasium") || n.endsWith("memorandum") ||
                n.endsWith("millennium") || n.endsWith("spectrum") || n.endsWith("stadium") ||
                n.endsWith("ultimatum") || n.endsWith("medium") || n.endsWith("vacuum") ||
                n.endsWith("consortium"))
            return noun.substring(0, noun.length() - 2) + "a";
        if (n.endsWith("alumnus") || n.endsWith("locus") || n.endsWith("nucleus") ||
                n.endsWith("stimulus") || n.endsWith("focus") || n.endsWith("radius") ||
                n.endsWith("fungus"))
            return noun.substring(0, noun.length() - 2) + "i";
        if (n.endsWith("criterion") || n.endsWith("phenomenon"))
            return noun.substring(0, noun.length() - 2) + "a";
        if (n.endsWith("s") || n.endsWith("x") || n.endsWith("z") || n.endsWith("ch") ||
                n.endsWith("sh"))
            return noun + "es";
        if (n.endsWith("alf") || n.endsWith("elf") || n.endsWith("olf") ||
                (n.endsWith("eaf") && !n.endsWith("deaf")) || n.endsWith("arf"))
            return noun.substring(0, noun.length() - 1) + "ves";
        if (n.endsWith("life") || n.endsWith("nife") || n.endsWith("wife"))
            return noun.substring(0, noun.length() - 2) + "ves";
        if (n.endsWith("o") && !n.endsWith("ao") && !n.endsWith("eo") && !n.endsWith("io") &&
                !n.endsWith("oo") && !n.endsWith("uo") && !n.endsWith("commando") &&
                !n.endsWith("crescendo") && !n.endsWith("dynamo") && !n.endsWith("embryo") &&
                !n.endsWith("ghetto") && !n.endsWith("manifesto") && !n.endsWith("photo") &&
                !n.endsWith("pro") && !n.endsWith("casino") && !n.endsWith("auto") &&
                !n.endsWith("macro") && !n.endsWith("zero") && !n.endsWith("solo") &&
                !n.endsWith("soprano") && !n.endsWith("basso") && !n.endsWith("alto") &&
                !n.endsWith("tempo") && !n.endsWith("piano") && !n.endsWith("virtuoso"))
            return noun + "es";
        return noun + "s";
    }
}
