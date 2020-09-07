package apex.mams.admin;

/**
 * Created by LENOVO on 12/18/2017.
 */

public class ImageUpload {
    public String name;
    public String url;

    public ImageUpload(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
    public ImageUpload() {

    }

}
