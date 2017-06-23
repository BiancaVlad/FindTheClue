package model;

/**
 * Created by Vlad on 17-Jun-17.
 */
public class ExternalLoginViewModel
{
    private String name;

    private String url;

    private String state;

    public ExternalLoginViewModel(String name, String url, String state) {
        this.name = name;
        this.url = url;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
