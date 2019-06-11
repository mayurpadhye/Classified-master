package mimosale.com.favourite;

public class FavListPojo {
    String id,   name,   user_id,  preference_id,   like_count, price, type,   image1,   image2,  discount,description;
boolean isChecked;

    public FavListPojo(String id, String name, String user_id, String preference_id, String like_count, String price, String type, String image1, String image2, String discount, boolean isChecked,String description) {
        this.id = id;
        this.name = name;
        this.user_id = user_id;
        this.preference_id = preference_id;
        this.like_count = like_count;
        this.price = price;
        this.type = type;
        this.image1 = image1;
        this.image2 = image2;
        this.discount = discount;
        this.isChecked = isChecked;
        this.description=description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPreference_id() {
        return preference_id;
    }

    public void setPreference_id(String preference_id) {
        this.preference_id = preference_id;
    }



    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
