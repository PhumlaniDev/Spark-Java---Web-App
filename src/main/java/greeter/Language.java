package greeter;

public enum Language {

    IsiXhosa("Mholo"),
    English("Hello"),
    Sesotho("Dumela"),
    Afrikaans("Hallo"),
    IsiNebele("Salibonani");

    private String Lang;

    Language(String lang) {
        Lang = lang;
    }

    public String getLang() {
        return Lang;
    }
}
