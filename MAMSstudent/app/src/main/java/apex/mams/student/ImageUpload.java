package apex.mams.student;

/**
 * Created by admin on 23-12-2017.
 */

class ImageUpload {
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
