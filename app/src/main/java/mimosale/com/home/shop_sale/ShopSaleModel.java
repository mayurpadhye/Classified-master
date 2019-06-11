package mimosale.com.home.shop_sale;

public class ShopSaleModel {
    String product_name;

    String id,name,user_id,preference_id,address_line1,address_line2,city,state,country,pincode,lat,lon,low_price,high_price,min_discount,max_discount;
    String start_date,end_date,phone,hash_tags,description,web_url,image,image2,type,discount;
String fav_status,like_count,like_status;



    public ShopSaleModel(String id, String name, String user_id, String preference_id, String address_line1, String address_line2, String city, String state, String country, String pincode, String lat, String lon, String low_price, String high_price, String discount, String start_date, String end_date, String phone, String hash_tags, String description, String web_url,String image,String image2,String type,String fav_status,String like_status,String like_count) {
        this.id = id;
        this.name = name;
        this.user_id = user_id;
        this.preference_id = preference_id;
        this.address_line1 = address_line1;
        this.address_line2 = address_line2;
        this.image2=image2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.lat = lat;
        this.lon = lon;
        this.low_price = low_price;
        this.high_price = high_price;
        this.discount = discount;

        this.start_date = start_date;
        this.end_date = end_date;
        this.phone = phone;
        this.hash_tags = hash_tags;
        this.description = description;
        this.web_url = web_url;
        this.image=image;
        this.type=type;
        this.fav_status=fav_status;
        this.like_status=like_status;
        this.like_count=like_count;
    }

    public String getFav_status() {
        return fav_status;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getLike_status() {
        return like_status;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }

    public void setFav_status(String fav_status) {
        this.fav_status = fav_status;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ShopSaleModel(String product_name) {
        this.product_name = product_name;
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

    public String getAddress_line1() {
        return address_line1;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLow_price() {
        return low_price;
    }

    public void setLow_price(String low_price) {
        this.low_price = low_price;
    }

    public String getHigh_price() {
        return high_price;
    }

    public void setHigh_price(String high_price) {
        this.high_price = high_price;
    }



    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHash_tags() {
        return hash_tags;
    }

    public void setHash_tags(String hash_tags) {
        this.hash_tags = hash_tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}
